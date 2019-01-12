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

import org.exbin.bined.ideplugin.preferences.BinaryEditorPreferences;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
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
import org.exbin.bined.BasicCodeAreaZone;
import org.exbin.bined.CaretPosition;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditationMode;
import org.exbin.bined.EditationOperation;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.capability.RowWrappingCapable.RowWrappingMode;
import org.exbin.bined.delta.DeltaDocument;
import org.exbin.bined.delta.FileDataSource;
import org.exbin.bined.delta.SegmentsRepository;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightCodeAreaPainter;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.netbeans.panel.BinEdOptionsPanelBorder;
import org.exbin.bined.netbeans.panel.BinarySearchPanel;
import org.exbin.bined.ideplugin.panel.ValuesPanel;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.bined.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.panel.BinaryStatusPanel;
import org.exbin.framework.bined.panel.ReplaceParameters;
import org.exbin.framework.bined.panel.SearchCondition;
import org.exbin.framework.bined.panel.SearchParameters;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
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
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.bined.netbeans.panel.BinarySearchPanelApi;
import org.exbin.bined.netbeans.preferences.PreferencesWrapper;
import org.exbin.framework.gui.about.panel.AboutPanel;
import org.exbin.framework.gui.utils.panel.CloseControlPanel;
import org.openide.util.NbPreferences;

/**
 * Hexadecimal editor top component.
 *
 * @version 0.2.0 2019/01/03
 * @author ExBin Project (http://exbin.org)
 */
