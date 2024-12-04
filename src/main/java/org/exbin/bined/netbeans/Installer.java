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
package org.exbin.bined.netbeans;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.bined.netbeans.preferences.IntegrationPreferences;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.about.AboutModule;
import org.exbin.framework.about.api.AboutModuleApi;
import org.exbin.framework.action.ActionModule;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.bookmarks.BinedBookmarksModule;
import org.exbin.framework.bined.compare.BinedCompareModule;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.bined.macro.BinedMacroModule;
import org.exbin.framework.bined.objectdata.BinedObjectDataModule;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.operation.bouncycastle.BinedOperationBouncycastleModule;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.bined.tool.content.BinedToolContentModule;
import org.exbin.framework.component.ComponentModule;
import org.exbin.framework.component.api.ComponentModuleApi;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.EditorModule;
import org.exbin.framework.file.FileModule;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.FrameModule;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.help.online.HelpOnlineModule;
import org.exbin.framework.language.LanguageModule;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.language.api.LanguageProvider;
import org.exbin.framework.operation.undo.OperationUndoModule;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.options.OptionsModule;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.preferences.PreferencesModule;
import org.exbin.framework.preferences.PreferencesWrapper;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.ui.UiModule;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.window.WindowModule;
import org.exbin.framework.window.api.WindowModuleApi;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Installer for BinEd plugin.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class Installer extends ModuleInstall {

    private static final List<IntegrationOptionsListener> INTEGRATION_OPTIONS_LISTENERS = new ArrayList<>();

    private static IntegrationOptions initialIntegrationOptions = null;
    private boolean initialized = false;

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            if (!initialized) {
                initialized = true;
                AppModuleProvider appModuleProvider = new AppModuleProvider();
                appModuleProvider.createModules();
                App.setModuleProvider(appModuleProvider);
                appModuleProvider.init();
            }

            if (initialIntegrationOptions == null) {
                initIntegrations();

                initialIntegrationOptions = new IntegrationPreferences(new PreferencesWrapper(NbPreferences.forModule(BinaryEditorPreferences.class)));
            }

            applyIntegrationOptions(initialIntegrationOptions);
        });
    }

    @Override
    public void uninstalled() {
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            uninstallIntegration();
        });
    }

    private void initIntegrations() {
        FileOpenAsBinaryAction.registerIntegration();
        OpenAsBinaryAction.registerIntegration();
        OpenAsBinaryToolsAction.registerIntegration();
        BinEdEditor.registerIntegration();
    }

    public static void addIntegrationOptionsListener(IntegrationOptionsListener integrationOptionsListener) {
        INTEGRATION_OPTIONS_LISTENERS.add(integrationOptionsListener);
        if (initialIntegrationOptions != null) {
            integrationOptionsListener.integrationInit(initialIntegrationOptions);
        }
    }

    public static void applyIntegrationOptions(IntegrationOptions integrationOptions) {
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        Locale languageLocale = integrationOptions.getLanguageLocale();
        if (languageLocale.equals(Locale.ROOT)) {
            // Try to match to IDE locale
            Locale ideLocale = Locale.getDefault();
            List<Locale> locales = new ArrayList<>();
            for (LanguageProvider languageRecord : languageModule.getLanguagePlugins()) {
                locales.add(languageRecord.getLocale());
            }
            List<Locale.LanguageRange> localeRange = new ArrayList<>();
            String languageTag = ideLocale.toLanguageTag();
            if ("zh-CN".equals(languageTag)) {
                // TODO detect match to zh_Hans somehow
                languageTag = "zh";
            }
            localeRange.add(new Locale.LanguageRange(languageTag));
            List<Locale> match = Locale.filter(localeRange, locales);
            if (!match.isEmpty()) {
                languageModule.switchToLanguage(match.get(0));
            }
        } else {
            languageModule.switchToLanguage(languageLocale);
        }
        for (IntegrationOptionsListener listener : INTEGRATION_OPTIONS_LISTENERS) {
            listener.integrationInit(integrationOptions);
        }
    }

    private static void uninstallIntegration() {
        for (IntegrationOptionsListener listener : INTEGRATION_OPTIONS_LISTENERS) {
            listener.uninstallIntegration();
        }
    }

    @ParametersAreNonnullByDefault
    public interface IntegrationOptionsListener {

        void integrationInit(IntegrationOptions integrationOptions);

        void uninstallIntegration();
    }

    @ParametersAreNonnullByDefault
    private static class AppModuleProvider implements ModuleProvider {

        private final Map<Class<?>, Module> modules = new HashMap<>();

        private void createModules() {
            modules.put(LanguageModuleApi.class, new LanguageModule());
            modules.put(ActionModuleApi.class, new ActionModule());
            modules.put(OperationUndoModuleApi.class, new OperationUndoModule());
            modules.put(OptionsModuleApi.class, new OptionsModule());
            modules.put(PreferencesModuleApi.class, new PreferencesModule());
            modules.put(UiModuleApi.class, new UiModule());
            modules.put(ComponentModuleApi.class, new ComponentModule());
            modules.put(WindowModuleApi.class, new WindowModule());
            modules.put(FrameModuleApi.class, new FrameModule());
            modules.put(FileModuleApi.class, new FileModule());
            modules.put(EditorModuleApi.class, new EditorModule());
            modules.put(HelpOnlineModule.class, new HelpOnlineModule());
            modules.put(BinedModule.class, new BinedModule());
            modules.put(BinedSearchModule.class, new BinedSearchModule());
            modules.put(BinedOperationModule.class, new BinedOperationModule());
            modules.put(BinedOperationBouncycastleModule.class, new BinedOperationBouncycastleModule());
            modules.put(BinedObjectDataModule.class, new BinedObjectDataModule());
            modules.put(BinedToolContentModule.class, new BinedToolContentModule());
            modules.put(BinedCompareModule.class, new BinedCompareModule());
            modules.put(BinedInspectorModule.class, new BinedInspectorModule());
            modules.put(BinedBookmarksModule.class, new BinedBookmarksModule());
            modules.put(BinedMacroModule.class, new BinedMacroModule());
            modules.put(AboutModuleApi.class, new AboutModule());
        }

        private void init() {
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            preferencesModule.setupAppPreferences(BinEdNetBeansPlugin.class);
            Preferences preferences = preferencesModule.getAppPreferences();

            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            frameModule.createMainMenu();
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            actionModule.registerMenuClipboardActions();
            actionModule.registerToolBarClipboardActions();

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            ResourceBundle bundle = languageModule.getBundle(BinEdNetBeansPlugin.class);
            languageModule.setAppBundle(bundle);

            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
            windowModule.setHideHeaderPanels(true);

            AboutModuleApi aboutModule = App.getModule(AboutModuleApi.class);
            OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
            optionsModule.registerMenuAction();

            HelpOnlineModule helpOnlineModule = App.getModule(HelpOnlineModule.class);
            try {
                helpOnlineModule.setOnlineHelpUrl(new URL(bundle.getString("online_help_url")));
            } catch (MalformedURLException ex) {
                Logger.getLogger(Installer.class.getName()).log(Level.SEVERE, null, ex);
            }

            BinEdNetBeansEditorProvider editorProvider = new BinEdNetBeansEditorProvider();
            BinedModule binedModule = App.getModule(BinedModule.class);
            binedModule.setEditorProvider(editorProvider);

            BinedSearchModule binedSearchModule = App.getModule(BinedSearchModule.class);
            binedSearchModule.setEditorProvider(editorProvider);

            BinedOperationModule binedOperationModule = App.getModule(BinedOperationModule.class);
            binedOperationModule.setEditorProvider(editorProvider);

            BinedOperationBouncycastleModule binedOperationBouncycastleModule = App.getModule(BinedOperationBouncycastleModule.class);
            binedOperationBouncycastleModule.setEditorProvider(editorProvider);

            BinedToolContentModule binedToolContentModule = App.getModule(BinedToolContentModule.class);

            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
            binedInspectorModule.setEditorProvider(editorProvider);

            BinedCompareModule binedCompareModule = App.getModule(BinedCompareModule.class);
            binedCompareModule.registerToolsOptionsMenuActions();

            BinedBookmarksModule binedBookmarksModule = App.getModule(BinedBookmarksModule.class);
            binedBookmarksModule.setEditorProvider(editorProvider);

            BinedMacroModule binedMacroModule = App.getModule(BinedMacroModule.class);
            binedMacroModule.setEditorProvider(editorProvider);

            binedModule.registerCodeAreaPopupMenu();
            binedSearchModule.registerEditFindPopupMenuActions();
            binedOperationModule.registerBlockEditPopupMenuActions();
            binedToolContentModule.registerClipboardContentMenu();
            binedToolContentModule.registerDragDropContentMenu();
            binedInspectorModule.registerViewValuesPanelMenuActions();
            binedInspectorModule.registerOptionsPanels();
            binedMacroModule.registerMacrosPopupMenuActions();
            binedBookmarksModule.registerBookmarksPopupMenuActions();

            String toolsSubMenuId = BinEdNetBeansPlugin.PLUGIN_PREFIX + "toolsMenu";
            actionModule.registerMenu(toolsSubMenuId, BinedModule.MODULE_ID);
            Action positionCodeTypeSubMenuAction = new AbstractAction("Tools") {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
            // positionCodeTypeSubMenuAction.putValue(Action.SHORT_DESCRIPTION, resourceBundle.getString("positionCodeTypeSubMenu.shortDescription"));
            actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, BinedModule.MODULE_ID, toolsSubMenuId, positionCodeTypeSubMenuAction, new MenuPosition(PositionMode.BOTTOM_LAST));
            actionModule.registerMenuItem(toolsSubMenuId, BinedModule.MODULE_ID, binedCompareModule.createCompareFilesAction(), new MenuPosition(PositionMode.TOP));
            actionModule.registerMenuItem(toolsSubMenuId, BinedModule.MODULE_ID, binedToolContentModule.createClipboardContentAction(), new MenuPosition(PositionMode.TOP));
            actionModule.registerMenuItem(toolsSubMenuId, BinedModule.MODULE_ID, binedToolContentModule.createDragDropContentAction(), new MenuPosition(PositionMode.TOP));

            String aboutMenuGroup = BinEdNetBeansPlugin.PLUGIN_PREFIX + "helpAboutMenuGroup";
            actionModule.registerMenuGroup(BinedModule.CODE_AREA_POPUP_MENU_ID, new MenuGroup(aboutMenuGroup, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.ABOVE));
            actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, HelpOnlineModule.MODULE_ID, helpOnlineModule.createOnlineHelpAction(), new MenuPosition(aboutMenuGroup));
            actionModule.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, AboutModule.MODULE_ID, aboutModule.createAboutAction(), new MenuPosition(aboutMenuGroup));

            ComponentActivationListener componentActivationListener =
                    frameModule.getFrameHandler().getComponentActivationListener();
            componentActivationListener.updated(EditorProvider.class, editorProvider);
        }

        @Override
        public void launch(Runnable runnable) {
        }

        @Nonnull
        @Override
        public <T extends Module> T getModule(Class<T> moduleClass) {
            return (T) modules.get(moduleClass);
        }
    }
}
