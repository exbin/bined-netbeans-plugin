/*
 * Copyright (C) ExBin Project, https://exbin.org
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
import org.jspecify.annotations.NullMarked;
import javax.swing.SwingUtilities;
import org.exbin.bined.EditMode;
import org.exbin.bined.netbeans.gui.BinEdFilePanel;
import org.exbin.bined.netbeans.main.BinaryUndoSwingHandler;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.jaguif.App;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.jaguif.component.UndoRedoWrapper;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.bined.jaguif.document.BinEdFileManager;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.bined.jaguif.document.BinedDocumentModule;
import org.exbin.bined.jaguif.document.settings.BinaryFileProcessingOptions;
import org.exbin.jaguif.context.api.ContextStateManagement;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.file.api.FileDocumentSource;
import org.exbin.jaguif.file.api.FileModuleApi;
import org.exbin.jaguif.file.api.SaveModifiedResult;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.netbeans.api.settings.ConvertAsProperties;
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
 */
@ConvertAsProperties(dtd = "-//org.exbin.bined//BinaryEditor//EN", autostore = false)
@TopComponent.Description(preferredID = "BinaryEditorTopComponent", iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(displayName = "#CTL_BinaryEditorAction", preferredID = "BinaryEditorTopComponent")
@NullMarked
public final class BinaryEditorTopComponent extends TopComponent implements Serializable, UndoRedo.Provider {

    protected static final String BINARY_EDITOR_TOP_COMPONENT_STRING = "CTL_BinaryEditorTopComponent";
    protected static final String BINARY_EDITOR_TOP_COMPONENT_HINT_STRING = "HINT_BinaryEditorTopComponent";

    protected DataObject dataObject;
    protected final InstanceContent content = new InstanceContent();

    protected final BinEdFilePanel filePanel;
    protected final BinaryFileDocument fileDocument;
    protected final BinaryUndoSwingHandler undoHandler;

    protected final BinaryEditorNode node;

    protected final BinaryEditorTopComponentSavable savable;
    protected boolean opened = false;
    protected String displayName;

    public BinaryEditorTopComponent() {
        initComponents();

        node = new BinaryEditorNode(this);
        fileDocument = new BinaryFileDocument();
        filePanel = new BinEdFilePanel();
        filePanel.setDocument(fileDocument);
        BinedDocumentModule binedDocumentModule = App.getModule(BinedDocumentModule.class);
        BinEdFileManager fileManager = binedDocumentModule.getFileManager();
        BinEdDataComponent dataComponent = fileDocument.getDataComponent();
        fileManager.initDataComponent(dataComponent);
        undoHandler = new BinaryUndoSwingHandler(fileDocument.getCodeArea(), new UndoRedo.Manager());
        ((UndoRedoWrapper) fileDocument.getUndoHandler().get()).setUndoRedo(undoHandler);
        fileDocument.setUndoHandler(undoHandler);
        // Setting undo handler resets command handler so let's reiniciate - rework later
        fileManager.initCommandHandler(dataComponent);
        savable = new BinaryEditorTopComponentSavable(fileDocument);

        this.add(filePanel, BorderLayout.CENTER);

        content.add(node);

        setActivatedNodes(new Node[]{node});

        setName(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_STRING));
        setToolTipText(NbBundle.getMessage(BinaryEditorTopComponent.class, BINARY_EDITOR_TOP_COMPONENT_HINT_STRING));

        undoHandler.addChangeListener(() -> {
            updateModified();
        });

        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManager = optionsSettingsModule.getMainSettingsManager();
        SettingsOptionsProvider settingsOptionsProvider = settingsManager.getSettingsOptionsProvider();
        fileDocument.applySettings(settingsOptionsProvider);

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        filePanel.loadFromOptions(optionsModule.getAppOptions());

        associateLookup(new AbstractLookup(content));
    }

    public void openDataObject(DataObject dataObject) {
        displayName = dataObject.getPrimaryFile().getNameExt();
        setHtmlDisplayName(displayName);

        this.dataObject = dataObject;
        openFile(fileDocument);
        savable.setDataObject(dataObject);
        opened = true;
    }

    @Override
    public boolean canClose() {
        if (!fileDocument.isModified()) {
            return true;
        }

        FileModuleApi fileModule = App.getModule(FileModuleApi.class);
        SaveModifiedResult result;
//        do {
            result = fileModule.showSaveModified(fileDocument.getComponent());
            if (result == SaveModifiedResult.CANCEL) {
                return false;
            } else if (result == SaveModifiedResult.SAVE) {
                saveFile();
                return true;
            }
//        } while (result != SaveModifiedResult.DISCARD);
        return true;
    }

    private void updateModified() {
        boolean modified = fileDocument.isModified();
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

    public BinaryFileDocument getFileDocument() {
        return fileDocument;
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
        fileDocument.getComponent().requestFocus();
    }

    @Override
    public void componentClosed() {
        if (savable != null) {
            savable.deactivate();
        }
        fileDocument.closeData();
        super.componentClosed();
    }

    public void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    public void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public void componentActivated() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ContextStateManagement contextManager = frameModule.getFrameStateManager();
        BinEdNetBeansDocking docking = (BinEdNetBeansDocking) contextManager.getActiveState(ContextDocking.class);
        docking.setActiveDocument(fileDocument);
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

    public void openFile(BinaryFileDocument fileDocument) {
        CodeAreaCore codeArea = fileDocument.getCodeArea();
        boolean editable = dataObject.getPrimaryFile().canWrite();
        URI fileUri = dataObject.getPrimaryFile().toURI();
        // TODO pass handling mode correctly

        if (fileUri == null) {
            fileDocument.loadFrom(new FileObjectDocumentSource(dataObject.getPrimaryFile()));
        } else {
            ((EditModeCapable) codeArea).setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
            File file = Utilities.toFile(fileUri);
            fileDocument.loadFrom(new FileDocumentSource(file));
        }
    }
    
    public void saveFile() {
        fileDocument.saveTo(fileDocument.getDocumentSource().get());
    }
}
