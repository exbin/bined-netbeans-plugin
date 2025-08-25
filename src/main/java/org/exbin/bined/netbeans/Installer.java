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
import javax.swing.ImageIcon;
import org.exbin.bined.netbeans.diff.BinaryDiffVisualizer;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.bined.netbeans.options.gui.IntegrationOptionsPanel;
import org.exbin.framework.App;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.about.AboutModule;
import org.exbin.framework.about.api.AboutModuleApi;
import org.exbin.framework.action.ActionModule;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ComponentActivationListener;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.menu.api.SeparationMenuContributionRule;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.bookmarks.BinedBookmarksModule;
import org.exbin.framework.bined.compare.BinedCompareModule;
import org.exbin.framework.bined.editor.BinedEditorModule;
import org.exbin.framework.bined.inspector.BinedInspectorModule;
import org.exbin.framework.bined.macro.BinedMacroModule;
import org.exbin.framework.bined.objectdata.BinedObjectDataModule;
import org.exbin.framework.bined.operation.BinedOperationModule;
import org.exbin.framework.bined.operation.bouncycastle.BinedOperationBouncycastleModule;
import org.exbin.framework.bined.search.BinedSearchModule;
import org.exbin.framework.bined.theme.BinedThemeModule;
import org.exbin.framework.bined.tool.content.BinedToolContentModule;
import org.exbin.framework.bined.viewer.BinedViewerModule;
import org.exbin.framework.component.ComponentModule;
import org.exbin.framework.component.api.ComponentModuleApi;
import org.exbin.framework.editor.api.EditorModuleApi;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.EditorModule;
import org.exbin.framework.file.FileModule;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.frame.FrameModule;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.help.HelpModule;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.help.online.HelpOnlineModule;
import org.exbin.framework.language.LanguageModule;
import org.exbin.framework.language.api.IconSetProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.language.api.LanguageProvider;
import org.exbin.framework.menu.MenuModule;
import org.exbin.framework.menu.api.MenuModuleApi;
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
import org.exbin.framework.plugin.language.fi_FI.LanguageFiFiModule;
import org.exbin.framework.plugin.language.fr_FR.LanguageFrFrModule;
import org.exbin.framework.plugin.language.hi_IN.LanguageHiInModule;
import org.exbin.framework.plugin.language.in_ID.LanguageInIdModule;
import org.exbin.framework.plugin.language.it_IT.LanguageItItModule;
import org.exbin.framework.plugin.language.ja_JP.LanguageJaJpModule;
import org.exbin.framework.plugin.language.ko_KR.LanguageKoKrModule;
import org.exbin.framework.plugin.language.pl_PL.LanguagePlPlModule;
import org.exbin.framework.plugin.language.pt_PT.LanguagePtPtModule;
import org.exbin.framework.plugin.language.ru_RU.LanguageRuRuModule;
import org.exbin.framework.plugin.language.sv_SE.LanguageSvSeModule;
import org.exbin.framework.plugin.language.vi_VN.LanguageViVnModule;
import org.exbin.framework.plugin.language.zh_HK.LanguageZhHkModule;
import org.exbin.framework.plugin.language.zh_Hans.LanguageZhHansModule;
import org.exbin.framework.plugin.language.zh_Hant.LanguageZhHantModule;
import org.exbin.framework.plugin.language.zh_TW.LanguageZhTwModule;
import org.exbin.framework.plugins.iconset.material.IconSetMaterialModule;
import org.exbin.framework.preferences.PreferencesModule;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.toolbar.ToolBarModule;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.ui.UiModule;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.gui.LanguageOptionsPanel;
import org.exbin.framework.ui.model.LanguageRecord;
import org.exbin.framework.ui.theme.ThemeOptionsManager;
import org.exbin.framework.ui.theme.UiThemeModule;
import org.exbin.framework.ui.theme.api.UiThemeModuleApi;
import org.exbin.framework.window.WindowModule;
import org.exbin.framework.window.api.WindowModuleApi;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

