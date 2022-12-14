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
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import org.exbin.bined.CodeType;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.action.gui.DropDownButton;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Binary editor toolbar panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdToolbarPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinEdToolbarPanel.class);

    private final BinaryEditorPreferences preferences;
    private final ExtCodeArea codeArea;
    private final AbstractAction optionsAction;
    private final AbstractAction onlineHelpAction;

    private final AbstractAction cycleCodeTypesAction;
    private final JRadioButtonMenuItem binaryCodeTypeAction;
    private final JRadioButtonMenuItem octalCodeTypeAction;
    private final JRadioButtonMenuItem decimalCodeTypeAction;
    private final JRadioButtonMenuItem hexadecimalCodeTypeAction;
    private final ButtonGroup codeTypeButtonGroup;
    private DropDownButton codeTypeDropDown;

    public BinEdToolbarPanel(BinaryEditorPreferences preferences, ExtCodeArea codeArea, AbstractAction optionsAction, AbstractAction onlineHelpAction) {
        this.preferences = preferences;
        this.codeArea = codeArea;
        this.optionsAction = optionsAction;
        this.onlineHelpAction = onlineHelpAction;

        codeTypeButtonGroup = new ButtonGroup();
        binaryCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Binary") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.BINARY);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(binaryCodeTypeAction);
        octalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Octal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.OCTAL);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(octalCodeTypeAction);
        decimalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Decimal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.DECIMAL);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(decimalCodeTypeAction);
        hexadecimalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Hexadecimal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setCodeType(CodeType.HEXADECIMAL);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(hexadecimalCodeTypeAction);
        cycleCodeTypesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int codeTypePos = codeArea.getCodeType().ordinal();
                CodeType[] values = CodeType.values();
                CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                codeArea.setCodeType(next);
                updateCycleButtonState();
            }
        };

        initComponents();
        init();
    }

    private void init() {
        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, "Cycle thru code types");
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(binaryCodeTypeAction);
        cycleCodeTypesPopupMenu.add(octalCodeTypeAction);
        cycleCodeTypesPopupMenu.add(decimalCodeTypeAction);
        cycleCodeTypesPopupMenu.add(hexadecimalCodeTypeAction);
        codeTypeDropDown = new DropDownButton(cycleCodeTypesAction, cycleCodeTypesPopupMenu);
        updateCycleButtonState();
        controlToolBar.add(codeTypeDropDown);

        controlToolBar.addSeparator();
        JButton optionsButton = new JButton();
        optionsButton.setAction(optionsAction);
        optionsButton.setToolTipText("Options");
        optionsButton.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/options/gui/resources/icons/Preferences16.gif")));
        controlToolBar.add(optionsButton);

        JButton onlineHelpButton = new JButton();
        onlineHelpButton.setAction(onlineHelpAction);
        onlineHelpButton.setToolTipText("Online Help");
        onlineHelpButton.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/open_icon_library/icons/png/16x16/actions/help.png")));
        controlToolBar.add(onlineHelpButton);
    }

    private void updateCycleButtonState() {
        CodeType codeType = codeArea.getCodeType();
        codeTypeDropDown.setActionText(codeType.name().substring(0, 3));
        switch (codeType) {
            case BINARY: {
                if (!binaryCodeTypeAction.isSelected()) {
                    binaryCodeTypeAction.setSelected(true);
                }
                break;
            }
            case OCTAL: {
                if (!octalCodeTypeAction.isSelected()) {
                    octalCodeTypeAction.setSelected(true);
                }
                break;
            }
            case DECIMAL: {
                if (!decimalCodeTypeAction.isSelected()) {
                    decimalCodeTypeAction.setSelected(true);
                }
                break;
            }
            case HEXADECIMAL: {
                if (!hexadecimalCodeTypeAction.isSelected()) {
                    hexadecimalCodeTypeAction.setSelected(true);
                }
                break;
            }
        }
    }

    public void applyFromCodeArea() {
        updateCycleButtonState();
        updateUnprintables();
    }

    public void loadFromPreferences() {
        codeArea.setCodeType(preferences.getCodeAreaPreferences().getCodeType());
        updateCycleButtonState();
        updateUnprintables();
    }

    public void updateUnprintables() {
        showUnprintablesToggleButton.setSelected(codeArea.isShowUnprintables());
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlToolBar = new javax.swing.JToolBar();
        showUnprintablesToggleButton = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();

        controlToolBar.setBorder(null);
        controlToolBar.setFloatable(false);
        controlToolBar.setRollover(true);

        showUnprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/netbeans/resources/icons/insert-pilcrow.png"))); // NOI18N
        showUnprintablesToggleButton.setToolTipText(resourceBundle.getString("showUnprintablesToggleButton.toolTipText")); // NOI18N
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
        codeArea.setShowUnprintables(showUnprintablesToggleButton.isSelected());
    }//GEN-LAST:event_showUnprintablesToggleButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToggleButton showUnprintablesToggleButton;
    // End of variables declaration//GEN-END:variables

}
