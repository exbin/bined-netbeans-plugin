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
package org.exbin.deltahex.netbeans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * @version 0.1.5 2017/02/26
 * @author ExBin Project (http://exbin.org)
 */
@ActionID(
        category = "File",
        id = "org.exbin.deltahex.netbeans.FileOpenAsHexAction"
)
@ActionRegistration(
        iconBase = "org/exbin/deltahex/netbeans/resources/icons/icon.png",
        displayName = "#CTL_FileOpenAsHexAction"
)
@ActionReference(path = "Menu/File", position = 850)
@Messages("CTL_FileOpenAsHexAction=Open File as Hex...")
public final class FileOpenAsHexAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        final Mode editorMode = WindowManager.getDefault().findMode("editor");
        if (editorMode == null) {
            return;
        }

        final HexEditorTopComponent hexEditor = new HexEditorTopComponent();
        editorMode.dockInto(hexEditor);

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(hexEditor);
        if (result == JFileChooser.APPROVE_OPTION) {
            FileObject fileObject = FileUtil.toFileObject(fileChooser.getSelectedFile());
            try {
                DataObject dataObject = DataObject.find(fileObject);
                hexEditor.openDataObject(dataObject);
                hexEditor.open();
                hexEditor.requestActive();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
