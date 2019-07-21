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
package org.exbin.framework.bined.options;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.editor.text.preferences.TextEncodingPreferences;
import org.exbin.framework.gui.options.api.OptionsData;

/**
 * Charset options.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EncodingOptions implements OptionsData {

    private String selectedEncoding = "UTF-8";
    private List<String> encodings = new ArrayList<>();

    @Nonnull
    public String getSelectedEncoding() {
        return selectedEncoding;
    }

    @Nonnull
    public void setSelectedEncoding(String selectedEncoding) {
        this.selectedEncoding = selectedEncoding;
    }

    @Nonnull
    public List<String> getEncodings() {
        return encodings;
    }

    public void setEncodings(List<String> encodings) {
        this.encodings = encodings;
    }

    public void loadFromParameters(TextEncodingPreferences preferences) {
        selectedEncoding = preferences.getSelectedEncoding();
        encodings = preferences.getEncodings();
    }

    public void saveToParameters(TextEncodingPreferences preferences) {
        preferences.setSelectedEncoding(selectedEncoding);
        preferences.setEncodings(encodings);
    }

    public void applyFromCodeArea(ExtCodeArea codeArea) {
        selectedEncoding = ((CharsetCapable) codeArea).getCharset().name();
    }

    public void applyToCodeArea(ExtCodeArea codeArea) {
        ((CharsetCapable) codeArea).setCharset(Charset.forName(selectedEncoding));
    }

    public void setOptions(EncodingOptions charsetOptions) {
        selectedEncoding = charsetOptions.selectedEncoding;
        encodings = new ArrayList<>();
        encodings.addAll(charsetOptions.encodings);
    }
}
