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
package org.exbin.deltahex.netbeans.panel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Encoding selection panel.
 *
 * @version 0.1.4 2016/12/20
 * @author ExBin Project (http://exbin.org)
 */
public class AddEncodingPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(AddEncodingPanel.class);
    private final EncodingsTableModel tableModel = new EncodingsTableModel();

    public AddEncodingPanel() {
        initComponents();
        init();
    }

    private void init() {
        encodingsTable.setModel(tableModel);

        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                nameTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                nameTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                nameTextChanged();
            }

            private void nameTextChanged() {
                tableModel.setNameFilter(nameTextField.getText());
            }
        });

        countryTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                countryTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                countryTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                countryTextChanged();
            }

            private void countryTextChanged() {
                tableModel.setCountryFilter(countryTextField.getText());
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filterPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        countryLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        clearButton = new javax.swing.JButton();
        countryTextField = new javax.swing.JTextField();
        mainPanel = new javax.swing.JPanel();
        supportedEncodingsLabel = new javax.swing.JLabel();
        encodinsScrollPane = new javax.swing.JScrollPane();
        encodingsTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        filterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("filterPanel.title"))); // NOI18N

        nameLabel.setText(resourceBundle.getString("nameLabel.text")); // NOI18N

        countryLabel.setText(resourceBundle.getString("countryLabel.text")); // NOI18N

        clearButton.setText(resourceBundle.getString("clearButton.text")); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
        filterPanel.setLayout(filterPanelLayout);
        filterPanelLayout.setHorizontalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filterPanelLayout.createSequentialGroup()
                .addContainerGap(162, Short.MAX_VALUE)
                .addComponent(countryLabel)
                .addGap(132, 132, 132))
            .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(filterPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(filterPanelLayout.createSequentialGroup()
                            .addComponent(nameLabel)
                            .addGap(0, 103, Short.MAX_VALUE))
                        .addComponent(nameTextField))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(countryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        filterPanelLayout.setVerticalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(countryLabel)
                .addContainerGap(43, Short.MAX_VALUE))
            .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(filterPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(nameLabel)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(countryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(clearButton))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        add(filterPanel, java.awt.BorderLayout.PAGE_START);

        supportedEncodingsLabel.setText(resourceBundle.getString("supportedEncodingsLabel.text")); // NOI18N

        encodinsScrollPane.setViewportView(encodingsTable);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(encodinsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(supportedEncodingsLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(supportedEncodingsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodinsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        nameTextField.setText("");
        countryTextField.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    public void setUsedEncodings(List<String> encodings) {
        tableModel.setUsedEncodings(encodings);
    }

    public List<String> getEncodings() {
        ArrayList<String> result = new ArrayList<>();
        int[] selectedValues = encodingsTable.getSelectedRows();
        for (int rowIndex : selectedValues) {
            String value = (String) tableModel.getValueAt(rowIndex, 0);
            result.add((String) value);
        }
        return result;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel countryLabel;
    private javax.swing.JTextField countryTextField;
    private javax.swing.JTable encodingsTable;
    private javax.swing.JScrollPane encodinsScrollPane;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel supportedEncodingsLabel;
    // End of variables declaration//GEN-END:variables
}
