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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import org.exbin.deltahex.netbeans.panel.DialogControlPanel;
import org.exbin.deltahex.netbeans.panel.TextEncodingPanel;
import org.exbin.deltahex.netbeans.panel.TextEncodingPanelApi;
import org.exbin.deltahex.netbeans.panel.TextEncodingStatusApi;
import org.exbin.deltahex.netbeans.utils.ActionUtils;
import org.exbin.deltahex.netbeans.utils.LanguageUtils;
import org.exbin.deltahex.netbeans.utils.WindowUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 * Encodings handler.
 *
 * @version 0.1.4 2016/12/20
 * @author ExBin Project (http://exbin.org)
 */
public class EncodingsHandler implements TextEncodingPanelApi {

    private final ResourceBundle resourceBundle;

    private TextEncodingStatusApi textEncodingStatus;
    private List<String> encodings = null;
    private ActionListener encodingActionListener;
    private ButtonGroup encodingButtonGroup;
    private javax.swing.JMenu toolsEncodingMenu;
    private javax.swing.JRadioButtonMenuItem utfEncodingRadioButtonMenuItem;
    private ActionListener utfEncodingActionListener;

    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String PREFERENCES_TEXT_ENCODING_PREFIX = "textEncoding.";
    public static final String PREFERENCES_TEXT_ENCODING_DEFAULT = "textEncoding.default";

    public static final String UTF_ENCODING_TEXT = "UTF-8 (default)";
    public static final String UTF_ENCODING_TOOLTIP = "Set encoding UTF-8";

    private Action manageEncodingsAction;

    public EncodingsHandler(TextEncodingStatusApi textStatus) {
        resourceBundle = LanguageUtils.getResourceBundleByClass(EncodingsHandler.class);
        textEncodingStatus = textStatus;
        init();
        EncodingsHandler.this.rebuildEncodings();
    }

    private void init() {
        encodings = new ArrayList<>();
        encodingButtonGroup = new ButtonGroup();

        encodingActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedEncoding(((JRadioButtonMenuItem) e.getSource()).getText());
            }
        };

        utfEncodingRadioButtonMenuItem = new JRadioButtonMenuItem();
        utfEncodingRadioButtonMenuItem.setSelected(true);
        utfEncodingRadioButtonMenuItem.setText(UTF_ENCODING_TEXT);
        utfEncodingRadioButtonMenuItem.setToolTipText(UTF_ENCODING_TOOLTIP);
        utfEncodingActionListener = new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSelectedEncoding(ENCODING_UTF8);
            }
        };
        utfEncodingRadioButtonMenuItem.addActionListener(utfEncodingActionListener);

        encodingButtonGroup.add(utfEncodingRadioButtonMenuItem);
        manageEncodingsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final TextEncodingPanel textEncodingPanel = new TextEncodingPanel(EncodingsHandler.this);
                textEncodingPanel.setEncodingList(encodings);
                final DialogControlPanel controlPanel = new DialogControlPanel();
                JPanel dialogPanel = WindowUtils.createDialogPanel(textEncodingPanel, controlPanel);
                dialogPanel.setVisible(true);
                DialogDescriptor dialogDescriptor = new DialogDescriptor(dialogPanel, "Manage Encodings", true, new Object[0], null, 0, null, null);
                final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
                controlPanel.setControlListener(new DialogControlPanel.ControlPanelListener() {
                    @Override
                    public void controlActionPerformed(DialogControlPanel.ControlActionType actionType) {
                        if (actionType != DialogControlPanel.ControlActionType.CANCEL) {
                            encodings = textEncodingPanel.getEncodingList();
                            rebuildEncodings();
                        }

                        WindowUtils.closeWindow(dialog);
                    }
                });
                dialog.setVisible(true);
