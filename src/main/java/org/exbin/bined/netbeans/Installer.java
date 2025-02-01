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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.bined.netbeans.options.gui.IntegrationOptionsPanel;
import org.exbin.bined.netbeans.options.impl.IntegrationOptionsImpl;
import org.exbin.bined.netbeans.preferences.IntegrationPreferences;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.about.AboutModule;
import org.exbin.framework.about.api.AboutModuleApi;
import org.exbin.framework.action.ActionModule;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.GroupMenuContributionRule;
import org.exbin.framework.action.api.MenuContribution;
import org.exbin.framework.action.api.MenuManagement;
import org.exbin.framework.action.api.PositionMenuContributionRule;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMenuContributionRule;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.bined.BinEdFileManager;
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
import org.exbin.framework.language.api.IconSetProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.language.api.LanguageProvider;
import org.exbin.framework.operation.undo.OperationUndoModule;
import org.exbin.framework.operation.undo.api.OperationUndoModuleApi;
import org.exbin.framework.options.OptionsModule;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPanelType;
import org.exbin.framework.plugin.language.cs_CZ.LanguageCsCzModule;
import org.exbin.framework.plugin.language.de_DE.LanguageDeDeModule;
import org.exbin.framework.plugin.language.es_ES.LanguageEsEsModule;
import org.exbin.framework.plugin.language.fr_FR.LanguageFrFrModule;
import org.exbin.framework.plugin.language.it_IT.LanguageItItModule;
import org.exbin.framework.plugin.language.ja_JP.LanguageJaJpModule;
import org.exbin.framework.plugin.language.ko_KR.LanguageKoKrModule;
import org.exbin.framework.plugin.language.pl_PL.LanguagePlPlModule;
import org.exbin.framework.plugin.language.ru_RU.LanguageRuRuModule;
import org.exbin.framework.plugin.language.zh_Hans.LanguageZhHansModule;
import org.exbin.framework.plugin.language.zh_Hant.LanguageZhHantModule;
import org.exbin.framework.plugins.iconset.material.IconSetMaterialModule;
import org.exbin.framework.preferences.PreferencesModule;
import org.exbin.framework.preferences.PreferencesWrapper;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.ui.MainOptionsManager;
import org.exbin.framework.ui.UiModule;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.model.LanguageRecord;
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

            // applyIntegrationOptions(initialIntegrationOptions);
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
            } else {
                languageModule.switchToLanguage(Locale.US);
            }
        } else {
            languageModule.switchToLanguage(languageLocale);
        }

        String iconSet = integrationOptions.getIconSet();
        if (!iconSet.isEmpty()) {
            languageModule.switchToIconSet(iconSet);
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

            // Language plugins
            modules.put(LanguageCsCzModule.class, new LanguageCsCzModule());
            modules.put(LanguageDeDeModule.class, new LanguageDeDeModule());
            modules.put(LanguageEsEsModule.class, new LanguageEsEsModule());
            modules.put(LanguageFrFrModule.class, new LanguageFrFrModule());
            modules.put(LanguageItItModule.class, new LanguageItItModule());
            modules.put(LanguageJaJpModule.class, new LanguageJaJpModule());
            modules.put(LanguageKoKrModule.class, new LanguageKoKrModule());
            modules.put(LanguagePlPlModule.class, new LanguagePlPlModule());
            modules.put(LanguageRuRuModule.class, new LanguageRuRuModule());
            modules.put(LanguageZhHansModule.class, new LanguageZhHansModule());
            modules.put(LanguageZhHantModule.class, new LanguageZhHantModule());

            // Iconset plugins
            modules.put(IconSetMaterialModule.class, new IconSetMaterialModule());
        }

        private void init() {
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            preferencesModule.setupAppPreferences(BinEdNetBeansPlugin.class);
            Preferences preferences = preferencesModule.getAppPreferences();

            App.getModule(LanguageCsCzModule.class).register();
            App.getModule(LanguageDeDeModule.class).register();
            App.getModule(LanguageEsEsModule.class).register();
            App.getModule(LanguageFrFrModule.class).register();
            App.getModule(LanguageItItModule.class).register();
            App.getModule(LanguageJaJpModule.class).register();
            App.getModule(LanguageKoKrModule.class).register();
            App.getModule(LanguagePlPlModule.class).register();
            App.getModule(LanguageRuRuModule.class).register();
            App.getModule(LanguageZhHansModule.class).register();
            App.getModule(LanguageZhHantModule.class).register();
            App.getModule(IconSetMaterialModule.class).register();

            initialIntegrationOptions = new IntegrationPreferences(preferences);
            applyIntegrationOptions(initialIntegrationOptions);

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
            optionsModule.setOptionsPanelType(OptionsPanelType.LIST);
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
            binedOperationBouncycastleModule.register();

            BinedToolContentModule binedToolContentModule = App.getModule(BinedToolContentModule.class);

            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
            binedInspectorModule.setEditorProvider(editorProvider);

            BinedCompareModule binedCompareModule = App.getModule(BinedCompareModule.class);
            binedCompareModule.registerToolsOptionsMenuActions();

            BinedBookmarksModule binedBookmarksModule = App.getModule(BinedBookmarksModule.class);

            BinedMacroModule binedMacroModule = App.getModule(BinedMacroModule.class);
            binedMacroModule.setEditorProvider(editorProvider);

            optionsModule.addOptionsPage(new DefaultOptionsPage<IntegrationOptionsImpl>() {

                private IntegrationOptionsPanel panel;

                @Nonnull
                @Override
                public OptionsComponent<IntegrationOptionsImpl> createPanel() {
                    if (panel == null) {
                        panel = new IntegrationOptionsPanel();
                        ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(MainOptionsManager.class);
                        panel.setDefaultLocaleName("<" + resourceBundle.getString("locale.defaultLanguage") + ">");
                        List<LanguageRecord> languageLocales = new ArrayList<>();
                        languageLocales.add(new LanguageRecord(Locale.ROOT, null));
                        languageLocales.add(new LanguageRecord(new Locale("en", "US"), new ImageIcon(getClass().getResource(resourceBundle.getString("locale.englishFlag")))));

                        List<LanguageRecord> languageRecords = new ArrayList<>();
                        List<LanguageProvider> languagePlugins = languageModule.getLanguagePlugins();
                        for (LanguageProvider languageProvider : languagePlugins) {
                            languageRecords.add(new LanguageRecord(languageProvider.getLocale(), languageProvider.getFlag().orElse(null)));
                        }
                        languageLocales.addAll(languageRecords);

                        List<String> iconSets = new ArrayList<>();
                        iconSets.add("");
                        List<String> iconSetNames = new ArrayList<>();
                        iconSetNames.add(resourceBundle.getString("iconset.defaultTheme"));
                        List<IconSetProvider> providers = App.getModule(LanguageModuleApi.class).getIconSets();
                        for (IconSetProvider provider : providers) {
                            iconSets.add(provider.getId());
                            iconSetNames.add(provider.getName());
                        }

                        panel.setLanguageLocales(languageLocales);
                        panel.setIconSets(iconSets, iconSetNames);
                    }

                    return panel;
                }

                @Nonnull
                @Override
                public ResourceBundle getResourceBundle() {
                    return App.getModule(LanguageModuleApi.class).getBundle(IntegrationOptionsPanel.class);
                }

                @Nonnull
                @Override
                public IntegrationOptionsImpl createOptions() {
                    return new IntegrationOptionsImpl();
                }

                @Override
                public void loadFromPreferences(Preferences preferences, IntegrationOptionsImpl options) {
                    options.loadFromPreferences(new IntegrationPreferences(preferences));
                }

                @Override
                public void saveToPreferences(Preferences preferences, IntegrationOptionsImpl options) {
                    options.saveToPreferences(new IntegrationPreferences(preferences));
                }

                @Override
                public void applyPreferencesChanges(IntegrationOptionsImpl options) {
                    applyIntegrationOptions(options);
                }
            });
            binedModule.registerCodeAreaPopupMenu();
            binedModule.registerOptionsPanels();
            binedSearchModule.registerEditFindPopupMenuActions();
            binedOperationModule.registerBlockEditPopupMenuActions();
            binedToolContentModule.registerClipboardContentMenu();
            binedToolContentModule.registerDragDropContentMenu();
            binedInspectorModule.registerViewValuesPanelMenuActions();
            binedInspectorModule.registerOptionsPanels();
            binedMacroModule.registerMacrosPopupMenuActions();
            binedBookmarksModule.registerBookmarksPopupMenuActions();

            String toolsSubMenuId = BinEdNetBeansPlugin.PLUGIN_PREFIX + "toolsMenu";
            MenuManagement menuManagement = actionModule.getMenuManagement(BinedModule.MODULE_ID);
            menuManagement.registerMenu(toolsSubMenuId);
            Action toolsSubMenuAction = new AbstractAction(((FrameModule) frameModule).getResourceBundle().getString("toolsMenu.text")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
            // toolsSubMenuAction.putValue(Action.SHORT_DESCRIPTION, ((FrameModule) frameModule).getResourceBundle().getString("toolsMenu.shortDescription"));
            MenuContribution menuContribution = menuManagement.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, toolsSubMenuId, toolsSubMenuAction);
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMode.BOTTOM_LAST));
            menuContribution = menuManagement.registerMenuItem(toolsSubMenuId, binedCompareModule.createCompareFilesAction());
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMode.TOP));
            menuContribution = menuManagement.registerMenuItem(toolsSubMenuId, binedToolContentModule.createClipboardContentAction());
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMode.TOP));
            menuContribution = menuManagement.registerMenuItem(toolsSubMenuId, binedToolContentModule.createDragDropContentAction());
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMode.TOP));

            String aboutMenuGroup = BinEdNetBeansPlugin.PLUGIN_PREFIX + "helpAboutMenuGroup";
            menuContribution = menuManagement.registerMenuGroup(BinedModule.CODE_AREA_POPUP_MENU_ID, aboutMenuGroup);
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMode.BOTTOM_LAST));
            menuManagement.registerMenuRule(menuContribution, new SeparationMenuContributionRule(SeparationMode.ABOVE));
            menuContribution = menuManagement.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, helpOnlineModule.createOnlineHelpAction());
            menuManagement.registerMenuRule(menuContribution, new GroupMenuContributionRule(aboutMenuGroup));
            menuContribution = menuManagement.registerMenuItem(BinedModule.CODE_AREA_POPUP_MENU_ID, aboutModule.createAboutAction());
            menuManagement.registerMenuRule(menuContribution, new GroupMenuContributionRule(aboutMenuGroup));

            ComponentActivationListener componentActivationListener
                    = frameModule.getFrameHandler().getComponentActivationListener();
            componentActivationListener.updated(EditorProvider.class, editorProvider);
        }

        @Nonnull
        @Override
        public Class getManifestClass() {
            return BinEdNetBeansPlugin.class;
        }

        @Override
        public void launch(Runnable runnable) {
        }

        @Override
        public void launch(String launchModuleId, String[] args) {
        }

        @Nonnull
        @Override
        public <T extends Module> T getModule(Class<T> moduleClass) {
            return (T) modules.get(moduleClass);
        }
    }
}
