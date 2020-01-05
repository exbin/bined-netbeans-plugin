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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.auxiliary.paged_data.PagedData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.auxiliary.paged_data.delta.FileDataSource;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.EditationMode;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.netbeans.panel.BinEdComponentPanel;
import org.exbin.framework.bined.FileHandlingMode;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Binary editor top component.
 *
 * @version 0.2.2 2020/01/05
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
    private final Image editorIcon = new ImageIcon(getClass().getResource("/org/exbin/bined/netbeans/resources/icons/icon.png")).getImage();

    private final BinEdComponentPanel componentPanel;

    private final BinaryEditorNode node;

    private final UndoRedo.Manager undoRedo;
    private final Savable savable;
    private final InstanceContent content = new InstanceContent();

    private boolean opened = false;
    protected String displayName;
    private DataObject dataObject;

    public BinaryEditorTopComponent() {
        initComponents();

        componentPanel = new BinEdComponentPanel();
        undoRedo = new UndoRedo.Manager();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryUndoSwingHandler undoHandler = new BinaryUndoSwingHandler(codeArea, undoRedo);
        componentPanel.setUndoHandler(undoHandler);
        add(componentPanel, BorderLayout.CENTER);

        node = new BinaryEditorNode(this);
        content.add(node);
        savable = new Savable(this, codeArea);

        setActivatedNodes(new Node[]{node});

        setName(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_STRING));
        setToolTipText(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_HINT_STRING));

        componentPanel.setModifiedChangeListener(() -> {
            updateModified();
        });

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

        associateLookup(new AbstractLookup(content));
    }

    @Override
    public boolean canClose() {
        if (!componentPanel.isModified()) {
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

    private void updateModified() {
        boolean modified = componentPanel.isModified();
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

    /**
     * Attempts to release current file and warn if document was modified.
     *
     * @return true if successful
     */
    private boolean releaseFile() {

        if (dataObject == null) {
            return true;
        }

        while (componentPanel.isModified()) {
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
        componentPanel.openDataObject(dataObject);
        this.dataObject = dataObject;
        displayName = dataObject.getPrimaryFile().getNameExt();
        setHtmlDisplayName(displayName);
        setIcon(editorIcon);
        openFile(dataObject);
        savable.setDataObject(dataObject);
        opened = true;

//        final Charset charset = Charset.forName(FileEncodingQuery.getEncoding(dataObject.getPrimaryFile()).name());
//        if (charsetChangeListener != null) {
//            charsetChangeListener.charsetChanged();
//        }
//        codeArea.setCharset(charset);
    }

    private void openFile(DataObject dataObject) {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        boolean editable = dataObject.getPrimaryFile().canWrite();
        SegmentsRepository segmentsRepository = BinEdComponentPanel.getSegmentsRepository();
        URI fileUri = dataObject.getPrimaryFile().toURI();
        if (fileUri == null) {
            InputStream stream = null;
            try {
                stream = dataObject.getPrimaryFile().getInputStream();
                if (stream != null) {
                    ((EditableBinaryData) codeArea.getContentData()).loadFromStream(stream);
                    codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } else {
            try {
                BinaryData oldData = codeArea.getContentData();
                codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
                File file = new File(fileUri);
                if (componentPanel.getFileHandlingMode() == FileHandlingMode.DELTA) {
                    FileDataSource fileSource = segmentsRepository.openFileSource(file, editable ? FileDataSource.EditationMode.READ_WRITE : FileDataSource.EditationMode.READ_ONLY);
                    DeltaDocument document = segmentsRepository.createDocument(fileSource);
                    codeArea.setContentData(document);
                    oldData.dispose();
                } else {
                    try (FileInputStream fileStream = new FileInputStream(file)) {
                        BinaryData data = codeArea.getContentData();
                        if (!(data instanceof PagedData)) {
                            data = new PagedData();
                            oldData.dispose();
                        }
                        ((EditableBinaryData) data).loadFromStream(fileStream);
                        codeArea.setContentData(data);
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void saveDataObject(DataObject dataObject) throws IOException {
        componentPanel.saveDataObject(dataObject);
        saveFile(dataObject);
        updateModified();
    }

    public void saveFile(DataObject dataObject) {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        SegmentsRepository segmentsRepository = BinEdComponentPanel.getSegmentsRepository();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            try {
                segmentsRepository.saveDocument((DeltaDocument) data);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            OutputStream stream;
            try {
                stream = dataObject.getPrimaryFile().getOutputStream();
                try {
                    codeArea.getContentData().saveToStream(stream);
                    stream.flush();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        super.componentOpened();
        componentPanel.getCodeArea().requestFocus();
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
        componentPanel.closeData();
    }

    public void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    public void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    public ExtCodeArea getCodeArea() {
        return componentPanel.getCodeArea();
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
}
