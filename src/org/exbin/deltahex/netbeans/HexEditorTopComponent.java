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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.exbin.deltahex.CodeType;
import org.exbin.deltahex.operation.CodeCommandHandler;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.utils.binary_data.PagedData;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.api.settings.ConvertAsProperties;
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
 * Hexadecimal editor top component.
 *
 * @version 0.1.3 2016/11/09
 * @author ExBin Project (http://exbin.org)
 */
@ConvertAsProperties(dtd = "-//org.exbin.deltahex//HexEditor//EN", autostore = false)
@TopComponent.Description(preferredID = "HexEditorTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(displayName = "#CTL_HexEditorAction", preferredID = "HexEditorTopComponent")
public final class HexEditorTopComponent extends TopComponent implements UndoRedo.Provider {

    private final HexEditorNode node;
    private final CodeArea codeArea;
    private final UndoRedo.Manager undoRedo;
    private final Savable savable;
    private final InstanceContent content = new InstanceContent();
    private final int metaMask;

    private boolean opened = false;
    private boolean modified = false;
    protected String displayName;

    public HexEditorTopComponent() {
        initComponents();

        codeArea = new CodeArea();
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        codeArea.getCaret().setBlinkRate(300);

        undoRedo = new UndoRedo.Manager();
        HexUndoSwingHandler undoHandler = new HexUndoSwingHandler(codeArea, undoRedo);

        codeArea.setData(new PagedData());
        CodeCommandHandler commandHandler = new CodeCommandHandler(codeArea, undoHandler);
        codeArea.setCommandHandler(commandHandler);
        super.add(codeArea, BorderLayout.CENTER);

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

        node = new HexEditorNode(codeArea);
        content.add(node);
        savable = new Savable(this, codeArea);

        setActivatedNodes(new Node[]{node});

        undoHandler.addUndoUpdateListener(new XBUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                codeArea.repaint();
            }

            @Override
            public void undoCommandAdded(final Command command) {
                setModified(true);
            }
        });

        setName(NbBundle.getMessage(HexEditorTopComponent.class, "CTL_HexEditorTopComponent"));
        setToolTipText(NbBundle.getMessage(HexEditorTopComponent.class, "HINT_HexEditorTopComponent"));

        encodingComboBox.setModel(new DefaultComboBoxModel<>(getSupportedEncodings()));
        encodingComboBox.setSelectedItem(codeArea.getCharset().name());
        codeTypeComboBox.setSelectedIndex(codeArea.getCodeType().ordinal());

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

        associateLookup(new AbstractLookup(content));
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

    public void openDataObject(DataObject dataObject) {
        displayName = dataObject.getPrimaryFile().getNameExt();
        setHtmlDisplayName(displayName);
        node.openFile(dataObject);
        savable.setDataObject(dataObject);
        opened = true;

        final Charset charset = FileEncodingQuery.getEncoding(dataObject.getPrimaryFile());
        encodingComboBox.setSelectedItem(charset.name());
        codeArea.setCharset(charset);
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

        infoToolbar = new javax.swing.JPanel();
        encodingLabel = new javax.swing.JLabel();
        encodingComboBox = new javax.swing.JComboBox<>();
        controlToolBar = new javax.swing.JToolBar();
        lineWrappingToggleButton = new javax.swing.JToggleButton();
        showUnprintablesToggleButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        codeTypeComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(encodingLabel, org.openide.util.NbBundle.getMessage(HexEditorTopComponent.class, "HexEditorTopComponent.encodingLabel.text")); // NOI18N

        encodingComboBox.setPreferredSize(new java.awt.Dimension(200, 20));
        encodingComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encodingComboBoxActionPerformed(evt);
            }
        });

        controlToolBar.setBorder(null);
        controlToolBar.setFloatable(false);
        controlToolBar.setRollover(true);

        lineWrappingToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/deltahex/netbeans/resource/icons/deltahex-linewrap.png"))); // NOI18N
        lineWrappingToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(HexEditorTopComponent.class, "HexEditorTopComponent.lineWrappingToggleButton.toolTipText")); // NOI18N
        lineWrappingToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineWrappingToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(lineWrappingToggleButton);

        showUnprintablesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/deltahex/netbeans/resource/icons/insert-pilcrow.png"))); // NOI18N
        showUnprintablesToggleButton.setToolTipText(org.openide.util.NbBundle.getMessage(HexEditorTopComponent.class, "HexEditorTopComponent.showUnprintablesToggleButton.toolTipText")); // NOI18N
        showUnprintablesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUnprintablesToggleButtonActionPerformed(evt);
            }
        });
        controlToolBar.add(showUnprintablesToggleButton);
        controlToolBar.add(jSeparator1);

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
                .addComponent(controlToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        infoToolbarLayout.setVerticalGroup(
            infoToolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(controlToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(infoToolbarLayout.createSequentialGroup()
                .addComponent(encodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(encodingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(infoToolbar, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void encodingComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_encodingComboBoxActionPerformed
        codeArea.setCharset(Charset.forName(encodingComboBox.getSelectedItem().toString()));
    }//GEN-LAST:event_encodingComboBoxActionPerformed

    private void lineWrappingToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineWrappingToggleButtonActionPerformed
        codeArea.setWrapMode(lineWrappingToggleButton.isSelected());
    }//GEN-LAST:event_lineWrappingToggleButtonActionPerformed

    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUnprintablesToggleButtonActionPerformed
        codeArea.setShowUnprintableCharacters(showUnprintablesToggleButton.isSelected());
    }//GEN-LAST:event_showUnprintablesToggleButtonActionPerformed

    private void codeTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codeTypeComboBoxActionPerformed
        codeArea.setCodeType(CodeType.values()[codeTypeComboBox.getSelectedIndex()]);
    }//GEN-LAST:event_codeTypeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> codeTypeComboBox;
    private javax.swing.JToolBar controlToolBar;
    private javax.swing.JComboBox<String> encodingComboBox;
    private javax.swing.JLabel encodingLabel;
    private javax.swing.JPanel infoToolbar;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToggleButton lineWrappingToggleButton;
    private javax.swing.JToggleButton showUnprintablesToggleButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        codeArea.requestFocus();
    }

    @Override
    public void componentClosed() {
        if (savable != null) {
            savable.deactivate();
        }
    }

    public void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    public void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    private String[] getSupportedEncodings() {
        return Charset.availableCharsets().keySet().toArray(new String[0]);
    }

    private JPopupMenu createContextMenu() {
        final JPopupMenu result = new JPopupMenu();

        final JMenuItem cutMenuItem = new JMenuItem("Cut");
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, metaMask));
        cutMenuItem.setEnabled(codeArea.hasSelection());
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
        pasteMenuItem.setEnabled(codeArea.canPaste());
        pasteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.paste();
                result.setVisible(false);
            }
        });
        result.add(pasteMenuItem);

        final JMenuItem pasteFromCodeMenuItem = new JMenuItem("Paste from Code");
        pasteFromCodeMenuItem.setEnabled(codeArea.canPaste());
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
        deleteMenuItem.setEnabled(codeArea.hasSelection());
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

        return result;
    }
}
