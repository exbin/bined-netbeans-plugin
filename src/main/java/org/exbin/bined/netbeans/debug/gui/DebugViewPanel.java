/*
 * Copyright (C) ExBin Project, https://exbin.org
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.bined.EditMode;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.netbeans.debug.DebugViewDataProvider;
import org.exbin.bined.netbeans.gui.BinEdToolbarPanel;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.basic.color.CodeAreaColorsProfile;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActiveComponent;
import org.exbin.jaguif.action.api.ComponentActivationListener;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.bined.jaguif.BinEdDocumentView;
import org.exbin.bined.jaguif.BinEdFileManager;
import org.exbin.bined.jaguif.BinaryStatusApi;
import org.exbin.bined.jaguif.BinedModule;
import org.exbin.bined.jaguif.component.action.GoToPositionAction;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.bined.jaguif.gui.BinaryStatusPanel;
import org.exbin.bined.jaguif.handler.CodeAreaPopupMenuHandler;
import org.exbin.bined.jaguif.options.StatusOptions;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.bined.section.layout.SectionCodeAreaLayoutProfile;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.exbin.jaguif.text.encoding.EncodingsHandler;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;
import org.exbin.jaguif.action.api.clipboard.ClipboardOperationController;
import org.exbin.bined.jaguif.component.BinEdDataComponent;
import org.exbin.jaguif.options.action.OptionsAction;
import org.exbin.jaguif.utils.DesktopUtils;

/**
 * Panel to show debug view.
 */
@NullMarked
public class DebugViewPanel extends javax.swing.JPanel {

    private final List<DebugViewDataProvider> providers = new ArrayList<>();
    private int selectedProvider = 0;

    protected final Font defaultFont;
    protected final SectionCodeAreaLayoutProfile defaultLayoutProfile;
    protected final SectionCodeAreaThemeProfile defaultThemeProfile;
    protected final CodeAreaColorsProfile defaultColorProfile;

    private final JPanel panel;
    private BinEdToolbarPanel toolbarPanel = new BinEdToolbarPanel();
    private StatusBar statusBar;
    private final BinEdDataComponent dataComponent;

    public DebugViewPanel() {
        panel = new JPanel(new BorderLayout());
        dataComponent = new BinEdDocumentView();

        SectCodeArea codeArea = (SectCodeArea) dataComponent.getCodeArea();
        defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        defaultLayoutProfile = codeArea.getLayoutProfile();
        defaultThemeProfile = codeArea.getThemeProfile();
        defaultColorProfile = codeArea.getColorsProfile();

        initComponents();
        init();
    }

