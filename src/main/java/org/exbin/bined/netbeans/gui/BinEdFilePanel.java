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
import org.exbin.bined.jaguif.component.gui.BinEdComponentPanel;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.utils.DesktopUtils;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JViewport;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.netbeans.BinEdNetBeansDocking;
import org.exbin.bined.netbeans.utils.ActionUtils;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.docking.api.ContextDocking;
import org.exbin.jaguif.document.api.ContextDocument;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.bookmarks.BinedBookmarksModule;
import org.exbin.bined.jaguif.macro.BinedMacroModule;
import org.exbin.jaguif.context.ContextStateManager;
import org.exbin.jaguif.context.api.ContextStateManagement;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextChangeListener;
import org.exbin.jaguif.context.api.ContextComponent;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.context.api.ContextMonitoringRegistration;
import org.exbin.jaguif.context.api.ContextMonitoringManagement;
import org.exbin.jaguif.context.api.StateUpdateType;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.options.settings.action.SettingsAction;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.search.api.ContextSearch;
import org.exbin.jaguif.text.encoding.ContextEncoding;

/**
 * Binary editor file panel.
 */
@NullMarked
public class BinEdFilePanel extends JPanel {

    protected @Nullable BinaryFileDocument fileDocument;
    protected BinEdToolbarPanel toolbarPanel = new BinEdToolbarPanel();
    protected ContextStateManagement statusContextManager;
    protected ContextChangeListener contextChangeListener;
    protected StatusBar statusBar;

    public BinEdFilePanel() {
        super(new BorderLayout());
        add(toolbarPanel, BorderLayout.NORTH);

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        StatusBarModuleApi statusBarModule = App.getModule(StatusBarModuleApi.class);
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        ContextStateManagement contextManager = frameModule.getFrameStateManager();
        statusContextManager = contextModule.createChildStateManager(contextManager);
        contextChangeListener = new ContextChangeListener() {
            @Override
            public <T> void notifyStateChanged(Class<T> stateClass, @Nullable T activeState) {
                if (fileDocument == null) {
                    return;
                }

                if (stateClass == ContextDocument.class && activeState != fileDocument) {
                    return;
                }

                if (stateClass == ContextComponent.class && activeState != fileDocument.getDataComponent()) {
                    return;
                }

                if (stateClass == ContextEncoding.class && activeState != fileDocument.getDataComponent()) {
                    return;
                }

                statusContextManager.changeActiveState(stateClass, activeState);
            }

            @Override
            public <T> void notifyStateUpdated(Class<T> stateClass, T activeState, StateUpdateType stateUpdateType) {
                if (fileDocument == null) {
                    return;
                }

                if (stateClass == ContextDocument.class && activeState != fileDocument) {
                    return;
                }

                if (stateClass == ContextComponent.class && activeState != fileDocument.getDataComponent()) {
                    return;
                }

                if (stateClass == ContextEncoding.class && activeState != fileDocument.getDataComponent()) {
                    return;
                }

                statusContextManager.updateActiveState(stateClass, activeState, stateUpdateType);
            }
        };
        contextManager.addChangeListener(contextChangeListener);
        ContextMonitoringManagement statusUpdateManager = contextModule.createMonitoringManager(statusContextManager);
        ContextMonitoringRegistration statusContextRegistrator = contextModule.createMonitoringRegistrator(statusUpdateManager, statusContextManager);
        statusBar = statusBarModule.createStatusBar(BinedComponentModule.BINARY_STATUS_BAR_ID, statusContextRegistrator);
    }

    public void detach() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ContextStateManagement contextManager = frameModule.getFrameStateManager();
        contextManager.removeChangeListener(contextChangeListener);
    }

    public void setDocument(BinaryFileDocument fileDocument) {
        this.fileDocument = fileDocument;
        statusContextManager.changeActiveState(ContextDocument.class, fileDocument);
        statusContextManager.changeActiveState(ContextComponent.class, fileDocument.getDataComponent());
        statusContextManager.changeActiveState(ContextEncoding.class, fileDocument.getDataComponent());
        statusContextManager.changeActiveState(ContextSearch.class, fileDocument.getDataComponent().getSearchController().orElse(null));

        BinEdComponentPanel componentPanel = fileDocument.getComponent();
        SectCodeArea codeArea = (SectCodeArea) fileDocument.getCodeArea();

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

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        BinEdNetBeansDocking docking = (BinEdNetBeansDocking) frameModule.getFrameStateManager().getActiveState(ContextDocking.class);
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        SettingsAction settingsAction = (SettingsAction) optionsSettingsModule.createSettingsAction();
        settingsAction.setDialogParentComponent(() -> frameModule.getFrame());
        AbstractAction wrapperSettingsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsAction.actionPerformed(e);
                toolbarPanel.applyFromCodeArea();
            }
        };
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        java.util.ResourceBundle optionsResourceBundle = languageModule.getBundle(org.exbin.jaguif.options.settings.OptionsSettingsModule.class);
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(wrapperSettingsAction, optionsResourceBundle, SettingsAction.ACTION_ID);
        wrapperSettingsAction.putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        toolbarPanel.setOptionsAction(wrapperSettingsAction);

        BinedComponentModule binedComponentModule = App.getModule(BinedComponentModule.class);
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

                docking.setActiveDocument(fileDocument);

                // TODO Temporary workaround for unfinished rework of actions
                {
                    ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
                    ContextStateManagement contextManagement = new ContextStateManager();
                    ContextMonitoringManagement updateManagement = contextModule.createMonitoringManager(contextManagement);
                    contextManagement.changeActiveState(ContextComponent.class, fileDocument.getDataComponent());
                    contextManagement.changeActiveState(DialogParentComponent.class, () -> frameModule.getFrame());

                    BinedBookmarksModule binedBookmarksModule = App.getModule(BinedBookmarksModule.class);
                    AbstractAction manageBookmarksAction = binedBookmarksModule.getManageBookmarksAction();
                    ContextMonitoringRegistration contextRegistrar = contextModule.createMonitoringRegistrator(updateManagement, contextManagement);
                    contextRegistrar.registerContextMonitoring((ContextChange) manageBookmarksAction.getValue(ActionConsts.ACTION_CONTEXT_CHANGE));

                    BinedMacroModule binedMacroModule = App.getModule(BinedMacroModule.class);
                    // ContextMonitoringManagement updateManagement = frameModule.getFrameMonitoringManager();
                    contextRegistrar.registerContextMonitoring((ContextChange) binedMacroModule.getMacroManager().getMacrosMenu().getAction().getValue(ActionConsts.ACTION_CONTEXT_CHANGE));
                    contextRegistrar.finish();
                }

                JPopupMenu popupMenu = binedComponentModule.createBinaryDocumentPopupMenu(codeArea, clickedX, clickedY);
                ActionUtils.replaceAction(popupMenu, SettingsAction.ACTION_ID + "Action", wrapperSettingsAction);
                popupMenu.show(invoker, x, y);
            }
        });

        docking.addDocument(fileDocument, statusBar);
        docking.setActiveDocument(fileDocument);

        add(statusBar.getComponent(), BorderLayout.SOUTH);

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
        // TODO fileDocument.onInitFromPreferences(appPreferences);
        toolbarPanel.applyFromCodeArea();
    }
}
