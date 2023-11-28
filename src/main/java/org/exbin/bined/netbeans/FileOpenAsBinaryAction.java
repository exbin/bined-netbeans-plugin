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
import javax.swing.JFileChooser;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * Open file in binary editor action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ActionID(
        category = "File",
        id = FileOpenAsBinaryAction.ACTION_ID
)
@ActionRegistration(
        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        displayName = "#CTL_FileOpenAsBinaryAction"
)
// @ActionReference(path = FileOpenAsBinaryAction.ACTION_PATH, position = FileOpenAsBinaryAction.ACTION_POSITION)
@NbBundle.Messages("CTL_FileOpenAsBinaryAction=Open File as Binary...")
@ParametersAreNonnullByDefault
public final class FileOpenAsBinaryAction implements ActionListener {

    public static final String ACTION_ID = "org.exbin.bined.netbeans.FileOpenAsBinaryAction";
    public static final String ACTION_STRING = ACTION_ID + ".shadow";
    public static final String ACTION_PATH = "Menu/File";
    public static final String ACTION_INSTANCE = "Actions/File/org-exbin-bined-netbeans-FileOpenAsBinaryAction.instance";
    public static final int ACTION_POSITION = 850;

    public FileOpenAsBinaryAction() {
    }
    
    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {
            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
                if (integrationOptions.isRegisterFileMenuOpenAsBinary()) {
                    install();
                } else {
                    uninstall();
                }
            }

            @Override
            public void uninstallIntegration() {
                uninstall();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final Mode editorMode = WindowManager.getDefault().findMode("editor");
        if (editorMode == null) {
            return;
        }

        final BinaryEditorTopComponent editorComponent = new BinaryEditorTopComponent();
        editorMode.dockInto(editorComponent);

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(editorComponent);
        if (result == JFileChooser.APPROVE_OPTION) {
            FileObject fileObject = FileUtil.toFileObject(fileChooser.getSelectedFile());
            try {
                DataObject dataObject = DataObject.find(fileObject);
                editorComponent.openDataObject(dataObject);
                editorComponent.open();
                editorComponent.requestActive();
            } catch (DataObjectNotFoundException ex) {
                Logger.getLogger(FileOpenAsBinaryAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void install() {
        FileObject node = FileUtil.getSystemConfigFile(ACTION_PATH);
        if (node == null) {
            return;
        }

        try {
            int actionPosition = ACTION_POSITION;
            final FileObject openAsBinaryAction = node.getFileObject(ACTION_STRING);
            if (openAsBinaryAction == null) {
                final FileObject action = node.createData(ACTION_STRING);
                action.setAttribute(OpenAsBinaryAction.ORIGINAL_FILE_ATTRIBUTE, ACTION_INSTANCE);
                action.setAttribute(OpenAsBinaryAction.POSITION_ATTRIBUTE, actionPosition);
            } else {
                openAsBinaryAction.setAttribute(OpenAsBinaryAction.POSITION_ATTRIBUTE, actionPosition);
            }
        } catch (IOException ex) {
            Logger.getLogger(OpenAsBinaryAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void uninstall() {
        FileObject node = FileUtil.getSystemConfigFile(ACTION_PATH);
        if (node == null) {
            return;
        }

        try {
            final FileObject openAsBinaryAction = node.getFileObject(ACTION_STRING);
            if (openAsBinaryAction != null) {
                openAsBinaryAction.delete();
                node.refresh();
            }
        } catch (IOException ex) {
            Logger.getLogger(OpenAsBinaryAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
