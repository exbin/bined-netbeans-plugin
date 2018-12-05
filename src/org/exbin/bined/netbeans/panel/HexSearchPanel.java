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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.exbin.bined.ScrollBarVisibility;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.ExtendedBackgroundPaintMode;
import org.exbin.framework.bined.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.panel.FindHexPanel;
import org.exbin.framework.bined.panel.HexMultilinePanel;
import org.exbin.framework.bined.panel.HexSearchComboBoxPanel;
import org.exbin.framework.bined.panel.ReplaceParameters;
import org.exbin.framework.bined.panel.SearchCondition;
import org.exbin.framework.bined.panel.SearchHistoryModel;
import org.exbin.framework.bined.panel.SearchParameters;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.DefaultControlHandler;
import org.exbin.framework.gui.utils.panel.DefaultControlPanel;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.ByteArrayEditableData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 * Hexadecimal editor search panel.
 *
 * @version 0.2.0 2018/12/05
 * @author ExBin Project (http://exbin.org)
 */
public class HexSearchPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(HexSearchPanel.class);

    private Thread searchStartThread;
    private Thread searchThread;
    private final SearchParameters searchParameters = new SearchParameters();
    private final ReplaceParameters replaceParameters = new ReplaceParameters();
    private final HexSearchPanelApi hexSearchPanelApi;
    private int matchesCount;
    private int matchPosition;
    private final ExtCodeArea hexadecimalRenderer = new ExtCodeArea();

    private boolean replaceMode = true;
    private ComboBoxEditor findComboBoxEditor;
    private HexSearchComboBoxPanel findComboBoxEditorComponent;
    private ComboBoxEditor replaceComboBoxEditor;
    private HexSearchComboBoxPanel replaceComboBoxEditorComponent;

    private final List<SearchCondition> searchHistory = new ArrayList<>();
    private final List<SearchCondition> replaceHistory = new ArrayList<>();

    private ClosePanelListener closePanelListener = null;
    private CodeAreaPopupMenuHandler hexCodePopupMenuHandler;

    public HexSearchPanel(HexSearchPanelApi hexSearchPanelApi) {
        initComponents();
        this.hexSearchPanelApi = hexSearchPanelApi;
        init();
    }

    private void init() {
        hexadecimalRenderer.setShowHeader(false);
        hexadecimalRenderer.setShowRowPosition(false);
        hexadecimalRenderer.setRowWrapping(RowWrappingCapable.RowWrappingMode.WRAPPING);
        hexadecimalRenderer.setWrappingBytesGroupSize(0);
        hexadecimalRenderer.setBackgroundPaintMode(ExtendedBackgroundPaintMode.PLAIN);
        hexadecimalRenderer.setVerticalScrollBarVisibility(ScrollBarVisibility.NEVER);
        hexadecimalRenderer.setHorizontalScrollBarVisibility(ScrollBarVisibility.NEVER);
        hexadecimalRenderer.setContentData(new ByteArrayEditableData(new byte[]{1, 2, 3}));

        final KeyAdapter editorKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    SearchCondition condition = searchParameters.getCondition();
                    if (!condition.isEmpty()) {
                        clearSearch();
                    } else {
                        cancelSearch();
                        closePanel();
                    }
                }
            }
        };

        findComboBoxEditorComponent = new HexSearchComboBoxPanel();
        findComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
            private final JPanel panel = new JPanel();
            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return panel;
                }

                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
                } else {
                    hexadecimalRenderer.setContentData(value.getBinaryData());
                    hexadecimalRenderer.setPreferredSize(new Dimension(200, 20));
                    Color backgroundColor;
                    if (isSelected) {
                        backgroundColor = list.getSelectionBackground();
                    } else {
                        backgroundColor = list.getBackground();
                    }
// TODO                    ColorsGroup mainColors = new ColorsGroup(hexadecimalRenderer.getMainColors());
//                    mainColors.setBothBackgroundColors(backgroundColor);
//                    hexadecimalRenderer.setMainColors(mainColors);
                    return hexadecimalRenderer;
                }
            }
        });
        findComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return findComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                SearchCondition condition;
                if (item == null || item instanceof String) {
                    condition = new SearchCondition();
                    condition.setSearchMode(SearchCondition.SearchMode.TEXT);
                    if (item != null) {
                        condition.setSearchText((String) item);
                    }
                } else {
                    condition = (SearchCondition) item;
                }
                searchParameters.setCondition(new SearchCondition(condition));
                SearchCondition currentItem = findComboBoxEditorComponent.getItem();
                if (item != currentItem) {
                    findComboBoxEditorComponent.setItem(condition);
                    updateFindStatus();
                }
            }

            @Override
            public Object getItem() {
                return findComboBoxEditorComponent.getItem();
            }

            @Override
            public void selectAll() {
                findComboBoxEditorComponent.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }
        };
        findComboBox.setEditor(findComboBoxEditor);

        findComboBoxEditorComponent.setValueChangedListener(new HexSearchComboBoxPanel.ValueChangedListener() {
            @Override
            public void valueChanged() {
                comboBoxValueChanged();
            }
        });
        findComboBoxEditorComponent.addValueKeyListener(editorKeyListener);
        findComboBox.setModel(new SearchHistoryModel(searchHistory));

        replaceComboBoxEditorComponent = new HexSearchComboBoxPanel();
        replaceComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
            private final JPanel panel = new JPanel();
            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return panel;
                }

                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
                } else {
                    hexadecimalRenderer.setContentData(value.getBinaryData());
                    hexadecimalRenderer.setPreferredSize(new Dimension(200, 20));
                    Color backgroundColor;
                    if (isSelected) {
                        backgroundColor = list.getSelectionBackground();
                    } else {
                        backgroundColor = list.getBackground();
                    }
// TODO                    ColorsGroup mainColors = new ColorsGroup(hexadecimalRenderer.getMainColors());
//                    mainColors.setBothBackgroundColors(backgroundColor);
//                    hexadecimalRenderer.setMainColors(mainColors);
                    return hexadecimalRenderer;
                }
            }
        });
        replaceComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return replaceComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                SearchCondition condition;
                if (item == null || item instanceof String) {
                    condition = new SearchCondition();
                    condition.setSearchMode(SearchCondition.SearchMode.TEXT);
                    if (item != null) {
                        condition.setSearchText((String) item);
                    }
                } else {
                    condition = (SearchCondition) item;
                }
                replaceParameters.setCondition(new SearchCondition(condition));
                SearchCondition currentItem = replaceComboBoxEditorComponent.getItem();
                if (item != currentItem) {
                    replaceComboBoxEditorComponent.setItem(condition);
                    updateReplaceStatus();
                }
            }

            @Override
            public Object getItem() {
                return replaceComboBoxEditorComponent.getItem();
            }

            @Override
            public void selectAll() {
                replaceComboBoxEditorComponent.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }
        };
        replaceComboBox.setEditor(replaceComboBoxEditor);

        replaceComboBoxEditorComponent.addValueKeyListener(editorKeyListener);
        replaceComboBox.setModel(new SearchHistoryModel(replaceHistory));
    }

    public void switchReplaceMode(boolean replaceMode) {
        if (this.replaceMode != replaceMode) {
            this.replaceMode = replaceMode;
            if (replaceMode) {
                add(replacePanel, BorderLayout.SOUTH);
            } else {
                remove(replacePanel);
            }
            revalidate();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topSeparator = new javax.swing.JSeparator();
        findPanel = new javax.swing.JPanel();
        findLabel = new javax.swing.JLabel();
        findTypeToolBar = new javax.swing.JToolBar();
        findTypeButton = new javax.swing.JButton();
        findComboBox = new javax.swing.JComboBox<>();
        findToolBar = new javax.swing.JToolBar();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        matchCaseToggleButton = new javax.swing.JToggleButton();
        multipleMatchesToggleButton = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();
        optionsButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        closeToolBar = new javax.swing.JToolBar();
        closeButton = new javax.swing.JButton();
        replacePanel = new javax.swing.JPanel();
        replaceLabel = new javax.swing.JLabel();
        replaceTypeToolBar = new javax.swing.JToolBar();
        replaceTypeButton = new javax.swing.JButton();
        replaceComboBox = new javax.swing.JComboBox<>();
        replaceToolBar = new javax.swing.JToolBar();
        replaceButton = new javax.swing.JButton();
        replaceAllButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        topSeparator.setName("topSeparator"); // NOI18N
        add(topSeparator, java.awt.BorderLayout.NORTH);

        findPanel.setName("findPanel"); // NOI18N

        findLabel.setText(resourceBundle.getString("findLabel.text")); // NOI18N
        findLabel.setName("findLabel"); // NOI18N

        findTypeToolBar.setBorder(null);
        findTypeToolBar.setFloatable(false);
        findTypeToolBar.setRollover(true);
        findTypeToolBar.setFocusable(false);
        findTypeToolBar.setName("findTypeToolBar"); // NOI18N

        findTypeButton.setText("T");
        findTypeButton.setToolTipText(resourceBundle.getString("findTypeButton.toolTipText")); // NOI18N
        findTypeButton.setFocusable(false);
        findTypeButton.setMaximumSize(new java.awt.Dimension(27, 27));
        findTypeButton.setMinimumSize(new java.awt.Dimension(27, 27));
        findTypeButton.setName("findTypeButton"); // NOI18N
        findTypeButton.setPreferredSize(new java.awt.Dimension(27, 27));
        findTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findTypeButtonActionPerformed(evt);
            }
        });
        findTypeToolBar.add(findTypeButton);

        findComboBox.setEditable(true);
        findComboBox.setSelectedItem("");
        findComboBox.setName("findComboBox"); // NOI18N

        findToolBar.setBorder(null);
        findToolBar.setFloatable(false);
        findToolBar.setRollover(true);
        findToolBar.setFocusable(false);
        findToolBar.setName("findToolBar"); // NOI18N

        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/open_icon_library/icons/png/16x16/actions/arrow-left.png"))); // NOI18N
        prevButton.setEnabled(false);
        prevButton.setFocusable(false);
        prevButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevButton.setName("prevButton"); // NOI18N
        prevButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });
        findToolBar.add(prevButton);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/open_icon_library/icons/png/16x16/actions/arrow-right.png"))); // NOI18N
        nextButton.setEnabled(false);
        nextButton.setFocusable(false);
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.setName("nextButton"); // NOI18N
        nextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        findToolBar.add(nextButton);

        matchCaseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/case_sensitive.gif"))); // NOI18N
        matchCaseToggleButton.setSelected(true);
        matchCaseToggleButton.setToolTipText(resourceBundle.getString("matchCaseToggleButton.toolTipText")); // NOI18N
        matchCaseToggleButton.setFocusable(false);
        matchCaseToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        matchCaseToggleButton.setName("matchCaseToggleButton"); // NOI18N
        matchCaseToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        matchCaseToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchCaseToggleButtonActionPerformed(evt);
            }
        });
        findToolBar.add(matchCaseToggleButton);

        multipleMatchesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/mark_occurrences.png"))); // NOI18N
        multipleMatchesToggleButton.setSelected(true);
        multipleMatchesToggleButton.setToolTipText(resourceBundle.getString("multipleMatchesToggleButton.toolTipText")); // NOI18N
        multipleMatchesToggleButton.setFocusable(false);
        multipleMatchesToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        multipleMatchesToggleButton.setName("multipleMatchesToggleButton"); // NOI18N
        multipleMatchesToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        multipleMatchesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multipleMatchesToggleButtonActionPerformed(evt);
            }
        });
        findToolBar.add(multipleMatchesToggleButton);

        separator1.setName("separator1"); // NOI18N
        findToolBar.add(separator1);

        optionsButton.setText(resourceBundle.getString("optionsButton.text")); // NOI18N
        optionsButton.setFocusable(false);
        optionsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        optionsButton.setName("optionsButton"); // NOI18N
        optionsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsButtonActionPerformed(evt);
            }
        });
        findToolBar.add(optionsButton);

        infoLabel.setEnabled(false);
        infoLabel.setName("infoLabel"); // NOI18N

        closeToolBar.setBorder(null);
        closeToolBar.setFloatable(false);
        closeToolBar.setRollover(true);
        closeToolBar.setName("closeToolBar"); // NOI18N

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/open_icon_library/icons/png/16x16/actions/dialog-cancel-3.png"))); // NOI18N
        closeButton.setToolTipText(resourceBundle.getString("closeButton.toolTipText")); // NOI18N
        closeButton.setFocusable(false);
        closeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        closeToolBar.add(closeButton);

        javax.swing.GroupLayout findPanelLayout = new javax.swing.GroupLayout(findPanel);
        findPanel.setLayout(findPanelLayout);
        findPanelLayout.setHorizontalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, findPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(findLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findTypeToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findComboBox, 0, 519, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        findPanelLayout.setVerticalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(closeToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findTypeToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(findComboBox)
        );

        add(findPanel, java.awt.BorderLayout.CENTER);

        replacePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 0, 0));
        replacePanel.setName("replacePanel"); // NOI18N
        replacePanel.setPreferredSize(new java.awt.Dimension(1015, 28));

        replaceLabel.setText(resourceBundle.getString("replaceLabel.text")); // NOI18N
        replaceLabel.setName("replaceLabel"); // NOI18N

        replaceTypeToolBar.setBorder(null);
        replaceTypeToolBar.setFloatable(false);
        replaceTypeToolBar.setRollover(true);
        replaceTypeToolBar.setFocusable(false);
        replaceTypeToolBar.setName("replaceTypeToolBar"); // NOI18N

        replaceTypeButton.setText(resourceBundle.getString("HexSearchPanel.replaceTypeButton.text")); // NOI18N
        replaceTypeButton.setToolTipText(resourceBundle.getString("replaceTypeButton.toolTipText")); // NOI18N
        replaceTypeButton.setDefaultCapable(false);
        replaceTypeButton.setFocusable(false);
        replaceTypeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        replaceTypeButton.setMaximumSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setMinimumSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setName("replaceTypeButton"); // NOI18N
        replaceTypeButton.setPreferredSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        replaceTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceTypeButtonActionPerformed(evt);
            }
        });
        replaceTypeToolBar.add(replaceTypeButton);

        replaceComboBox.setEditable(true);
        replaceComboBox.setSelectedItem("");
        replaceComboBox.setName("replaceComboBox"); // NOI18N

        replaceToolBar.setBorder(null);
        replaceToolBar.setFloatable(false);
        replaceToolBar.setRollover(true);
        replaceToolBar.setFocusable(false);
        replaceToolBar.setName("replaceToolBar"); // NOI18N

        replaceButton.setText(resourceBundle.getString("replaceButton.text")); // NOI18N
        replaceButton.setEnabled(false);
        replaceButton.setFocusable(false);
        replaceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        replaceButton.setName("replaceButton"); // NOI18N
        replaceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        replaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceButtonActionPerformed(evt);
            }
        });
        replaceToolBar.add(replaceButton);

        replaceAllButton.setText(resourceBundle.getString("replaceAllButton.text")); // NOI18N
        replaceAllButton.setEnabled(false);
        replaceAllButton.setFocusable(false);
        replaceAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        replaceAllButton.setName("replaceAllButton"); // NOI18N
        replaceAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceAllButtonActionPerformed(evt);
            }
        });
        replaceToolBar.add(replaceAllButton);

        javax.swing.GroupLayout replacePanelLayout = new javax.swing.GroupLayout(replacePanel);
        replacePanel.setLayout(replacePanelLayout);
        replacePanelLayout.setHorizontalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(replaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceTypeToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceComboBox, 0, 734, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(replaceToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        replacePanelLayout.setVerticalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(replaceTypeToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(replaceComboBox)
            .addComponent(replaceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(replaceToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(replacePanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsButtonActionPerformed
        cancelSearch();
        final FindHexPanel findHexPanel = new FindHexPanel();
        findHexPanel.setSelected();
        findHexPanel.setSearchHistory(searchHistory);
        findHexPanel.setSearchParameters(searchParameters);
        replaceParameters.setPerformReplace(replaceMode);
        findHexPanel.setReplaceParameters(replaceParameters);
        findHexPanel.setHexCodePopupMenuHandler(hexCodePopupMenuHandler);
        DefaultControlPanel controlPanel = new DefaultControlPanel(findHexPanel.getResourceBundle());
        JPanel dialogPanel = WindowUtils.createDialogPanel(findHexPanel, controlPanel);
        DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, "Find Text", true, new Object[0], null, 0, null, null);
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
        findHexPanel.setMultilineEditorListener(new FindHexPanel.MultilineEditorListener() {
            @Override
            public SearchCondition multilineEdit(SearchCondition condition) {
                final HexMultilinePanel multilinePanel = new HexMultilinePanel();
                multilinePanel.setHexCodePopupMenuHandler(hexCodePopupMenuHandler);
                multilinePanel.setCondition(condition);
                DefaultControlPanel controlPanel = new DefaultControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(multilinePanel, controlPanel);
                DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, "Multiline Hex/Text", true, new Object[0], null, 0, null, null);
                final Dialog multilineDialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
                final SearchConditionResult result = new SearchConditionResult();
                controlPanel.setHandler(new DefaultControlHandler() {
                    @Override
                    public void controlActionPerformed(DefaultControlHandler.ControlActionType actionType) {
                        if (actionType == DefaultControlHandler.ControlActionType.OK) {
                            result.searchCondition = multilinePanel.getCondition();
                            updateFindStatus();
                        }

                        WindowUtils.closeWindow(multilineDialog);
                    }
                });
                WindowUtils.assignGlobalKeyListener(multilineDialog, controlPanel.createOkCancelListener());
                multilineDialog.setLocationRelativeTo(dialog);
                multilineDialog.setVisible(true);
                multilinePanel.detachMenu();
                return result.searchCondition;
            }

            class SearchConditionResult {

                SearchCondition searchCondition = null;
            }
        });
        controlPanel.setHandler(new DefaultControlHandler() {
            @Override
            public void controlActionPerformed(DefaultControlHandler.ControlActionType actionType) {
                if (actionType == ControlActionType.OK) {
                    SearchParameters dialogSearchParameters = findHexPanel.getSearchParameters();
                    ((SearchHistoryModel) findComboBox.getModel()).addSearchCondition(dialogSearchParameters.getCondition());
                    dialogSearchParameters.setFromParameters(dialogSearchParameters);
                    findComboBoxEditorComponent.setItem(dialogSearchParameters.getCondition());
                    updateFindStatus();

                    ReplaceParameters dialogReplaceParameters = findHexPanel.getReplaceParameters();
                    switchReplaceMode(dialogReplaceParameters.isPerformReplace());
                    hexSearchPanelApi.performFind(dialogSearchParameters);
                }
                findHexPanel.detachMenu();
                WindowUtils.closeWindow(dialog);
            }
        });
        WindowUtils.assignGlobalKeyListener(dialog, controlPanel.createOkCancelListener());
//        dialog.setLocationRelativeTo(frameModule.getFrame());
        dialog.setVisible(true);
    }//GEN-LAST:event_optionsButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        matchPosition--;
        hexSearchPanelApi.setMatchPosition(matchPosition);
        setStatus(matchesCount, matchPosition);
    }//GEN-LAST:event_prevButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        matchPosition++;
        hexSearchPanelApi.setMatchPosition(matchPosition);
        setStatus(matchesCount, matchPosition);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void multipleMatchesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multipleMatchesToggleButtonActionPerformed
        searchParameters.setMultipleMatches(multipleMatchesToggleButton.isSelected());
        performSearch();
    }//GEN-LAST:event_multipleMatchesToggleButtonActionPerformed

    private void matchCaseToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchCaseToggleButtonActionPerformed
        searchParameters.setMatchCase(matchCaseToggleButton.isSelected());
        performSearch();
    }//GEN-LAST:event_matchCaseToggleButtonActionPerformed

    private void findTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findTypeButtonActionPerformed
        SearchCondition condition = searchParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            condition.setSearchMode(SearchCondition.SearchMode.BINARY);
        } else {
            condition.setSearchMode(SearchCondition.SearchMode.TEXT);
        }

        findComboBoxEditor.setItem(condition);
        findComboBox.setEditor(findComboBoxEditor);
        findComboBox.repaint();
        performSearch();
    }//GEN-LAST:event_findTypeButtonActionPerformed

    private void updateFindStatus() {
        SearchCondition condition = searchParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            findTypeButton.setText("T");
            matchCaseToggleButton.setEnabled(true);
        } else {
            findTypeButton.setText("B");
            matchCaseToggleButton.setEnabled(false);
        }
    }

    private void updateReplaceStatus() {
        SearchCondition condition = replaceParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            replaceTypeButton.setText("T");
        } else {
            replaceTypeButton.setText("B");
        }
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        closePanel();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void replaceTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceTypeButtonActionPerformed
        SearchCondition condition = replaceParameters.getCondition();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            condition.setSearchMode(SearchCondition.SearchMode.BINARY);
        } else {
            condition.setSearchMode(SearchCondition.SearchMode.TEXT);
        }

        replaceComboBoxEditor.setItem(condition);
        replaceComboBox.setEditor(replaceComboBoxEditor);
        replaceComboBox.repaint();
    }//GEN-LAST:event_replaceTypeButtonActionPerformed

    private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceButtonActionPerformed
        performReplace();
    }//GEN-LAST:event_replaceButtonActionPerformed

    private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceAllButtonActionPerformed
        performReplaceAll();
    }//GEN-LAST:event_replaceAllButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JToolBar closeToolBar;
    private javax.swing.JComboBox<SearchCondition> findComboBox;
    private javax.swing.JLabel findLabel;
    private javax.swing.JPanel findPanel;
    private javax.swing.JToolBar findToolBar;
    private javax.swing.JButton findTypeButton;
    private javax.swing.JToolBar findTypeToolBar;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JToggleButton matchCaseToggleButton;
    private javax.swing.JToggleButton multipleMatchesToggleButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton optionsButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton replaceAllButton;
    private javax.swing.JButton replaceButton;
    private javax.swing.JComboBox<SearchCondition> replaceComboBox;
    private javax.swing.JLabel replaceLabel;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JToolBar replaceToolBar;
    private javax.swing.JButton replaceTypeButton;
    private javax.swing.JToolBar replaceTypeToolBar;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JSeparator topSeparator;
    // End of variables declaration//GEN-END:variables

    private void comboBoxValueChanged() {
        SearchCondition condition = searchParameters.getCondition();
        SearchCondition searchCondition = (SearchCondition) findComboBox.getEditor().getItem();

        switch (searchCondition.getSearchMode()) {
            case TEXT: {
                String searchText = searchCondition.getSearchText();
                if (searchText == null || searchText.isEmpty()) {
                    condition.setSearchText(searchText);
                    performFind();
                    return;
                }

                if (searchText.equals(condition.getSearchText())) {
                    return;
                }

                condition.setSearchText(searchText);
                break;
            }
            case BINARY: {
                EditableBinaryData searchData = (EditableBinaryData) searchCondition.getBinaryData();
                if (searchData == null || searchData.isEmpty()) {
                    condition.setBinaryData(null);
                    performFind();
                    return;
                }

                if (dataEquals(searchData, condition.getBinaryData())) {
                    return;
                }

                ByteArrayEditableData data = new ByteArrayEditableData();
                data.insert(0, searchData);
                condition.setBinaryData(data);
                break;
            }
        }
        hexSearchPanelApi.updatePosition();
        performSearch(500);
    }

    private void performSearch() {
        performSearch(0);
    }

    private void performSearch(final int delay) {
        if (searchStartThread != null) {
            searchStartThread.interrupt();
        }
        searchStartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    if (searchThread != null) {
                        searchThread.interrupt();
                    }
                    searchThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            performFind();
                        }
                    });
                    searchThread.start();
                } catch (InterruptedException ex) {
                    // don't search
                }
            }
        });
        searchStartThread.start();
    }

    public void clearSearch() {
        SearchCondition condition = searchParameters.getCondition();
        if (!condition.isEmpty()) {
            condition.clear();
            findComboBox.getEditor().setItem(new SearchCondition());
            performSearch();
        }
    }

    public void requestSearchFocus() {
        findComboBox.requestFocus();
        findComboBoxEditorComponent.requestFocus();
    }

    public void cancelSearch() {
        if (searchThread != null) {
            searchThread.interrupt();
        }
    }

    public void performFind() {
        hexSearchPanelApi.performFind(searchParameters);
        findComboBoxEditorComponent.setRunningUpdate(true);
        ((SearchHistoryModel) findComboBox.getModel()).addSearchCondition(searchParameters.getCondition());
        findComboBoxEditorComponent.setRunningUpdate(false);
    }

    public void performReplace() {
        replaceParameters.setCondition(replaceComboBoxEditorComponent.getItem());
        hexSearchPanelApi.performReplace(searchParameters, replaceParameters);
    }

    public void performReplaceAll() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updatePosition(long position, long dataSize) {
        long startPosition;
        if (searchParameters.isSearchFromCursor()) {
            startPosition = position;
        } else {
            switch (searchParameters.getSearchDirection()) {
                case FORWARD: {
                    startPosition = 0;
                    break;
                }
                case BACKWARD: {
                    startPosition = dataSize - 1;
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal search type " + searchParameters.getSearchDirection().name());
            }
        }
        searchParameters.setStartPosition(startPosition);
    }

    public void setStatus(int matchesCount, int matchPosition) {
        this.matchesCount = matchesCount;
        this.matchPosition = matchPosition;
        switch (matchesCount) {
            case 0:
                infoLabel.setText("No matches found");
                break;
            case 1:
                infoLabel.setText("Single match found");
                break;
            default:
                infoLabel.setText("Match " + (matchPosition + 1) + " of " + matchesCount);
                break;
        }
        updateMatchStatus();
    }

    public void clearStatus() {
        infoLabel.setText("");
        matchesCount = 0;
        matchPosition = -1;
        updateMatchStatus();
    }

    public void dataChanged() {
        hexSearchPanelApi.clearMatches();
        performSearch(500);
    }

    public void closePanel() {
        if (closePanelListener != null) {
            clearSearch();
            closePanelListener.panelClosed();
        }
    }

    public ClosePanelListener getClosePanelListener() {
        return closePanelListener;
    }

    public void setClosePanelListener(ClosePanelListener closePanelListener) {
        this.closePanelListener = closePanelListener;
    }

    private void updateMatchStatus() {
        prevButton.setEnabled(matchesCount > 1 && matchPosition > 0);
        nextButton.setEnabled(matchPosition < matchesCount - 1);
        replaceButton.setEnabled(matchesCount > 0);
        replaceAllButton.setEnabled(matchesCount > 0);
    }

    // TODO implement optimalized method
    private boolean dataEquals(EditableBinaryData binaryData, BinaryData comparedData) {
        if (binaryData == null || comparedData == null || binaryData.getDataSize() != comparedData.getDataSize()) {
            return false;
        }

        for (int position = 0; position < binaryData.getDataSize(); position++) {
            if (binaryData.getByte(position) != comparedData.getByte(position)) {
                return false;
            }
        }

        return true;
    }

    public void setHexCodePopupMenuHandler(CodeAreaPopupMenuHandler hexCodePopupMenuHandler) {
        this.hexCodePopupMenuHandler = hexCodePopupMenuHandler;
        findComboBoxEditorComponent.setHexCodePopupMenuHandler(hexCodePopupMenuHandler, "");
    }

    /**
     * Listener for panel closing.
     */
    public static interface ClosePanelListener {

        void panelClosed();
    }
}