@ConvertAsProperties(dtd = "-//org.exbin.bined//BinaryEditor//EN", autostore = false)
@TopComponent.Description(preferredID = "BinaryEditorTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(displayName = "#CTL_BinaryEditorAction", preferredID = "BinaryEditorTopComponent")
public final class BinaryEditorTopComponent extends TopComponent implements MultiViewElement, Serializable, UndoRedo.Provider {

    private static final String BINARY_EDITOR_TOP_COMPONENT_STRING = "CTL_BinaryEditorTopComponent";
    private static final String BINARY_EDITOR_TOP_COMPONENT_HINT_STRING = "HINT_BinaryEditorTopComponent";

    public static final String ACTION_CLIPBOARD_CUT = "cut-to-clipboard";
    public static final String ACTION_CLIPBOARD_COPY = "copy-to-clipboard";
    public static final String ACTION_CLIPBOARD_PASTE = "paste-from-clipboard";
    private static final int FIND_MATCHES_LIMIT = 100;

    private final BinaryEditorPreferences preferences;
    private final BinaryEditorNode node;
    private static SegmentsRepository segmentsRepository = null;
    private final ExtCodeArea codeArea;
    private final UndoRedo.Manager undoRedo;
    private final BinaryUndoSwingHandler undoHandler;
    private final Savable savable;
    private final InstanceContent content = new InstanceContent();
    private final int metaMask;

    private BinaryStatusPanel statusPanel;
    private BinaryStatusApi binaryStatus;
    private TextEncodingStatusApi encodingStatus;
    private CharsetChangeListener charsetChangeListener = null;
    private GoToHandler goToHandler;
    private EncodingsHandler encodingsHandler;
    private boolean findTextPanelVisible = false;
    private BinarySearchPanel hexSearchPanel = null;
    private ValuesPanel valuesPanel = null;
    private JScrollPane valuesPanelScrollPane = null;
    private boolean valuesPanelVisible = false;

    private boolean opened = false;
    private boolean modified = false;
    private boolean deltaMemoryMode = false;
    protected String displayName;
    private long documentOriginalSize;
    private DataObject dataObject;

    public BinaryEditorTopComponent() {
        initComponents();

        preferences = new BinaryEditorPreferences(new PreferencesWrapper(NbPreferences.forModule(BinaryEditorPreferences.class)));

        codeArea = new ExtCodeArea();
        codeArea.setPainter(new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea));
        codeArea.setCodeFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.getCaret().setBlinkRate(300);
        statusPanel = new BinaryStatusPanel();
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
                preferences.setSelectedEncoding(encodingName);
            }
        });

        undoRedo = new UndoRedo.Manager();
        undoHandler = new BinaryUndoSwingHandler(codeArea, undoRedo);

        loadFromPreferences();

        getSegmentsRepository();
        setNewData();
        CodeAreaOperationCommandHandler commandHandler = new CodeAreaOperationCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        codeAreaPanel.add(codeArea, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        registerHexStatus(statusPanel);
        goToHandler = new GoToHandler(codeArea);

        codeArea.setComponentPopupMenu(new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                JPopupMenu popupMenu = createContextMenu(x, y);
                popupMenu.show(invoker, x, y);
            }
        });

        node = new BinaryEditorNode(this);
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

        codeArea.addDataChangedListener(() -> {
            if (hexSearchPanel != null && hexSearchPanel.isVisible()) {
                hexSearchPanel.dataChanged();
            }
            updateCurrentDocumentSize();
        });

        setName(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_STRING));
        setToolTipText(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_HINT_STRING));

        applyFromCodeArea();

        getActionMap().put(ACTION_CLIPBOARD_COPY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copy();
            }
        });
        getActionMap().put(ACTION_CLIPBOARD_CUT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.cut();
            }
        });
        getActionMap().put(ACTION_CLIPBOARD_PASTE, new AbstractAction() {
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
        showUnprintablesToggleButton.setSelected(codeArea.isShowUnprintables());
        lineWrappingToggleButton.setSelected(codeArea.getRowWrapping() == RowWrappingCapable.RowWrappingMode.WRAPPING);
    }

    public void registerHexStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        codeArea.addCaretMovedListener((CaretPosition caretPosition) -> {
            String position = String.valueOf(caretPosition.getDataPosition());
            position += ":" + caretPosition.getCodeOffset();
            binaryStatus.setCursorPosition(position);
        });

        codeArea.addEditationModeChangedListener(binaryStatus::setEditationMode);
        binaryStatus.setEditationMode(codeArea.getEditationMode(), codeArea.getEditationOperation());

        binaryStatus.setControlHandler(new BinaryStatusApi.StatusControlHandler() {
            @Override
            public void changeEditationOperation(EditationOperation editationOperation) {
                codeArea.setEditationOperation(editationOperation);
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
            public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
                boolean newDeltaMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE;
                if (newDeltaMode != deltaMemoryMode) {
                    switchDeltaMemoryMode(newDeltaMode);
                    preferences.setDeltaMemoryMode(deltaMemoryMode);
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
                if (codeArea.getContentData() instanceof DeltaDocument) {
                    BinaryData oldData = codeArea.getContentData();
                    PagedData data = new PagedData();
                    data.insert(0, codeArea.getContentData());
                    codeArea.setContentData(data);
                    oldData.dispose();
                } else {
                    BinaryData oldData = codeArea.getContentData();
                    DeltaDocument document = segmentsRepository.createDocument();
                    document.insert(0, oldData);
                    codeArea.setContentData(document);
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
        setCharsetChangeListener(() -> {
            String selectedEncoding = codeArea.getCharset().name();
            encodingStatus.setEncoding(selectedEncoding);
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
                SwingUtilities.invokeAndWait(() -> {
                    setHtmlDisplayName(htmlDisplayName);
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void setNewData() {
        if (deltaMemoryMode) {
            codeArea.setContentData(segmentsRepository.createDocument());
        } else {
            codeArea.setContentData(new PagedData());
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
        long dataSize = codeArea.getContentData().getDataSize();
        long difference = dataSize - documentOriginalSize;
        binaryStatus.setCurrentDocumentSize(dataSize + " (" + (difference > 0 ? "+" + difference : difference) + ")");
    }

    public boolean isDeltaMemoryMode() {
        return deltaMemoryMode;
    }

    public void setDeltaMemoryMode(boolean deltaMemoryMode) {
        this.deltaMemoryMode = deltaMemoryMode;
    }

    private void updateCurrentMemoryMode() {
        BinaryStatusApi.MemoryMode memoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
        if (codeArea.getEditationMode() == EditationMode.READ_ONLY) {
            memoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getContentData() instanceof DeltaDocument) {
            memoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
        }

        if (binaryStatus != null) {
            binaryStatus.setMemoryMode(memoryMode);
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

        lineWrappingToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/netbeans/resources/icons/bined-linewrap.png"))); // NOI18N
        lineWrappingToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(BinaryEditorTopComponent.class, "BinaryEditorTopComponent.lineWrappingToggleButton.toolTipText")); // NOI18N
        lineWrappingToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineWrappingToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(lineWrappingToggleButton);

        showUnprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/bined/netbeans/resources/icons/insert-pilcrow.png"))); // NOI18N
        showUnprintablesToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(BinaryEditorTopComponent.class, "BinaryEditorTopComponent.showUnprintablesToggleButton.toolTipText")); // NOI18N
        showUnprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUnprintablesToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(showUnprintablesToggleButton);
        controlToolBar.add(separator1);

        codeTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BIN", "OCT", "DEC", "HEX" }));
        codeTypeComboBox.setSelectedIndex(3);
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
            .addGroup(infoToolbarLayout.createSequentialGroup()
                .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        codeAreaPanel.add(infoToolbar, java.awt.BorderLayout.PAGE_START);

        add(codeAreaPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void lineWrappingToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineWrappingToggleButtonActionPerformed
        codeArea.setRowWrapping(lineWrappingToggleButton.isSelected() ? RowWrappingCapable.RowWrappingMode.WRAPPING : RowWrappingCapable.RowWrappingMode.NO_WRAPPING);
        preferences.setRowWrapping(lineWrappingToggleButton.isSelected());
    }//GEN-LAST:event_lineWrappingToggleButtonActionPerformed

    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUnprintablesToggleButtonActionPerformed
        codeArea.setShowUnprintables(showUnprintablesToggleButton.isSelected());
        preferences.setShowUnprintables(lineWrappingToggleButton.isSelected());
    }//GEN-LAST:event_showUnprintablesToggleButtonActionPerformed

    private void codeTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codeTypeComboBoxActionPerformed
        CodeType codeType = CodeType.values()[codeTypeComboBox.getSelectedIndex()];
        codeArea.setCodeType(codeType);
        preferences.setCodeType(codeType);
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
        BinaryData data = codeArea.getContentData();
        codeArea.setContentData(new ByteArrayData());
        if (data instanceof DeltaDocument) {
            FileDataSource fileSource = ((DeltaDocument) data).getFileSource();
            data.dispose();
            segmentsRepository.detachFileSource(fileSource);
            segmentsRepository.closeFileSource(fileSource);
        } else {
            if (data != null) {
                data.dispose();
            }
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

    @Nonnull
    private JPopupMenu createContextMenu(int x, int y) {
        final JPopupMenu result = new JPopupMenu();

        BasicCodeAreaZone positionZone = codeArea.getPositionZone(x, y);

        final JMenuItem cutMenuItem = new JMenuItem("Cut");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, metaMask));
        cutMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
        cutMenuItem.addActionListener((ActionEvent e) -> {
            codeArea.cut();
            result.setVisible(false);
        });
        result.add(cutMenuItem);

        final JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, metaMask));
        copyMenuItem.setEnabled(codeArea.hasSelection());
        copyMenuItem.addActionListener((ActionEvent e) -> {
            codeArea.copy();
            result.setVisible(false);
        });
        result.add(copyMenuItem);

        final JMenuItem copyAsCodeMenuItem = new JMenuItem("Copy as Code");
        copyAsCodeMenuItem.setEnabled(codeArea.hasSelection());
        copyAsCodeMenuItem.addActionListener((ActionEvent e) -> {
            codeArea.copyAsCode();
            result.setVisible(false);
        });
        result.add(copyAsCodeMenuItem);

        final JMenuItem pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, metaMask));
        pasteMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
        pasteMenuItem.addActionListener((ActionEvent e) -> {
            codeArea.paste();
            result.setVisible(false);
        });
        result.add(pasteMenuItem);

        final JMenuItem pasteFromCodeMenuItem = new JMenuItem("Paste from Code");
        pasteFromCodeMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
        pasteFromCodeMenuItem.addActionListener((ActionEvent e) -> {
            try {
                codeArea.pasteFromCode();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(codeArea, ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
            }
            result.setVisible(false);
        });
        result.add(pasteFromCodeMenuItem);

        final JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
        deleteMenuItem.addActionListener((ActionEvent e) -> {
            codeArea.delete();
            result.setVisible(false);
        });
        result.add(deleteMenuItem);
        result.addSeparator();

        final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, metaMask));
        selectAllMenuItem.addActionListener((ActionEvent e) -> {
            codeArea.selectAll();
            result.setVisible(false);
        });
        result.add(selectAllMenuItem);
        result.addSeparator();

        final JMenuItem goToMenuItem = new JMenuItem("Go To...");
        goToMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, metaMask));
        goToMenuItem.addActionListener((ActionEvent e) -> {
            goToHandler.getGoToLineAction().actionPerformed(null);
        });
        result.add(goToMenuItem);

        final JMenuItem findMenuItem = new JMenuItem("Find...");
        findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, metaMask));
        findMenuItem.addActionListener((ActionEvent e) -> {
            showSearchPanel(false);
        });
        result.add(findMenuItem);

        final JMenuItem replaceMenuItem = new JMenuItem("Replace...");
        replaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, metaMask));
        replaceMenuItem.setEnabled(codeArea.isEditable());
        replaceMenuItem.addActionListener((ActionEvent e) -> {
            showSearchPanel(true);
        });
        result.add(replaceMenuItem);
        result.addSeparator();
        final JMenuItem optionsMenuItem = new JMenuItem("Options...");
        optionsMenuItem.addActionListener((ActionEvent e) -> {
            final BinEdOptionsPanelBorder optionsPanel = new BinEdOptionsPanelBorder();
            optionsPanel.setFromCodeArea(codeArea);
            optionsPanel.setShowValuesPanel(valuesPanelVisible);
            OptionsControlPanel optionsControlPanel = new OptionsControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(optionsPanel, optionsControlPanel);
            DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, "Options", true, new Object[0], null, 0, null, null);

            final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
            optionsControlPanel.setHandler((OptionsControlHandler.ControlActionType actionType) -> {
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
            });
            WindowUtils.assignGlobalKeyListener(dialog, optionsControlPanel.createOkCancelListener());
            dialog.setSize(650, 460);
            dialog.setVisible(true);
        });
        result.add(optionsMenuItem);
        result.addSeparator();

        final JMenuItem aboutMenuItem = new JMenuItem("About...");
        aboutMenuItem.addActionListener((ActionEvent e) -> {
            AboutPanel aboutPanel = new AboutPanel();
            aboutPanel.setupFields();
            CloseControlPanel closeControlPanel = new CloseControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(aboutPanel, closeControlPanel);
            DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, "About Plugin", true, new Object[0], null, 0, null, null);

            final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
            closeControlPanel.setHandler(() -> {
                WindowUtils.closeWindow(dialog);
            });
            WindowUtils.assignGlobalKeyListener(dialog, closeControlPanel.createOkCancelListener());
            dialog.setSize(650, 460);
            dialog.setVisible(true);
        });
        result.add(aboutMenuItem);

        return result;
    }

    public void showSearchPanel(boolean replace) {
        if (hexSearchPanel == null) {
            hexSearchPanel = new BinarySearchPanel(new BinarySearchPanelApi() {
                @Override
                public void performFind(SearchParameters searchParameters) {
                    ExtendedHighlightCodeAreaPainter painter = (ExtendedHighlightCodeAreaPainter) codeArea.getPainter();
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
                    ExtendedHighlightCodeAreaPainter painter = (ExtendedHighlightCodeAreaPainter) codeArea.getPainter();
                    painter.setCurrentMatchIndex(matchPosition);
                    ExtendedHighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
                    codeArea.revealPosition(new CodeAreaCaretPosition(currentMatch.getPosition(), 0, codeArea.getActiveSection()));
                    codeArea.repaint();
                }

                @Override
                public void updatePosition() {
                    hexSearchPanel.updatePosition(codeArea.getCaretPosition().getDataPosition(), codeArea.getDataSize());
                }

                @Override
                public void performReplace(SearchParameters searchParameters, ReplaceParameters replaceParameters) {
                    SearchCondition replaceCondition = replaceParameters.getCondition();
                    ExtendedHighlightCodeAreaPainter painter = (ExtendedHighlightCodeAreaPainter) codeArea.getPainter();
                    ExtendedHighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();
                    if (currentMatch != null) {
                        EditableBinaryData editableData = ((EditableBinaryData) codeArea.getContentData());
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
                    ExtendedHighlightCodeAreaPainter painter = (ExtendedHighlightCodeAreaPainter) codeArea.getPainter();
                    painter.clearMatches();
                }
            });
            hexSearchPanel.setHexCodePopupMenuHandler(new CodeAreaPopupMenuHandler() {
                @Override
                public JPopupMenu createPopupMenu(ExtCodeArea codeArea, String menuPostfix) {
                    return createCodeAreaPopupMenu(codeArea, menuPostfix);
                }

                @Override
                public void dropPopupMenu(String menuPostfix) {
                }
            });
            hexSearchPanel.setClosePanelListener(this::hideSearchPanel);
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

    private JPopupMenu createCodeAreaPopupMenu(final ExtCodeArea codeArea, String menuPostfix) {
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
        ExtendedHighlightCodeAreaPainter painter = (ExtendedHighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();

        long position = searchParameters.getStartPosition();
        String findText;
        if (searchParameters.isMatchCase()) {
            findText = condition.getSearchText();
        } else {
            findText = condition.getSearchText().toLowerCase();
        }
        BinaryData data = codeArea.getContentData();

        List<ExtendedHighlightCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

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
                ExtendedHighlightCodeAreaPainter.SearchMatch match = new ExtendedHighlightCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(matchLength);
                foundMatches.add(match);

                if (foundMatches.size() == FIND_MATCHES_LIMIT || !searchParameters.isMultipleMatches()) {
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
            ExtendedHighlightCodeAreaPainter.SearchMatch firstMatch = painter.getCurrentMatch();
            codeArea.revealPosition(new CodeAreaCaretPosition(firstMatch.getPosition(), 0, codeArea.getActiveSection()));
        }
        hexSearchPanel.setStatus(foundMatches.size(), 0);
        codeArea.repaint();
    }

    /**
     * Performs search by binary data.
     */
    private void searchForBinaryData(SearchParameters searchParameters) {
        ExtendedHighlightCodeAreaPainter painter = (ExtendedHighlightCodeAreaPainter) codeArea.getPainter();
        SearchCondition condition = searchParameters.getCondition();
        long position = codeArea.getCaretPosition().getDataPosition();
        ExtendedHighlightCodeAreaPainter.SearchMatch currentMatch = painter.getCurrentMatch();

        if (currentMatch != null) {
            if (currentMatch.getPosition() == position) {
                position++;
            }
            painter.clearMatches();
        } else if (!searchParameters.isSearchFromCursor()) {
            position = 0;
        }

        BinaryData searchData = condition.getBinaryData();
        BinaryData data = codeArea.getContentData();

        List<ExtendedHighlightCodeAreaPainter.SearchMatch> foundMatches = new ArrayList<>();

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
                ExtendedHighlightCodeAreaPainter.SearchMatch match = new ExtendedHighlightCodeAreaPainter.SearchMatch();
                match.setPosition(position);
                match.setLength(searchData.getDataSize());
                foundMatches.add(match);

                if (foundMatches.size() == FIND_MATCHES_LIMIT || !searchParameters.isMultipleMatches()) {
                    break;
                }
            }

            position++;
        }

        painter.setMatches(foundMatches);
        if (foundMatches.size() > 0) {
            painter.setCurrentMatchIndex(0);
            ExtendedHighlightCodeAreaPainter.SearchMatch firstMatch = painter.getCurrentMatch();
            codeArea.revealPosition(new CodeAreaCaretPosition(firstMatch.getPosition(), 0, codeArea.getActiveSection()));
        }
        hexSearchPanel.setStatus(foundMatches.size(), 0);
        codeArea.repaint();
    }

    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    private void loadFromPreferences() {
        deltaMemoryMode = preferences.isDeltaMemoryMode();
        CodeType codeType = preferences.getCodeType();
        codeArea.setCodeType(codeType);
        codeTypeComboBox.setSelectedIndex(codeType.ordinal());
        String selectedEncoding = preferences.getSelectedEncoding();
        statusPanel.setEncoding(selectedEncoding);
        codeArea.setCharset(Charset.forName(selectedEncoding));
//        int bytesPerLine = preferences.getInt(BinaryEditorTopComponent.PREFERENCES_BYTES_PER_LINE, 16);
// TODO        codeArea.setLineLength(bytesPerLine);

        boolean showNonprintables = preferences.isShowNonprintables();
        showUnprintablesToggleButton.setSelected(showNonprintables);
        codeArea.setShowUnprintables(showNonprintables);

        boolean lineWrapping = preferences.isRowWrapping();
        codeArea.setRowWrapping(lineWrapping ? RowWrappingMode.WRAPPING : RowWrappingMode.NO_WRAPPING);
        lineWrappingToggleButton.setSelected(lineWrapping);

        encodingsHandler.loadFromPreferences(preferences);

        // Layout
//        ExtendedCodeAreaLayoutProfile layoutProfile = codeArea.getLayoutProfile();
//        layoutProfile.setShowHeader(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_HEADER, true));
//        String headerSpaceTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE_TYPE, CodeAreaSpace.SpaceType.HALF_UNIT.name());
//        codeArea.setHeaderSpaceType(CodeAreaSpace.SpaceType.valueOf(headerSpaceTypeName));
//        codeArea.setHeaderSpaceSize(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_HEADER_SPACE, 0));
//        codeArea.setShowLineNumbers(preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_SHOW_LINE_NUMBERS, true));
//        String lineNumbersSpaceTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE_TYPE, CodeAreaSpace.SpaceType.ONE_UNIT.name());
//        codeArea.setLineNumberSpaceType(CodeAreaSpace.SpaceType.valueOf(lineNumbersSpaceTypeName));
//        codeArea.setLineNumberSpaceSize(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_SPACE, 8));
//        String lineNumbersLengthTypeName = preferences.get(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH_TYPE, CodeAreaLineNumberLength.LineNumberType.SPECIFIED.name());
//        codeArea.setLineNumberType(CodeAreaLineNumberLength.LineNumberType.valueOf(lineNumbersLengthTypeName));
//        codeArea.setLineNumberSpecifiedLength(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_LINE_NUMBERS_LENGTH, 8));
//        codeArea.setByteGroupSize(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_BYTE_GROUP_SIZE, 1));
//        codeArea.setSpaceGroupSize(preferences.getInt(BinaryEditorTopComponent.PREFERENCES_SPACE_GROUP_SIZE, 0));
//        codeArea.setLayoutProfile(layoutProfile);
        // Mode
        codeArea.setViewMode(preferences.getViewMode());
        codeArea.setCodeType(preferences.getCodeType());
        ((ExtendedHighlightNonAsciiCodeAreaPainter) codeArea.getPainter()).setNonAsciiHighlightingEnabled(preferences.isCodeColorization());
        // Memory mode handled from outside by isDeltaMemoryMode() method, worth fixing?
        // Decoration
        ExtendedCodeAreaThemeProfile themeProfile = codeArea.getThemeProfile();
        themeProfile.setBackgroundPaintMode(preferences.getBackgroundPaintMode());
        themeProfile.setPaintRowPosBackground(preferences.isPaintRowPosBackground());
// TODO        int decorationMode = (preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_HEADER_LINE, true) ? CodeArea.DECORATION_HEADER_LINE : 0)
// TODO                + (preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_PREVIEW_LINE, true) ? CodeArea.DECORATION_PREVIEW_LINE : 0)
// TODO                + (preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_BOX, false) ? CodeArea.DECORATION_BOX : 0)
// TODO                + (preferences.getBoolean(BinaryEditorTopComponent.PREFERENCES_DECORATION_LINENUM_LINE, true) ? CodeArea.DECORATION_LINENUM_LINE : 0);
// TODO        codeArea.setDecorationMode(decorationMode);
        codeArea.setThemeProfile(themeProfile);
        codeArea.setCodeCharactersCase(preferences.getCodeCharactersCase());
        codeArea.setPositionCodeType(preferences.getPositionCodeType());

        // Font
        Boolean useDefaultFont = preferences.isUseDefaultFont();

        if (!useDefaultFont) {
            codeArea.setCodeFont(preferences.getCodeFont(codeArea.getCodeFont()));
        }
        boolean showValuesPanel = preferences.isShowValuesPanel();
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
