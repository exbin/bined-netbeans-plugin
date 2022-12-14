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
package org.exbin.bined.netbeans.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.preferences.PreferencesWrapper;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

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
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_BinEd=BinEd", "AdvancedOption_Keywords_BinEd=BinEd hexadecimal editor"})
public final class BinEdOptionsPanelController extends OptionsPanelController {

    private BinEdOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().loadFromPreferences();
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(() -> {
            getPanel().saveToPreferences();
            changed = false;
        });
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
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

    private BinEdOptionsPanel getPanel() {
        if (panel == null) {
            panel = new BinEdOptionsPanel(this);
            panel.setPreferences(new BinaryEditorPreferences(new PreferencesWrapper(NbPreferences.forModule(BinaryEditorPreferences.class))));
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
