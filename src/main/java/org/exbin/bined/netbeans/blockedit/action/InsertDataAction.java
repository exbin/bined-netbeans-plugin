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
package org.exbin.bined.netbeans.blockedit.action;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditOperation;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.swing.command.CodeAreaCommand;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.search.SearchCondition;
import org.exbin.framework.bined.search.gui.BinaryMultilinePanel;
import org.exbin.framework.bined.blockedit.gui.InsertDataPanel;
import org.exbin.framework.bined.blockedit.operation.InsertDataOperation;
import org.exbin.framework.bined.blockedit.operation.ReplaceDataOperation;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.handler.DefaultControlHandler.ControlActionType;
import org.exbin.framework.utils.gui.DefaultControlPanel;

/**
 * Insert data action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class InsertDataAction extends AbstractAction {

    private final ExtCodeArea codeArea;
    private BinaryDataUndoHandler undoHandler;

    public InsertDataAction(ExtCodeArea codeArea) {
        this.codeArea = codeArea;
    }

    public void setUndoHandler(BinaryDataUndoHandler undoHandler) {
        this.undoHandler = undoHandler;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final EditableBinaryData sampleBinaryData = new ByteArrayEditableData();
        final InsertDataPanel insertDataPanel = new InsertDataPanel();
        DefaultControlPanel insertDataControlPanel = new DefaultControlPanel(insertDataPanel.getResourceBundle());
        JPanel insertDataDialogPanel = WindowUtils.createDialogPanel(insertDataPanel, insertDataControlPanel);
        final DialogWrapper dialog = WindowUtils.createDialog(insertDataDialogPanel, (Component) event.getSource(), insertDataPanel.getResourceBundle().getString("dialog.title"), Dialog.ModalityType.APPLICATION_MODAL);
        insertDataPanel.setController(() -> {
            final BinaryMultilinePanel multilinePanel = new BinaryMultilinePanel();
            SearchCondition searchCondition = new SearchCondition();
            EditableBinaryData conditionData = new ByteArrayEditableData();
            conditionData.insert(0, sampleBinaryData);
            searchCondition.setBinaryData(conditionData);
            searchCondition.setSearchMode(SearchCondition.SearchMode.BINARY);
            multilinePanel.setCondition(searchCondition);
//            multilinePanel.setCodeAreaPopupMenuHandler(binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.BASIC));
            DefaultControlPanel controlPanel = new DefaultControlPanel();
            JPanel dialogPanel = WindowUtils.createDialogPanel(multilinePanel, controlPanel);
            final DialogWrapper multilineDialog = WindowUtils.createDialog(dialogPanel, dialog.getWindow(), "Multiline Data", Dialog.ModalityType.APPLICATION_MODAL);
            controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
                if (actionType == DefaultControlHandler.ControlActionType.OK) {
                    SearchCondition condition = multilinePanel.getCondition();
                    sampleBinaryData.clear();
                    sampleBinaryData.insert(0, condition.getBinaryData());
                    insertDataPanel.setFillWith(InsertDataOperation.FillWithType.SAMPLE);
                    long dataLength = insertDataPanel.getDataLength();
                    if (dataLength < sampleBinaryData.getDataSize()) {
                        insertDataPanel.setDataLength(sampleBinaryData.getDataSize());
                    }
                }

                multilineDialog.close();
                multilineDialog.dispose();
            });
            multilineDialog.showCentered(dialog.getWindow());
//                    multilinePanel.detachMenu();
        });
        insertDataControlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == ControlActionType.OK) {
                insertDataPanel.acceptInput();
                long dataLength = insertDataPanel.getDataLength();
                InsertDataOperation.FillWithType fillWithType = insertDataPanel.getFillWithType();
                EditOperation activeOperation = codeArea.getActiveOperation();
                CodeAreaCommand command;
                switch (activeOperation) {
                    case INSERT: {
                        InsertDataOperation operation = new InsertDataOperation(codeArea, codeArea.getDataPosition(), dataLength, fillWithType, sampleBinaryData);
                        command = new InsertDataOperation.InsertDataCommand(operation);
                        break;
                    }
                    case OVERWRITE: {
                        ReplaceDataOperation operation = new ReplaceDataOperation(codeArea, codeArea.getDataPosition(), dataLength, fillWithType, sampleBinaryData);
                        command = new ReplaceDataOperation.ReplaceDataCommand(operation);
                        break;
                    }
                    default:
                        throw CodeAreaUtils.getInvalidTypeException(activeOperation);
                }
                try {
                    undoHandler.execute(command);
                } catch (BinaryDataOperationException ex) {
                    Logger.getLogger(InsertDataAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            dialog.close();
            dialog.dispose();
        });
        SwingUtilities.invokeLater(insertDataPanel::initFocus);
        dialog.showCentered((Component) event.getSource());
    }
}
