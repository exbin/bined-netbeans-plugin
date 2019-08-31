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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.exbin.bined.netbeans.panel.DebugViewPanel;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.spi.debugger.ui.DebuggingView;
import org.openide.util.ImageUtilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Debug view button.
 *
 * @version 0.2.0 2019/08/25
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public final class BinaryDebugButton extends JButton {

    public BinaryDebugButton() {
        super(ImageUtilities.loadImageIcon("org/exbin/bined/netbeans/resources/icons/icon.png", false));
        addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Mode editorMode = WindowManager.getDefault().findMode("LocalsView");
                TopComponent activeTC = TopComponent.getRegistry().getActivated();
                DebuggingView debuggingView = DebuggingView.getDefault();
//                DebuggerManager.getDebuggerManager().get
//                TopComponent outputWindow = WindowManager.getDefault().findTopComponent();
//                activeTC.
//                ViewFactory viewFactory = activeTC.getLookup().lookup(ViewFactory.class);
//                viewFactory.View.LOCALS_VIEW_NAME
//                JOptionPane optionPane = new JOptionPane("Test");
//                optionPane.setVisible(true);
                DebugViewPanel debugViewPanel = new DebugViewPanel();
            }
        });
        setToolTipText("Show as Binary");
    }
}
