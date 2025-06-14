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
package org.exbin.bined.netbeans;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.exbin.bined.EditMode;
import org.exbin.bined.netbeans.gui.BinEdFilePanel;
import org.exbin.bined.netbeans.main.BinaryUndoSwingHandler;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.UndoRedoWrapper;
import org.exbin.framework.bined.editor.options.BinaryEditorOptions;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 * Binary editor top component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ConvertAsProperties(dtd = "-//org.exbin.bined//BinaryEditor//EN", autostore = false)
@TopComponent.Description(preferredID = "BinaryEditorTopComponent", iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(displayName = "#CTL_BinaryEditorAction", preferredID = "BinaryEditorTopComponent")
@ParametersAreNonnullByDefault
public final class BinaryEditorTopComponent extends TopComponent implements MultiViewElement, Serializable, UndoRedo.Provider {

    private static final String BINARY_EDITOR_TOP_COMPONENT_STRING = "CTL_BinaryEditorTopComponent";
    private static final String BINARY_EDITOR_TOP_COMPONENT_HINT_STRING = "HINT_BinaryEditorTopComponent";

    private DataObject dataObject;
    private final InstanceContent content = new InstanceContent();

    private final BinEdFilePanel filePanel;
    private final BinEdFileHandler fileHandler;
    private final BinaryUndoSwingHandler undoHandler;

    private final BinaryEditorNode node;

    private final BinaryEditorTopComponentSavable savable;
    private boolean opened = false;
    protected String displayName;

    public BinaryEditorTopComponent() {
        initComponents();

        node = new BinaryEditorNode(this);
        fileHandler = new BinEdFileHandler();
        filePanel = new BinEdFilePanel();
        filePanel.setFileHandler(fileHandler);
        BinedModule binedModule = App.getModule(BinedModule.class);
        binedModule.getFileManager().initFileHandler(fileHandler);

        undoHandler = new BinaryUndoSwingHandler(fileHandler.getCodeArea(), new UndoRedo.Manager());
        ((UndoRedoWrapper) fileHandler.getUndoRedo().get()).setUndoRedo(undoHandler);
        fileHandler.getComponent().setUndoRedo(undoHandler);
        // Setting undo handler resets command handler so let's reiniciate - rework later
        binedModule.getFileManager().initCommandHandler(fileHandler.getComponent());
        savable = new BinaryEditorTopComponentSavable(fileHandler);

        this.add(filePanel, BorderLayout.CENTER);

        content.add(node);

        setActivatedNodes(new Node[]{node});

        setName(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_STRING));
        setToolTipText(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_HINT_STRING));

        // TODO
//        editorFile.setModifiedChangeListener(() -> {
//            updateModified();
//        });

        associateLookup(new AbstractLookup(content));
    }

    public void openDataObject(DataObject dataObject) {
        displayName = dataObject.getPrimaryFile().getNameExt();
        setHtmlDisplayName(displayName);

        this.dataObject = dataObject;
        openFile(fileHandler);
        savable.setDataObject(dataObject);
        opened = true;
    }

    @Override
    public boolean canClose() {
        if (!fileHandler.isModified()) {
            return true;
        }

        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        return fileModule.getFileActions().showAskForSaveDialog(fileHandler, null, null);
    }

    private void updateModified() {
        boolean modified = fileHandler.isModified();
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
                Logger.getLogger(BinaryEditorTopComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public UndoRedo getUndoRedo() {
        return undoHandler.getUndoManager();
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
        fileHandler.requestFocus();
    }

    @Override
    public void componentClosed() {
        if (savable != null) {
            savable.deactivate();
        }
        fileHandler.closeData();
        super.componentClosed();
    }

    public void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    public void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return new JLabel("Test");
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
//        this.callback = callback;
    }

    @Nonnull
    @Override
    public CloseOperationState canCloseElement() {
        boolean modified = fileHandler.isModified();
        if (modified) {
            throw new UnsupportedOperationException("Not supported yet.");
//            return new CloseOperationState();
        } else {
            return CloseOperationState.STATE_OK;
        }
    }

    @Override
    public void componentActivated() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        ((BinEdNetBeansEditorProvider) binedModule.getEditorProvider()).setActiveFile(fileHandler);
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

    public void openFile(BinEdFileHandler fileHandler) {
        SectCodeArea codeArea = fileHandler.getCodeArea();
        boolean editable = dataObject.getPrimaryFile().canWrite();
        URI fileUri = dataObject.getPrimaryFile().toURI();
        // TODO pass handling mode correctly
        
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        OptionsStorage optionsStorage = preferencesModule.getAppPreferences();
        BinaryEditorOptions editorOptions = new BinaryEditorOptions(optionsStorage);
        fileHandler.setNewData(editorOptions.getFileHandlingMode());
        if (fileUri == null) {
            InputStream stream = null;
            try {
                stream = dataObject.getPrimaryFile().getInputStream();
                if (stream != null) {
                    fileHandler.loadFromStream(stream);
                }
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
            File file = Utilities.toFile(fileUri);
            fileHandler.loadFromFile(file.toURI(), null);
        }

        BinaryStatusPanel statusPanel = filePanel.getStatusPanel();
        statusPanel.setCurrentDocumentSize(fileHandler.getCodeArea().getDataSize(), fileHandler.getDocumentOriginalSize());
        statusPanel.updateStatus();
    }
}
