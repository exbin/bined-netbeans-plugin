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
package org.exbin.bined.netbeans.debug.panel;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.exbin.bined.EditationMode;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.bined.netbeans.panel.BinEdComponentPanel;

/**
 * Panel to show debug view.
 *
 * @version 0.2.2 2020/01/02
 * @author ExBin Project (http://exbin.org)
 */
public class DebugViewPanel extends javax.swing.JPanel {
    
    private final BinEdComponentPanel componentPanel;

    public DebugViewPanel() {
        componentPanel = new BinEdComponentPanel();
        setLayout(new BorderLayout());

        initComponents();
        infoToolbar = new javax.swing.JPanel();
        infoToolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        controlToolBar = infoToolbar;
//        showUnprintablesToggleButton = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();

//        showUnprintablesToggleButton.setFocusable(false);
//        showUnprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/netbeans/resources/icons/insert-pilcrow.png"))); // NOI18N
//        showUnprintablesToggleButton.setToolTipText("Show symbols for unprintable/whitespace characters");
//        showUnprintablesToggleButton.addActionListener(this::showUnprintablesToggleButtonActionPerformed);
//        controlToolBar.add(showUnprintablesToggleButton);
        controlToolBar.add(jSeparator3);

        componentPanel.getCodeArea().setEditationMode(EditationMode.READ_ONLY);

        add(componentPanel, BorderLayout.CENTER);

//        registerBinaryStatus(statusPanel);

//        codeTypeButtonGroup = new ButtonGroup();
//        binaryCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Binary") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                codeArea.setCodeType(CodeType.BINARY);
//                updateCycleButtonName();
//            }
//        });
//        codeTypeButtonGroup.add(binaryCodeTypeAction);
//        octalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Octal") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                codeArea.setCodeType(CodeType.OCTAL);
//                updateCycleButtonName();
//            }
//        });
//        codeTypeButtonGroup.add(octalCodeTypeAction);
//        decimalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Decimal") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                codeArea.setCodeType(CodeType.DECIMAL);
//                updateCycleButtonName();
//            }
//        });
//        codeTypeButtonGroup.add(decimalCodeTypeAction);
//        hexadecimalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Hexadecimal") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                codeArea.setCodeType(CodeType.HEXADECIMAL);
//                updateCycleButtonName();
//            }
//        });
//        codeTypeButtonGroup.add(hexadecimalCodeTypeAction);
//        cycleCodeTypesAction = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                int codeTypePos = codeArea.getCodeType().ordinal();
//                CodeType[] values = CodeType.values();
//                CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
//                codeArea.setCodeType(next);
//                updateCycleButtonName();
//            }
//        };

//        codeArea.setComponentPopupMenu(new JPopupMenu() {
//            @Override
//            public void show(Component invoker, int x, int y) {
//                int clickedX = x;
//                int clickedY = y;
//                if (invoker instanceof JViewport) {
//                    clickedX += ((JViewport) invoker).getParent().getX();
//                    clickedY += ((JViewport) invoker).getParent().getY();
//                }
//                JPopupMenu popupMenu = createContextMenu(clickedX, clickedY);
//                popupMenu.show(invoker, x, y);
//            }
//        });

//        codeArea.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent keyEvent) {
//                int modifiers = keyEvent.getModifiers();
//                if (modifiers == ActionUtils.getMetaMask()) {
//                    int keyCode = keyEvent.getKeyCode();
//                    switch (keyCode) {
//                        case KeyEvent.VK_F: {
////                            showSearchPanel(false);
//                            break;
//                        }
//                        case KeyEvent.VK_G: {
//                            goToRowAction.actionPerformed(new ActionEvent(keyEvent.getSource(), keyEvent.getID(), ""));
//                            break;
//                        }
//                    }
//                }
//            }
//        });

        init();

//        initialLoadFromPreferences();
//
//        goToRowAction = new GoToPositionAction(codeArea);
//
//        applyFromCodeArea();
    }

    private void init() {
//        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, "Cycle thru code types");
//        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
//        cycleCodeTypesPopupMenu.add(binaryCodeTypeAction);
//        cycleCodeTypesPopupMenu.add(octalCodeTypeAction);
//        cycleCodeTypesPopupMenu.add(decimalCodeTypeAction);
//        cycleCodeTypesPopupMenu.add(hexadecimalCodeTypeAction);
//        codeTypeDropDown = new DropDownButton(cycleCodeTypesAction, cycleCodeTypesPopupMenu);
//        updateCycleButtonName();
//        controlToolBar.add(codeTypeDropDown);
    }