    private void init() {
        BinedModule binedModule = App.getModule(BinedModule.class);
        BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
        BinEdFileManager fileManager = binedModule.getFileManager();
        BinEdComponentPanel componentPanel = (BinEdComponentPanel) dataComponent.getComponent();
        fileManager.initComponentPanel(componentPanel);

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
//        editorComponent.onInitFromPreferences(new BinaryEditorOptions(preferencesModule.getAppPreferences()));

        SectCodeArea codeArea = dataComponent.getCodeArea();
        BinEdDataComponent binEdDataComponent = new BinEdDataComponent(codeArea);
        codeArea.setEditMode(EditMode.READ_ONLY);

        toolbarPanel.setTargetComponent(componentPanel);
        toolbarPanel.setCodeAreaControl(new BinEdToolbarPanel.Control() {
            @Override
            public CodeType getCodeType() {
                return codeArea.getCodeType();
            }

            @Override
            public void setCodeType(CodeType codeType) {
                codeArea.setCodeType(codeType);
            }

            @Override
            public boolean isShowNonprintables() {
                ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) codeArea.getPainter();
                NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
                return CodeAreaUtils.requireNonNull(nonprintablesCodeAreaAssessor).isShowNonprintables();
            }

            @Override
            public void setShowNonprintables(boolean showNonprintables) {
                ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) codeArea.getPainter();
                NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
                CodeAreaUtils.requireNonNull(nonprintablesCodeAreaAssessor).setShowNonprintables(showNonprintables);
            }

            @Override
            public void repaint() {
                codeArea.repaint();
            }
        });
        toolbarPanel.setOnlineHelpAction(createOnlineHelpAction());

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsAction optionsAction = (OptionsAction) optionsModule.createOptionsAction();
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        optionsAction.setDialogParentComponent(() -> frameModule.getFrame());
        AbstractAction wrapperAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionsAction.actionPerformed(e);
                toolbarPanel.applyFromCodeArea();
                statusBar.updateStatus();
            }
        };
        toolbarPanel.setOptionsAction(wrapperAction);

        CodeAreaPopupMenuHandler codeAreaPopupMenuHandler
                = binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.NORMAL);
        codeArea.setComponentPopupMenu(new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                ComponentActivationListener componentActivationListener
                        = frameModule.getFrameHandler().getComponentActivationListener();

                componentActivationListener.updated(ActiveComponent.class, binEdDataComponent);
                componentActivationListener.updated(DialogParentComponent.class, () -> binEdDataComponent.getCodeArea());
                componentActivationListener.updated(ClipboardOperationController.class, binEdDataComponent);

                String popupMenuId = "DebugViewPanel.popup";
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                }
                JPopupMenu popupMenu = codeAreaPopupMenuHandler.createPopupMenu(codeArea, popupMenuId, clickedX, clickedY);
                popupMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        codeAreaPopupMenuHandler.dropPopupMenu(popupMenuId);
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });

                popupMenu.show(invoker, x, y);
            }
        });

        EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
        encodingsHandler.loadFromOptions(new TextEncodingOptions(optionsModule.getAppPreferences()));
        statusBar.setController(new BinaryStatusController());
        statusBar.loadFromOptions(new StatusOptions(optionsModule.getAppPreferences()));
        statusBar.setMinimumSize(new Dimension(0, getMinimumSize().height));
        registerBinaryStatus(statusBar);

        panel.add(toolbarPanel, BorderLayout.NORTH);
        panel.add(statusBar, BorderLayout.SOUTH);
        panel.add(dataComponent.getComponent(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();

        this.add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
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

        providerComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                providerComboBoxItemStateChanged(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());
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
            add(providerComboBox, java.awt.BorderLayout.PAGE_START);
        }

        providers.add(provider);
        providerComboBox.addItem(provider.getName());
    }

    public void setContentData(@Nullable BinaryData data) {
        dataComponent.setContentData(data);
        long dataSize = data == null ? 0 : data.getDataSize();
        documentOriginalSize = dataSize;
        statusBar.setCurrentDocumentSize(dataSize, documentOriginalSize);
    }

    private AbstractAction createOnlineHelpAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LanguageModuleApi languageModuleApi = App.getModule(LanguageModuleApi.class);
                DesktopUtils.openDesktopURL(languageModuleApi.getAppBundle().getString("online_help_url"));
            }
        };
    }

    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        this.binaryStatus = binaryStatus;

        SectCodeArea codeArea = dataComponent.getCodeArea();
        codeArea.addDataChangedListener(() -> {
            updateCurrentDocumentSize();
        });

        codeArea.addSelectionChangedListener(() -> {
            binaryStatus.setSelectionRange(codeArea.getSelection());
        });

        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });

        codeArea.addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            binaryStatus.setEditMode(mode, operation);
        });

        updateStatus();
    }

    public void updateStatus() {
        updateCurrentDocumentSize();
        updateCurrentCaretPosition();
        updateCurrentSelectionRange();
        // updateCurrentMemoryMode();
        updateCurrentEditMode();
    }

    private void updateCurrentDocumentSize() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = dataComponent.getCodeArea();
        long dataSize = codeArea.getDataSize();
        binaryStatus.setCurrentDocumentSize(dataSize, documentOriginalSize);
    }

    private void updateCurrentCaretPosition() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = dataComponent.getCodeArea();
        CodeAreaCaretPosition caretPosition = codeArea.getActiveCaretPosition();
        binaryStatus.setCursorPosition(caretPosition);
    }

    private void updateCurrentSelectionRange() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = dataComponent.getCodeArea();
        SelectionRange selectionRange = codeArea.getSelection();
        binaryStatus.setSelectionRange(selectionRange);
    }

    private void updateCurrentEditMode() {
        if (binaryStatus == null) {
            return;
        }

        SectCodeArea codeArea = dataComponent.getCodeArea();
        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
    }

    private class BinaryStatusController implements BinaryStatusPanel.Controller, BinaryStatusPanel.EncodingsController {

        @Override
        public void changeEditOperation(EditOperation editOperation) {
            SectCodeArea codeArea = dataComponent.getCodeArea();
            codeArea.setEditOperation(editOperation);
        }

        @Override
        public void changeCursorPosition() {
            SectCodeArea codeArea = dataComponent.getCodeArea();
            GoToPositionAction action = new GoToPositionAction();
            action.setCodeArea(codeArea);
            action.actionPerformed(null);
        }

        @Override
        public void cycleNextEncoding() {
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
            if (encodingsHandler != null) {
                encodingsHandler.cycleNextEncoding();
            }
        }

        @Override
        public void cyclePreviousEncoding() {
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
            if (encodingsHandler != null) {
                encodingsHandler.cyclePreviousEncoding();
            }
        }

        @Override
        public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
            if (encodingsHandler != null) {
                encodingsHandler.popupEncodingsMenu(mouseEvent);
            }
        }
    }
}
