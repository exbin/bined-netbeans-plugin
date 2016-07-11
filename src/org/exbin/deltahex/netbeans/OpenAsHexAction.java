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
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * Open file in hexadecimal editor action.
 *
 * @version 0.1.1 2016/05/28
 * @author ExBin Project (http://exbin.org)
 */
@ActionID(category = "File", id = "org.exbin.deltahex.OpenAsHexAction")
@ActionRegistration(displayName = "#CTL_OpenAsHexAction")
@Messages("CTL_OpenAsHexAction=Open as Hex")
public final class OpenAsHexAction implements ActionListener {

    private final DataObject context;

    public OpenAsHexAction(DataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final Mode editorMode = WindowManager.getDefault().findMode("editor");
        if (editorMode == null) {
            return;
        }

        final HexEditorTopComponent hexEditor = new HexEditorTopComponent();
        editorMode.dockInto(hexEditor);

        hexEditor.openDataObject(context);
        hexEditor.open();
        hexEditor.requestActive();
    }
}
