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
package org.exbin.bined.netbeans.panel;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import org.exbin.bined.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.delta.DeltaDocument;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.bined.netbeans.PreferencesWrapper;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.bined.options.CharsetOptions;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.bined.options.EditorOptions;
import org.exbin.framework.bined.options.panel.CodeAreaOptionsPanel;
import org.exbin.framework.bined.options.panel.ColorProfilesPanel;
import org.exbin.framework.bined.options.panel.EditorOptionsPanel;
import org.exbin.framework.bined.options.panel.LayoutProfilesPanel;
import org.exbin.framework.bined.options.panel.ProfileSelectionPanel;
import org.exbin.framework.bined.options.panel.ThemeProfilesPanel;
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
 * @version 0.2.0 2019/03/02
 * @author ExBin Project (http://exbin.org)
 */
public class BinEdOptionsPanel extends javax.swing.JPanel {

    private final BinaryEditorPreferences preferences;
    private final BinEdOptionsPanelController controller;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinEdOptionsPanel.class);

    private DefaultListModel<CategoryItem> categoryModel = new DefaultListModel<>();
    private JPanel currentCategoryPanel = null;
    
    private final EditorOptions editorOptions = new EditorOptions();
    private final CharsetOptions charsetOptions = new CharsetOptions();
    private final CodeAreaOptions codeAreaOptions = new CodeAreaOptions();
    
    private final EditorOptionsPanel editorParametersPanel = new EditorOptionsPanel();
    private final CodeAreaOptionsPanel codeAreaParametersPanel = new CodeAreaOptionsPanel();
    private final LayoutProfilesPanel layoutProfilesPanel = new LayoutProfilesPanel();
    private final ProfileSelectionPanel layoutSelectionPanel = new ProfileSelectionPanel(layoutProfilesPanel);
    private final ThemeProfilesPanel themeProfilesPanel = new ThemeProfilesPanel();
    private final ProfileSelectionPanel themeSelectionPanel = new ProfileSelectionPanel(themeProfilesPanel);
    private final ColorProfilesPanel colorProfilesPanel = new ColorProfilesPanel();
    private final ProfileSelectionPanel colorSelectionPanel = new ProfileSelectionPanel(colorProfilesPanel);

    public BinEdOptionsPanel() {
        this(new BinEdOptionsPanelController());
    }

    public BinEdOptionsPanel(BinEdOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        preferences = new BinaryEditorPreferences(new PreferencesWrapper(NbPreferences.forModule(BinaryEditorPreferences.class)));

        categoryModel.addElement(new CategoryItem("Editor", editorParametersPanel));
        categoryModel.addElement(new CategoryItem("Code Area", codeAreaParametersPanel));
        categoryModel.addElement(new CategoryItem("Layout Profiles", layoutSelectionPanel));
        categoryModel.addElement(new CategoryItem("Theme Profiles", themeSelectionPanel));
        categoryModel.addElement(new CategoryItem("Colors Profiles", colorSelectionPanel));
        categoriesList.setModel(categoryModel);

        categoriesList.addListSelectionListener((ListSelectionEvent e) -> {
            int selectedIndex = categoriesList.getSelectedIndex();
            if (selectedIndex >= 0) {
                CategoryItem categoryItem = categoryModel.get(selectedIndex);
                currentCategoryPanel = categoryItem.getCategoryPanel();
                mainPane.setViewportView(currentCategoryPanel);
                mainPane.invalidate();
                revalidate();
                mainPane.repaint();
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

        categoriesLabel = new javax.swing.JLabel();
        categoriesScrollPane = new javax.swing.JScrollPane();
        categoriesList = new javax.swing.JList<>();
        mainPane = new javax.swing.JScrollPane();

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

    public void load() {
        editorOptions.loadFromParameters(preferences.getEditorParameters());
        codeAreaOptions.loadFromParameters(preferences.getCodeAreaParameters());
//        charsetOptions.loadFromParameters(preferences.getCharsetParameters());
        layoutProfilesPanel.loadFromParameters(preferences.getLayoutParameters());
        colorProfilesPanel.loadFromParameters(preferences.getColorParameters());
        themeProfilesPanel.loadFromParameters(preferences.getThemeParameters());
        
        editorParametersPanel.loadFromOptions(editorOptions);
        codeAreaParametersPanel.loadFromOptions(codeAreaOptions);
//        charsetParametersPanel.loadFromOptions(charsetOptions);

        // Layout
// TODO        wrapLineModeCheckBox.setSelected(preferences.isRowWrapping());
//        lineLengthSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_BYTES_PER_LINE, 16));
//        showHeaderCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_HEADER, true));
// TODO        String headerSpaceTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE_TYPE, CodeAreaSpace.SpaceType.HALF_UNIT.name());
// TODO        CodeAreaSpace.SpaceType headerSpaceType = CodeAreaSpace.SpaceType.valueOf(headerSpaceTypeName);
// TODO        headerSpaceComboBox.setSelectedIndex(headerSpaceType.ordinal());
//        headerSpaceSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE, 0));
//        showLineNumbersCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_LINE_NUMBERS, true));
// TODO        String lineNumbersSpaceTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE_TYPE, CodeAreaSpace.SpaceType.ONE_UNIT.name());
// TODO        CodeAreaSpace.SpaceType lineNumbersSpaceType = CodeAreaSpace.SpaceType.valueOf(lineNumbersSpaceTypeName);
// TODO        lineNumbersSpaceComboBox.setSelectedIndex(lineNumbersSpaceType.ordinal());
//        lineNumbersSpaceSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE, 8));
// TODO        String lineNumbersLengthTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH_TYPE, CodeAreaLineNumberLength.LineNumberType.SPECIFIED.name());
// TODO        CodeAreaLineNumberLength.LineNumberType lineNumbersLengthType = CodeAreaLineNumberLength.LineNumberType.valueOf(lineNumbersLengthTypeName);
// TODO        lineNumbersLengthComboBox.setSelectedIndex(lineNumbersLengthType.ordinal());
//        lineNumbersLengthSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH, 8));
//        byteGroupSizeSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_BYTE_GROUP_SIZE, 1));
//        spaceGroupSizeSpinner.setValue(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_SPACE_GROUP_SIZE, 0));
        // Mode
// TODO        CodeAreaViewMode viewMode = preferences.getViewMode();
// TODO        viewModeComboBox.setSelectedIndex(viewMode.ordinal());
// TODO        CodeType codeType = preferences.getCodeType();
// TODO        codeTypeComboBox.setSelectedIndex(codeType.ordinal());
// TODO        showNonprintableCharactersCheckBox.setSelected(preferences.isShowNonprintables());
// TODO        codeColorizationCheckBox.setSelected(preferences.isCodeColorization());
// TODO        showValuesPanelCheckBox.setSelected(preferences.isShowValuesPanel());
// TODO        memoryModeComboBox.setSelectedIndex(preferences.isDeltaMemoryMode() ? 0 : 1);
        // Decoration
// TODO        ExtendedBackgroundPaintMode backgroundMode = preferences.getBackgroundPaintMode();
// TODO        backgroundModeComboBox.setSelectedIndex(backgroundMode.ordinal());
//        lineNumbersBackgroundCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND, true));
//        decoratorHeaderLineCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_HEADER_LINE, true));
//        decoratorPreviewLineCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_PREVIEW_LINE, true));
//        decoratorBoxCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_BOX, false));
//        decoratorLineNumLineCheckBox.setSelected(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_LINENUM_LINE, true));
// TODO        CodeCharactersCase codeCharactersCase = preferences.getCodeCharactersCase();
// TODO        hexCharactersModeComboBox.setSelectedIndex(codeCharactersCase.ordinal());
// TODO        PositionCodeType positionCodeType = preferences.getPositionCodeType();
// TODO        positionCodeTypeComboBox.setSelectedIndex(positionCodeType.ordinal());
        // Font
// TODO        Boolean useDefaultFont = preferences.isUseDefaultFont();
// TODO        useDefaultFontCheckBox.setSelected(useDefaultFont);
// TODO        Font codeFont = preferences.getCodeFont(binEdFont);
// TODO        if (codeFont != null) {
// TODO            binEdFont = codeFont;
// TODO        }
// TODO        updateFontTextField();
    }

    public void store() {
        editorOptions.saveToParameters(preferences.getEditorParameters());
        codeAreaOptions.saveToParameters(preferences.getCodeAreaParameters());
//        charsetParametersPanel.saveToOptions(charsetOptions);
        layoutProfilesPanel.saveToParameters(preferences.getLayoutParameters());
        colorProfilesPanel.saveToParameters(preferences.getColorParameters());
        themeProfilesPanel.saveToParameters(preferences.getThemeParameters());

        editorParametersPanel.saveToOptions(editorOptions);
        codeAreaParametersPanel.saveToOptions(codeAreaOptions);
//        charsetOptions.saveToParameters(preferences.getCharsetParameters());

        // Layout
// TODO        preferences.setRowWrapping(wrapLineModeCheckBox.isSelected());
//        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_BYTES_PER_LINE, (Integer) lineLengthSpinner.getValue());
//        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_HEADER, showHeaderCheckBox.isSelected());
// TODO        preferences.put(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE_TYPE, CodeAreaSpace.SpaceType.values()[headerSpaceComboBox.getSelectedIndex()].name());
//        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE, (Integer) headerSpaceSpinner.getValue());
//        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_LINE_NUMBERS, showLineNumbersCheckBox.isSelected());
// TODO        preferences.put(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE_TYPE, CodeAreaSpace.SpaceType.values()[lineNumbersSpaceComboBox.getSelectedIndex()].name());
//        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE, (Integer) lineNumbersSpaceSpinner.getValue());
// TODO        preferences.put(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH_TYPE, CodeAreaLineNumberLength.LineNumberType.values()[lineNumbersLengthComboBox.getSelectedIndex()].name());
//        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH, (Integer) lineNumbersLengthSpinner.getValue());
//        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_BYTE_GROUP_SIZE, (Integer) byteGroupSizeSpinner.getValue());
//        preferences.putInt(BinaryEditorTopComponent.PREFERENCES_SPACE_GROUP_SIZE, (Integer) spaceGroupSizeSpinner.getValue());

        // Mode
// TODO        preferences.setViewMode(CodeAreaViewMode.values()[viewModeComboBox.getSelectedIndex()]);
// TODO        preferences.setCodeType(CodeType.values()[codeTypeComboBox.getSelectedIndex()]);
// TODO        preferences.setShowUnprintables(showNonprintableCharactersCheckBox.isSelected());
// TODO        preferences.setCodeColorization(codeColorizationCheckBox.isSelected());
// TODO        preferences.setShowValuesPanel(showValuesPanelCheckBox.isSelected());
// TODO        preferences.setDeltaMemoryMode(isDeltaMemoryMode());
        // Decoration
//        preferences.put(BinaryEditorTopComponent.PREFERENCES_BACKGROUND_MODE, BasicBackgroundPaintMode.values()[backgroundModeComboBox.getSelectedIndex()].name());
//        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND, lineNumbersBackgroundCheckBox.isSelected());
//        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_HEADER_LINE, decoratorHeaderLineCheckBox.isSelected());
//        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_PREVIEW_LINE, decoratorPreviewLineCheckBox.isSelected());
//        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_BOX, decoratorBoxCheckBox.isSelected());
//        preferences.putBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_LINENUM_LINE, decoratorLineNumLineCheckBox.isSelected());
// TODO        preferences.setCodeCharactersCase(CodeCharactersCase.values()[hexCharactersModeComboBox.getSelectedIndex()]);
// TODO        preferences.setPositionCodeType(PositionCodeType.values()[positionCodeTypeComboBox.getSelectedIndex()]);
        // Font
// TODO        preferences.setUseDefaultFont(useDefaultFontCheckBox.isSelected());
// TODO        preferences.setCodeFont(binEdFont);
    }

    public void setFromCodeArea(ExtCodeArea codeArea) {
        // Layout
// TODO        wrapLineModeCheckBox.setSelected(codeArea.getRowWrapping() == RowWrappingCapable.RowWrappingMode.WRAPPING);
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
// TODO        viewModeComboBox.setSelectedIndex(codeArea.getViewMode().ordinal());
// TODO        codeTypeComboBox.setSelectedIndex(codeArea.getCodeType().ordinal());
// TODO        showNonprintableCharactersCheckBox.setSelected(codeArea.isShowUnprintables());
// TODO        codeColorizationCheckBox.setSelected(((ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).isNonAsciiHighlightingEnabled());
// TODO        memoryModeComboBox.setSelectedIndex(codeArea.getContentData() instanceof DeltaDocument ? 0 : 1);
        // Decoration
        ExtendedCodeAreaThemeProfile themeProfile = codeArea.getThemeProfile();
// TODO        backgroundModeComboBox.setSelectedIndex(themeProfile.getBackgroundPaintMode().ordinal());
// TODO        lineNumbersBackgroundCheckBox.setSelected(codeArea.isLineNumberBackground());
// TODO        setDecorationMode(codeArea.getDecorationMode());
// TODO        hexCharactersModeComboBox.setSelectedIndex(codeArea.getCodeCharactersCase().ordinal());
// TODO        positionCodeTypeComboBox.setSelectedIndex(codeArea.getPositionCodeType().ordinal());

        // Font
//        binEdFont = codeArea.getCodeFont();
//        updateFontTextField();
// TODO        useDefaultFontCheckBox.setSelected(binEdFont.equals(binEdDefaultFont));
    }

    public void applyToCodeArea(ExtCodeArea codeArea) {
        // Layout
        ExtendedCodeAreaLayoutProfile layoutProfile = codeArea.getLayoutProfile();
// TODO        codeArea.setRowWrapping(wrapLineModeCheckBox.isSelected() ? RowWrappingCapable.RowWrappingMode.WRAPPING : RowWrappingCapable.RowWrappingMode.NO_WRAPPING);
// TODO        codeArea.setLineLength((Integer) lineLengthSpinner.getValue());
// TODO        layoutProfile.setShowHeader(showHeaderCheckBox.isSelected());
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
// TODO        codeArea.setViewMode(CodeAreaViewMode.values()[viewModeComboBox.getSelectedIndex()]);
// TODO        codeArea.setCodeType(CodeType.values()[codeTypeComboBox.getSelectedIndex()]);
// TODO        codeArea.setShowUnprintables(showNonprintableCharactersCheckBox.isSelected());
// TODO        ((ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).setNonAsciiHighlightingEnabled(codeColorizationCheckBox.isSelected());
        // Memory mode handled from outside by isDeltaMemoryMode() method, worth fixing?
        // Decoration
        ExtendedCodeAreaThemeProfile themeProfile = codeArea.getThemeProfile();
// TODO        themeProfile.setBackgroundPaintMode(ExtendedBackgroundPaintMode.values()[backgroundModeComboBox.getSelectedIndex()]);
// TODO        codeArea.setLineNumberBackground(lineNumbersBackgroundCheckBox.isSelected());
// TODO        codeArea.setDecorationMode(getDecorationMode());
// TODO        codeArea.setCodeCharactersCase(CodeCharactersCase.values()[hexCharactersModeComboBox.getSelectedIndex()]);
// TODO        codeArea.setPositionCodeType(PositionCodeType.values()[positionCodeTypeComboBox.getSelectedIndex()]);

        // Font
//        if (useDefaultFontCheckBox.isSelected()) {
//            codeArea.setCodeFont(binEdDefaultFont);
//        } else {
//            codeArea.setCodeFont(binEdFont);
//        }
    }

//    public boolean isShowValuesPanel() {
//        return showValuesPanelCheckBox.isSelected();
//    }
//
//    public void setShowValuesPanel(boolean flag) {
//        showValuesPanelCheckBox.setSelected(flag);
//    }
//
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
//    public boolean isDeltaMemoryMode() {
//        return memoryModeComboBox.getSelectedIndex() == 0;
//    }
    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categoriesLabel;
    private javax.swing.JList<CategoryItem> categoriesList;
    private javax.swing.JScrollPane categoriesScrollPane;
    private javax.swing.JScrollPane mainPane;
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