//                if (encodingPanel.getDialogOption() == JOptionPane.OK_OPTION) {
//                    codeArea.setCaretPosition(encodingPanel.getGoToPosition());
//                }
//                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
//                ManageEncodingsDialog dlg = new ManageEncodingsDialog(application, frameModule.getFrame(), EncodingsHandler.this, true);
//                dlg.setIconImage(application.getApplicationIcon());
//                TextEncodingPanel panel = dlg.getEncodingPanel();
//                panel.setEncodingList(new ArrayList<>(encodings));
//                dlg.setLocationRelativeTo(dlg.getParent());
//                dlg.setVisible(true);
//                if (dlg.getDialogOption() == JOptionPane.OK_OPTION) {
//                    encodings = panel.getEncodingList();
//                    encodingsRebuild();
//                }
            }
        };
        ActionUtils.setupAction(manageEncodingsAction, resourceBundle, "manageEncodingsAction");
        manageEncodingsAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        manageEncodingsAction.putValue(Action.NAME, manageEncodingsAction.getValue(Action.NAME) + ActionUtils.DIALOG_MENUITEM_EXT);

        toolsEncodingMenu = new JMenu();
        toolsEncodingMenu.add(utfEncodingRadioButtonMenuItem);
        toolsEncodingMenu.addSeparator();
        toolsEncodingMenu.add(manageEncodingsAction);
        toolsEncodingMenu.setText(resourceBundle.getString("toolsEncodingMenu.text"));
        toolsEncodingMenu.setToolTipText(resourceBundle.getString("toolsEncodingMenu.shortDescription"));
    }

    @Override
    public List<String> getEncodings() {
        return encodings;
    }

    @Override
    public void setEncodings(List<String> encodings) {
        this.encodings = encodings;
    }

    @Override
    public String getSelectedEncoding() {
        return textEncodingStatus.getEncoding(); // ((TextCharsetApi) editorProvider.getPanel()).getCharset().name();
    }

    @Override
    public void setSelectedEncoding(String encoding) {
        if (encoding != null) {
//            ((TextCharsetApi) editorProvider.getPanel()).setCharset(Charset.forName(encoding));
            textEncodingStatus.setEncoding(encoding);
        }
    }

    public void setTextEncodingStatus(TextEncodingStatusApi textEncodingStatus) {
        this.textEncodingStatus = textEncodingStatus;
    }

    public JMenu getToolsEncodingMenu() {
        return toolsEncodingMenu;
    }

    public void rebuildEncodings() {
        String encodingToolTip = "Set encoding ";
        for (int i = toolsEncodingMenu.getItemCount() - 2; i > 1; i--) {
            toolsEncodingMenu.remove(i);
        }

        if (encodings.size() > 0) {
            int selectedEncoding = encodings.indexOf(getSelectedEncoding());
            if (selectedEncoding < 0) {
                setSelectedEncoding(ENCODING_UTF8);
                utfEncodingRadioButtonMenuItem.setSelected(true);
            }
            toolsEncodingMenu.add(new JSeparator(), 1);
            for (int index = 0; index < encodings.size(); index++) {
                String encoding = encodings.get(index);
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(encoding, false);
                item.addActionListener(encodingActionListener);
                item.setToolTipText(encodingToolTip + encoding);
                toolsEncodingMenu.add(item, index + 2);
                encodingButtonGroup.add(item);
                if (index == selectedEncoding) {
                    item.setSelected(true);
                }
            }
        }
    }

    private void updateEncodingsSelection(int menuIndex) {
        if (menuIndex > 0) {
            menuIndex++;
        }
        JMenuItem item = toolsEncodingMenu.getItem(menuIndex);
        item.setSelected(true);
    }

    public void loadFromPreferences(Preferences preferences) {
        setSelectedEncoding(preferences.get(PREFERENCES_TEXT_ENCODING_DEFAULT, ENCODING_UTF8));
        encodings.clear();
        String value;
        int i = 0;
        do {
            value = preferences.get(PREFERENCES_TEXT_ENCODING_PREFIX + Integer.toString(i), null);
            if (value != null) {
                encodings.add(value);
                i++;
            }
        } while (value != null);
        rebuildEncodings();
    }

    public void cycleEncodings() {
        int menuIndex = 0;
        if (encodings.size() > 0) {
            int selectedEncoding = encodings.indexOf(getSelectedEncoding());
            if (selectedEncoding < 0) {
                setSelectedEncoding(encodings.get(0));
                menuIndex = 1;
            } else if (selectedEncoding < encodings.size() - 1) {
                setSelectedEncoding(encodings.get(selectedEncoding + 1));
                menuIndex = selectedEncoding + 2;
            } else {
                setSelectedEncoding(ENCODING_UTF8);
            }
        }

        updateEncodingsSelection(menuIndex);
    }

    public void popupEncodingsMenu(MouseEvent mouseEvent) {
        JPopupMenu popupMenu = new JPopupMenu();

        int selectedEncoding = encodings.indexOf(getSelectedEncoding());
        String encodingToolTip = "Set encoding ";
        JRadioButtonMenuItem utfEncoding = new JRadioButtonMenuItem("", false);
        utfEncoding.setText(UTF_ENCODING_TEXT);
        utfEncoding.setToolTipText(UTF_ENCODING_TOOLTIP);
        utfEncoding.addActionListener(utfEncodingActionListener);
        if (selectedEncoding < 0) {
            utfEncoding.setSelected(true);
        }
        popupMenu.add(utfEncoding);
        if (encodings.size() > 0) {

            popupMenu.add(new JSeparator(), 1);
            for (int index = 0; index < encodings.size(); index++) {
                String encoding = encodings.get(index);
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(encoding, false);
                item.addActionListener(encodingActionListener);
                item.setToolTipText(encodingToolTip + encoding);
                popupMenu.add(item, index + 2);
                if (index == selectedEncoding) {
                    item.setSelected(true);
                }
            }
        }

        popupMenu.add(new JSeparator());
        popupMenu.add(manageEncodingsAction);

        popupMenu.show((Component) mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
    }
}
