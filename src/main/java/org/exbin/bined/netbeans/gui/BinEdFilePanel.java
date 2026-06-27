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
package org.exbin.bined.netbeans.gui;

import org.exbin.bined.CodeType;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.jaguif.App;
import org.exbin.bined.jaguif.document.BinaryFileDocument;
import org.exbin.bined.jaguif.BinedModule;
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.bined.jaguif.gui.BinaryStatusPanel;
import org.exbin.bined.jaguif.handler.CodeAreaPopupMenuHandler;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.utils.DesktopUtils;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.jspecify.annotations.NullMarked;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.JViewport;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.EditOperation;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.netbeans.BinEdNetBeansEditorProvider;
import org.exbin.bined.netbeans.utils.ActionUtils;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextService;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.bined.jaguif.BinEdEditorProvider;
import org.exbin.bined.jaguif.BinEdFileManager;
import org.exbin.bined.jaguif.BinaryStatusApi;
import org.exbin.bined.jaguif.document.FileProcessingMode;
import org.exbin.bined.jaguif.component.action.GoToPositionAction;
import org.exbin.bined.jaguif.bookmarks.BinedBookmarksModule;
import org.exbin.bined.jaguif.editor.options.BinaryEditorOptions;
import org.exbin.bined.jaguif.macro.BinedMacroModule;
import org.exbin.bined.jaguif.viewer.BinedViewerModule;
import org.exbin.jaguif.context.ActiveContextManager;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.context.api.ContextUpdateManagement;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.options.action.OptionsAction;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.text.encoding.EncodingsHandler;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;

/**
 * TODO: Binary editor file panel.
 */
@NullMarked
public class BinEdFilePanel extends JPanel {

    private BinaryFileDocument fileDocument;
    private BinEdToolbarPanel toolbarPanel = new BinEdToolbarPanel();
    private StatusBar statusBar;

    public BinEdFilePanel() {
        super(new BorderLayout());
        add(toolbarPanel, BorderLayout.NORTH);

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        StatusBarModuleApi statusBarModule = App.getModule(StatusBarModuleApi.class);
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        ActiveContextManagement contextManager = frameModule.getFrameController().getContextManager();
        ContextUpdateManagement updateManager = frameModule.getFrameController().getUpdateManager();
        ContextRegistration contextRegistrator = contextModule.createContextRegistrator(FrameModuleApi.MAIN_STATUS_BAR_ID,  updateManager, contextManager);
        statusBar = statusBarModule.createStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID, contextRegistrator);
    }

    public void setDocument(BinaryFileDocument fileDocument) {
        this.fileDocument = fileDocument;
        BinEdComponentPanel componentPanel = fileDocument.getComponent();
        SectCodeArea codeArea = fileDocument.getCodeArea();

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
                statusPanel.updateStatus();
            }
        };
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        java.util.ResourceBundle optionsResourceBundle = languageModule.getBundle(org.exbin.framework.options.OptionsModule.class);
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(wrapperAction, optionsResourceBundle, OptionsAction.ACTION_ID);
        wrapperAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        toolbarPanel.setOptionsAction(wrapperAction);

        BinedModule binedModule = App.getModule(BinedModule.class);
        BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
        BinEdNetBeansEditorProvider editorProvider = (BinEdNetBeansEditorProvider) binedModule.getEditorProvider();
        CodeAreaPopupMenuHandler codeAreaPopupMenuHandler
                = binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.EDITOR);
        codeArea.setComponentPopupMenu(new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                String popupMenuId = "BinEdFilePanel.popup";
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                }

                // TODO Temporary workaround for unfinished rework of actions
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                ActionContextService actionContextService = frameModule.getFrameHandler().getActionContextService();
                BinedBookmarksModule binedBookmarksModule = App.getModule(BinedBookmarksModule.class);
                actionContextService.requestUpdate(binedBookmarksModule.getManageBookmarksAction());
                BinedMacroModule binedMacroModule = App.getModule(BinedMacroModule.class);
                actionContextService.requestUpdate(binedMacroModule.getMacroManager().getManageMacrosAction());

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
                ActionUtils.replaceAction(popupMenu, OptionsAction.ACTION_ID, wrapperAction);
                popupMenu.show(invoker, x, y);
            }
        });

        editorProvider.addFile(fileHandler);
        editorProvider.setActiveFile(fileHandler);

        BinEdFileManager fileManager = binedModule.getFileManager();
        EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
        fileManager.registerStatusBar(new BinaryStatusPanel());
        fileManager.setStatusControlHandler(new BinaryStatusController());

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        encodingsHandler.loadFromOptions(new TextEncodingOptions(optionsModule.getAppOptions()));
        add(statusBar, BorderLayout.SOUTH);

        add(componentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public BinEdToolbarPanel getToolbarPanel() {
        return toolbarPanel;
    }

    public SectCodeArea getCodeArea() {
        return (SectCodeArea) fileDocument.getCodeArea();
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

    public void loadFromOptions(OptionsStorage appPreferences) {
        fileDocument.getComponent().onInitFromPreferences(appPreferences);
        toolbarPanel.applyFromCodeArea();
    }

/*    @NullMarked
    private class BinaryStatusController implements BinaryStatusPanel.Controller, BinaryStatusPanel.EncodingsController, BinaryStatusPanel.MemoryModeController {

        @Override
        public void changeEditOperation(EditOperation editOperation) {
            BinedModule binedModule = App.getModule(BinedModule.class);
            BinEdNetBeansEditorProvider editorProvider = (BinEdNetBeansEditorProvider) binedModule.getEditorProvider();
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            if (activeFile.isPresent()) {
                ((BinEdFileHandler) activeFile.get()).getCodeArea().setEditOperation(editOperation);
            }
        }

        @Override
        public void changeCursorPosition() {
            GoToPositionAction action = new GoToPositionAction();
            action.setCodeArea(fileHandler.getCodeArea());
            action.actionPerformed(null);
        }

        @Override
        public void cycleNextEncoding() {
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
            encodingsHandler.cycleNextEncoding();
        }

        @Override
        public void cyclePreviousEncoding() {
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
            encodingsHandler.cyclePreviousEncoding();
        }

        @Override
        public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            EncodingsHandler encodingsHandler = binedViewerModule.getEncodingsHandler();
            encodingsHandler.popupEncodingsMenu(mouseEvent);
        }

        @Override
        public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
            BinedModule binedModule = App.getModule(BinedModule.class);
            BinEdNetBeansEditorProvider editorProvider = (BinEdNetBeansEditorProvider) binedModule.getEditorProvider();
            Optional<FileHandler> activeFile = editorProvider.getActiveFile();
            if (activeFile.isPresent()) {
                BinEdFileHandler fileHandler = (BinEdFileHandler) activeFile.get();
                FileProcessingMode fileProcessingMode = fileHandler.getFileProcessingMode();
                FileProcessingMode newProcessingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ? FileProcessingMode.DELTA : FileProcessingMode.MEMORY;
                if (newProcessingMode != fileProcessingMode) {
                    OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
                    BinaryEditorOptions options = new BinaryEditorOptions(optionsModule.getAppOptions());
                    if (editorProvider.releaseFile(fileHandler)) {
                        fileHandler.switchFileHandlingMode(newProcessingMode);
                        options.setFileHandlingMode(newProcessingMode);
                    }
                    ((BinEdEditorProvider) editorProvider).updateStatus();
                }
            }
        }
    } */
}
