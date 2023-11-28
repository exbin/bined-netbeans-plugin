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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.bined.netbeans.preferences.IntegrationPreferences;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.preferences.PreferencesWrapper;
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

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(() -> {
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
}
