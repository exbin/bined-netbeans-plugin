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
package org.exbin.bined.netbeans.main;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.ByteArrayData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.auxiliary.paged_data.PagedData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.auxiliary.paged_data.delta.FileDataSource;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditMode;
import org.exbin.bined.netbeans.gui.BinEdComponentPanel;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.gui.BinEdComponentFileApi;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileType;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.InstanceContent;

/**
 * File editor wrapper using BinEd editor component.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFileHandler implements FileHandler, BinEdComponentFileApi {

    private SegmentsRepository segmentsRepository = null;

    private final BinEdEditorComponent editorComponent;

    private DataObject dataObject;

    private final UndoRedo.Manager undoRedo;
    private final InstanceContent content = new InstanceContent();

    public BinEdFileHandler() {
        BinEdManager binEdManager = BinEdManager.getInstance();
        editorComponent = binEdManager.createBinEdEditor();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        undoRedo = new UndoRedo.Manager();
        BinaryUndoSwingHandler undoHandler = new BinaryUndoSwingHandler(codeArea, undoRedo);
        editorComponent.setFileApi(this);
        editorComponent.setUndoHandler(undoHandler);
    }

    public boolean isModified() {
        return editorComponent.isModified();
    }

    public boolean releaseFile() {
        return editorComponent.releaseFile();
    }

    @Nonnull
    public BinEdComponentPanel getComponent() {
        return editorComponent.getComponentPanel();
    }

    @Nonnull
    public InstanceContent getContent() {
        return content;
    }

    @Nonnull
    public UndoRedo.Manager getUndoRedo() {
        return undoRedo;
    }
    
    @Nonnull
    public ExtCodeArea getCodeArea() {
        return editorComponent.getCodeArea();
    }

    @Override
    public int getId() {
        return 0;
    }

    @Nonnull
    @Override
    public Optional<URI> getFileUri() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String getFileName() {
        return null;
    }

    @Nonnull
    @Override
    public Optional<FileType> getFileType() {
        return Optional.empty();
    }

    @Override
    public void setFileType(@Nullable FileType fileType) {
        // ignore
    }

    @Override
    public void newFile() {
        throw new UnsupportedOperationException("Not supported yet.");    }

    @Override
    public void loadFromFile(URI fileUri, @Nullable FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveToFile(URI fileUri, @Nullable FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void openFile(DataObject dataObject) {
        this.dataObject = dataObject;
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        boolean editable = dataObject.getPrimaryFile().canWrite();
        URI fileUri = dataObject.getPrimaryFile().toURI();
        if (fileUri == null) {
            InputStream stream = null;
            try {
                stream = dataObject.getPrimaryFile().getInputStream();
                if (stream != null) {
                    openDocument(stream, editable);
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
            try {
                codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
                File file = Utilities.toFile(fileUri);
                openDocument(file, editable);
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void openDocument(File file, boolean editable) throws IOException {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        FileHandlingMode fileHandlingMode = editorComponent.getFileHandlingMode();

        BinaryData oldData = codeArea.getContentData();
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            FileDataSource fileSource = segmentsRepository.openFileSource(file, editable ? FileDataSource.EditMode.READ_WRITE : FileDataSource.EditMode.READ_ONLY);
            DeltaDocument document = segmentsRepository.createDocument(fileSource);
            editorComponent.setContentData(document);
            if (oldData != null) {
                oldData.dispose();
            }
        } else {
            try ( FileInputStream fileStream = new FileInputStream(file)) {
                BinaryData data = codeArea.getContentData();
                if (!(data instanceof PagedData)) {
                    data = new PagedData();
                    if (oldData != null) {
                        oldData.dispose();
                    }
                }
                ((EditableBinaryData) data).loadFromStream(fileStream);
                editorComponent.setContentData(data);
            }
        }
        codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
    }

    public void openDocument(InputStream stream, boolean editable) throws IOException {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        setNewData();
        EditableBinaryData data = CodeAreaUtils.requireNonNull((EditableBinaryData) codeArea.getContentData());
        data.loadFromStream(stream);
        codeArea.setEditMode(editable ? EditMode.EXPANDING : EditMode.READ_ONLY);
        editorComponent.setContentData(data);
    }

    public void reloadFile() {
        openFile(dataObject);
    }

    public void saveFile() {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            try {
                segmentsRepository.saveDocument((DeltaDocument) data);
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            OutputStream stream;
            try {
                stream = dataObject.getPrimaryFile().getOutputStream();
                try {
                    BinaryData contentData = codeArea.getContentData();
                    if (contentData != null) {
                        contentData.saveToStream(stream);
                    }
                    stream.flush();
                } catch (IOException ex) {
                    Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BinEdFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void closeData() {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        BinaryData data = codeArea.getContentData();
        editorComponent.setContentData(new ByteArrayData());
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

    @Override
    public void saveDocument() {
        if (dataObject == null) {
            return;
        }

        saveFile();
    }

    @Override
    public void switchFileHandlingMode(FileHandlingMode newHandlingMode) {
        FileHandlingMode fileHandlingMode = editorComponent.getFileHandlingMode();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        if (newHandlingMode != fileHandlingMode) {
            // Switch memory mode
            if (dataObject != null) {
                // If document is connected to file, attempt to release first if modified and then simply reload
                if (isModified()) {
                    if (releaseFile()) {
                        openFile(dataObject);
                        codeArea.clearSelection();
                        codeArea.setCaretPosition(0);
                        editorComponent.setFileHandlingMode(newHandlingMode);
                    }
                } else {
                    editorComponent.setFileHandlingMode(newHandlingMode);
                    openFile(dataObject);
                }
            } else {
                // If document unsaved in memory, switch data in code area
                if (codeArea.getContentData() instanceof DeltaDocument) {
                    BinaryData oldData = codeArea.getContentData();
                    PagedData data = new PagedData();
                    data.insert(0, codeArea.getContentData());
                    editorComponent.setContentData(data);
                    if (oldData != null) {
                        oldData.dispose();
                    }
                } else {
                    BinaryData oldData = codeArea.getContentData();
                    DeltaDocument document = segmentsRepository.createDocument();
                    if (oldData != null) {
                        document.insert(0, oldData);
                        oldData.dispose();
                    }
                    editorComponent.setContentData(document);
                }

                editorComponent.getUndoHandler().clear();
                editorComponent.setFileHandlingMode(newHandlingMode);
            }
        }
    }

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    private void setNewData() {
        FileHandlingMode fileHandlingMode = editorComponent.getFileHandlingMode();
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            editorComponent.setContentData(segmentsRepository.createDocument());
        } else {
            editorComponent.setContentData(new PagedData());
        }
    }

    public void setSegmentsRepository(SegmentsRepository segmentsRepository) {
        this.segmentsRepository = segmentsRepository;
    }

    public void setModifiedChangeListener(BinEdEditorComponent.ModifiedStateListener modifiedChangeListener) {
        editorComponent.setModifiedChangeListener(modifiedChangeListener);
    }

    public void requestFocus() {
        editorComponent.getCodeArea().requestFocus();
    }
}
