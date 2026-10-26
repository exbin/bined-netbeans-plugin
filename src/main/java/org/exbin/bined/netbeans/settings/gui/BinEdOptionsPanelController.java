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
package org.exbin.bined.netbeans.settings.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jspecify.annotations.NullMarked;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.exbin.jaguif.App;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.context.api.ContextStateManagement;
import org.exbin.jaguif.options.OptionsModule;
import org.netbeans.spi.options.OptionsPanelController;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.settings.SettingsPageReceiver;
import org.exbin.jaguif.options.settings.gui.SettingsListPanel;
import org.exbin.jaguif.options.settings.OptionsSettingsManager;
import org.exbin.jaguif.options.settings.SettingsPage;
import org.exbin.jaguif.options.settings.action.SettingsAction;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.ApplySettingsDependsOnRule;
import org.exbin.jaguif.options.settings.api.ApplySettingsListener;
import org.exbin.jaguif.options.settings.api.InferenceOptions;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsApplier;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsComponentProvider;
import org.exbin.jaguif.options.settings.api.SettingsOptions;
import org.exbin.jaguif.options.settings.api.SettingsOptionsBuilder;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * Binary editor options panel.
 */
@NullMarked
@OptionsPanelController.SubRegistration(
        location = "Advanced",
        displayName = "#AdvancedOption_DisplayName_BinEd",
        keywords = "#AdvancedOption_Keywords_BinEd",
        keywordsCategory = "Advanced/BinEd"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_BinEd=BinEd", "AdvancedOption_Keywords_BinEd=BinEd binary/hex editor"})
public final class BinEdOptionsPanelController extends OptionsPanelController {

    private SettingsListPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        loadAll(panel.getSettingsPages());
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            saveAndApplyAll(panel.getSettingsPages());
            changed = false;
        });
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return true; // TODO getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private SettingsListPanel getPanel() {
        if (panel == null) {
            panel = new SettingsListPanel();
            String optionsRootCaption = App.getModule(OptionsSettingsModuleApi.class).getOptionsRootCaption().orElse(null);
            if (optionsRootCaption != null) {
                panel.setRootCaption(optionsRootCaption);
            }
            SettingsAction.SettingsPagesProvider pagesProvider = (SettingsPageReceiver pageReceiver) -> {
                OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
                ((OptionsSettingsManager) optionsSettingsModule.getMainSettingsManager()).passSettingsPages(pageReceiver);
            };

            pagesProvider.registerSettingsPages(panel);
            panel.pagesFinished();
            loadAll(panel.getSettingsPages());
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private void loadAll(Collection<SettingsPage> pages) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        SettingsOptionsProvider settingsOptionsProvider = optionsSettingsModule.getMainSettingsManager().getSettingsOptionsProvider();

        // TODO Run in top context
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ContextStateManagement contextManager = frameModule.getFrameStateManager();

        for (SettingsPage page : pages) {
            try {
                page.loadAll(settingsOptionsProvider, contextManager);
            } catch (Exception ex) {
                Logger.getLogger(BinEdOptionsPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveAndApplyAll(Collection<SettingsPage> pages) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement mainSettingsManager = optionsSettingsModule.getMainSettingsManager();
        SettingsOptionsProvider settingsOptionsProvider = mainSettingsManager.getSettingsOptionsProvider();

        // TODO Run in top context
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ContextStateManagement contextManager = frameModule.getFrameStateManager();

        for (SettingsPage page : pages) {
            try {
                page.saveAll(settingsOptionsProvider, contextManager);
            } catch (Exception ex) {
                Logger.getLogger(BinEdOptionsPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        applyAllOptions(contextManager, settingsOptionsProvider);
    }

    private void applyAllOptions(ContextStateManagement contextManager, SettingsOptionsProvider settingsOptionsProvider) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement mainSettingsManager = optionsSettingsModule.getMainSettingsManager();
        for (Class<? extends SettingsOptions> optionsClass : mainSettingsManager.getOptionsClasses()) {
            mainSettingsManager.applyOptions(optionsClass, settingsOptionsProvider);
        }

        mainSettingsManager.applyAllOptions(contextManager, settingsOptionsProvider);
    }
}
