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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.bined.swing.CodeAreaCore;
import org.openide.awt.UndoRedo;

/**
 * Undo handler for binary editor using AWT undo.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryUndoSwingHandler implements BinaryDataUndoHandler {

    private final CodeAreaCore codeArea;
    private final List<BinaryDataUndoUpdateListener> listeners = new ArrayList<>();
    private final UndoRedo.Manager undoManager;
    private long commandPosition;
    private long syncPointPosition = -1;

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
        setSyncPoint(0);
    }

    /**
     * Adds new step into revert list.
     *
     * @param command command
     * @throws BinaryDataOperationException if commands throws it
     */
    @Override
    public void execute(BinaryDataCommand command) throws BinaryDataOperationException {
        command.execute();
        commandAdded(command);
    }

    @Override
    public void addCommand(BinaryDataCommand command) {
        command.use();
        commandAdded(command);
    }

    private void commandAdded(final BinaryDataCommand command) {
        UndoableEdit edit = new UndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                commandPosition--;
                try {
                    command.undo();
                } catch (BinaryDataOperationException ex) {
                    Logger.getLogger(BinaryUndoSwingHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                undoUpdated();
            }

            @Override
            public boolean canUndo() {
                return command.canUndo();
            }

            @Override
            public void redo() throws CannotRedoException {
                commandPosition++;
                try {
                    command.redo();
                } catch (BinaryDataOperationException ex) {
                    Logger.getLogger(BinaryUndoSwingHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                undoUpdated();
            }

            @Override
            public boolean canRedo() {
                return command.canUndo();
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
                return command.getCaption();
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
        listeners.forEach((listener) -> listener.undoCommandAdded(command));
    }

    /**
     * Performs single undo step.
     *
     * @throws BinaryDataOperationException if commands throws it
     */
    @Override
    public void performUndo() throws BinaryDataOperationException {
        performUndoInt();
        undoUpdated();
    }

    private void performUndoInt() throws BinaryDataOperationException {
        undoManager.undo();
    }

    /**
     * Performs single redo step.
     *
     * @throws BinaryDataOperationException if commands throws it
     */
    @Override
    public void performRedo() throws BinaryDataOperationException {
        performRedoInt();
        undoUpdated();
    }

    private void performRedoInt() throws BinaryDataOperationException {
        undoManager.redo();
    }

    /**
     * Performs multiple undo step.
     *
     * @param count count of steps
     * @throws BinaryDataOperationException if commands throws it
     */
    @Override
    public void performUndo(int count) throws BinaryDataOperationException {
        for (int i = 0; i < count; i++) {
            performUndo();
        }
    }

    /**
     * Performs multiple redo step.
     *
     * @param count count of steps
     * @throws BinaryDataOperationException if commands throws it
     */
    @Override
    public void performRedo(int count) throws BinaryDataOperationException {
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
    public long getMaximumUndo() {
        return 0;
    }

    @Override
    public long getCommandPosition() {
        return commandPosition;
    }

    /**
     * Performs revert to sync point.
     *
     * @throws BinaryDataOperationException if commands throws it
     */
    @Override
    public void doSync() throws BinaryDataOperationException {
        setCommandPosition(syncPointPosition);
    }

    public void setUndoMaxCount(long maxUndo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getUndoMaximumSize() {
        return 0;
    }

    public void setUndoMaximumSize(long maxSize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Nonnull
    public UndoRedo.Manager getUndoManager() {
        return undoManager;
    }

    @Override
    public long getUsedSize() {
        return 0;
    }

    @Override
    public long getSyncPoint() {
        return syncPointPosition;
    }

    @Override
    public void setSyncPoint(long syncPoint) {
        this.syncPointPosition = syncPoint;
    }

    @Override
    public void setSyncPoint() {
        this.syncPointPosition = commandPosition;
    }

    @Nonnull
    @Override
    public List<BinaryDataCommand> getCommandList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Performs undo or redo operation to reach given position.
     *
     * @param targetPosition desired position
     * @throws BinaryDataOperationException if commands throws it
     */
    @Override
    public void setCommandPosition(long targetPosition) throws BinaryDataOperationException {
        if (targetPosition < commandPosition) {
            performUndo((int) (commandPosition - targetPosition));
        } else if (targetPosition > commandPosition) {
            performRedo((int) (targetPosition - commandPosition));
        }
    }

    private void undoUpdated() {
        codeArea.notifyDataChanged();
        listeners.forEach((listener) -> listener.undoCommandPositionChanged());
    }

    @Override
    public void addUndoUpdateListener(BinaryDataUndoUpdateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeUndoUpdateListener(BinaryDataUndoUpdateListener listener) {
        listeners.remove(listener);
    }
}
