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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.exbin.bined.netbeans.main.BinaryUndoSwingHandler;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.UndoRedoWrapper;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.actions.SaveAction;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditor;
import org.openide.text.DataEditorSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * BinEd native NetBeans editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
//@MultiViewElement.Registration(
//        displayName = "#BinEdEditor.displayName",
//        mimeType = BinEdDataObject.MIME_TYPE,
//        persistenceType = TopComponent.PERSISTENCE_NEVER,
//        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
//        preferredID = BinEdEditor.ELEMENT_ID,
//        position = BinEdEditor.POSITION_ATTRIBUTE
//)
@ParametersAreNonnullByDefault
public class BinEdEditor extends CloneableEditor implements MultiViewElement, HelpCtx.Provider {

    public static final String ELEMENT_ID = "org.exbin.bined.netbeans.BinEdEditor";
    public static final String ELEMENT_NAME = "org-exbin-bined-netbeans-BinEdEditor";
    public static final int POSITION_ATTRIBUTE = 900005;

    private static final String EDITORS_FOLDER = "Editors";
    private static final String MULTIVIEW_FOLDER = "MultiView";
    private static final String DYNAMIC_FILETYPE_PREFIX = "-nb";
    private static final String ELEMENT_INSTANCE = "Editors/application/octet-stream/" + MULTIVIEW_FOLDER + "/" + ELEMENT_NAME + ".instance";
    private static final String SHADOW_EXT = "shadow";
    private static final String ORIGINAL_FILE_ATTRIBUTE = "originalFile";

    private BinaryEditorTopComponent editorComponent;
    protected transient MultiViewElementCallback callback;
    private final Lookup lookup;

    public BinEdEditor(Lookup lookup) {
        this.lookup = lookup;
        
        // View could be restored before module is installed
        Installer.initBinEd();
    }

    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {
            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
//                if (integrationOptions.isRegisterBinaryMultiview()) {
//                    install();
//                } else {
//                    uninstall();
//                }
            }

