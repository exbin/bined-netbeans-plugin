/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JFileChooser;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * Open file in hexadecimal editor action.
 *
 * @version 0.2.0 2019/03/16
 * @author ExBin Project (http://exbin.org)
 */
@ActionID(
        category = "File",
        id = "org.exbin.bined.netbeans.FileOpenAsBinaryAction"
)
@ActionRegistration(
        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        displayName = "#CTL_FileOpenAsBinaryAction"
)
@ActionReference(path = "Menu/File", position = 850)
@Messages("CTL_FileOpenAsBinaryAction=Open File as Binary...")
@ParametersAreNonnullByDefault
public final class FileOpenAsBinaryAction implements ActionListener {

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
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
