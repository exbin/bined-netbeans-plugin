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
package org.exbin.framework.editor.text.preferences;

import org.exbin.framework.api.Preferences;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.editor.text.options.TextEncodingOptions;

/**
 * Text editor encodings preferences.
 *
 * @version 0.2.0 2019/06/09
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingPreferences implements TextEncodingOptions {

    public static final String ENCODING_UTF8 = "UTF-8";

    public static final String PREFERENCES_TEXT_ENCODING_PREFIX = "textEncoding.";
    public static final String PREFERENCES_TEXT_ENCODING_DEFAULT = PREFERENCES_TEXT_ENCODING_PREFIX + "default";
    public static final String PREFERENCES_TEXT_ENCODING_SELECTED = "selectedEncoding";

    private final Preferences preferences;

    public TextEncodingPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Nonnull
    public String getDefaultEncoding() {
        return preferences.get(PREFERENCES_TEXT_ENCODING_DEFAULT, ENCODING_UTF8);
    }

    public void setDefaultEncoding(String encodingName) {
        preferences.put(PREFERENCES_TEXT_ENCODING_DEFAULT, encodingName);
    }

    @Nonnull
    @Override
    public String getSelectedEncoding() {
        return preferences.get(PREFERENCES_TEXT_ENCODING_SELECTED, ENCODING_UTF8);
    }

    @Override
    public void setSelectedEncoding(String encodingName) {
        preferences.put(PREFERENCES_TEXT_ENCODING_SELECTED, encodingName);
    }

    @Nonnull
    @Override
    public List<String> getEncodings() {
        List<String> encodings = new ArrayList<>();
        String value;
        int i = 0;
        do {
            value = preferences.get(PREFERENCES_TEXT_ENCODING_PREFIX + Integer.toString(i));
            if (value != null) {
                encodings.add(value);
                i++;
            }
        } while (value != null);

        return encodings;
    }

    @Override
    public void setEncodings(List<String> encodings) {
        for (int i = 0; i < encodings.size(); i++) {
            preferences.put(PREFERENCES_TEXT_ENCODING_PREFIX + Integer.toString(i), encodings.get(i));
        }
        preferences.remove(PREFERENCES_TEXT_ENCODING_PREFIX + Integer.toString(encodings.size()));
    }
}