            @Override
            public void uninstallIntegration() {
                uninstall();
            }
        });
    }

    @Nonnull
    @Override
    public JComponent getVisualRepresentation() {
        if (editorComponent == null) {
            editorComponent = new BinaryEditorTopComponent();
        }
        return editorComponent;
    }

    @Nonnull
    @Override
    public JComponent getToolbarRepresentation() {
        return new JPanel(); // TODO editorFile.getComponent().getToolbarPanel().getToolBar();
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        if (!editorComponent.getFileHandler().isModified()) {
            return CloseOperationState.STATE_OK;
        }

        SaveAction saveAction = new SaveAction();
        DataObject dataObject = lookup.lookup(DataObject.class);
        saveAction.putValue(Action.LONG_DESCRIPTION, String.format("File %s is modified. Save?", dataObject.getPrimaryFile().getNameExt()));
        return MultiViewFactory.createUnsafeCloseState("editor", saveAction, MultiViewFactory.NOOP_CLOSE_ACTION);
    }

    @Nonnull
    @Override
    public Action[] getActions() {
        if (callback != null) {
            return callback.createDefaultActions();
        }

        return new Action[0];
    }

    @Nonnull
    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentActivated() {
        editorComponent.componentActivated();
//        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
//        ComponentActivationListener componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();
//        fileHandler.componentActivated(componentActivationListener);
    }

    @Override
    public void componentDeactivated() {
        editorComponent.componentDeactivated();
//        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
//        ComponentActivationListener componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();
//        fileHandler.componentDeactivated(componentActivationListener);
    }

    @Override
    public void componentOpened() {
        DataObject dataObject = lookup.lookup(DataObject.class);
        if (dataObject != null) {
            if (dataObject instanceof BinEdDataObject) {
                // throw new UnsupportedOperationException("Not supported yet.");
                // ((BinEdDataObject) dataObject).setVisualEditor(this);
            }

            openFile(dataObject);
            dataObject.getPrimaryFile().addFileChangeListener(new FileChangeAdapter() {
                @Override
                public void fileChanged(FileEvent fe) {
                    openFile(dataObject);
                }
            });

            if (callback != null) {
                callback.updateTitle(dataObject.getPrimaryFile().getNameExt());
            }
        }
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentClosed() {
        editorComponent.getFileHandler().closeData();
    }

    @Nullable
    @Override
    public UndoRedo getUndoRedo() {
        UndoRedoWrapper undoWrapper = (UndoRedoWrapper) (editorComponent.getFileHandler().getUndoRedo().orElse(null));
        return undoWrapper != null ? ((BinaryUndoSwingHandler) undoWrapper.getUndoRedo()).getUndoManager() : null;
    }

    @Nonnull
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void save() {
//        try {
//            getEditorSupport().saveDocument();
//        } catch (IOException ex) {
//            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        editorComponent.getFileHandler().saveFile();
        DataObject dataObject = lookup.lookup(DataObject.class);
        BinEdFileHandler fileHandler = editorComponent.getFileHandler();
        OutputStream stream = null;
        try {
            stream = dataObject.getPrimaryFile().getOutputStream();
            if (stream != null) {
                fileHandler.saveToStream(stream);
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        fileHandler.fileSync();
        editorComponent.updateStatus();
    }

    public void openFile(DataObject dataObject) {
        BinEdFileHandler fileHandler = editorComponent.getFileHandler();
        SectCodeArea codeArea = fileHandler.getCodeArea();
        boolean editable = dataObject.getPrimaryFile().canWrite();
//        URI fileUri = dataObject.getPrimaryFile().toURI();
//        if (fileUri == null) {
            InputStream stream = null;
            try {
                stream = dataObject.getPrimaryFile().getInputStream();
                if (stream != null) {
                    fileHandler.loadFromStream(stream);
                }
            } catch (IOException ex) {
                Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
//        } else {
//            codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
//            File file = Utilities.toFile(fileUri);
//            fileHandler.loadFromFile(file.toURI(), null);
//        }
        fileHandler.fileSync();
        editorComponent.updateStatus();
    }

    private DataEditorSupport getEditorSupport() {
        return (DataEditorSupport) cloneableEditorSupport();
    }

    public static void install() {
        FileObject allTypesFolder = FileUtil.getSystemConfigFile("Editors/" + MULTIVIEW_FOLDER);
        FileObject binaryTypeFolder = FileUtil.getSystemConfigFile("Editors/application/octet-stream/" + MULTIVIEW_FOLDER);
        try {
            FileObject allTypesObject = allTypesFolder.getFileObject(BinEdEditorMulti.ELEMENT_MULTI_NAME, "disabled");
            if (allTypesObject != null) {
                FileLock lock = null;
                try {
                    lock = allTypesObject.lock();
                    allTypesObject.rename(lock, BinEdEditorMulti.ELEMENT_MULTI_NAME, "instance");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                allTypesFolder.refresh();
            }
            FileObject binaryTypeObject = binaryTypeFolder.getFileObject(ELEMENT_NAME, "instance");
            if (binaryTypeObject != null) {
                FileLock lock = null;
                try {
                    lock = binaryTypeObject.lock();
                    binaryTypeObject.rename(lock, ELEMENT_NAME, "disabled");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                binaryTypeFolder.refresh();
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                installForMimeType(mimeSubType, type, subType);
            }
        }
        
        /*        FileObject targetFolder = FileUtil.getSystemConfigFile(EDITORS_FOLDER + "/" + MULTIVIEW_FOLDER);
        targetFolder.move(FileLock.NONE, targetFolder, ELEMENT_ID, SHADOW_EXT)
        FileObject elementRecord = targetFolder.getFileObject(ELEMENT_ID + "." + SHADOW_EXT);
        if (elementRecord == null) {
            try {
//                final FileObject record = targetFolder.createData(ELEMENT_ID + "." + SHADOW_EXT);
//                record.setAttribute(ORIGINAL_FILE_ATTRIBUTE, ELEMENT_INSTANCE);
//                record.setAttribute("position", POSITION_ATTRIBUTE);
//                targetFolder.refresh();
            } catch (IOException ex) {
                Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } */
    }

    public static void uninstall() {
        FileObject allTypesFolder = FileUtil.getSystemConfigFile("Editors/" + MULTIVIEW_FOLDER);
        FileObject binaryTypeFolder = FileUtil.getSystemConfigFile("Editors/application/octet-stream/" + MULTIVIEW_FOLDER);
        try {
            FileObject allTypesObject = allTypesFolder.getFileObject(BinEdEditorMulti.ELEMENT_MULTI_NAME, "instance");
            if (allTypesObject != null) {
                FileLock lock = null;
                try {
                    lock = allTypesObject.lock();
                    allTypesObject.rename(lock, BinEdEditorMulti.ELEMENT_MULTI_NAME, "disabled");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                allTypesFolder.refresh();
            }
            FileObject binaryTypeObject = binaryTypeFolder.getFileObject(ELEMENT_NAME, "disabled");
            if (binaryTypeObject != null) {
                FileLock lock = null;
                try {
                    lock = binaryTypeObject.lock();
                    binaryTypeObject.rename(lock, ELEMENT_NAME, "instance");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                binaryTypeFolder.refresh();
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*        
        FileObject targetFolder = FileUtil.getSystemConfigFile(EDITORS_FOLDER + "/" + MULTIVIEW_FOLDER);
        FileObject elementRecord = targetFolder.getFileObject(ELEMENT_ID + "." + SHADOW_EXT);
        if (elementRecord != null) {
            try {
                elementRecord.delete();
                targetFolder.refresh();
            } catch (IOException ex) {
                Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } */
    }

    /*
    public static void install() {
        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                installForMimeType(mimeSubType, type, subType);
            }
        }
    }

    public static void uninstall() {
        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                uninstallForMimeType(mimeSubType, type, subType);
            }
        }
    }
*/
    private static void installForMimeType(FileObject fileType, String mimeType, String mimeSubType) {
        if (!fileType.isFolder()) {
            return;
        }

        // It seems that NetBeans registers types with -nb postfix for dynamically loaded plugins
        if (mimeType.endsWith(DYNAMIC_FILETYPE_PREFIX) || BinEdDataObject.MIME_TYPE.equals(mimeType + "/" + mimeSubType)) {
            return;
        }

        try {
            FileObject multiViewFolder = fileType.getFileObject(MULTIVIEW_FOLDER);
            if (multiViewFolder == null) {
                multiViewFolder = FileUtil.createFolder(fileType, MULTIVIEW_FOLDER);
            }

            final FileObject editorRecord = multiViewFolder.getFileObject(ELEMENT_ID + "." + SHADOW_EXT);
            if (editorRecord == null) {
                FileObject record = multiViewFolder.createData(ELEMENT_ID + "." + SHADOW_EXT);
                record.setAttribute(ORIGINAL_FILE_ATTRIBUTE, ELEMENT_INSTANCE);
                record.setAttribute("position", POSITION_ATTRIBUTE);
                record.setAttribute("persistenceType", TopComponent.PERSISTENCE_NEVER);
                
                // Register multiview
                record = multiViewFolder.createData(BinEdEditorMulti.ELEMENT_MULTI_ID);
                record.setAttribute("displayName", "Binary");
                record.setAttribute("mimeType", "");
                record.setAttribute("class", "org.exbin.bined.netbeans.BinEdEditorMulti");
                Method method = org.netbeans.core.spi.multiview.MultiViewFactory.class.getMethod("createMultiViewDescription", Map.class);
                record.setAttribute("instanceCreate", method);
                record.setAttribute("instanceClass", "org.netbeans.core.multiview.ContextAwareDescription");
                record.setAttribute("iconBase", "org/exbin/bined/netbeans/resources/icons/icon.png");
                record.setAttribute("preferredID", BinEdEditorMulti.ELEMENT_ID);
                record.setAttribute("position", BinEdEditorMulti.POSITION_ATTRIBUTE);
                record.setAttribute("persistenceType", TopComponent.PERSISTENCE_NEVER);
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static void uninstallForMimeType(FileObject fileType, String mimeType, String mimeSubType) {
        if (!fileType.isFolder()) {
            return;
        }

        // It seems that NetBeans registers types with -nb postfix for dynamically loaded plugins
        if (mimeType.endsWith(DYNAMIC_FILETYPE_PREFIX) || BinEdDataObject.MIME_TYPE.equals(mimeType + "/" + mimeSubType)) {
            return;
        }

        try {
            FileObject multiViewFolder = fileType.getFileObject(MULTIVIEW_FOLDER);
            if (multiViewFolder == null) {
                return;
            }

            final FileObject editorRecord = multiViewFolder.getFileObject(ELEMENT_ID + "." + SHADOW_EXT);
            if (editorRecord != null) {
                editorRecord.delete();
                multiViewFolder.refresh();

                boolean hasAttributes = multiViewFolder.getAttributes().hasMoreElements();
                boolean hasChildren = multiViewFolder.getChildren().length > 0;
                if (!hasAttributes && !hasChildren) {
                    multiViewFolder.delete();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
