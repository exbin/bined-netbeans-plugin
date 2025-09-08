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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.exbin.bined.netbeans.main.BinaryUndoSwingHandler;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.UndoRedoWrapper;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditor;
import org.openide.text.DataEditorSupport;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * BinEd native NetBeans editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@MultiViewElement.Registration(
        displayName = "#BinEdEditor.displayName",
        mimeType = BinEdDataObject.MIME_TYPE,
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        preferredID = BinEdEditor.ELEMENT_ID,
        position = BinEdEditor.POSITION_ATTRIBUTE
)
@ParametersAreNonnullByDefault
public class BinEdEditor extends CloneableEditor implements MultiViewElement, HelpCtx.Provider {

    public static final String ELEMENT_ID = "org.exbin.bined.netbeans.BinEdEditor";
    public static final String ELEMENT_NAME = "org-exbin-bined-netbeans-BinEdEditor";
    public static final int POSITION_ATTRIBUTE = 900005; // Between "Source" and "History"

    protected BinaryEditorTopComponent editorComponent;
    protected transient MultiViewElementCallback callback;
    protected final Lookup lookup;

    public BinEdEditor(Lookup lookup) {
        this.lookup = lookup;
        
        // View could be restored before module is installed
        Installer.initBinEd();
    }

    @Nonnull
    @Override
    public JComponent getVisualRepresentation() {
        if (editorComponent == null) {
            editorComponent = new BinaryEditorTopComponent();
        }
        return editorComponent;
    }

    @Nonnull
    @Override
    public JComponent getToolbarRepresentation() {
        return new JPanel(); // TODO editorFile.getComponent().getToolbarPanel().getToolBar();
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        if (!editorComponent.getFileHandler().isModified()) {
            return CloseOperationState.STATE_OK;
        }

        AbstractAction saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        };
        DataObject dataObject = lookup.lookup(DataObject.class);
        saveAction.putValue(Action.LONG_DESCRIPTION, String.format("File %s is modified. Save?", dataObject.getPrimaryFile().getNameExt()));
        return MultiViewFactory.createUnsafeCloseState("editor", saveAction, MultiViewFactory.NOOP_CLOSE_ACTION);
    }

    @Nonnull
    @Override
    public Action[] getActions() {
        if (callback != null) {
            return callback.createDefaultActions();
        }

        return new Action[0];
    }

    @Nonnull
    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentActivated() {
        editorComponent.componentActivated();
//        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
//        ComponentActivationListener componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();
//        fileHandler.componentActivated(componentActivationListener);
    }

    @Override
    public void componentDeactivated() {
        editorComponent.componentDeactivated();
//        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
//        ComponentActivationListener componentActivationListener = frameModule.getFrameHandler().getComponentActivationListener();
//        fileHandler.componentDeactivated(componentActivationListener);
    }

    @Override
    public void componentOpened() {
        DataObject dataObject = lookup.lookup(DataObject.class);
        if (dataObject != null) {
            if (dataObject instanceof BinEdDataObject) {
                // TODO: Workaround for using multiview, find proper solution later
                ((BinEdDataObject) dataObject).setSaveAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveFile();
                    }
                });
            }

            openFile(dataObject);
            dataObject.getPrimaryFile().addFileChangeListener(new FileChangeAdapter() {
                @Override
                public void fileChanged(FileEvent fe) {
                    openFile(dataObject);
                }
            });

            if (callback != null) {
                callback.updateTitle(dataObject.getPrimaryFile().getNameExt());
            }
        }
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentClosed() {
        editorComponent.getFileHandler().closeData();
    }

    @Nullable
    @Override
    public UndoRedo getUndoRedo() {
        UndoRedoWrapper undoWrapper = (UndoRedoWrapper) (editorComponent.getFileHandler().getUndoRedo().orElse(null));
        return undoWrapper != null ? ((BinaryUndoSwingHandler) undoWrapper.getUndoRedo()).getUndoManager() : null;
    }

    @Nonnull
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public void openFile(DataObject dataObject) {
        BinEdFileHandler fileHandler = editorComponent.getFileHandler();
        SectCodeArea codeArea = fileHandler.getCodeArea();
        boolean editable = dataObject.getPrimaryFile().canWrite();
//        URI fileUri = dataObject.getPrimaryFile().toURI();
//        if (fileUri == null) {
            InputStream stream = null;
            try {
                stream = dataObject.getPrimaryFile().getInputStream();
                if (stream != null) {
                    fileHandler.loadFromStream(stream);
                }
            } catch (IOException ex) {
                Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
//        } else {
//            codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
//            File file = Utilities.toFile(fileUri);
//            fileHandler.loadFromFile(file.toURI(), null);
//        }
        fileHandler.fileSync();
        editorComponent.updateStatus();
    }

    public void saveFile() {
//        try {
//            getEditorSupport().saveDocument();
//        } catch (IOException ex) {
//            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        editorComponent.getFileHandler().saveFile();
        DataObject dataObject = lookup.lookup(DataObject.class);
        BinEdFileHandler fileHandler = editorComponent.getFileHandler();
        OutputStream stream = null;
        try {
            stream = dataObject.getPrimaryFile().getOutputStream();
            if (stream != null) {
                fileHandler.saveToStream(stream);
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Logger.getLogger(BinEdEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        fileHandler.fileSync();
        editorComponent.updateStatus();
    }

    private DataEditorSupport getEditorSupport() {
        return (DataEditorSupport) cloneableEditorSupport();
    }
}
