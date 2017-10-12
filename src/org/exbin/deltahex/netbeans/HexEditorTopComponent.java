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
package org.exbin.deltahex.netbeans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.exbin.deltahex.CaretMovedListener;
import org.exbin.deltahex.CaretPosition;
import org.exbin.deltahex.CodeAreaLineNumberLength;
import org.exbin.deltahex.CodeType;
import org.exbin.deltahex.DataChangedListener;
import org.exbin.deltahex.EditationAllowed;
import org.exbin.deltahex.EditationMode;
import org.exbin.deltahex.EditationModeChangedListener;
import org.exbin.deltahex.HexCharactersCase;
import org.exbin.deltahex.PositionCodeType;
import org.exbin.deltahex.Section;
import org.exbin.deltahex.ViewMode;
import org.exbin.deltahex.delta.DeltaDocument;
import org.exbin.deltahex.delta.FileDataSource;
import org.exbin.deltahex.delta.SegmentsRepository;
import org.exbin.deltahex.highlight.swing.HighlightCodeAreaPainter;
import org.exbin.deltahex.highlight.swing.HighlightNonAsciiCodeAreaPainter;
import org.exbin.deltahex.netbeans.panel.DeltaHexOptionsPanelBorder;
import org.exbin.deltahex.netbeans.panel.HexSearchPanel;
import org.exbin.deltahex.netbeans.panel.HexSearchPanelApi;
import org.exbin.deltahex.netbeans.panel.ValuesPanel;
import org.exbin.deltahex.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.deltahex.operation.BinaryDataCommand;
import org.exbin.deltahex.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.deltahex.swing.CodeAreaSpace;
import org.exbin.framework.deltahex.CodeAreaPopupMenuHandler;
import org.exbin.framework.deltahex.HexStatusApi;
import org.exbin.framework.deltahex.panel.HexStatusPanel;
import org.exbin.framework.deltahex.panel.ReplaceParameters;
import org.exbin.framework.deltahex.panel.SearchCondition;
import org.exbin.framework.deltahex.panel.SearchParameters;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.panel.TextFontOptionsPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.handler.OptionsControlHandler;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.ByteArrayData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.exbin.utils.binary_data.PagedData;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Hexadecimal editor top component.
 *
 * @version 0.1.8 2017/10/12
 * @author ExBin Project (http://exbin.org)
 */
@ConvertAsProperties(dtd = "-//org.exbin.deltahex//HexEditor//EN", autostore = false)
@TopComponent.Description(preferredID = "HexEditorTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(displayName = "#CTL_HexEditorAction", preferredID = "HexEditorTopComponent")
public final class HexEditorTopComponent extends TopComponent implements MultiViewElement, Serializable, UndoRedo.Provider {

    public static final String PREFERENCES_MEMORY_DELTA_MODE = "deltaMode";
    public static final String PREFERENCES_CODE_TYPE = "codeType";
    public static final String PREFERENCES_LINE_WRAPPING = "lineWrapping";
    public static final String PREFERENCES_SHOW_UNPRINTABLES = "showNonpritables";
    public static final String PREFERENCES_ENCODING_SELECTED = "selectedEncoding";
    public static final String PREFERENCES_ENCODING_PREFIX = "textEncoding.";
    public static final String PREFERENCES_BYTES_PER_LINE = "bytesPerLine";
    public static final String PREFERENCES_SHOW_HEADER = "showHeader";
    public static final String PREFERENCES_HEADER_SPACE_TYPE = "headerSpaceType";
    public static final String PREFERENCES_HEADER_SPACE = "headerSpace";
    public static final String PREFERENCES_SHOW_LINE_NUMBERS = "showLineNumbers";
    public static final String PREFERENCES_LINE_NUMBERS_LENGTH_TYPE = "lineNumbersLengthType";
    public static final String PREFERENCES_LINE_NUMBERS_LENGTH = "lineNumbersLength";
    public static final String PREFERENCES_LINE_NUMBERS_SPACE_TYPE = "lineNumbersSpaceType";
    public static final String PREFERENCES_LINE_NUMBERS_SPACE = "lineNumbersSpace";
    public static final String PREFERENCES_VIEW_MODE = "viewMode";
    public static final String PREFERENCES_BACKGROUND_MODE = "backgroundMode";
    public static final String PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND = "showLineNumbersBackground";
    public static final String PREFERENCES_POSITION_CODE_TYPE = "positionCodeType";
    public static final String PREFERENCES_HEX_CHARACTERS_CASE = "hexCharactersCase";
    public static final String PREFERENCES_DECORATION_HEADER_LINE = "decorationHeaderLine";
    public static final String PREFERENCES_DECORATION_PREVIEW_LINE = "decorationPreviewLine";
    public static final String PREFERENCES_DECORATION_BOX = "decorationBox";
    public static final String PREFERENCES_DECORATION_LINENUM_LINE = "decorationLineNumLine";
    public static final String PREFERENCES_BYTE_GROUP_SIZE = "byteGroupSize";
    public static final String PREFERENCES_SPACE_GROUP_SIZE = "spaceGroupSize";
    public static final String PREFERENCES_CODE_COLORIZATION = "codeColorization";
    public static final String PREFERENCES_SHOW_VALUES_PANEL = "valuesPanel";

    private final Preferences preferences;
    private final HexEditorNode node;
    private static SegmentsRepository segmentsRepository = null;
    private final CodeArea codeArea;
    private final UndoRedo.Manager undoRedo;
    private final HexUndoSwingHandler undoHandler;
    private final Savable savable;
    private final InstanceContent content = new InstanceContent();
    private final int metaMask;

    private HexStatusPanel statusPanel;
    private HexStatusApi hexStatus;
    private TextEncodingStatusApi encodingStatus;
    private CharsetChangeListener charsetChangeListener = null;
    private GoToHandler goToHandler;
    private EncodingsHandler encodingsHandler;
    private boolean findTextPanelVisible = false;
    private HexSearchPanel hexSearchPanel = null;
    private ValuesPanel valuesPanel = null;
    private JScrollPane valuesPanelScrollPane = null;
    private boolean valuesPanelVisible = false;

    private boolean opened = false;
    private boolean modified = false;
    private boolean deltaMemoryMode = false;
    protected String displayName;
    private long documentOriginalSize;
    private DataObject dataObject;

    public HexEditorTopComponent() {
        initComponents();

        preferences = NbPreferences.forModule(HexEditorTopComponent.class);

        codeArea = new CodeArea();
        codeArea.setPainter(new HighlightNonAsciiCodeAreaPainter(codeArea));
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.getCaret().setBlinkRate(300);
        statusPanel = new HexStatusPanel();
        registerEncodingStatus(statusPanel);
        encodingsHandler = new EncodingsHandler(new TextEncodingStatusApi() {
            @Override
            public String getEncoding() {
                return encodingStatus.getEncoding();
            }

            @Override
            public void setEncoding(String encodingName) {
                codeArea.setCharset(Charset.forName(encodingName));
                encodingStatus.setEncoding(encodingName);
                preferences.put(HexEditorTopComponent.PREFERENCES_ENCODING_SELECTED, encodingName);
            }
        });

        undoRedo = new UndoRedo.Manager();
        undoHandler = new HexUndoSwingHandler(codeArea, undoRedo);

        loadFromPreferences();

        getSegmentsRepository();
        setNewData();
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        codeAreaPanel.add(codeArea, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        registerHexStatus(statusPanel);
        goToHandler = new GoToHandler(codeArea);

        codeArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = createContextMenu();
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        node = new HexEditorNode(this);
        content.add(node);
        savable = new Savable(this, codeArea);

        setActivatedNodes(new Node[]{node});

        undoHandler.addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                codeArea.repaint();
                updateCurrentDocumentSize();
                updateModified();
            }

            @Override
            public void undoCommandAdded(final BinaryDataCommand command) {
                updateCurrentDocumentSize();
                updateModified();
            }
        });

        codeArea.addDataChangedListener(new DataChangedListener() {
            @Override
            public void dataChanged() {
                if (hexSearchPanel != null && hexSearchPanel.isVisible()) {
                    hexSearchPanel.dataChanged();
                }
                updateCurrentDocumentSize();
            }
        });

        setName(NbBundle.getMessage(HexEditorTopComponent.class, "CTL_HexEditorTopComponent"));
        setToolTipText(NbBundle.getMessage(HexEditorTopComponent.class, "HINT_HexEditorTopComponent"));

        applyFromCodeArea();

        getActionMap().put("copy-to-clipboard", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copy();
            }
        });
        getActionMap().put("cut-to-clipboard", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.cut();
            }
        });
        getActionMap().put("paste-from-clipboard", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.paste();
            }
        });

        int metaMaskValue;
        try {
            metaMaskValue = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        } catch (java.awt.HeadlessException ex) {
            metaMaskValue = java.awt.Event.CTRL_MASK;
        }
        metaMask = metaMaskValue;

        codeArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getModifiers() == metaMask) {
                    int keyCode = keyEvent.getKeyCode();
                    switch (keyCode) {
                        case KeyEvent.VK_F: {
                            showSearchPanel(false);
                            break;
                        }
                        case KeyEvent.VK_G: {
                            goToHandler.getGoToLineAction().actionPerformed(null);
                            break;
                        }
                    }
                }
            }
        });

        associateLookup(new AbstractLookup(content));
    }

    private void applyFromCodeArea() {
        codeTypeComboBox.setSelectedIndex(codeArea.getCodeType().ordinal());
        showUnprintablesToggleButton.setSelected(codeArea.isShowUnprintableCharacters());
        lineWrappingToggleButton.setSelected(codeArea.isWrapMode());
    }

    public void registerHexStatus(HexStatusApi hexStatusApi) {
        this.hexStatus = hexStatusApi;
        codeArea.addCaretMovedListener(new CaretMovedListener() {
            @Override
            public void caretMoved(CaretPosition caretPosition, Section section) {
                String position = String.valueOf(caretPosition.getDataPosition());
                position += ":" + caretPosition.getCodeOffset();
                hexStatus.setCursorPosition(position);
            }
        });

        codeArea.addEditationModeChangedListener(new EditationModeChangedListener() {
            @Override
            public void editationModeChanged(EditationMode mode) {
                hexStatus.setEditationMode(mode);
            }
        });
        hexStatus.setEditationMode(codeArea.getEditationMode());

        hexStatus.setControlHandler(new HexStatusApi.StatusControlHandler() {
            @Override
            public void changeEditationMode(EditationMode editationMode) {
                codeArea.setEditationMode(editationMode);
            }

            @Override
            public void changeCursorPosition() {
                goToHandler.getGoToLineAction().actionPerformed(null);
            }

            @Override
            public void cycleEncodings() {
                if (encodingsHandler != null) {
                    encodingsHandler.cycleEncodings();
                }
            }

            @Override
            public void popupEncodingsMenu(MouseEvent mouseEvent) {
                if (encodingsHandler != null) {
                    encodingsHandler.popupEncodingsMenu(mouseEvent);
                }
            }

            @Override
            public void changeMemoryMode(HexStatusApi.MemoryMode memoryMode) {
                boolean newDeltaMode = memoryMode == HexStatusApi.MemoryMode.DELTA_MODE;
                if (newDeltaMode != deltaMemoryMode) {
                    switchDeltaMemoryMode(newDeltaMode);
                    preferences.putBoolean(HexEditorTopComponent.PREFERENCES_MEMORY_DELTA_MODE, deltaMemoryMode);
                }
            }
        });
    }

    private void switchDeltaMemoryMode(boolean newDeltaMode) {
        if (newDeltaMode != deltaMemoryMode) {
            // Switch memory mode
            if (dataObject != null) {
                // If document is connected to file, attempt to release first if modified and then simply reload
                if (isModified()) {
                    if (releaseFile()) {
                        deltaMemoryMode = newDeltaMode;
                        openDataObject(dataObject);
                        codeArea.clearSelection();
                        codeArea.setCaretPosition(0);
                    }
                } else {
                    deltaMemoryMode = newDeltaMode;
                    openDataObject(dataObject);
                }
            } else {
                // If document unsaved in memory, switch data in code area
                if (codeArea.getData() instanceof DeltaDocument) {
                    BinaryData oldData = codeArea.getData();
                    PagedData data = new PagedData();
                    data.insert(0, codeArea.getData());
                    codeArea.setData(data);
                    oldData.dispose();
                } else {
                    BinaryData oldData = codeArea.getData();
                    DeltaDocument document = segmentsRepository.createDocument();
                    document.insert(0, oldData);
                    codeArea.setData(document);
                    oldData.dispose();
                }
                undoHandler.clear();
                codeArea.notifyDataChanged();
                updateCurrentMemoryMode();
                deltaMemoryMode = newDeltaMode;
            }
            deltaMemoryMode = newDeltaMode;
        }
    }

    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        setCharsetChangeListener(new CharsetChangeListener() {
            @Override
            public void charsetChanged() {
                String selectedEncoding = codeArea.getCharset().name();
                encodingStatus.setEncoding(selectedEncoding);
            }
        });
    }

    public void setCharsetChangeListener(CharsetChangeListener charsetChangeListener) {
        this.charsetChangeListener = charsetChangeListener;
    }

    @Override
    public boolean canClose() {
        if (!modified) {
            return true;
        }

        final Component parent = WindowManager.getDefault().getMainWindow();
        final Object[] options = new Object[]{"Save", "Discard", "Cancel"};
        final String message = "File " + displayName + " is modified. Save?";
        final int choice = JOptionPane.showOptionDialog(parent, message, "Question", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, JOptionPane.YES_OPTION);
        if (choice == JOptionPane.CANCEL_OPTION) {
            return false;
        }

        if (choice == JOptionPane.YES_OPTION) {
            try {
                savable.handleSave();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return true;
    }

    public boolean isModified() {
        return undoHandler.getCommandPosition() != undoHandler.getSyncPoint();
    }

    void setModified(boolean modified) {
        this.modified = modified;
        final String htmlDisplayName;
        if (modified && opened) {
            savable.activate();
            content.add(savable);
            htmlDisplayName = "<html><b>" + displayName + "</b></html>";
        } else {
            savable.deactivate();
            content.remove(savable);
            htmlDisplayName = displayName;
        }

        if (SwingUtilities.isEventDispatchThread()) {
            setHtmlDisplayName(htmlDisplayName);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        setHtmlDisplayName(htmlDisplayName);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void setNewData() {
        if (deltaMemoryMode) {
            codeArea.setData(segmentsRepository.createDocument());
        } else {
            codeArea.setData(new PagedData());
        }
    }

    /**
     * Attempts to release current file and warn if document was modified.
     *
     * @return true if successful
     */
    private boolean releaseFile() {

        if (dataObject == null) {
            return true;
        }

        while (isModified()) {
            Object[] options = {
                "Save",
                "Discard",
                "Cancel"
            };
            int result = JOptionPane.showOptionDialog(this,
                    "Document was modified! Do you wish to save it?",
                    "Save File?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (result == JOptionPane.NO_OPTION) {
                return true;
            }
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return false;
            }

            try {
                saveDataObject(dataObject);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return true;
    }

    public void openDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
        displayName = dataObject.getPrimaryFile().getNameExt();
        setHtmlDisplayName(displayName);
        node.openFile(dataObject);
        savable.setDataObject(dataObject);
        opened = true;
        documentOriginalSize = codeArea.getDataSize();
        updateCurrentDocumentSize();
        updateCurrentMemoryMode();

//        final Charset charset = Charset.forName(FileEncodingQuery.getEncoding(dataObject.getPrimaryFile()).name());
//        if (charsetChangeListener != null) {
//            charsetChangeListener.charsetChanged();
//        }
//        codeArea.setCharset(charset);
    }

    public void saveDataObject(DataObject dataObject) throws IOException {
        node.saveFile(dataObject);
        undoHandler.setSyncPoint();
        setModified(false);
        documentOriginalSize = codeArea.getDataSize();
        updateCurrentDocumentSize();
        updateCurrentMemoryMode();
    }

    @Override
    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    public void updatePosition() {
        // hexSearchPanel.updatePosition(codeArea.getCaretPosition().getDataPosition(), codeArea.getDataSize());
    }

    private void updateCurrentDocumentSize() {
        long dataSize = codeArea.getData().getDataSize();
        long difference = dataSize - documentOriginalSize;
        hexStatus.setCurrentDocumentSize(dataSize + " (" + (difference > 0 ? "+" + difference : difference) + ")");
    }

    public boolean isDeltaMemoryMode() {
        return deltaMemoryMode;
    }

    public void setDeltaMemoryMode(boolean deltaMemoryMode) {
        this.deltaMemoryMode = deltaMemoryMode;
    }

    private void updateCurrentMemoryMode() {
        HexStatusApi.MemoryMode memoryMode = HexStatusApi.MemoryMode.RAM_MEMORY;
        if (codeArea.getEditationAllowed() == EditationAllowed.READ_ONLY) {
            memoryMode = HexStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getData() instanceof DeltaDocument) {
            memoryMode = HexStatusApi.MemoryMode.DELTA_MODE;
        }

        if (hexStatus != null) {
            hexStatus.setMemoryMode(memoryMode);
        }
    }

    private void updateModified() {
        setModified(undoHandler.getSyncPoint() != undoHandler.getCommandPosition());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        codeAreaPanel = new javax.swing.JPanel();
        infoToolbar = new javax.swing.JPanel();
        controlToolBar = new javax.swing.JToolBar();
        lineWrappingToggleButton = new javax.swing.JToggleButton();
        showUnprintablesToggleButton = new javax.swing.JToggleButton();
        separator1 = new javax.swing.JToolBar.Separator();
        codeTypeComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        codeAreaPanel.setLayout(new java.awt.BorderLayout());

        controlToolBar.setBorder(null);
        controlToolBar.setFloatable(false);
        controlToolBar.setRollover(true);

        lineWrappingToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/deltahex/netbeans/resources/icons/deltahex-linewrap.png"))); // NOI18N
        lineWrappingToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(HexEditorTopComponent.class, "HexEditorTopComponent.lineWrappingToggleButton.toolTipText")); // NOI18N
        lineWrappingToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineWrappingToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(lineWrappingToggleButton);

        showUnprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/deltahex/netbeans/resources/icons/insert-pilcrow.png"))); // NOI18N
        showUnprintablesToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(HexEditorTopComponent.class, "HexEditorTopComponent.showUnprintablesToggleButton.toolTipText")); // NOI18N
        showUnprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUnprintablesToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(showUnprintablesToggleButton);
        controlToolBar.add(separator1);

        codeTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BIN", "OCT", "DEC", "HEX" }));
        codeTypeComboBox.setMaximumSize(new java.awt.Dimension(58, 25));
        codeTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codeTypeComboBoxActionPerformed(evt);
            }
        });
        controlToolBar.add(codeTypeComboBox);

        javax.swing.GroupLayout infoToolbarLayout = new javax.swing.GroupLayout(infoToolbar);
        infoToolbar.setLayout(infoToolbarLayout);
        infoToolbarLayout.setHorizontalGroup(
            infoToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoToolbarLayout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 579, Short.MAX_VALUE))
        );
        infoToolbarLayout.setVerticalGroup(
            infoToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        codeAreaPanel.add(infoToolbar, java.awt.BorderLayout.PAGE_START);

        add(codeAreaPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void lineWrappingToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineWrappingToggleButtonActionPerformed
        codeArea.setWrapMode(lineWrappingToggleButton.isSelected());
        preferences.putBoolean(HexEditorTopComponent.PREFERENCES_LINE_WRAPPING, lineWrappingToggleButton.isSelected());
    }//GEN-LAST:event_lineWrappingToggleButtonActionPerformed

    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUnprintablesToggleButtonActionPerformed
        codeArea.setShowUnprintableCharacters(showUnprintablesToggleButton.isSelected());
        preferences.putBoolean(HexEditorTopComponent.PREFERENCES_SHOW_UNPRINTABLES, lineWrappingToggleButton.isSelected());
    }//GEN-LAST:event_showUnprintablesToggleButtonActionPerformed

    private void codeTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codeTypeComboBoxActionPerformed
        CodeType codeType = CodeType.values()[codeTypeComboBox.getSelectedIndex()];
        codeArea.setCodeType(codeType);
        preferences.put(HexEditorTopComponent.PREFERENCES_CODE_TYPE, codeType.name());
    }//GEN-LAST:event_codeTypeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel codeAreaPanel;
    private javax.swing.JComboBox<String> codeTypeComboBox;
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JPanel infoToolbar;
    private javax.swing.JToggleButton lineWrappingToggleButton;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToggleButton showUnprintablesToggleButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        super.componentOpened();
        codeArea.requestFocus();
    }

    @Override
    public void componentClosed() {
        if (savable != null) {
            savable.deactivate();
        }
        closeData();
        super.componentClosed();
    }

    private void closeData() {
        BinaryData data = codeArea.getData();
        codeArea.setData(new ByteArrayData());
        if (data instanceof DeltaDocument) {
            FileDataSource fileSource = ((DeltaDocument) data).getFileSource();
            data.dispose();
            segmentsRepository.detachFileSource(fileSource);
            segmentsRepository.closeFileSource(fileSource);
        } else {
            data.dispose();
        }
    }

    public static synchronized SegmentsRepository getSegmentsRepository() {
        if (segmentsRepository == null) {
            segmentsRepository = new SegmentsRepository();
        }

        return segmentsRepository;
    }

    public void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    public void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    private JPopupMenu createContextMenu() {
        final JPopupMenu result = new JPopupMenu();

        final JMenuItem cutMenuItem = new JMenuItem("Cut");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, metaMask));
        cutMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
        cutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.cut();
                result.setVisible(false);
            }
        });
        result.add(cutMenuItem);

        final JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, metaMask));
        copyMenuItem.setEnabled(codeArea.hasSelection());
        copyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copy();
                result.setVisible(false);
            }
        });
        result.add(copyMenuItem);

        final JMenuItem copyAsCodeMenuItem = new JMenuItem("Copy as Code");
        copyAsCodeMenuItem.setEnabled(codeArea.hasSelection());
        copyAsCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copyAsCode();
                result.setVisible(false);
            }
        });
        result.add(copyAsCodeMenuItem);

        final JMenuItem pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, metaMask));
        pasteMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
        pasteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.paste();
                result.setVisible(false);
            }
        });
        result.add(pasteMenuItem);

        final JMenuItem pasteFromCodeMenuItem = new JMenuItem("Paste from Code");
        pasteFromCodeMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
        pasteFromCodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    codeArea.pasteFromCode();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(codeArea, ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
                }
                result.setVisible(false);
            }
        });
        result.add(pasteFromCodeMenuItem);

        final JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
        deleteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.delete();
                result.setVisible(false);
            }
        });
        result.add(deleteMenuItem);
        result.addSeparator();

        final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, metaMask));
        selectAllMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.selectAll();
                result.setVisible(false);
            }
        });
        result.add(selectAllMenuItem);
        result.addSeparator();

        final JMenuItem goToMenuItem = new JMenuItem("Go To...");
        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, metaMask));
        goToMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToHandler.getGoToLineAction().actionPerformed(null);
            }
        });
        result.add(goToMenuItem);

        final JMenuItem findMenuItem = new JMenuItem("Find...");
        findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, metaMask));
        findMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchPanel(false);
            }
        });
        result.add(findMenuItem);

        final JMenuItem replaceMenuItem = new JMenuItem("Replace...");
        replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, metaMask));
        replaceMenuItem.setEnabled(codeArea.isEditable());
        replaceMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchPanel(true);
            }
        });
        result.add(replaceMenuItem);
        result.addSeparator();
        final JMenuItem optionsMenuItem = new JMenuItem("Options...");
        optionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final DeltaHexOptionsPanelBorder optionsPanel = new DeltaHexOptionsPanelBorder();
                optionsPanel.setFromCodeArea(codeArea);
                optionsPanel.setShowValuesPanel(valuesPanelVisible);
                OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(optionsPanel, optionsControlPanel);
                DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, "Options", true, new Object[0], null, 0, null, null);

                final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
                optionsControlPanel.setHandler(new OptionsControlHandler() {
                    @Override
                    public void controlActionPerformed(OptionsControlHandler.ControlActionType actionType) {
                        if (actionType == OptionsControlHandler.ControlActionType.SAVE) {
                            optionsPanel.store();
                        }
                        if (actionType != OptionsControlHandler.ControlActionType.CANCEL) {
                            optionsPanel.applyToCodeArea(codeArea);
                            boolean applyShowValuesPanel = optionsPanel.isShowValuesPanel();
                            if (applyShowValuesPanel) {
                                showValuesPanel();
                            } else {
                                hideValuesPanel();
                            }
                            applyFromCodeArea();
                            switchDeltaMemoryMode(optionsPanel.isDeltaMemoryMode());
                            codeArea.repaint();
                        }

                        WindowUtils.closeWindow(dialog);
                    }
                });
                WindowUtils.assignGlobalKeyListener(dialog, optionsControlPanel.createOkCancelListener());
                dialog.setSize(650, 460);
                dialog.setVisible(true);
            }
        });
        result.add(optionsMenuItem);

        return result;
    }

    public void showSearchPanel(boolean replace) {
        if (hexSearchPanel == null) {
            hexSearchPanel = new HexSearchPanel(new HexSearchPanelApi() {
                @Override
                public void performFind(SearchParameters searchParameters) {
                    HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
                    SearchCondition condition = searchParameters.getCondition();
                    hexSearchPanel.clearStatus();
                    if (condition.isEmpty()) {
                        painter.clearMatches();
                        codeArea.repaint();
                        return;
                    }

                    long position;
                    if (searchParameters.isSearchFromCursor()) {
                        position = codeArea.getCaretPosition().getDataPosition();
                    } else {
                        switch (searchParameters.getSearchDirection()) {
                            case FORWARD: {
                                position = 0;
                                break;
                            }
                            case BACKWARD: {
                                position = codeArea.getDataSize() - 1;
                                break;
                            }
                            default:
                                throw new IllegalStateException("Illegal search type " + searchParameters.getSearchDirection().name());
                        }
                    }
                    searchParameters.setStartPosition(position);

                    switch (condition.getSearchMode()) {
                        case TEXT: {
                            searchForText(searchParameters);
                            break;
                        }
                        case BINARY: {
                            searchForBinaryData(searchParameters);
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected search mode " + condition.getSearchMode().name());
                    }
                }

                @Override
                public void setMatchPosition(int matchPosition) {
                    HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
                    painter.setCurrentMatchIndex(matchPosition);
                    HighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
                    codeArea.revealPosition(currentMatch.getPosition(), codeArea.getActiveSection());
                    codeArea.repaint();
                }

                @Override
                public void updatePosition() {
                    hexSearchPanel.updatePosition(codeArea.getCaretPosition().getDataPosition(), codeArea.getDataSize());
                }

                @Override
                public void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters) {
                    SearchCondition replaceCondition = replaceParameters.getCondition();
                    HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
                    HighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
                    if (currentMatch != null) {
                        EditableBinaryData editableData = ((EditableBinaryData) codeArea.getData());
                        editableData.remove(currentMatch.getPosition(), currentMatch.getLength());
                        if (replaceCondition.getSearchMode() == SearchCondition.SearchMode.BINARY) {
                            editableData.insert(currentMatch.getPosition(), replaceCondition.getBinaryData());
                        } else {
                            editableData.insert(currentMatch.getPosition(), replaceCondition.getSearchText().getBytes(codeArea.getCharset()));
                        }
                        painter.getMatches().remove(currentMatch);
                        codeArea.repaint();
                    }
                }

                @Override
                public void clearMatches() {
                    HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
                    painter.clearMatches();
                }
            });
            hexSearchPanel.setHexCodePopupMenuHandler(new CodeAreaPopupMenuHandler() {
                @Override
                public JPopupMenu createPopupMenu(CodeArea codeArea, String menuPostfix) {
                    return createCodeAreaPopupMenu(codeArea, menuPostfix);
                }

                @Override
                public void dropPopupMenu(String menuPostfix) {
                }
            });
            hexSearchPanel.setClosePanelListener(new HexSearchPanel.ClosePanelListener() {
                @Override
                public void panelClosed() {
                    hideSearchPanel();
                }
            });
        }

        if (!findTextPanelVisible) {
            codeAreaPanel.add(hexSearchPanel, BorderLayout.SOUTH);
            codeAreaPanel.revalidate();
            revalidate();
            findTextPanelVisible = true;
            hexSearchPanel.requestSearchFocus();
        }
        hexSearchPanel.switchReplaceMode(replace);
    }

    public void hideSearchPanel() {
        if (findTextPanelVisible) {
            hexSearchPanel.cancelSearch();
            hexSearchPanel.clearSearch();
            codeAreaPanel.remove(hexSearchPanel);
            codeAreaPanel.revalidate();
            revalidate();
            findTextPanelVisible = false;
        }
    }

    public void showValuesPanel() {
        if (!valuesPanelVisible) {
            valuesPanelVisible = true;
            if (valuesPanel == null) {
                valuesPanel = new ValuesPanel();
                valuesPanel.setCodeArea(codeArea, undoHandler);
                valuesPanelScrollPane = new JScrollPane(valuesPanel);
                valuesPanelScrollPane.setBorder(null);
            }
            codeAreaPanel.add(valuesPanelScrollPane, BorderLayout.EAST);
            valuesPanel.enableUpdate();
            valuesPanel.updateValues();
            codeAreaPanel.revalidate();
            revalidate();
        }
    }

    public void hideValuesPanel() {
        if (valuesPanelVisible) {
            valuesPanelVisible = false;
            valuesPanel.disableUpdate();
            codeAreaPanel.remove(valuesPanelScrollPane);
            codeAreaPanel.revalidate();
            revalidate();
        }
    }

    private JPopupMenu createCodeAreaPopupMenu(final CodeArea codeArea, String menuPostfix) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem cutMenuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.cut();
            }

            @Override
            public boolean isEnabled() {
                return codeArea.hasSelection();
            }
        });
        cutMenuItem.setText("Cut");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, metaMask));
        popupMenu.add(cutMenuItem);
        JMenuItem copyMenuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copy();
            }

            @Override
            public boolean isEnabled() {
                return codeArea.hasSelection();
            }
        });
        copyMenuItem.setText("Copy");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, metaMask));
        popupMenu.add(copyMenuItem);
        JMenuItem pasteMenuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.paste();
            }

            @Override
            public boolean isEnabled() {
                return codeArea.canPaste();
            }
        });
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, metaMask));
        popupMenu.add(pasteMenuItem);
        JMenuItem deleteMenuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.delete();
            }

            @Override
            public boolean isEnabled() {
                return codeArea.hasSelection();
            }
        });
        deleteMenuItem.setText("Delete");
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        popupMenu.add(deleteMenuItem);
        JMenuItem selectAllMenuItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.selectAll();
            }
        });
        selectAllMenuItem.setText("Select All");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, metaMask));
        popupMenu.add(selectAllMenuItem);

        return popupMenu;
    }

    /**
     * Performs search by text/characters.
     */
    private void searchForText(SearchParameters searchParameters) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();

        long position = searchParameters.getStartPosition();
        String findText;
        if (searchParameters.isMatchCase()) {
            findText = condition.getSearchText();
        } else {
            findText = condition.getSearchText().toLowerCase();
        }
        BinaryData data = codeArea.getData();

        List<HighlightCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

        Charset charset = codeArea.getCharset();
        CharsetEncoder encoder = charset.newEncoder();
        int maxBytesPerChar = (int) encoder.maxBytesPerChar();
        byte[] charData = new byte[maxBytesPerChar];
        long dataSize = data.getDataSize();
        while (position <= dataSize - findText.length()) {
            int matchCharLength = 0;
            int matchLength = 0;
            while (matchCharLength < findText.length()) {
                long searchPosition = position + matchLength;
                int bytesToUse = maxBytesPerChar;
                if (searchPosition + bytesToUse > dataSize) {
                    bytesToUse = (int) (dataSize - searchPosition);
                }
                data.copyToArray(searchPosition, charData, 0, bytesToUse);
                char singleChar = new String(charData, charset).charAt(0);
                String singleCharString = String.valueOf(singleChar);
                int characterLength = singleCharString.getBytes(charset).length;

                if (searchParameters.isMatchCase()) {
                    if (singleChar != findText.charAt(matchCharLength)) {
                        break;
                    }
                } else if (singleCharString.toLowerCase().charAt(0) != findText.charAt(matchCharLength)) {
                    break;
                }
                matchCharLength++;
                matchLength += characterLength;
            }

            if (matchCharLength == findText.length()) {
                HighlightCodeAreaPainter.SearchMatch match = new HighlightCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(matchLength);
                foundMatches.add(match);

                if (foundMatches.size() == 100 || !searchParameters.isMultipleMatches()) {
                    break;
                }
            }

            switch (searchParameters.getSearchDirection()) {
                case FORWARD: {
                    position++;
                    break;
                }
                case BACKWARD: {
                    position--;
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal search type " + searchParameters.getSearchDirection().name());
            }
        }

        painter.setMatches(foundMatches);
        if (foundMatches.size() > 0) {
            painter.setCurrentMatchIndex(0);
            HighlightCodeAreaPainter.SearchMatch firstMatch = painter.getCurrentMatch();
            codeArea.revealPosition(firstMatch.getPosition(), codeArea.getActiveSection());
        }
        hexSearchPanel.setStatus(foundMatches.size(), 0);
        codeArea.repaint();
    }

    /**
     * Performs search by binary data.
     */
    private void searchForBinaryData(SearchParameters searchParameters) {
        HighlightCodeAreaPainter painter = (HighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();
        long position = codeArea.getCaretPosition().getDataPosition();
        HighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();

        if (currentMatch != null) {
            if (currentMatch.getPosition() == position) {
                position++;
            }
            painter.clearMatches();
        } else if (!searchParameters.isSearchFromCursor()) {
            position = 0;
        }

        BinaryData searchData = condition.getBinaryData();
        BinaryData data = codeArea.getData();

        List<HighlightCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

        long dataSize = data.getDataSize();
        while (position < dataSize - searchData.getDataSize()) {
            int matchLength = 0;
            while (matchLength < searchData.getDataSize()) {
                if (data.getByte(position + matchLength) != searchData.getByte(matchLength)) {
                    break;
                }
                matchLength++;
            }

            if (matchLength == searchData.getDataSize()) {
                HighlightCodeAreaPainter.SearchMatch match = new HighlightCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(searchData.getDataSize());
                foundMatches.add(match);

                if (foundMatches.size() == 100 || !searchParameters.isMultipleMatches()) {
                    break;
                }
            }

            position++;
        }

        painter.setMatches(foundMatches);
        if (foundMatches.size() > 0) {
            painter.setCurrentMatchIndex(0);
            HighlightCodeAreaPainter.SearchMatch firstMatch = painter.getCurrentMatch();
            codeArea.revealPosition(firstMatch.getPosition(), codeArea.getActiveSection());
        }
        hexSearchPanel.setStatus(foundMatches.size(), 0);
        codeArea.repaint();
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    private void loadFromPreferences() {
        deltaMemoryMode = preferences.getBoolean(HexEditorTopComponent.PREFERENCES_MEMORY_DELTA_MODE, true);
        CodeType codeType = CodeType.valueOf(preferences.get(HexEditorTopComponent.PREFERENCES_CODE_TYPE, "HEXADECIMAL"));
        codeArea.setCodeType(codeType);
        codeTypeComboBox.setSelectedIndex(codeType.ordinal());
        String selectedEncoding = preferences.get(HexEditorTopComponent.PREFERENCES_ENCODING_SELECTED, "UTF-8");
        statusPanel.setEncoding(selectedEncoding);
        codeArea.setCharset(Charset.forName(selectedEncoding));
        int bytesPerLine = preferences.getInt(HexEditorTopComponent.PREFERENCES_BYTES_PER_LINE, 16);
        codeArea.setLineLength(bytesPerLine);

        boolean showNonprintables = preferences.getBoolean(HexEditorTopComponent.PREFERENCES_SHOW_UNPRINTABLES, false);
        showUnprintablesToggleButton.setSelected(showNonprintables);
        codeArea.setShowUnprintableCharacters(showNonprintables);

        boolean lineWrapping = preferences.getBoolean(HexEditorTopComponent.PREFERENCES_LINE_WRAPPING, false);
        codeArea.setWrapMode(lineWrapping);
        lineWrappingToggleButton.setSelected(lineWrapping);

        encodingsHandler.loadFromPreferences(preferences);

        // Layout
        codeArea.setShowHeader(preferences.getBoolean(HexEditorTopComponent.PREFERENCES_SHOW_HEADER, true));
        String headerSpaceTypeName = preferences.get(HexEditorTopComponent.PREFERENCES_HEADER_SPACE_TYPE, CodeAreaSpace.SpaceType.HALF_UNIT.name());
        codeArea.setHeaderSpaceType(CodeAreaSpace.SpaceType.valueOf(headerSpaceTypeName));
        codeArea.setHeaderSpaceSize(preferences.getInt(HexEditorTopComponent.PREFERENCES_HEADER_SPACE, 0));
        codeArea.setShowLineNumbers(preferences.getBoolean(HexEditorTopComponent.PREFERENCES_SHOW_LINE_NUMBERS, true));
        String lineNumbersSpaceTypeName = preferences.get(HexEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE_TYPE, CodeAreaSpace.SpaceType.ONE_UNIT.name());
        codeArea.setLineNumberSpaceType(CodeAreaSpace.SpaceType.valueOf(lineNumbersSpaceTypeName));
        codeArea.setLineNumberSpaceSize(preferences.getInt(HexEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE, 8));
        String lineNumbersLengthTypeName = preferences.get(HexEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH_TYPE, CodeAreaLineNumberLength.LineNumberType.SPECIFIED.name());
        codeArea.setLineNumberType(CodeAreaLineNumberLength.LineNumberType.valueOf(lineNumbersLengthTypeName));
        codeArea.setLineNumberSpecifiedLength(preferences.getInt(HexEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH, 8));
        codeArea.setByteGroupSize(preferences.getInt(HexEditorTopComponent.PREFERENCES_BYTE_GROUP_SIZE, 1));
        codeArea.setSpaceGroupSize(preferences.getInt(HexEditorTopComponent.PREFERENCES_SPACE_GROUP_SIZE, 0));

        // Mode
        codeArea.setViewMode(ViewMode.valueOf(preferences.get(HexEditorTopComponent.PREFERENCES_VIEW_MODE, ViewMode.DUAL.name())));
        codeArea.setCodeType(CodeType.valueOf(preferences.get(HexEditorTopComponent.PREFERENCES_CODE_TYPE, CodeType.HEXADECIMAL.name())));
        ((HighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).setNonAsciiHighlightingEnabled(preferences.getBoolean(HexEditorTopComponent.PREFERENCES_CODE_COLORIZATION, true));
        // Memory mode handled from outside by isDeltaMemoryMode() method, worth fixing?

        // Decoration
        codeArea.setBackgroundMode(CodeArea.BackgroundMode.valueOf(preferences.get(HexEditorTopComponent.PREFERENCES_BACKGROUND_MODE, CodeArea.BackgroundMode.STRIPPED.name())));
        codeArea.setLineNumberBackground(preferences.getBoolean(HexEditorTopComponent.PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND, true));
        int decorationMode = (preferences.getBoolean(HexEditorTopComponent.PREFERENCES_DECORATION_HEADER_LINE, true) ? CodeArea.DECORATION_HEADER_LINE : 0)
                + (preferences.getBoolean(HexEditorTopComponent.PREFERENCES_DECORATION_PREVIEW_LINE, true) ? CodeArea.DECORATION_PREVIEW_LINE : 0)
                + (preferences.getBoolean(HexEditorTopComponent.PREFERENCES_DECORATION_BOX, false) ? CodeArea.DECORATION_BOX : 0)
                + (preferences.getBoolean(HexEditorTopComponent.PREFERENCES_DECORATION_LINENUM_LINE, true) ? CodeArea.DECORATION_LINENUM_LINE : 0);
        codeArea.setDecorationMode(decorationMode);
        codeArea.setHexCharactersCase(HexCharactersCase.valueOf(preferences.get(HexEditorTopComponent.PREFERENCES_HEX_CHARACTERS_CASE, HexCharactersCase.UPPER.name())));
        codeArea.setPositionCodeType(PositionCodeType.valueOf(preferences.get(HexEditorTopComponent.PREFERENCES_POSITION_CODE_TYPE, PositionCodeType.HEXADECIMAL.name())));

        // Font
        Boolean useDefaultColor = Boolean.valueOf(preferences.get(TextFontOptionsPanel.PREFERENCES_TEXT_FONT_DEFAULT, Boolean.toString(true)));

        if (!useDefaultColor) {
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
            Font derivedFont = codeArea.getFont().deriveFont(attribs);
            codeArea.setFont(derivedFont);
        }
        boolean showValuesPanel = preferences.getBoolean(HexEditorTopComponent.PREFERENCES_SHOW_VALUES_PANEL, true);
        if (showValuesPanel) {
            showValuesPanel();
        }
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return new JToolBar();
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
//        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
//        if (entry.getDataObject().isModified()) {
//            return this.cos;
//        } else {
            return CloseOperationState.STATE_OK;
//        }
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }

    public static interface CharsetChangeListener {

        public void charsetChanged();
    }
}
