/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.bined.preferences;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.PreferencesWrapper;
import org.openide.util.NbPreferences;

/**
 * Hexadecimal editor preferences.
 *
 * @version 0.2.0 2019/03/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorPreferences {

    private final static String PREFERENCES_VERSION = "version";
    private final static String PREFERENCES_VERSION_VALUE = "0.2.0";

    private final Preferences preferences;

    private final EditorParameters editorParameters;
    private final CodeAreaParameters codeAreaParameters;
    private final LayoutParameters layoutParameters;
    private final ThemeParameters themeParameters;
    private final ColorParameters colorParameters;

    public BinaryEditorPreferences(Preferences preferences) {
        this.preferences = preferences;
        
        editorParameters = new EditorParameters(preferences);
        codeAreaParameters = new CodeAreaParameters(preferences);
        layoutParameters = new LayoutParameters(preferences);
        themeParameters = new ThemeParameters(preferences);
        colorParameters = new ColorParameters(preferences);

        String storedVersion = preferences.get(PREFERENCES_VERSION, "");
        if ("".equals(storedVersion)) {
            try {
                importLegacyPreferences();
            } finally {
                preferences.put(PREFERENCES_VERSION, PREFERENCES_VERSION_VALUE);
                preferences.flush();
            }
        }
    }

    @Nonnull
    public EditorParameters getEditorParameters() {
        return editorParameters;
    }

    @Nonnull
    public CodeAreaParameters getCodeAreaParameters() {
        return codeAreaParameters;
    }

    @Nonnull
    public LayoutParameters getLayoutParameters() {
        return layoutParameters;
    }

    @Nonnull
    public ThemeParameters getThemeParameters() {
        return themeParameters;
    }

    @Nonnull
    public ColorParameters getColorParameters() {
        return colorParameters;
    }

    private void importLegacyPreferences() {
        LegacyPreferences legacyPreferences = new LegacyPreferences(new PreferencesWrapper(NbPreferences.forModule(BinaryEditorPreferences.class)));
    }
}
