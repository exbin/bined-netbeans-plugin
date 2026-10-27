/*
 * Copyright (C) ExBin Project, https://exbin.org
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
import java.awt.event.ActionListener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import org.exbin.auxiliary.dropdownbutton.DropDownButton;
import org.exbin.bined.CodeType;
import org.exbin.jaguif.App;
import org.exbin.bined.jaguif.viewer.settings.CodeAreaOptions;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;

/**
 * Binary editor toolbar panel.
 */
@NullMarked
public class BinEdToolbarPanel extends javax.swing.JPanel {

    protected final java.util.ResourceBundle componentResourceBundle;
    protected final java.util.ResourceBundle viewerResourceBundle;
    protected final java.util.ResourceBundle optionsSettingsResourceBundle;
    protected final java.util.ResourceBundle onlineHelpResourceBundle;

    protected @Nullable Control codeAreaControl;
    protected ActionListener optionsAction;
    protected ActionListener onlineHelpAction;

    protected final AbstractAction cycleCodeTypesAction;
    protected final JRadioButtonMenuItem binaryCodeTypeMenuItem;
    protected final JRadioButtonMenuItem octalCodeTypeMenuItem;
    protected final JRadioButtonMenuItem decimalCodeTypeMenuItem;
    protected final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem;
    protected final ButtonGroup codeTypeButtonGroup;
    protected DropDownButton codeTypeDropDown;

    public BinEdToolbarPanel() {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        componentResourceBundle = languageModule.getBundle(org.exbin.bined.jaguif.component.BinedComponentModule.class);
        viewerResourceBundle = languageModule.getBundle(org.exbin.bined.jaguif.viewer.BinedViewerModule.class);
        optionsSettingsResourceBundle = languageModule.getBundle(org.exbin.jaguif.options.settings.OptionsSettingsModule.class);
        onlineHelpResourceBundle = languageModule.getBundle(org.exbin.jaguif.help.online.action.OnlineHelpAction.class);

        codeTypeButtonGroup = new ButtonGroup();
        Action binaryCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.BINARY);
                updateCycleButtonState();
            }
        };
        binaryCodeTypeAction.putValue(Action.NAME, viewerResourceBundle.getString("binaryCodeTypeAction.text"));
        binaryCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, viewerResourceBundle.getString("binaryCodeTypeAction.shortDescription"));
        binaryCodeTypeMenuItem = new JRadioButtonMenuItem(binaryCodeTypeAction);
        codeTypeButtonGroup.add(binaryCodeTypeMenuItem);
        Action octalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.OCTAL);
                updateCycleButtonState();
            }
        };
        octalCodeTypeAction.putValue(Action.NAME, viewerResourceBundle.getString("octalCodeTypeAction.text"));
        octalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, viewerResourceBundle.getString("octalCodeTypeAction.shortDescription"));
        octalCodeTypeMenuItem = new JRadioButtonMenuItem(octalCodeTypeAction);
        codeTypeButtonGroup.add(octalCodeTypeMenuItem);
        Action decimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.DECIMAL);
                updateCycleButtonState();
            }
        };
        decimalCodeTypeAction.putValue(Action.NAME, viewerResourceBundle.getString("decimalCodeTypeAction.text"));
        decimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, viewerResourceBundle.getString("decimalCodeTypeAction.shortDescription"));
        decimalCodeTypeMenuItem = new JRadioButtonMenuItem(decimalCodeTypeAction);
        codeTypeButtonGroup.add(decimalCodeTypeMenuItem);
        Action hexadecimalCodeTypeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeAreaControl.setCodeType(CodeType.HEXADECIMAL);
                updateCycleButtonState();
            }
        };
        hexadecimalCodeTypeAction.putValue(Action.NAME, viewerResourceBundle.getString("hexadecimalCodeTypeAction.text"));
        hexadecimalCodeTypeAction.putValue(Action.SHORT_DESCRIPTION, viewerResourceBundle.getString("hexadecimalCodeTypeAction.shortDescription"));
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
        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, viewerResourceBundle.getString("cycleCodeTypesAction.shortDescription"));
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(binaryCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(octalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(decimalCodeTypeMenuItem);
        cycleCodeTypesPopupMenu.add(hexadecimalCodeTypeMenuItem);
        codeTypeDropDown = new DropDownButton(cycleCodeTypesAction, cycleCodeTypesPopupMenu);
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
        optionsButton.setToolTipText(optionsSettingsResourceBundle.getString("settingsAction.text"));
        optionsButton.setIcon(new ImageIcon(getClass().getResource(optionsSettingsResourceBundle.getString("settingsAction.smallIcon"))));
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
        onlineHelpButton.setIcon(new ImageIcon(getClass().getResource("/org/exbin/bined/netbeans/resources/icons/help.png")));
        controlToolBar.add(onlineHelpButton);
    }

    public void setTargetComponent(JComponent targetComponent) {
        // controlToolBar.setTargetComponent(targetComponent);
    }

    public void setCodeAreaControl(Control codeAreaControl) {
        this.codeAreaControl = codeAreaControl;
        updateNonprintables();
        updateCycleButtonState();
    }

    public void setOptionsAction(ActionListener optionsAction) {
        this.optionsAction = optionsAction;
    }

    public void setOnlineHelpAction(ActionListener onlineHelpAction) {
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
        updateNonprintables();
    }

    public void loadFromOptions(OptionsStorage options) {
        codeAreaControl.setCodeType(new CodeAreaOptions(options).getCodeType());
        updateCycleButtonState();
        updateNonprintables();
    }

    public void updateNonprintables() {
        showNonprintablesToggleButton.setSelected(codeAreaControl.isShowNonprintables());
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (codeTypeDropDown != null) {
            codeTypeDropDown.updateUI();
        }
    }

    public JToolBar getToolBar() {
        return controlToolBar;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlToolBar = new javax.swing.JToolBar();
        showNonprintablesToggleButton = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();

        controlToolBar.setBorder(null);
        controlToolBar.setRollover(true);

        showNonprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(componentResourceBundle.getString("toggleNonprintablesAction.smallIcon"))));
        showNonprintablesToggleButton.setToolTipText(componentResourceBundle.getString("viewNonprintablesAction.text")); // NOI18N
        showNonprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showNonprintablesToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(showNonprintablesToggleButton);
        controlToolBar.add(separator1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 354, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showNonprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showNonprintablesToggleButtonActionPerformed
        codeAreaControl.setShowNonprintables(showNonprintablesToggleButton.isSelected());
    }//GEN-LAST:event_showNonprintablesToggleButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToggleButton showNonprintablesToggleButton;
    // End of variables declaration//GEN-END:variables

    @NullMarked
    public interface Control {

        CodeType getCodeType();

        void setCodeType(CodeType codeType);

        boolean isShowNonprintables();

        void setShowNonprintables(boolean showNonprintables);

        void repaint();
    }
}
