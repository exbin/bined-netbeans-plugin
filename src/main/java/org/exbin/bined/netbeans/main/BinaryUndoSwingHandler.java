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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.undo.BinaryDataUndoRedoChangeListener;
import org.exbin.bined.operation.undo.BinaryDataUndoRedo;
import org.exbin.bined.operation.undo.BinaryDataUndoableCommand;
import org.exbin.bined.swing.CodeAreaCore;
import org.openide.awt.UndoRedo;

/**
 * Undo handler for binary editor using AWT undo.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryUndoSwingHandler implements BinaryDataUndoRedo {

    private final CodeAreaCore codeArea;
    private final List<BinaryDataUndoRedoChangeListener> listeners = new ArrayList<>();
    private final UndoRedo.Manager undoManager;
    private int commandPosition;
    private int syncPointPosition = -1;

    /**
     * Creates a new instance.
     *
     * @param codeArea code area component
     * @param undoManager undo manager
     */
    public BinaryUndoSwingHandler(CodeAreaCore codeArea, UndoRedo.Manager undoManager) {
        this.codeArea = codeArea;
        this.undoManager = undoManager;
        init();
    }

    private void init() {
        commandPosition = 0;
        setSyncPosition(0);
    }

    /**
     * Adds new step into revert list.
     *
     * @param command command
     */
    @Override
    public void execute(BinaryDataCommand command) {
        command.execute();
        commandAdded(command);
    }

    private void commandAdded(final BinaryDataCommand command) {
        UndoableEdit edit = new UndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                commandPosition--;
                ((BinaryDataUndoableCommand) command).undo();
                undoUpdated();
            }

            @Override
            public boolean canUndo() {
                return undoManager.canUndo();
            }

            @Override
            public void redo() throws CannotRedoException {
                commandPosition++;
                ((BinaryDataUndoableCommand) command).redo();
                undoUpdated();
            }

            @Override
            public boolean canRedo() {
                return undoManager.canRedo();
            }

            @Override
            public void die() {
            }

            @Override
            public boolean addEdit(UndoableEdit anEdit) {
                return false;
            }

            @Override
            public boolean replaceEdit(UndoableEdit anEdit) {
                return false;
            }

            @Override
            public boolean isSignificant() {
                return true;
            }

            @Nonnull
            @Override
            public String getPresentationName() {
                return command.getName();
            }

            @Nonnull
            @Override
            public String getUndoPresentationName() {
                return "";
            }

            @Nonnull
            @Override
            public String getRedoPresentationName() {
                return "";
            }
        };
        undoManager.undoableEditHappened(new UndoableEditEvent(codeArea, edit));

        commandPosition++;
        undoUpdated();
        listeners.forEach((listener) -> listener.undoChanged());
    }

    /**
     * Performs single undo step.
     */
    @Override
    public void performUndo() {
        performUndoInt();
        undoUpdated();
    }

    private void performUndoInt() {
        undoManager.undo();
    }

    /**
     * Performs single redo step.
     */
    @Override
    public void performRedo() {
        performRedoInt();
        undoUpdated();
    }

    private void performRedoInt() {
        undoManager.redo();
    }

    /**
     * Performs multiple undo step.
     *
     * @param count count of steps
     */
    @Override
    public void performUndo(int count) {
        for (int i = 0; i < count; i++) {
            performUndo();
        }
    }

    /**
     * Performs multiple redo step.
     *
     * @param count count of steps
     */
    @Override
    public void performRedo(int count) {
        for (int i = 0; i < count; i++) {
            performRedo();
        }
    }

    @Override
    public void clear() {
        init();
    }

    @Override
    public boolean canUndo() {
        return undoManager.canUndo();
    }

    @Override
    public boolean canRedo() {
        return undoManager.canRedo();
    }

    @Override
    public int getCommandPosition() {
        return commandPosition;
    }

    /**
     * Performs revert to sync point.
     */
    @Override
    public void performSync() {
        setCommandPosition(syncPointPosition);
    }

    @Nonnull
    public UndoRedo.Manager getUndoManager() {
        return undoManager;
    }

    @Override
    public int getSyncPosition() {
        return syncPointPosition;
    }

    @Override
    public void setSyncPosition(int syncPosition) {
        this.syncPointPosition = syncPosition;
    }

    @Override
    public void setSyncPosition() {
        this.syncPointPosition = commandPosition;
    }

    /**
     * Performs undo or redo operation to reach given position.
     *
     * @param targetPosition desired position
     */
    public void setCommandPosition(int targetPosition) {
        if (targetPosition < commandPosition) {
            performUndo(commandPosition - targetPosition);
        } else if (targetPosition > commandPosition) {
            performRedo(targetPosition - commandPosition);
        }
    }

    private void undoUpdated() {
        codeArea.notifyDataChanged();
        listeners.forEach((listener) -> listener.undoChanged());
    }

    @Nonnull
    @Override
    public List<BinaryDataCommand> getCommandList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<BinaryDataCommand> getTopUndoCommand() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCommandsCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isModified() {
        return commandPosition != syncPointPosition;
    }

    @Override
    public void addChangeListener(BinaryDataUndoRedoChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeChangeListener(BinaryDataUndoRedoChangeListener listener) {
        listeners.remove(listener);
    }
}
