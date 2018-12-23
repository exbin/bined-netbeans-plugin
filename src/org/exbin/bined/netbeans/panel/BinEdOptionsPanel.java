/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.bined.netbeans.panel;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.exbin.bined.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.basic.BasicBackgroundPaintMode;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.delta.DeltaDocument;
import org.exbin.bined.extended.theme.ExtendedBackgroundPaintMode;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.netbeans.BinaryEditorTopComponent;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.editor.text.panel.TextFontOptionsPanel;
import org.exbin.framework.editor.text.panel.TextFontPanel;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbPreferences;

/**
 * Hexadecimal editor options panel.
 *
 * @version 0.2.0 2018/12/22
 * @author ExBin Project (http://exbin.org)
 */
public class BinEdOptionsPanel extends javax.swing.JPanel {

    private final Preferences preferences;
    private final BinEdOptionsPanelController controller;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinEdOptionsPanel.class);

    private DefaultListModel<CategoryItem> categoryModel = new DefaultListModel<>();
    private JPanel currentCategoryPanel = null;

    private Font binEdDefaultFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private Font binEdFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    public BinEdOptionsPanel() {
        this(null);
        updateFontTextField();
    }

    public BinEdOptionsPanel(BinEdOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        preferences = NbPreferences.forModule(BinaryEditorTopComponent.class);

        categoryModel.addElement(new CategoryItem("Mode", modePanel));
        categoryModel.addElement(new CategoryItem("Layout", layoutPanel));
        categoryModel.addElement(new CategoryItem("Decoration", decorationPanel));
        categoryModel.addElement(new CategoryItem("Fonts & Colors", fontsAndColorPanel));
        categoriesList.setModel(categoryModel);

        categoriesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedIndex = categoriesList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    CategoryItem categoryItem = categoryModel.get(selectedIndex);
                    currentCategoryPanel = categoryItem.getCategoryPanel();
                    mainPane.setViewportView(currentCategoryPanel);
                    mainPane.invalidate();
                    revalidate();
                    mainPane.repaint();
                }
            }
        });
        categoriesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, ((CategoryItem) value).categoryName, index, isSelected, cellHasFocus);
                return component;
            }
        });
        categoriesList.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        layoutPanel = new javax.swing.JPanel();
        wrapLineModeCheckBox = new javax.swing.JCheckBox();
        lineLengthLabel = new javax.swing.JLabel();
        lineLengthSpinner = new javax.swing.JSpinner();
        headerPanel = new javax.swing.JPanel();
        showHeaderCheckBox = new javax.swing.JCheckBox();
        headerSpaceLabel = new javax.swing.JLabel();
        headerSpaceComboBox = new javax.swing.JComboBox<>();
        headerSpaceSpinner = new javax.swing.JSpinner();
        lineNumbersPanel = new javax.swing.JPanel();
        showLineNumbersCheckBox = new javax.swing.JCheckBox();
        lineNumberLengthLabel = new javax.swing.JLabel();
        lineNumbersLengthComboBox = new javax.swing.JComboBox<>();
        lineNumbersLengthSpinner = new javax.swing.JSpinner();
        lineNumberSpaceLabel = new javax.swing.JLabel();
        lineNumbersSpaceComboBox = new javax.swing.JComboBox<>();
        lineNumbersSpaceSpinner = new javax.swing.JSpinner();
        byteGroupSizeLabel = new javax.swing.JLabel();
        byteGroupSizeSpinner = new javax.swing.JSpinner();
        spaceGroupSizeSpinner = new javax.swing.JSpinner();
        spaceGroupSizeLabel = new javax.swing.JLabel();
        modePanel = new javax.swing.JPanel();
        codeTypeScrollModeLabel = new javax.swing.JLabel();
        codeTypeComboBox = new javax.swing.JComboBox<>();
        viewModeComboBox = new javax.swing.JComboBox<>();
        showNonprintableCharactersCheckBox = new javax.swing.JCheckBox();
        viewModeScrollModeLabel = new javax.swing.JLabel();
        codeColorizationCheckBox = new javax.swing.JCheckBox();
        memoryModeLabel = new javax.swing.JLabel();
        memoryModeComboBox = new javax.swing.JComboBox<>();
        showValuesPanelCheckBox = new javax.swing.JCheckBox();
        decorationPanel = new javax.swing.JPanel();
        hexCharactersModeLabel = new javax.swing.JLabel();
        backgroundModeLabel = new javax.swing.JLabel();
        hexCharactersModeComboBox = new javax.swing.JComboBox<>();
        backgroundModeComboBox = new javax.swing.JComboBox<>();
        positionCodeTypeLabel = new javax.swing.JLabel();
        lineNumbersBackgroundCheckBox = new javax.swing.JCheckBox();
        positionCodeTypeComboBox = new javax.swing.JComboBox<>();
        linesPanel = new javax.swing.JPanel();
        decoratorLineNumLineCheckBox = new javax.swing.JCheckBox();
        decoratorPreviewLineCheckBox = new javax.swing.JCheckBox();
        decoratorBoxCheckBox = new javax.swing.JCheckBox();
        decoratorHeaderLineCheckBox = new javax.swing.JCheckBox();
        fontsAndColorPanel = new javax.swing.JPanel();
        fontLabel = new javax.swing.JLabel();
        fontTextField = new javax.swing.JTextField();
        selectFontButton = new javax.swing.JButton();
        useDefaultFontCheckBox = new javax.swing.JCheckBox();
        colorsLabel = new javax.swing.JLabel();
        categoriesLabel = new javax.swing.JLabel();
        categoriesScrollPane = new javax.swing.JScrollPane();
        categoriesList = new javax.swing.JList<>();
        mainPane = new javax.swing.JScrollPane();

        org.openide.awt.Mnemonics.setLocalizedText(wrapLineModeCheckBox, resourceBundle.getString("wrapLineModeCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lineLengthLabel, resourceBundle.getString("lineLengthLabel.text")); // NOI18N

        lineLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(16, 1, null, 1));

        headerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("headerPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(showHeaderCheckBox, resourceBundle.getString("showHeaderCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headerSpaceLabel, resourceBundle.getString("headerSpaceLabel.text")); // NOI18N

        headerSpaceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NONE", "SPECIFIED", "QUARTER_UNIT", "HALF_UNIT", "ONE_UNIT", "ONE_AND_HALF_UNIT", "DOUBLE_UNIT" }));
        headerSpaceComboBox.setSelectedIndex(2);

        headerSpaceSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addComponent(headerSpaceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(headerSpaceSpinner))
                    .addGroup(headerPanelLayout.createSequentialGroup()
                        .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(showHeaderCheckBox)
                            .addComponent(headerSpaceLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(showHeaderCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headerSpaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerSpaceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(headerSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lineNumbersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("lineNumbersPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(showLineNumbersCheckBox, resourceBundle.getString("showLineNumbersCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lineNumberLengthLabel, resourceBundle.getString("lineNumberLengthLabel.text")); // NOI18N

        lineNumbersLengthComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AUTO", "SPECIFIED" }));
        lineNumbersLengthComboBox.setSelectedIndex(1);

        lineNumbersLengthSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        lineNumbersLengthSpinner.setValue(8);

        org.openide.awt.Mnemonics.setLocalizedText(lineNumberSpaceLabel, resourceBundle.getString("lineNumberSpaceLabel.text")); // NOI18N

        lineNumbersSpaceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NONE", "SPECIFIED", "QUARTER_UNIT", "HALF_UNIT", "ONE_UNIT", "ONE_AND_HALF_UNIT", "DOUBLE_UNIT" }));
        lineNumbersSpaceComboBox.setSelectedIndex(4);

        lineNumbersSpaceSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        javax.swing.GroupLayout lineNumbersPanelLayout = new javax.swing.GroupLayout(lineNumbersPanel);
        lineNumbersPanel.setLayout(lineNumbersPanelLayout);
        lineNumbersPanelLayout.setHorizontalGroup(
            lineNumbersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lineNumbersPanelLayout.createSequentialGroup()
                .addComponent(showLineNumbersCheckBox)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(lineNumbersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lineNumbersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lineNumbersPanelLayout.createSequentialGroup()
                        .addComponent(lineNumbersLengthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lineNumbersLengthSpinner))
                    .addGroup(lineNumbersPanelLayout.createSequentialGroup()
                        .addGroup(lineNumbersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineNumberLengthLabel)
                            .addComponent(lineNumberSpaceLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(lineNumbersPanelLayout.createSequentialGroup()
                        .addComponent(lineNumbersSpaceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lineNumbersSpaceSpinner)))
                .addContainerGap())
        );
        lineNumbersPanelLayout.setVerticalGroup(
            lineNumbersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lineNumbersPanelLayout.createSequentialGroup()
                .addComponent(showLineNumbersCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineNumberLengthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lineNumbersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineNumbersLengthComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lineNumbersLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineNumberSpaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lineNumbersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lineNumbersSpaceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lineNumbersSpaceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(byteGroupSizeLabel, resourceBundle.getString("byteGroupSizeLabel.text")); // NOI18N

        byteGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        spaceGroupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        org.openide.awt.Mnemonics.setLocalizedText(spaceGroupSizeLabel, resourceBundle.getString("spaceGroupSizeLabel.text")); // NOI18N

        javax.swing.GroupLayout layoutPanelLayout = new javax.swing.GroupLayout(layoutPanel);
        layoutPanel.setLayout(layoutPanelLayout);
        layoutPanelLayout.setHorizontalGroup(
            layoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layoutPanelLayout.createSequentialGroup()
                        .addGroup(layoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineLengthLabel)
                            .addComponent(wrapLineModeCheckBox))
                        .addGap(67, 67, 67))
                    .addComponent(spaceGroupSizeSpinner)
                    .addComponent(byteGroupSizeSpinner)
                    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lineLengthSpinner)
                    .addComponent(lineNumbersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layoutPanelLayout.createSequentialGroup()
                        .addGroup(layoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(byteGroupSizeLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spaceGroupSizeLabel, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layoutPanelLayout.setVerticalGroup(
            layoutPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layoutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wrapLineModeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineLengthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineNumbersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(byteGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(byteGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spaceGroupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spaceGroupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(codeTypeScrollModeLabel, resourceBundle.getString("codeTypeScrollModeLabel.text")); // NOI18N

        codeTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BINARY", "OCTAL", "DECIMAL", "HEXADECIMAL" }));
        codeTypeComboBox.setSelectedIndex(3);

        viewModeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DUAL", "HEXADECIMAL", "PREVIEW" }));

        org.openide.awt.Mnemonics.setLocalizedText(showNonprintableCharactersCheckBox, resourceBundle.getString("showNonprintableCharactersCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(viewModeScrollModeLabel, resourceBundle.getString("viewModeScrollModeLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(codeColorizationCheckBox, resourceBundle.getString("codeColorizationCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(memoryModeLabel, resourceBundle.getString("memoryModeLabel.text")); // NOI18N

        memoryModeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DELTA", "MEMORY" }));

        org.openide.awt.Mnemonics.setLocalizedText(showValuesPanelCheckBox, resourceBundle.getString("showValuesPanelCheckBox.text")); // NOI18N

        javax.swing.GroupLayout modePanelLayout = new javax.swing.GroupLayout(modePanel);
        modePanel.setLayout(modePanelLayout);
        modePanelLayout.setHorizontalGroup(
            modePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(memoryModeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(viewModeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(showNonprintableCharactersCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(codeTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(codeColorizationCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(modePanelLayout.createSequentialGroup()
                        .addGroup(modePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(viewModeScrollModeLabel)
                            .addComponent(codeTypeScrollModeLabel)
                            .addComponent(memoryModeLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(showValuesPanelCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        modePanelLayout.setVerticalGroup(
            modePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viewModeScrollModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeTypeScrollModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showNonprintableCharactersCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeColorizationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showValuesPanelCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(memoryModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(memoryModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(hexCharactersModeLabel, resourceBundle.getString("hexCharactersModeLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(backgroundModeLabel, resourceBundle.getString("backgroundModeLabel.text")); // NOI18N

        hexCharactersModeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LOWER", "UPPER" }));

        backgroundModeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NONE", "PLAIN", "STRIPED", "GRIDDED" }));
        backgroundModeComboBox.setSelectedIndex(2);

        org.openide.awt.Mnemonics.setLocalizedText(positionCodeTypeLabel, resourceBundle.getString("positionCodeTypeLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lineNumbersBackgroundCheckBox, resourceBundle.getString("lineNumbersBackgroundCheckBox.text")); // NOI18N

        positionCodeTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OCTAL", "DECIMAL", "HEXADECIMAL" }));
        positionCodeTypeComboBox.setSelectedIndex(2);

        linesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("linesPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(decoratorLineNumLineCheckBox, resourceBundle.getString("decoratorLineNumLineCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(decoratorPreviewLineCheckBox, resourceBundle.getString("decoratorPreviewLineCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(decoratorBoxCheckBox, resourceBundle.getString("decoratorBoxCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(decoratorHeaderLineCheckBox, resourceBundle.getString("decoratorHeaderLineCheckBox.text")); // NOI18N

        javax.swing.GroupLayout linesPanelLayout = new javax.swing.GroupLayout(linesPanel);
        linesPanel.setLayout(linesPanelLayout);
        linesPanelLayout.setHorizontalGroup(
            linesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(linesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(decoratorLineNumLineCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(decoratorPreviewLineCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(decoratorBoxCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(decoratorHeaderLineCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        linesPanelLayout.setVerticalGroup(
            linesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(linesPanelLayout.createSequentialGroup()
                .addComponent(decoratorHeaderLineCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decoratorLineNumLineCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decoratorPreviewLineCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decoratorBoxCheckBox))
        );

        javax.swing.GroupLayout decorationPanelLayout = new javax.swing.GroupLayout(decorationPanel);
        decorationPanel.setLayout(decorationPanelLayout);
        decorationPanelLayout.setHorizontalGroup(
            decorationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decorationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(decorationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(decorationPanelLayout.createSequentialGroup()
                        .addComponent(backgroundModeLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(decorationPanelLayout.createSequentialGroup()
                        .addGroup(decorationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hexCharactersModeLabel)
                            .addComponent(positionCodeTypeLabel))
                        .addGap(138, 138, 138))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, decorationPanelLayout.createSequentialGroup()
                        .addGroup(decorationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(positionCodeTypeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(hexCharactersModeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(backgroundModeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lineNumbersBackgroundCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(linesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        decorationPanelLayout.setVerticalGroup(
            decorationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, decorationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backgroundModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backgroundModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lineNumbersBackgroundCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(linesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hexCharactersModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hexCharactersModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(positionCodeTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(positionCodeTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(fontLabel, resourceBundle.getString("fontLabel.text")); // NOI18N

        fontTextField.setEditable(false);
        fontTextField.setText(resourceBundle.getString("fontTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(selectFontButton, resourceBundle.getString("selectFontButton.text")); // NOI18N
        selectFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFontButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(useDefaultFontCheckBox, resourceBundle.getString("useDefaultFontCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(colorsLabel, resourceBundle.getString("colorsLabel.text")); // NOI18N

        javax.swing.GroupLayout fontsAndColorPanelLayout = new javax.swing.GroupLayout(fontsAndColorPanel);
        fontsAndColorPanel.setLayout(fontsAndColorPanelLayout);
        fontsAndColorPanelLayout.setHorizontalGroup(
            fontsAndColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fontsAndColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fontsAndColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fontsAndColorPanelLayout.createSequentialGroup()
                        .addComponent(fontTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectFontButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(fontsAndColorPanelLayout.createSequentialGroup()
                        .addGroup(fontsAndColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(useDefaultFontCheckBox)
                            .addComponent(fontLabel)
                            .addComponent(colorsLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        fontsAndColorPanelLayout.setVerticalGroup(
            fontsAndColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fontsAndColorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useDefaultFontCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fontsAndColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectFontButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(colorsLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(categoriesLabel, "Categories:");

        categoriesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categoriesScrollPane.setViewportView(categoriesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(categoriesLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(categoriesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPane, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(categoriesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(categoriesScrollPane)
                    .addComponent(mainPane)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFontButtonActionPerformed
        final TextFontPanel textFontPanel = new TextFontPanel();

        DefaultControlPanel textFontControlPanel = new DefaultControlPanel();
        textFontPanel.setStoredFont(binEdFont);
        textFontPanel.setVisible(true);
        JPanel dialogPanel = WindowUtils.createDialogPanel(textFontPanel, textFontControlPanel);
        DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, "Select Font", true, new Object[0], null, 0, null, null);

        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        textFontControlPanel.setHandler(new DefaultControlHandler() {
            @Override
            public void controlActionPerformed(DefaultControlHandler.ControlActionType actionType) {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                    binEdFont = textFontPanel.getStoredFont();
                    updateFontTextField();
                    useDefaultFontCheckBox.setSelected(false);
                }

                WindowUtils.closeWindow(dialog);
            }
        });
        WindowUtils.assignGlobalKeyListener(dialog, textFontControlPanel.createOkCancelListener());
        dialog.setVisible(true);
    }//GEN-LAST:event_selectFontButtonActionPerformed

    public void load() {
        // Layout
        wrapLineModeCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_LINE_WRAPPING, false));
        lineLengthSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_BYTES_PER_LINE, 16));
        showHeaderCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_HEADER, true));
// TODO        String headerSpaceTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE_TYPE, CodeAreaSpace.SpaceType.HALF_UNIT.name());
// TODO        CodeAreaSpace.SpaceType headerSpaceType = CodeAreaSpace.SpaceType.valueOf(headerSpaceTypeName);
// TODO        headerSpaceComboBox.setSelectedIndex(headerSpaceType.ordinal());
        headerSpaceSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE, 0));
        showLineNumbersCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_LINE_NUMBERS, true));
// TODO        String lineNumbersSpaceTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE_TYPE, CodeAreaSpace.SpaceType.ONE_UNIT.name());
// TODO        CodeAreaSpace.SpaceType lineNumbersSpaceType = CodeAreaSpace.SpaceType.valueOf(lineNumbersSpaceTypeName);
// TODO        lineNumbersSpaceComboBox.setSelectedIndex(lineNumbersSpaceType.ordinal());
        lineNumbersSpaceSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE, 8));
// TODO        String lineNumbersLengthTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH_TYPE, CodeAreaLineNumberLength.LineNumberType.SPECIFIED.name());
// TODO        CodeAreaLineNumberLength.LineNumberType lineNumbersLengthType = CodeAreaLineNumberLength.LineNumberType.valueOf(lineNumbersLengthTypeName);
// TODO        lineNumbersLengthComboBox.setSelectedIndex(lineNumbersLengthType.ordinal());
        lineNumbersLengthSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH, 8));
        byteGroupSizeSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_BYTE_GROUP_SIZE, 1));
        spaceGroupSizeSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_SPACE_GROUP_SIZE, 0));

        // Mode
        CodeAreaViewMode viewMode = CodeAreaViewMode.valueOf(preferences.get(BinaryEditorTopComponent.PREFERENCES_VIEW_MODE, CodeAreaViewMode.DUAL.name()));
        viewModeComboBox.setSelectedIndex(viewMode.ordinal());
        CodeType codeType = CodeType.valueOf(preferences.get(BinaryEditorTopComponent.PREFERENCES_CODE_TYPE, CodeType.HEXADECIMAL.name()));
        codeTypeComboBox.setSelectedIndex(codeType.ordinal());
        showNonprintableCharactersCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_UNPRINTABLES, true));
        codeColorizationCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_CODE_COLORIZATION, true));
        showValuesPanelCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_VALUES_PANEL, true));
        memoryModeComboBox.setSelectedIndex(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_MEMORY_DELTA_MODE, true) ? 0 : 1);

        // Decoration
        ExtendedBackgroundPaintMode backgroundMode = convertBackgroundPaintMode(preferences.get(BinaryEditorTopComponent.PREFERENCES_BACKGROUND_MODE, ExtendedBackgroundPaintMode.STRIPED.name()));
        backgroundModeComboBox.setSelectedIndex(backgroundMode.ordinal());
        lineNumbersBackgroundCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND, true));
        decoratorHeaderLineCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_HEADER_LINE, true));
        decoratorPreviewLineCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_PREVIEW_LINE, true));
        decoratorBoxCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_BOX, false));
        decoratorLineNumLineCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_LINENUM_LINE, true));
        CodeCharactersCase codeCharactersCase = CodeCharactersCase.valueOf(preferences.get(BinaryEditorTopComponent.PREFERENCES_HEX_CHARACTERS_CASE, CodeCharactersCase.UPPER.name()));
        hexCharactersModeComboBox.setSelectedIndex(codeCharactersCase.ordinal());
        PositionCodeType positionCodeType = PositionCodeType.valueOf(preferences.get(BinaryEditorTopComponent.PREFERENCES_POSITION_CODE_TYPE, PositionCodeType.HEXADECIMAL.name()));
        positionCodeTypeComboBox.setSelectedIndex(positionCodeType.ordinal());

        // Font
        Boolean useDefaultFont = Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_DEFAULT, Boolean.toString(true)));
        useDefaultFontCheckBox.setSelected(useDefaultFont);

        String value;
        Map<TextAttribute, Object> attribs = new HashMap<>();
        value = preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_FAMILY, null);
        if (value != null) {
            attribs.put(TextAttribute.FAMILY, value);
        }
        value = preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_SIZE, null);
        if (value != null) {
            attribs.put(TextAttribute.SIZE, new Integer(value).floatValue());
        }
        if (Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_UNDERLINE, null))) {
            attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        }
        if (Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_STRIKETHROUGH, null))) {
            attribs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_STRONG, null))) {
            attribs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        if (Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_ITALIC, null))) {
            attribs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_SUBSCRIPT, null))) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        }
        if (Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_SUPERSCRIPT, null))) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
        }
        Font derivedFont = binEdFont.deriveFont(attribs);
        if (derivedFont != null) {
            binEdFont = derivedFont;
        }
        updateFontTextField();
    }

    public void store() {
        // Layout
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_LINE_WRAPPING, wrapLineModeCheckBox.isSelected());
        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_BYTES_PER_LINE, (Integer) lineLengthSpinner.getValue());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_HEADER, showHeaderCheckBox.isSelected());
// TODO        preferences.put(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE_TYPE, CodeAreaSpace.SpaceType.values()[headerSpaceComboBox.getSelectedIndex()].name());
        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE, (Integer) headerSpaceSpinner.getValue());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_LINE_NUMBERS, showLineNumbersCheckBox.isSelected());
// TODO        preferences.put(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE_TYPE, CodeAreaSpace.SpaceType.values()[lineNumbersSpaceComboBox.getSelectedIndex()].name());
        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE, (Integer) lineNumbersSpaceSpinner.getValue());
// TODO        preferences.put(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH_TYPE, CodeAreaLineNumberLength.LineNumberType.values()[lineNumbersLengthComboBox.getSelectedIndex()].name());
        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH, (Integer) lineNumbersLengthSpinner.getValue());
        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_BYTE_GROUP_SIZE, (Integer) byteGroupSizeSpinner.getValue());
        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_SPACE_GROUP_SIZE, (Integer) spaceGroupSizeSpinner.getValue());

        // Mode
        preferences.put(BinaryEditorTopComponent.PREFERENCES_VIEW_MODE, CodeAreaViewMode.values()[viewModeComboBox.getSelectedIndex()].name());
        preferences.put(BinaryEditorTopComponent.PREFERENCES_CODE_TYPE, CodeType.values()[codeTypeComboBox.getSelectedIndex()].name());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_UNPRINTABLES, showNonprintableCharactersCheckBox.isSelected());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_CODE_COLORIZATION, codeColorizationCheckBox.isSelected());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_VALUES_PANEL, showValuesPanelCheckBox.isSelected());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_MEMORY_DELTA_MODE, isDeltaMemoryMode());

        // Decoration
        preferences.put(BinaryEditorTopComponent.PREFERENCES_BACKGROUND_MODE, BasicBackgroundPaintMode.values()[backgroundModeComboBox.getSelectedIndex()].name());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND, lineNumbersBackgroundCheckBox.isSelected());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_HEADER_LINE, decoratorHeaderLineCheckBox.isSelected());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_PREVIEW_LINE, decoratorPreviewLineCheckBox.isSelected());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_BOX, decoratorBoxCheckBox.isSelected());
        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_LINENUM_LINE, decoratorLineNumLineCheckBox.isSelected());
        preferences.put(BinaryEditorTopComponent.PREFERENCES_HEX_CHARACTERS_CASE, CodeCharactersCase.values()[hexCharactersModeComboBox.getSelectedIndex()].name());
        preferences.put(BinaryEditorTopComponent.PREFERENCES_POSITION_CODE_TYPE, PositionCodeType.values()[positionCodeTypeComboBox.getSelectedIndex()].name());

        // Font
        preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_DEFAULT, Boolean.toString(useDefaultFontCheckBox.isSelected()));

        Map<TextAttribute, ?> attribs = binEdFont.getAttributes();
        String value = (String) attribs.get(TextAttribute.FAMILY);
        if (value != null) {
            preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_FAMILY, value);
        } else {
            preferences.remove(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_FAMILY);
        }
        Float fontSize = (Float) attribs.get(TextAttribute.SIZE);
        if (fontSize != null) {
            preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_SIZE, Integer.toString((int) (float) fontSize));
        } else {
            preferences.remove(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_SIZE);
        }
        preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_UNDERLINE, Boolean.toString(TextAttribute.UNDERLINE_LOW_ONE_PIXEL.equals(attribs.get(TextAttribute.UNDERLINE))));
        preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_STRIKETHROUGH, Boolean.toString(TextAttribute.STRIKETHROUGH_ON.equals(attribs.get(TextAttribute.STRIKETHROUGH))));
        preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_STRONG, Boolean.toString(TextAttribute.WEIGHT_BOLD.equals(attribs.get(TextAttribute.WEIGHT))));
        preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_ITALIC, Boolean.toString(TextAttribute.POSTURE_OBLIQUE.equals(attribs.get(TextAttribute.POSTURE))));
        preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_SUBSCRIPT, Boolean.toString(TextAttribute.SUPERSCRIPT_SUB.equals(attribs.get(TextAttribute.SUPERSCRIPT))));
        preferences.put(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_SUPERSCRIPT, Boolean.toString(TextAttribute.SUPERSCRIPT_SUPER.equals(attribs.get(TextAttribute.SUPERSCRIPT))));
    }

    public void setFromCodeArea(ExtCodeArea codeArea) {
        // Layout
        wrapLineModeCheckBox.setSelected(codeArea.getRowWrapping() == RowWrappingCapable.RowWrappingMode.WRAPPING);
// TODO        lineLengthSpinner.setValue(codeArea.getLineLength());
//        showHeaderCheckBox.setSelected(codeArea.isShowHeader());
//        headerSpaceComboBox.setSelectedIndex(codeArea.getHeaderSpaceType().ordinal());
//        headerSpaceSpinner.setValue(codeArea.getHeaderSpaceSize());
//        showLineNumbersCheckBox.setSelected(codeArea.isShowLineNumbers());
//        lineNumbersSpaceComboBox.setSelectedIndex(codeArea.getLineNumberSpaceType().ordinal());
//        lineNumbersSpaceSpinner.setValue(codeArea.getLineNumberSpaceSize());
//        lineNumbersLengthComboBox.setSelectedIndex(codeArea.getLineNumberType().ordinal());
//        lineNumbersLengthSpinner.setValue(codeArea.getLineNumberSpecifiedLength());
//        byteGroupSizeSpinner.setValue(codeArea.getByteGroupSize());
//        spaceGroupSizeSpinner.setValue(codeArea.getSpaceGroupSize());

        // Mode
        viewModeComboBox.setSelectedIndex(codeArea.getViewMode().ordinal());
        codeTypeComboBox.setSelectedIndex(codeArea.getCodeType().ordinal());
        showNonprintableCharactersCheckBox.setSelected(codeArea.isShowUnprintables());
        codeColorizationCheckBox.setSelected(((ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).isNonAsciiHighlightingEnabled());
        memoryModeComboBox.setSelectedIndex(codeArea.getContentData() instanceof DeltaDocument ? 0 : 1);

        // Decoration
        ExtendedCodeAreaThemeProfile themeProfile = codeArea.getThemeProfile();
        backgroundModeComboBox.setSelectedIndex(themeProfile.getBackgroundPaintMode().ordinal());
// TODO        lineNumbersBackgroundCheckBox.setSelected(codeArea.isLineNumberBackground());
// TODO        setDecorationMode(codeArea.getDecorationMode());
        hexCharactersModeComboBox.setSelectedIndex(codeArea.getCodeCharactersCase().ordinal());
        positionCodeTypeComboBox.setSelectedIndex(codeArea.getPositionCodeType().ordinal());

        // Font
        binEdFont = codeArea.getCodeFont();
        updateFontTextField();
        useDefaultFontCheckBox.setSelected(binEdFont.equals(binEdDefaultFont));
    }

    public void applyToCodeArea(ExtCodeArea codeArea) {
        // Layout
        ExtendedCodeAreaLayoutProfile layoutProfile = codeArea.getLayoutProfile();
        codeArea.setRowWrapping(wrapLineModeCheckBox.isSelected() ? RowWrappingCapable.RowWrappingMode.WRAPPING : RowWrappingCapable.RowWrappingMode.NO_WRAPPING);
// TODO        codeArea.setLineLength((Integer) lineLengthSpinner.getValue());
        layoutProfile.setShowHeader(showHeaderCheckBox.isSelected());
//        codeArea.setHeaderSpaceType(CodeAreaSpace.SpaceType.values()[headerSpaceComboBox.getSelectedIndex()]);
//        codeArea.setHeaderSpaceSize((Integer) headerSpaceSpinner.getValue());
//        codeArea.setShowLineNumbers(showLineNumbersCheckBox.isSelected());
//        codeArea.setLineNumberSpaceType(CodeAreaSpace.SpaceType.values()[lineNumbersSpaceComboBox.getSelectedIndex()]);
//        codeArea.setLineNumberSpaceSize((Integer) lineNumbersSpaceSpinner.getValue());
//        codeArea.setLineNumberType(CodeAreaLineNumberLength.LineNumberType.values()[lineNumbersLengthComboBox.getSelectedIndex()]);
//        codeArea.setLineNumberSpecifiedLength((Integer) lineNumbersLengthSpinner.getValue());
//        codeArea.setByteGroupSize((Integer) byteGroupSizeSpinner.getValue());
//        codeArea.setSpaceGroupSize((Integer) spaceGroupSizeSpinner.getValue());

        // Mode
        codeArea.setViewMode(CodeAreaViewMode.values()[viewModeComboBox.getSelectedIndex()]);
        codeArea.setCodeType(CodeType.values()[codeTypeComboBox.getSelectedIndex()]);
        codeArea.setShowUnprintables(showNonprintableCharactersCheckBox.isSelected());
        ((ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).setNonAsciiHighlightingEnabled(codeColorizationCheckBox.isSelected());
        // Memory mode handled from outside by isDeltaMemoryMode() method, worth fixing?

        // Decoration
        ExtendedCodeAreaThemeProfile themeProfile = codeArea.getThemeProfile();
        themeProfile.setBackgroundPaintMode(ExtendedBackgroundPaintMode.values()[backgroundModeComboBox.getSelectedIndex()]);
// TODO        codeArea.setLineNumberBackground(lineNumbersBackgroundCheckBox.isSelected());
// TODO        codeArea.setDecorationMode(getDecorationMode());
        codeArea.setCodeCharactersCase(CodeCharactersCase.values()[hexCharactersModeComboBox.getSelectedIndex()]);
        codeArea.setPositionCodeType(PositionCodeType.values()[positionCodeTypeComboBox.getSelectedIndex()]);

        // Font
        if (useDefaultFontCheckBox.isSelected()) {
            codeArea.setCodeFont(binEdDefaultFont);
        } else {
            codeArea.setCodeFont(binEdFont);
        }
    }

    public boolean isShowValuesPanel() {
        return showValuesPanelCheckBox.isSelected();
    }

    public void setShowValuesPanel(boolean flag) {
        showValuesPanelCheckBox.setSelected(flag);
    }

// TODO
//    private int getDecorationMode() {
//        return (decoratorHeaderLineCheckBox.isSelected() ? CodeArea.DECORATION_HEADER_LINE : 0)
//                + (decoratorPreviewLineCheckBox.isSelected() ? CodeArea.DECORATION_PREVIEW_LINE : 0)
//                + (decoratorBoxCheckBox.isSelected() ? CodeArea.DECORATION_BOX : 0)
//                + (decoratorLineNumLineCheckBox.isSelected() ? CodeArea.DECORATION_LINENUM_LINE : 0);
//    }
//
//    private void setDecorationMode(int decorationMode) {
//        decoratorHeaderLineCheckBox.setSelected((decorationMode & CodeArea.DECORATION_HEADER_LINE) > 0);
//        decoratorLineNumLineCheckBox.setSelected((decorationMode & CodeArea.DECORATION_LINENUM_LINE) > 0);
//        decoratorPreviewLineCheckBox.setSelected((decorationMode & CodeArea.DECORATION_PREVIEW_LINE) > 0);
//        decoratorBoxCheckBox.setSelected((decorationMode & CodeArea.DECORATION_BOX) > 0);
//    }
    public boolean isDeltaMemoryMode() {
        return memoryModeComboBox.getSelectedIndex() == 0;
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    private void updateFontTextField() {
        int fontStyle = binEdFont.getStyle();
        String fontStyleName;
        if ((fontStyle & (Font.BOLD + Font.ITALIC)) == Font.BOLD + Font.ITALIC) {
            fontStyleName = "Bold Italic";
        } else if ((fontStyle & Font.BOLD) > 0) {
            fontStyleName = "Bold";
        } else if ((fontStyle & Font.ITALIC) > 0) {
            fontStyleName = "Italic";
        } else {
            fontStyleName = "Plain";
        }
        fontTextField.setText(binEdFont.getFamily() + " " + String.valueOf(binEdFont.getSize()) + " " + fontStyleName);
    }

    private ExtendedBackgroundPaintMode convertBackgroundPaintMode(String value) {
        if ("STRIPPED".equals(value)) {
            return ExtendedBackgroundPaintMode.STRIPED;
        }
        return ExtendedBackgroundPaintMode.valueOf(value);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> backgroundModeComboBox;
    private javax.swing.JLabel backgroundModeLabel;
    private javax.swing.JLabel byteGroupSizeLabel;
    private javax.swing.JSpinner byteGroupSizeSpinner;
    private javax.swing.JLabel categoriesLabel;
    private javax.swing.JList<CategoryItem> categoriesList;
    private javax.swing.JScrollPane categoriesScrollPane;
    private javax.swing.JCheckBox codeColorizationCheckBox;
    private javax.swing.JComboBox<String> codeTypeComboBox;
    private javax.swing.JLabel codeTypeScrollModeLabel;
    private javax.swing.JLabel colorsLabel;
    private javax.swing.JPanel decorationPanel;
    private javax.swing.JCheckBox decoratorBoxCheckBox;
    private javax.swing.JCheckBox decoratorHeaderLineCheckBox;
    private javax.swing.JCheckBox decoratorLineNumLineCheckBox;
    private javax.swing.JCheckBox decoratorPreviewLineCheckBox;
    private javax.swing.JLabel fontLabel;
    private javax.swing.JTextField fontTextField;
    private javax.swing.JPanel fontsAndColorPanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JComboBox<String> headerSpaceComboBox;
    private javax.swing.JLabel headerSpaceLabel;
    private javax.swing.JSpinner headerSpaceSpinner;
    private javax.swing.JComboBox<String> hexCharactersModeComboBox;
    private javax.swing.JLabel hexCharactersModeLabel;
    private javax.swing.JPanel layoutPanel;
    private javax.swing.JLabel lineLengthLabel;
    private javax.swing.JSpinner lineLengthSpinner;
    private javax.swing.JLabel lineNumberLengthLabel;
    private javax.swing.JLabel lineNumberSpaceLabel;
    private javax.swing.JCheckBox lineNumbersBackgroundCheckBox;
    private javax.swing.JComboBox<String> lineNumbersLengthComboBox;
    private javax.swing.JSpinner lineNumbersLengthSpinner;
    private javax.swing.JPanel lineNumbersPanel;
    private javax.swing.JComboBox<String> lineNumbersSpaceComboBox;
    private javax.swing.JSpinner lineNumbersSpaceSpinner;
    private javax.swing.JPanel linesPanel;
    private javax.swing.JScrollPane mainPane;
    private javax.swing.JComboBox<String> memoryModeComboBox;
    private javax.swing.JLabel memoryModeLabel;
    private javax.swing.JPanel modePanel;
    private javax.swing.JComboBox<String> positionCodeTypeComboBox;
    private javax.swing.JLabel positionCodeTypeLabel;
    private javax.swing.JButton selectFontButton;
    private javax.swing.JCheckBox showHeaderCheckBox;
    private javax.swing.JCheckBox showLineNumbersCheckBox;
    private javax.swing.JCheckBox showNonprintableCharactersCheckBox;
    private javax.swing.JCheckBox showValuesPanelCheckBox;
    private javax.swing.JLabel spaceGroupSizeLabel;
    private javax.swing.JSpinner spaceGroupSizeSpinner;
    private javax.swing.JCheckBox useDefaultFontCheckBox;
    private javax.swing.JComboBox<String> viewModeComboBox;
    private javax.swing.JLabel viewModeScrollModeLabel;
    private javax.swing.JCheckBox wrapLineModeCheckBox;
    // End of variables declaration//GEN-END:variables

    private static class CategoryItem {

        String categoryName;
        JPanel categoryPanel;

        public CategoryItem(String categoryName, JPanel categoryPanel) {
            this.categoryName = categoryName;
            this.categoryPanel = categoryPanel;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public JPanel getCategoryPanel() {
            return categoryPanel;
        }
    }
}
