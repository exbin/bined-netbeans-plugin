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
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Open file in binary editor action.
 *
 * @version 0.2.0 2018/12/22
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
@ActionID(category = "File", id = "org.exbin.bined.OpenAsBinaryAction")
@ActionRegistration(displayName = "#CTL_OpenAsBinaryAction")
@NbBundle.Messages("CTL_OpenAsBinaryAction=Open as Binary")
public final class OpenAsBinaryAction implements ActionListener {

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
}
