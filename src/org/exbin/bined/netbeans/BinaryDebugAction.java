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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;

/**
 * Debug view action.
 *
 * @version 0.2.0 2019/03/19
 * @author ExBin Project (http://exbin.org)
 */
@ActionID(
        category = "Debug",
        id = "org.exbin.bined.netbeans.BinaryDebugAction"
)
@ActionRegistration(
        displayName = "#CTL_BinaryDebugAction"
)
//@Messages("CTL_BinaryDebugAction=Show as Binary")
public final class BinaryDebugAction implements ActionListener {

    private final List<ObjectVariable> context;

    public BinaryDebugAction(List<ObjectVariable> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // new UIPanel(context);
        DialogWrapper dialog = WindowUtils.createDialog(new JPanel(), (Component) e.getSource(), "Selected Variables", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.show();
    }
}
