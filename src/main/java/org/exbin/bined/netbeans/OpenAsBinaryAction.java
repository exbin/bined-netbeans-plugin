/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.bined.netbeans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Open file in binary editor action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ActionID(category = "File", id = OpenAsBinaryAction.ACTION_ID)
@ActionRegistration(displayName = "#CTL_OpenAsBinaryAction")
@NbBundle.Messages("CTL_OpenAsBinaryAction=Open as Binary")
@ParametersAreNonnullByDefault
public final class OpenAsBinaryAction implements ActionListener {

    public static final String ACTION_ID = "org.exbin.bined.OpenAsBinaryAction";

    public static final String POSITION_ATTRIBUTE = "position";
    public static final String ORIGINAL_FILE_ATTRIBUTE = "originalFile";

    private static final String OPEN_AS_BINARY_ACTION_INSTANCE = "Actions/File/org-exbin-bined-OpenAsBinaryAction.instance";
    private static final String OPEN_AS_BINARY_ACTION_STRING = "org-exbin-bined-OpenAsBinaryAction.shadow";
    private static final String LOADERS_FOLDER = "Loaders";
    private static final String ACTIONS_FOLDER = "Actions";
    private static final String DYNAMIC_FILETYPE_PREFIX = "-nb";
    private static final String OPENIDE_OPEN_ACTION_STRING = "org-openide-actions-OpenAction.shadow";
    private static final String OPEN_ACTION_STRING = "OpenAction.shadow";
    private static final String CUT_TO_CLIPBOARD_ACTION_STRING = "CutAction.shadow";
    
    public OpenAsBinaryAction() {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final Mode editorMode = WindowManager.getDefault().findMode("editor");
        if (editorMode == null) {
            return;
        }

        final BinaryEditorTopComponent binaryEditor = new BinaryEditorTopComponent();
        editorMode.dockInto(binaryEditor);

        TopComponent activeTC = TopComponent.getRegistry().getActivated();
        DataObject dataObject = activeTC.getLookup().lookup(DataObject.class);
        if (dataObject instanceof DataShadow) {
            dataObject = ((DataShadow) dataObject).getOriginal();
        }

        if (dataObject != null) {
            binaryEditor.openDataObject(dataObject);
            binaryEditor.open();
            binaryEditor.requestActive();
        }
    }

    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {

            private Boolean contextOpenAsBinaryState = null;

            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
                if (integrationOptions.isRegisterContextOpenAsBinary()) {
                    if (contextOpenAsBinaryState == null || !contextOpenAsBinaryState) {
                        installInContextMenu();
                        contextOpenAsBinaryState = true;
                    }
                } else {
                    if (contextOpenAsBinaryState == null || contextOpenAsBinaryState) {
                        uninstallInContextMenu();
                        contextOpenAsBinaryState = false;
                    }
                }
            }

