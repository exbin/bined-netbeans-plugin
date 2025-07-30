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
package org.exbin.bined.netbeans.options.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.exbin.framework.App;
import org.exbin.framework.options.OptionsModule;
import org.exbin.framework.options.action.OptionsAction;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageReceiver;
import org.exbin.framework.options.gui.OptionsListPanel;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * Binary editor options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@OptionsPanelController.SubRegistration(
        location = "Advanced",
        displayName = "#AdvancedOption_DisplayName_BinEd",
        keywords = "#AdvancedOption_Keywords_BinEd",
        keywordsCategory = "Advanced/BinEd"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_BinEd=BinEd", "AdvancedOption_Keywords_BinEd=BinEd binary/hex editor"})
public final class BinEdOptionsPanelController extends OptionsPanelController {

    private OptionsListPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().loadAllFromPreferences();
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            getPanel().saveAndApplyAll();
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

    @Nonnull
    private OptionsListPanel getPanel() {
        if (panel == null) {
            panel = new OptionsListPanel();
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            String optionsRootCaption = App.getModule(OptionsModuleApi.class).getOptionsRootCaption().orElse(null);
            if (optionsRootCaption != null) {
                panel.setRootCaption(optionsRootCaption);
            }
            OptionsAction.OptionsPagesProvider optionsPagesProvider = (OptionsPageReceiver optionsTreePanel) -> {
                OptionsModule optionsModule = (OptionsModule) App.getModule(OptionsModuleApi.class);
                optionsModule.getOptionsPageManager().passOptionsPages(optionsTreePanel);
            };

            optionsPagesProvider.registerOptionsPages(panel);
            panel.setPreferences(preferencesModule.getAppPreferences());
            panel.pagesFinished();
            panel.loadAllFromPreferences();
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
}