/**
 * Installer for BinEd plugin.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class Installer extends ModuleInstall {

    private static final String BINARY_PLUGIN_ID = "binary";
    private static final List<IntegrationOptionsListener> INTEGRATION_OPTIONS_LISTENERS = new ArrayList<>();

    private static IntegrationOptions initialIntegrationOptions = null;
    private static boolean binEdInitialized = false;

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            Installer.initBinEd();
        });
    }
    

    @Override
    public void uninstalled() {
        WindowManager.getDefault().invokeWhenUIReady(() -> {
            uninstallIntegration();
        });
    }
    
    synchronized public static void initBinEd() {
        if (!binEdInitialized) {
            binEdInitialized = true;
            AppModuleProvider appModuleProvider = new AppModuleProvider();
            appModuleProvider.createModules();
            App.setModuleProvider(appModuleProvider);
            appModuleProvider.init();
            initIntegrations();

        }

        // applyIntegrationOptions(initialIntegrationOptions);
    }

    private static void initIntegrations() {
        FileOpenAsBinaryAction.registerIntegration();
        OpenAsBinaryAction.registerIntegration();
        OpenAsBinaryToolsAction.registerIntegration();
        BinEdEditor.registerIntegration();
        BinaryDiffVisualizer.registerIntegration();
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
            modules.put(UiThemeModuleApi.class, new UiThemeModule());
            modules.put(HelpModuleApi.class, new HelpModule());
            modules.put(MenuModuleApi.class, new MenuModule());
            modules.put(ToolBarModuleApi.class, new ToolBarModule());
            modules.put(ComponentModuleApi.class, new ComponentModule());
            modules.put(WindowModuleApi.class, new WindowModule());
            modules.put(FrameModuleApi.class, new FrameModule());
            modules.put(FileModuleApi.class, new FileModule());
            modules.put(EditorModuleApi.class, new EditorModule());
            modules.put(HelpOnlineModule.class, new HelpOnlineModule());
            modules.put(BinedModule.class, new BinedModule());
            modules.put(BinedViewerModule.class, new BinedViewerModule());
            modules.put(BinedEditorModule.class, new BinedEditorModule());
            modules.put(BinedThemeModule.class, new BinedThemeModule());
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
            modules.put(LanguageFiFiModule.class, new LanguageFiFiModule());
            modules.put(LanguageFrFrModule.class, new LanguageFrFrModule());
            modules.put(LanguageHiInModule.class, new LanguageHiInModule());
            modules.put(LanguageInIdModule.class, new LanguageInIdModule());
            modules.put(LanguageItItModule.class, new LanguageItItModule());
            modules.put(LanguageJaJpModule.class, new LanguageJaJpModule());
            modules.put(LanguageKoKrModule.class, new LanguageKoKrModule());
            modules.put(LanguagePlPlModule.class, new LanguagePlPlModule());
            modules.put(LanguagePtPtModule.class, new LanguagePtPtModule());
            modules.put(LanguageRuRuModule.class, new LanguageRuRuModule());
            modules.put(LanguageSvSeModule.class, new LanguageSvSeModule());
            modules.put(LanguageViVnModule.class, new LanguageViVnModule());
            modules.put(LanguageZhHansModule.class, new LanguageZhHansModule());
            modules.put(LanguageZhHantModule.class, new LanguageZhHantModule());
            modules.put(LanguageZhHkModule.class, new LanguageZhHkModule());
            modules.put(LanguageZhTwModule.class, new LanguageZhTwModule());

            // Iconset plugins
            modules.put(IconSetMaterialModule.class, new IconSetMaterialModule());
        }

        private void init() {
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            preferencesModule.setupAppPreferences(BinEdNetBeansPlugin.class);
            OptionsStorage optionsStorage = preferencesModule.getAppPreferences();

            App.getModule(LanguageCsCzModule.class).register();
            App.getModule(LanguageDeDeModule.class).register();
            App.getModule(LanguageEsEsModule.class).register();
            App.getModule(LanguageFiFiModule.class).register();
            App.getModule(LanguageFrFrModule.class).register();
            App.getModule(LanguageHiInModule.class).register();
            App.getModule(LanguageInIdModule.class).register();
            App.getModule(LanguageItItModule.class).register();
            App.getModule(LanguageJaJpModule.class).register();
            App.getModule(LanguageKoKrModule.class).register();
            App.getModule(LanguagePlPlModule.class).register();
            App.getModule(LanguageRuRuModule.class).register();
            App.getModule(LanguageSvSeModule.class).register();
            App.getModule(LanguageViVnModule.class).register();
            App.getModule(LanguageZhHansModule.class).register();
            App.getModule(LanguageZhHantModule.class).register();
            App.getModule(LanguageZhHkModule.class).register();
            App.getModule(LanguageZhTwModule.class).register();
            App.getModule(IconSetMaterialModule.class).register();

            BinedBookmarksModule binedBookmarksModule = App.getModule(BinedBookmarksModule.class);
            binedBookmarksModule.register();
            BinedMacroModule binedMacroModule = App.getModule(BinedMacroModule.class);
            binedMacroModule.register();
            BinedOperationBouncycastleModule binedOperationBouncycastleModule = App.getModule(BinedOperationBouncycastleModule.class);
            binedOperationBouncycastleModule.register();

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            ResourceBundle bundle = languageModule.getBundle(BinEdNetBeansPlugin.class);
            languageModule.setAppBundle(bundle);

            initialIntegrationOptions = new IntegrationOptions(optionsStorage);
            applyIntegrationOptions(initialIntegrationOptions);

            UiModuleApi uiModule = App.getModule(UiModuleApi.class);
            uiModule.executePostInitActions();
            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            frameModule.init();
            ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
            MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);

            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
            windowModule.setHideHeaderPanels(true);

            AboutModuleApi aboutModule = App.getModule(AboutModuleApi.class);
            OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
            optionsModule.setOptionsPanelType(OptionsPanelType.LIST);
            optionsModule.setOptionsRootCaption(App.getModule(LanguageModuleApi.class).getBundle(IntegrationOptionsPanel.class).getString("options.caption"));
            // optionsModule.registerMenuAction();

            HelpOnlineModule helpOnlineModule = App.getModule(HelpOnlineModule.class);
            try {
                helpOnlineModule.setOnlineHelpUrl(new URL(bundle.getString("online_help_url")));
                helpOnlineModule.registerOpeningHandler();
            } catch (MalformedURLException ex) {
                Logger.getLogger(Installer.class.getName()).log(Level.SEVERE, null, ex);
            }

            BinEdNetBeansEditorProvider editorProvider = new BinEdNetBeansEditorProvider();
            EditorModuleApi editorModule = App.getModule(EditorModuleApi.class);
            editorModule.registerEditor(BINARY_PLUGIN_ID, editorProvider);
            BinedModule binedModule = App.getModule(BinedModule.class);
            BinedViewerModule binedViewerModule = App.getModule(BinedViewerModule.class);
            BinedEditorModule binedEditorModule = App.getModule(BinedEditorModule.class);
            BinedThemeModule binedThemeModule = App.getModule(BinedThemeModule.class);
            binedModule.setEditorProvider(editorProvider);
            binedBookmarksModule.getBookmarksManager().setEditorProvider(editorProvider);
            binedMacroModule.setEditorProvider(editorProvider);

            BinedSearchModule binedSearchModule = App.getModule(BinedSearchModule.class);
            binedSearchModule.setEditorProvider(editorProvider);

            BinedOperationModule binedOperationModule = App.getModule(BinedOperationModule.class);
            binedOperationModule.addBasicMethods();

            BinedToolContentModule binedToolContentModule = App.getModule(BinedToolContentModule.class);

            BinedInspectorModule binedInspectorModule = App.getModule(BinedInspectorModule.class);
            binedInspectorModule.setEditorProvider(editorProvider);

            BinedCompareModule binedCompareModule = App.getModule(BinedCompareModule.class);
            binedCompareModule.registerToolsOptionsMenuActions();

            optionsModule.getOptionsPageManagement(BinedModule.MODULE_ID).registerPage(new DefaultOptionsPage<IntegrationOptions>() {

                public static final String PAGE_ID = "integration";

                private IntegrationOptionsPanel panel;

                @Nonnull
                @Override
                public String getId() {
                    return PAGE_ID;
                }

                @Nonnull
                @Override
                public OptionsComponent<IntegrationOptions> createComponent() {
                    if (panel == null) {
                        panel = new IntegrationOptionsPanel();
                        ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(LanguageOptionsPanel.class);
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
                        ResourceBundle themeResourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ThemeOptionsManager.class);
                        iconSetNames.add(themeResourceBundle.getString("iconset.defaultTheme"));
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
                public IntegrationOptions createOptions() {
                    return new IntegrationOptions(optionsStorage);
                }

                @Override
                public void loadFromPreferences(OptionsStorage optionsStorage, IntegrationOptions options) {
                    new IntegrationOptions(optionsStorage).copyTo(options);
                }

                @Override
                public void saveToPreferences(OptionsStorage optionsStorage, IntegrationOptions options) {
                    options.copyTo(new IntegrationOptions(optionsStorage));
                }

                @Override
                public void applyPreferencesChanges(IntegrationOptions options) {
                    applyIntegrationOptions(options);
                }
            });
            binedModule.registerCodeAreaPopupMenu();
            binedViewerModule.registerCodeAreaPopupMenu();
            binedEditorModule.registerCodeAreaPopupMenu();
            editorModule.registerOptionsPanels();
            binedViewerModule.registerOptionsPanels();
            binedViewerModule.registerViewModeMenu();
            binedViewerModule.registerCodeTypeMenu();
            binedViewerModule.registerPositionCodeTypeMenu();
            binedViewerModule.registerHexCharactersCaseHandlerMenu();
            binedViewerModule.registerLayoutMenu();
            binedEditorModule.registerOptionsPanels();
            binedThemeModule.registerOptionsPanels();
            binedSearchModule.registerEditFindPopupMenuActions();
            binedSearchModule.registerSearchComponent();
            binedOperationModule.registerBlockEditPopupMenuActions();
            binedToolContentModule.registerClipboardContentMenu();
            binedToolContentModule.registerDragDropContentMenu();
            binedInspectorModule.registerOptionsPanels();
            binedInspectorModule.registerShowParsingPanelMenuActions();
            binedInspectorModule.registerShowParsingPanelPopupMenuActions();

            String toolsSubMenuId = BinEdNetBeansPlugin.PLUGIN_PREFIX + "toolsMenu";
            MenuManagement menuManagement = menuModule.getMenuManagement(BinedModule.CODE_AREA_POPUP_MENU_ID, BinedModule.MODULE_ID);
            Action toolsSubMenuAction = new AbstractAction(((FrameModule) frameModule).getResourceBundle().getString("toolsMenu.text")) {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            };
            // toolsSubMenuAction.putValue(Action.SHORT_DESCRIPTION, ((FrameModule) frameModule).getResourceBundle().getString("toolsMenu.shortDescription"));
            MenuContribution menuContribution = menuManagement.registerMenuItem(toolsSubMenuId, toolsSubMenuAction);
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.BOTTOM_LAST));
            MenuManagement subMenu = menuManagement.getSubMenu(toolsSubMenuId);
            menuContribution = subMenu.registerMenuItem(binedCompareModule.createCompareFilesAction());
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
            menuContribution = subMenu.registerMenuItem(binedToolContentModule.createClipboardContentAction());
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
            menuContribution = subMenu.registerMenuItem(binedToolContentModule.createDragDropContentAction());
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));

            String aboutMenuGroup = BinEdNetBeansPlugin.PLUGIN_PREFIX + "helpAboutMenuGroup";
            menuContribution = menuManagement.registerMenuGroup(aboutMenuGroup);
            menuManagement.registerMenuRule(menuContribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.BOTTOM_LAST));
            menuManagement.registerMenuRule(menuContribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.ABOVE));
            menuContribution = menuManagement.registerMenuItem(helpOnlineModule.createOnlineHelpAction());
            menuManagement.registerMenuRule(menuContribution, new GroupMenuContributionRule(aboutMenuGroup));
            menuContribution = menuManagement.registerMenuItem(aboutModule.createAboutAction());
            menuManagement.registerMenuRule(menuContribution, new GroupMenuContributionRule(aboutMenuGroup));

            ComponentActivationListener componentActivationListener
                    = frameModule.getFrameHandler().getComponentActivationListener();
            componentActivationListener.updated(EditorProvider.class, editorProvider);
            componentActivationListener.updated(DialogParentComponent.class, () -> frameModule.getFrame());
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

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <T extends Module> T getModule(Class<T> moduleClass) {
            return (T) modules.get(moduleClass);
        }
    }
}