    private JPanel controlToolBar;
    private javax.swing.JPanel infoToolbar;
    private javax.swing.JToolBar.Separator jSeparator3;
//    private javax.swing.JToggleButton showUnprintablesToggleButton;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        methodComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        methodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(methodComboBox, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> methodComboBox;
    // End of variables declaration//GEN-END:variables

//    private void updateCycleButtonName() {
//        CodeType codeType = codeArea.getCodeType();
//        codeTypeDropDown.setActionText(codeType.name().substring(0, 3));
//        switch (codeType) {
//            case BINARY: {
//                if (!binaryCodeTypeAction.isSelected()) {
//                    binaryCodeTypeAction.setSelected(true);
//                }
//                break;
//            }
//            case OCTAL: {
//                if (!octalCodeTypeAction.isSelected()) {
//                    octalCodeTypeAction.setSelected(true);
//                }
//                break;
//            }
//            case DECIMAL: {
//                if (!decimalCodeTypeAction.isSelected()) {
//                    decimalCodeTypeAction.setSelected(true);
//                }
//                break;
//            }
//            case HEXADECIMAL: {
//                if (!hexadecimalCodeTypeAction.isSelected()) {
//                    hexadecimalCodeTypeAction.setSelected(true);
//                }
//                break;
//            }
//        }
//    }

//    private JPopupMenu createContextMenu(int x, int y) {
//        final JPopupMenu result = new JPopupMenu();
//
//        BasicCodeAreaZone positionZone = codeArea.getPositionZone(x, y);
//
//        final JMenuItem copyMenuItem = new JMenuItem("Copy");
//        copyMenuItem.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/gui/menu/resources/icons/tango-icon-theme/16x16/actions/edit-copy.png")));
//        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionUtils.getMetaMask()));
//        copyMenuItem.setEnabled(codeArea.hasSelection());
//        copyMenuItem.addActionListener(e -> {
//            codeArea.copy();
//            result.setVisible(false);
//        });
//        result.add(copyMenuItem);
//
//        final JMenuItem copyAsCodeMenuItem = new JMenuItem("Copy as Code");
//        copyAsCodeMenuItem.setEnabled(codeArea.hasSelection());
//        copyAsCodeMenuItem.addActionListener(e -> {
//            codeArea.copyAsCode();
//            result.setVisible(false);
//        });
//        result.add(copyAsCodeMenuItem);
//
//        result.addSeparator();
//
//        final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
//        selectAllMenuItem.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/gui/menu/resources/icons/tango-icon-theme/16x16/actions/edit-select-all.png")));
//        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionUtils.getMetaMask()));
//        selectAllMenuItem.addActionListener(e -> {
//            codeArea.selectAll();
//            result.setVisible(false);
//        });
//        result.add(selectAllMenuItem);
//        result.addSeparator();
//
//        final JMenuItem goToMenuItem = new JMenuItem("Go To" + ActionUtils.DIALOG_MENUITEM_EXT);
//        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionUtils.getMetaMask()));
//        goToMenuItem.addActionListener(goToRowAction);
//        result.add(goToMenuItem);
//
//        return result;
//    }

//    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {
//        codeArea.setShowUnprintables(showUnprintablesToggleButton.isSelected());
//    }

//    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
//        this.encodingStatus = encodingStatusApi;
//        setCharsetChangeListener(() -> {
//            String selectedEncoding = codeArea.getCharset().name();
//            encodingStatus.setEncoding(selectedEncoding);
//        });
//    }

//    public void setCharsetChangeListener(CharsetChangeListener charsetChangeListener) {
//        this.charsetChangeListener = charsetChangeListener;
//    }

//    private void applyFromCodeArea() {
//        updateCycleButtonName();
//        showUnprintablesToggleButton.setSelected(codeArea.isShowUnprintables());
//    }


//    private void toolbarPanelLoadFromPreferences() {
//        codeArea.setCodeType(preferences.getCodeAreaPreferences().getCodeType());
//        updateCycleButtonName();
//        showUnprintablesToggleButton.setSelected(preferences.getCodeAreaPreferences().isShowUnprintables());
//    }

//    public void showValuesPanel() {
//        if (!valuesPanelVisible) {
//            valuesPanelVisible = true;
//            if (valuesPanel == null) {
//                valuesPanel = new ValuesPanel();
//                valuesPanel.setCodeArea(codeArea, null);
//                valuesPanelScrollPane = new JScrollPane(valuesPanel);
//            }
//            add(valuesPanelScrollPane, BorderLayout.EAST);
//            valuesPanel.enableUpdate();
//            valuesPanel.updateValues();
//            valuesPanelScrollPane.revalidate();
//            valuesPanel.revalidate();
//            revalidate();
//        }
//    }

    public void setData(BinaryData data) {
        componentPanel.getCodeArea().setContentData(data);
    }
}