            @Override
            public void uninstallIntegration() {
                uninstallInContextMenu();
            }
        });
    }

    public static void installInContextMenu() {
        final FileObject loaders = FileUtil.getConfigFile(LOADERS_FOLDER);
        for (FileObject category : loaders.getChildren()) {
            for (FileObject fileType : category.getChildren()) {
                installForFileObject(fileType);
            }
        }
    }

    public static void uninstallInContextMenu() {
        final FileObject loaders = FileUtil.getConfigFile(LOADERS_FOLDER);
        for (FileObject category : loaders.getChildren()) {
            for (FileObject fileType : category.getChildren()) {
                uninstallForFileObject(fileType);
            }
        }
    }

    private static void installForFileObject(FileObject fileType) {
        if (!fileType.isFolder()) {
            return;
        }
        try {
            String fileTypeName = fileType.getName();
            // It seems that NetBeans registers types with -nb postfix for dynamically loaded plugins
            if (fileTypeName.endsWith(DYNAMIC_FILETYPE_PREFIX)) {
                // Drop legacy action registration
                FileObject fileTypeFolder = fileType.getParent().getFileObject(fileTypeName);
                if (fileTypeFolder != null) {
                    final FileObject actionsFolder = fileTypeFolder.getFileObject(ACTIONS_FOLDER);
                    if (actionsFolder != null) {
                        final FileObject openAsBinaryAction = actionsFolder.getFileObject(OPEN_AS_BINARY_ACTION_STRING);
                        if (openAsBinaryAction != null) {
                            openAsBinaryAction.delete();
                            actionsFolder.refresh();
                        }
                        boolean hasAttributes = actionsFolder.getAttributes().hasMoreElements();
                        boolean hasChildren = actionsFolder.getChildren().length > 0;
                        if (!hasAttributes && !hasChildren) {
                            actionsFolder.delete();
                        }
                    }

                    boolean hasAttributes = fileTypeFolder.getAttributes().hasMoreElements();
                    boolean hasChildren = fileTypeFolder.getChildren().length > 0;
                    if (!hasAttributes && !hasChildren) {
                        fileTypeFolder.delete();
                    }
                }

                fileType = FileUtil.createFolder(fileType.getParent(), fileTypeName.substring(0, fileTypeName.length() - 3));
            }

            FileObject actionsFolder = fileType.getFileObject(ACTIONS_FOLDER);
            if (actionsFolder == null) {
                actionsFolder = FileUtil.createFolder(fileType, ACTIONS_FOLDER);
            }

            /**
             * Attempt to establish correct position in context menu for
             * different mime types.
             *
             * TODO: check on different platforms and collisions with other
             * modules / modes
             */
            int actionPosition = 175;
            final FileObject openAction = actionsFolder.getFileObject(OPEN_ACTION_STRING);
            final FileObject openIdeOpenAction = actionsFolder.getFileObject(OPENIDE_OPEN_ACTION_STRING);
            final FileObject cutAction = actionsFolder.getFileObject(CUT_TO_CLIPBOARD_ACTION_STRING);
            if (cutAction != null) {
                Object position = cutAction.getAttribute(POSITION_ATTRIBUTE);
                if (position instanceof Integer) {
                    if ((Integer) position < actionPosition) {
                        actionPosition = (Integer) position - 25;
                    }
                }
            }
            if (openAction != null) {
                Object position = openAction.getAttribute(POSITION_ATTRIBUTE);
                if (position instanceof Integer) {
                    if ((Integer) position + 25 > actionPosition) {
                        actionPosition = (Integer) position + 25;
                    }
                }
            }
            if (openIdeOpenAction != null) {
                Object position = openIdeOpenAction.getAttribute(POSITION_ATTRIBUTE);
                if (position instanceof Integer) {
                    if ((Integer) position + 25 > actionPosition) {
                        actionPosition = (Integer) position + 25;
                    }
                }
            }

            final FileObject openAsBinaryAction = actionsFolder.getFileObject(OPEN_AS_BINARY_ACTION_STRING);
            if (openAsBinaryAction == null) {
                final FileObject action = actionsFolder.createData(OPEN_AS_BINARY_ACTION_STRING);
                action.setAttribute(ORIGINAL_FILE_ATTRIBUTE, OPEN_AS_BINARY_ACTION_INSTANCE);
                action.setAttribute(POSITION_ATTRIBUTE, actionPosition);
            } else {
                openAsBinaryAction.setAttribute(POSITION_ATTRIBUTE, actionPosition);
            }
        } catch (IOException ex) {
            Logger.getLogger(OpenAsBinaryAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void uninstallForFileObject(FileObject fileType) {
        if (!fileType.isFolder()) {
            return;
        }

        try {
            String fileTypeName = fileType.getName();
            if (fileTypeName.endsWith(DYNAMIC_FILETYPE_PREFIX)) {
                fileTypeName = fileTypeName.substring(0, fileTypeName.length() - 3);
            }

            FileObject fileTypeFolder = fileType.getParent().getFileObject(fileTypeName);
            if (fileTypeFolder != null) {
                final FileObject actionsFolder = fileTypeFolder.getFileObject(ACTIONS_FOLDER);
                if (actionsFolder != null) {
                    final FileObject openAsBinaryAction = actionsFolder.getFileObject(OPEN_AS_BINARY_ACTION_STRING);
                    if (openAsBinaryAction != null) {
                        openAsBinaryAction.delete();
                        actionsFolder.refresh();

                        boolean hasAttributes = actionsFolder.getAttributes().hasMoreElements();
                        boolean hasChildren = actionsFolder.getChildren().length > 0;
                        if (!hasAttributes && !hasChildren) {
                            actionsFolder.delete();
                        }

                        hasAttributes = fileTypeFolder.getAttributes().hasMoreElements();
                        hasChildren = fileTypeFolder.getChildren().length > 0;
                        if (!hasAttributes && !hasChildren) {
                            fileTypeFolder.delete();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OpenAsBinaryAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
