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
package org.exbin.bined.netbeans.gui;

import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import org.exbin.bined.CodeType;
import org.exbin.framework.App;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.action.gui.DropDownButton;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Binary editor toolbar panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdToolbarPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getResourceBundleByBundleName("org/exbin/framework/bined/resources/BinedModule");
    private final java.util.ResourceBundle optionsResourceBundle = App.getModule(LanguageModuleApi.class).getResourceBundleByBundleName("org/exbin/framework/options/resources/OptionsModule");
    private final java.util.ResourceBundle onlineHelpResourceBundle = App.getModule(LanguageModuleApi.class).getResourceBundleByBundleName("org/exbin/framework/help/online/action/resources/OnlineHelpAction");

    private final Control codeAreaControl;
    private AbstractAction optionsAction;
    private AbstractAction onlineHelpAction;

    private final AbstractAction cycleCodeTypesAction;
    private final JRadioButtonMenuItem binaryCodeTypeMenuItem;
    private final JRadioButtonMenuItem octalCodeTypeMenuItem;
    private final JRadioButtonMenuItem decimalCodeTypeMenuItem;
    private final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem;
    private final ButtonGroup codeTypeButtonGroup;
    private DropDownButton codeTypeDropDown;

    public BinEdToolbarPanel(JComponent targetComponent, Control codeAreaControl) {
        // BinaryEditorPreferences preferences, ExtCodeArea codeArea, AbstractAction optionsAction, AbstractAction onlineHelpAction
        this.codeAreaControl = codeAreaControl;

        codeTypeButtonGroup = new ButtonGroup();
        Action binaryCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.BINARY);
                updateCycleButtonState();
            }
        };
        binaryCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("binaryCodeTypeAction.text"));
        binaryCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("binaryCodeTypeAction.shortDescription"));
        binaryCodeTypeMenuItem = new JRadioButtonMenuItem(binaryCodeTypeAction);
        codeTypeButtonGroup.add(binaryCodeTypeMenuItem);
        Action octalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.OCTAL);
                updateCycleButtonState();
            }
        };
        octalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("octalCodeTypeAction.text"));
        octalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("octalCodeTypeAction.shortDescription"));
        octalCodeTypeMenuItem = new JRadioButtonMenuItem(octalCodeTypeAction);
        codeTypeButtonGroup.add(octalCodeTypeMenuItem);
        Action decimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.DECIMAL);
                updateCycleButtonState();
            }
        };
        decimalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("decimalCodeTypeAction.text"));
        decimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("decimalCodeTypeAction.shortDescription"));
        decimalCodeTypeMenuItem = new JRadioButtonMenuItem(decimalCodeTypeAction);
        codeTypeButtonGroup.add(decimalCodeTypeMenuItem);
        Action hexadecimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.HEXADECIMAL);
                updateCycleButtonState();
            }
        };
        hexadecimalCodeTypeAction.putValue(Action.NAME, resourceBundle.getString("hexadecimalCodeTypeAction.text"));
        hexadecimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("hexadecimalCodeTypeAction.shortDescription"));
        hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem(hexadecimalCodeTypeAction);
        codeTypeButtonGroup.add(hexadecimalCodeTypeMenuItem);
        cycleCodeTypesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int codeTypePos = codeAreaControl.getCodeType().ordinal();
                CodeType[] values = CodeType.values();
                CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                codeAreaControl.setCodeType(next);
                updateCycleButtonState();
            }
        };

        initComponents();
        init();
    }

    private void init() {
        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("cycleCodeTypesAction.shortDescription"));
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(binaryCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(octalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(decimalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(hexadecimalCodeTypeMenuItem);
        codeTypeDropDown = new DropDownButton(cycleCodeTypesAction, cycleCodeTypesPopupMenu);
        updateCycleButtonState();
        controlToolBar.add(codeTypeDropDown);

        controlToolBar.addSeparator();
        JButton optionsButton = new JButton();
        optionsButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (optionsAction != null) {
                    optionsAction.actionPerformed(e);
                }
            }
        });
        optionsButton.setToolTipText(optionsResourceBundle.getString("optionsAction.text"));
        optionsButton.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/options/gui/resources/icons/Preferences16.gif")));
        controlToolBar.add(optionsButton);

        JButton onlineHelpButton = new JButton();
        onlineHelpButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onlineHelpAction != null) {
                    onlineHelpAction.actionPerformed(e);
                }
            }
        });
        onlineHelpButton.setToolTipText(onlineHelpResourceBundle.getString("onlineHelpAction.text"));
        onlineHelpButton.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/open_icon_library/icons/png/16x16/actions/help.png")));
        controlToolBar.add(onlineHelpButton);
    }

    public void setOptionsAction(AbstractAction optionsAction) {
        this.optionsAction = optionsAction;
    }

    public void setOnlineHelpAction(AbstractAction onlineHelpAction) {
        this.onlineHelpAction = onlineHelpAction;
    }

    private void updateCycleButtonState() {
        CodeType codeType = codeAreaControl.getCodeType();
        codeTypeDropDown.setActionText(codeType.name().substring(0, 3));
        switch (codeType) {
            case BINARY: {
                if (!binaryCodeTypeMenuItem.isSelected()) {
                    binaryCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case OCTAL: {
                if (!octalCodeTypeMenuItem.isSelected()) {
                    octalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case DECIMAL: {
                if (!decimalCodeTypeMenuItem.isSelected()) {
                    decimalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
            case HEXADECIMAL: {
                if (!hexadecimalCodeTypeMenuItem.isSelected()) {
                    hexadecimalCodeTypeMenuItem.setSelected(true);
                }
                break;
            }
        }
    }

    public void applyFromCodeArea() {
        updateCycleButtonState();
        updateUnprintables();
    }

    public void loadFromPreferences(BinaryEditorPreferences preferences) {
        codeAreaControl.setCodeType(preferences.getCodeAreaPreferences().getCodeType());
        updateCycleButtonState();
        updateUnprintables();
    }

    public void updateUnprintables() {
        showUnprintablesToggleButton.setSelected(codeAreaControl.isShowUnprintables());
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (codeTypeDropDown != null) {
            codeTypeDropDown.updateUI();
        }
    }

    @Nonnull
    public JToolBar getToolBar() {
        return controlToolBar;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlToolBar = new javax.swing.JToolBar();
        showUnprintablesToggleButton = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();

        controlToolBar.setBorder(null);
        controlToolBar.setRollover(true);

        showUnprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/netbeans/resources/icons/insert-pilcrow.png"))); // NOI18N
        showUnprintablesToggleButton.setToolTipText(resourceBundle.getString("viewUnprintablesAction.text")); // NOI18N
        showUnprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUnprintablesToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(showUnprintablesToggleButton);
        controlToolBar.add(separator1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 338, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUnprintablesToggleButtonActionPerformed
        codeAreaControl.setShowUnprintables(showUnprintablesToggleButton.isSelected());
    }//GEN-LAST:event_showUnprintablesToggleButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToggleButton showUnprintablesToggleButton;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Control {

        @Nonnull
        CodeType getCodeType();

        void setCodeType(CodeType codeType);

        boolean isShowUnprintables();

        void setShowUnprintables(boolean showUnprintables);

        void repaint();
    }
}
