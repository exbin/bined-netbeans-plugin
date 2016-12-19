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

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.xbup.operation.Command;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.operation.undo.XBUndoUpdateListener;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;

/**
 * Undo handler for hexadecimal editor using Swing undo.
 *
 * @version 0.1.3 2016/08/31
 * @author ExBin Project (http://exbin.org)
 */
public class HexUndoSwingHandler implements XBUndoHandler {

    private final CodeArea codeArea;
    private final List<XBUndoUpdateListener> listeners = new ArrayList<>();
    private final UndoRedo.Manager undoManager;
    private long commandPosition;
    private long syncPointPosition = -1;

    /**
     * Creates a new instance.
     *
     * @param codeArea hexadecimal component
     * @param undoManager undo manager
     */
    public HexUndoSwingHandler(CodeArea codeArea, UndoRedo.Manager undoManager) {
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
     * @throws java.lang.Exception if commands throws it
     */
    @Override
    public void execute(Command command) throws Exception {
        command.execute();
        commandAdded(command);
    }

    @Override
    public void addCommand(Command command) {
        command.use();
        commandAdded(command);
    }

    private void commandAdded(final Command command) {
        UndoableEdit edit = new UndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                commandPosition--;
                try {
                    command.undo();
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
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
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
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

            @Override
            public String getPresentationName() {
                return command.getCaption();
            }

            @Override
            public String getUndoPresentationName() {
                return "";
            }

            @Override
            public String getRedoPresentationName() {
                return "";
            }
        };
        undoManager.undoableEditHappened(new UndoableEditEvent(codeArea, edit));

        commandPosition++;
        undoUpdated();
        for (XBUndoUpdateListener listener : listeners) {
            listener.undoCommandAdded(command);
        }
    }

    /**
     * Performs single undo step.
     *
     * @throws java.lang.Exception if commands throws it
     */
    @Override
    public void performUndo() throws Exception {
        performUndoInt();
        undoUpdated();
    }

    private void performUndoInt() throws Exception {
        undoManager.undo();
    }

    /**
     * Performs single redo step.
     *
     * @throws java.lang.Exception if commands throws it
     */
    @Override
    public void performRedo() throws Exception {
        performRedoInt();
        undoUpdated();
    }

    private void performRedoInt() throws Exception {
        undoManager.redo();
    }

    /**
     * Performs multiple undo step.
     *
     * @param count count of steps
     * @throws Exception if commands throws it
     */
    @Override
    public void performUndo(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            performUndo();
        }
    }

    /**
     * Performs multiple redo step.
     *
     * @param count count of steps
     * @throws Exception if commands throws it
     */
    @Override
    public void performRedo(int count) throws Exception {
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
     * @throws java.lang.Exception if commands throws it
     */
    @Override
    public void doSync() throws Exception {
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

    @Override
    public List<Command> getCommandList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Performs undo or redo operation to reach given position.
     *
     * @param targetPosition desired position
     * @throws java.lang.Exception if commands throws it
     */
    @Override
    public void setCommandPosition(long targetPosition) throws Exception {
        if (targetPosition < commandPosition) {
            performUndo((int) (commandPosition - targetPosition));
        } else if (targetPosition > commandPosition) {
            performRedo((int) (targetPosition - commandPosition));
        }
    }

    private void undoUpdated() {
        for (XBUndoUpdateListener listener : listeners) {
            listener.undoCommandPositionChanged();
        }
    }

    @Override
    public void addUndoUpdateListener(XBUndoUpdateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeUndoUpdateListener(XBUndoUpdateListener listener) {
        listeners.remove(listener);
    }
}
