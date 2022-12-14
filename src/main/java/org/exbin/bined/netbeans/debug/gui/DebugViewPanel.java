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
package org.exbin.bined.netbeans.debug.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.EditMode;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.bined.netbeans.debug.DebugViewDataProvider;
import org.exbin.bined.netbeans.gui.BinEdComponentFileApi;
import org.exbin.bined.netbeans.gui.BinEdComponentPanel;
import org.exbin.framework.bined.FileHandlingMode;

/**
 * Panel to show debug view.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DebugViewPanel extends javax.swing.JPanel {

    private final List<DebugViewDataProvider> providers = new ArrayList<>();
    private int selectedProvider = 0;

    private final BinEdComponentPanel componentPanel;

    public DebugViewPanel() {
        componentPanel = new BinEdComponentPanel();

        initComponents();
        init();
    }

    private void init() {
        componentPanel.getCodeArea().setEditMode(EditMode.READ_ONLY);
        componentPanel.setFileApi(new BinEdComponentFileApi() {
            @Override
            public boolean isSaveSupported() {
                return false;
            }

            @Override
            public void saveDocument() {
            }

            @Override
            public void switchFileHandlingMode(FileHandlingMode newHandlingMode) {
            }

            @Override
            public void closeData() {
            }
        });

        this.add(componentPanel, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        providerComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        providerComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                providerComboBoxItemStateChanged(evt);
            }
        });
        add(providerComboBox, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void providerComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_providerComboBoxItemStateChanged
        int selectedIndex = providerComboBox.getSelectedIndex();
        if (selectedProvider != selectedIndex) {
            selectedProvider = selectedIndex;
            setContentData(providers.get(selectedProvider).getData());
        }
    }//GEN-LAST:event_providerComboBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> providerComboBox;
    // End of variables declaration//GEN-END:variables

    public void addProvider(DebugViewDataProvider provider) {
        if (providers.isEmpty()) {
            setContentData(provider.getData());
        }

        providers.add(provider);
        providerComboBox.addItem(provider.getName());
    }

    public void setContentData(BinaryData data) {
        componentPanel.setContentData(data);
    }
}
