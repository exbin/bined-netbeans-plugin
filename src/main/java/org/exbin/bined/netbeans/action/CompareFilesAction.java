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
package org.exbin.bined.netbeans.action;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.ByteArrayData;
import org.exbin.auxiliary.paged_data.PagedData;
import org.exbin.framework.bined.compare.gui.CompareFilesPanel;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.CloseControlPanel;

/**
 * Compare files action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CompareFilesAction extends AbstractAction {

    private final ExtCodeArea codeArea;

    public CompareFilesAction(ExtCodeArea codeArea) {
        this.codeArea = codeArea;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final CompareFilesPanel compareFilesPanel = new CompareFilesPanel();
        ResourceBundle panelResourceBundle = compareFilesPanel.getResourceBundle();
        CloseControlPanel controlPanel = new CloseControlPanel(panelResourceBundle);
        JPanel dialogPanel = WindowUtils.createDialogPanel(compareFilesPanel, controlPanel);
        Dimension preferredSize = dialogPanel.getPreferredSize();
        dialogPanel.setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + 450));
        final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, (Component) event.getSource(), panelResourceBundle.getString("dialog.title"), Dialog.ModalityType.APPLICATION_MODAL);
        controlPanel.setHandler(dialog::close);

        List<String> availableFiles = new ArrayList<>();
        availableFiles.add("Current File");
        compareFilesPanel.setController(new CompareFilesPanel.Controller() {
            @Nullable
            @Override
            public CompareFilesPanel.FileRecord openFile() {
                final File[] result = new File[1];

                JFileChooser fileChooser = new JFileChooser();
                int dialogResult = fileChooser.showOpenDialog((Component) event.getSource());
                if (dialogResult == JFileChooser.APPROVE_OPTION) {
                    result[0] = fileChooser.getSelectedFile();
                } else {
                    return null;
                }

                try ( FileInputStream stream = new FileInputStream(result[0])) {
                    PagedData pagedData = new PagedData();
                    pagedData.loadFromStream(stream);
                    return new CompareFilesPanel.FileRecord(result[0].getAbsolutePath(), pagedData);
                } catch (IOException ex) {
                    Logger.getLogger(CompareFilesAction.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Nonnull
            @Override
            public BinaryData getFileData(int index) {
                BinaryData contentData = codeArea.getContentData();
                return contentData != null ? contentData : new ByteArrayData();
            }
        });
        compareFilesPanel.setAvailableFiles(availableFiles);
        compareFilesPanel.setLeftIndex(1);
        dialog.showCentered((Component) event.getSource());
    }
}
