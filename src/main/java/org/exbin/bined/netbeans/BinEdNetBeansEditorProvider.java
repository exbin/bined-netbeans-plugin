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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.swing.CodeAreaCore;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.bined.BinEdEditorProvider;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.MultiEditorProvider;
import org.exbin.framework.file.api.EditableFileHandler;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.file.api.FileOperations;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.operation.undo.api.UndoRedoState;
import org.exbin.framework.text.encoding.TextEncodingStatusApi;
import org.exbin.framework.action.api.clipboard.ClipboardSupported;
import org.exbin.framework.action.api.clipboard.TextClipboardSupported;

/**
 * Editor provider wrapper for NetBeans BinEd editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdNetBeansEditorProvider implements MultiEditorProvider, BinEdEditorProvider {

    protected final List<FileHandler> fileHandlers = new ArrayList<>();

    @Nullable
    protected BinEdFileHandler activeFile = null;
    private BinaryStatusApi binaryStatus;
    private TextEncodingStatusApi textEncodingStatusApi;

    public BinEdNetBeansEditorProvider() {
    }

    @Nonnull
    @Override
    public List<FileHandler> getFileHandlers() {
        return fileHandlers;
    }

    @Nonnull
    @Override
    public String getName(FileHandler fileHandler) {
        return fileHandler.getTitle();
    }

    @Override
    public void saveFile(FileHandler fileHandler) {
        ((BinEdFileHandler) fileHandler).saveFile();
    }

    @Override
    public void saveAsFile(FileHandler fileHandler) {

    }

    @Override
    public void closeFile() {

    }

    @Override
    public void closeFile(FileHandler fileHandler) {

    }

    @Override
    public void closeOtherFiles(FileHandler fileHandler) {

    }

    @Override
    public void closeAllFiles() {

    }

    @Override
    public void saveAllFiles() {

    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        if (activeFile != null) {
            return activeFile.getComponent();
        }

        throw new IllegalStateException("Unsupported file handler");
    }

    public void addFile(BinEdFileHandler fileHandler) {
        fileHandlers.add(fileHandler);


        SectCodeArea codeArea = fileHandler.getCodeArea();
        codeArea.addDataChangedListener(() -> {
            activeFile.getComponent().notifyDataChanged();
            updateCurrentDocumentSize();
        });

        codeArea.addSelectionChangedListener(() -> {
            binaryStatus.setSelectionRange(codeArea.getSelection());
            updateClipboardActionsStatus();
        });

        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });

        codeArea.addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            binaryStatus.setEditMode(mode, operation);
        });

        EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
        editorModule.notifyEditorComponentCreated(fileHandler.getComponent());
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.ofNullable(activeFile);
    }

    public void setActiveFile(@Nullable FileHandler fileHandler) {
        if (activeFile != fileHandler) {
            activeFile = (BinEdFileHandler) fileHandler;
            activeFileChanged();
        }
    }

    public void activeFileChanged() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ComponentActivationListener componentActivationListener =
                frameModule.getFrameHandler().getComponentActivationListener();

        SectCodeArea extCodeArea = null;
        TextClipboardSupported clipboardActionsHandler = null;
        UndoRedoState undoRedo = null;
        if (activeFile instanceof BinEdFileHandler) {
            BinEdFileHandler binEdFileHandler = (BinEdFileHandler) activeFile;
            extCodeArea = binEdFileHandler.getCodeArea();
            undoRedo = binEdFileHandler.getUndoRedo().orElse(null);
            clipboardActionsHandler = binEdFileHandler.getClipboardActionsController();
        }

        componentActivationListener.updated(FileHandler.class, activeFile);
        componentActivationListener.updated(FileOperations.class, this);
        componentActivationListener.updated(CodeAreaCore.class, extCodeArea);
        componentActivationListener.updated(UndoRedoState.class, undoRedo);
        componentActivationListener.updated(ClipboardSupported.class, clipboardActionsHandler);

        //        if (this.undoHandler != null) {
//            this.undoHandler.setActiveFile(this.activeFile);
//        }
    }

    @Nonnull
    @Override
    public String getWindowTitle(String windowTitle) {
        if (activeFile == null) {
            return windowTitle;
        }

        return windowTitle + " - " + activeFile.getTitle();
    }

    @Override
    public void openFile(URI uri, FileType fileType) {

    }

    @Override
    public void newFile() {

    }

    @Override
    public void openFile() {

    }

    @Override
    public void saveFile() {

    }

    @Override
    public void saveAsFile() {

    }

    @Override
    public boolean canSave() {
        if (activeFile == null) {
            return false;
        }

        return activeFile.canSave();
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler instanceof EditableFileHandler && ((EditableFileHandler) fileHandler).isModified()) {
            FileModuleApi fileModule = App.getModule(FileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, null, this);
        }

        return true;
    }

    @Override
    public boolean releaseAllFiles() {
        return false;
    }

    @Override
    public void loadFromFile(String s) throws URISyntaxException {

    }

    @Override
    public void loadFromFile(URI uri, @Nullable FileType fileType) {

    }

    @Nonnull
    @Override
    public Optional<File> getLastUsedDirectory() {
        return Optional.empty();
    }

    @Override
    public void setLastUsedDirectory(@Nullable File file) {

    }

    @Override
    public void updateRecentFilesList(URI uri, FileType fileType) {

    }
    
    @Override
    public void registerUndoHandler() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        updateStatus();
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        this.textEncodingStatusApi = encodingStatus;
        updateCurrentEncoding();
    }

    public void updateCurrentEncoding() {
        if (textEncodingStatusApi == null) {
            return;
        }

        textEncodingStatusApi.setEncoding(activeFile.getTextEncodingHandler().getCharset().name());
    }

    @Override
    public void updateStatus() {
        updateCurrentDocumentSize();
        updateCurrentCaretPosition();
        updateCurrentSelectionRange();
        updateCurrentMemoryMode();
        updateCurrentEditMode();

        if (textEncodingStatusApi != null) {
            updateCurrentEncoding();
        }
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = activeFile.getCodeArea();
        long documentOriginalSize = activeFile.getDocumentOriginalSize();
        long dataSize = codeArea.getDataSize();
        binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = activeFile.getCodeArea();
        CodeAreaCaretPosition caretPosition = codeArea.getActiveCaretPosition();
        binaryStatus.setCursorPosition(caretPosition);
    }

    private void updateCurrentSelectionRange() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = activeFile.getCodeArea();
        SelectionRange selectionRange = codeArea.getSelection();
        binaryStatus.setSelectionRange(selectionRange);
    }

    private void updateCurrentMemoryMode() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = activeFile.getCodeArea();
        BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
        if (((EditModeCapable) codeArea).getEditMode() == EditMode.READ_ONLY) {
            newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getContentData() instanceof DeltaDocument) {
            newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
        }

        binaryStatus.setMemoryMode(newMemoryMode);
    }

    private void updateCurrentEditMode() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = activeFile.getCodeArea();
        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
    }

    private void updateClipboardActionsStatus() {
        // ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        // TODO ((ClipboardActionsUpdater) actionModule.getClipboardActions()).updateClipboardActions();
    }
}
