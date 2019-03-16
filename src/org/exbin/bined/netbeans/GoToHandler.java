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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.panel.GoToBinaryPanel;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 * Go to handler.
 *
 * @version 0.2.0 2019/03/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GoToHandler {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GoToHandler.class);

    private final ExtCodeArea codeArea;
    private Action goToRowAction;

    public GoToHandler(ExtCodeArea codeArea) {
        this.codeArea = codeArea;
        init();
    }

    private void init() {
    }

    @Nonnull
    public Action getGoToRowAction() {
        if (goToRowAction == null) {
            goToRowAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    final GoToBinaryPanel goToPanel = new GoToBinaryPanel();
                    DefaultControlPanel goToControlPanel = new DefaultControlPanel(goToPanel.getResourceBundle());
                    goToPanel.setCursorPosition(codeArea.getCaretPosition().getDataPosition());
                    goToPanel.setMaxPosition(codeArea.getDataSize());
                    JPanel dialogPanel = WindowUtils.createDialogPanel(goToPanel, goToControlPanel);
                    DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, resourceBundle.getString("goToRowAction.dialogTitle"), true, new Object[0], null, 0, null, null);

                    final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
                    goToPanel.initFocus();
                    goToControlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                        if (actionType == DefaultControlHandler.ControlActionType.OK) {
                            goToPanel.acceptInput();
                            codeArea.setCaretPosition(goToPanel.getGoToPosition());
                        }

                        WindowUtils.closeWindow(dialog);
                    });
                    WindowUtils.assignGlobalKeyListener(dialog, goToControlPanel.createOkCancelListener());
                    dialog.setVisible(true);
                }
            };
        }
        return goToRowAction;
    }
}
