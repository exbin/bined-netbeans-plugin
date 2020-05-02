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

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.editor.text.options.TextFontOptions;

/**
 * Text font preferences.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontPreferences implements TextFontOptions {

    public static final String PREFERENCES_TEXT_FONT_PREFIX = "textFont.";
    public static final String PREFERENCES_TEXT_FONT_DEFAULT = PREFERENCES_TEXT_FONT_PREFIX + "default";
    public static final String PREFERENCES_TEXT_FONT_FAMILY = PREFERENCES_TEXT_FONT_PREFIX + "family";
    public static final String PREFERENCES_TEXT_FONT_SIZE = PREFERENCES_TEXT_FONT_PREFIX + "size";
    public static final String PREFERENCES_TEXT_FONT_UNDERLINE = PREFERENCES_TEXT_FONT_PREFIX + "underline";
    public static final String PREFERENCES_TEXT_FONT_STRIKETHROUGH = PREFERENCES_TEXT_FONT_PREFIX + "strikethrough";
    public static final String PREFERENCES_TEXT_FONT_STRONG = PREFERENCES_TEXT_FONT_PREFIX + "strong";
    public static final String PREFERENCES_TEXT_FONT_ITALIC = PREFERENCES_TEXT_FONT_PREFIX + "italic";
    public static final String PREFERENCES_TEXT_FONT_SUBSCRIPT = PREFERENCES_TEXT_FONT_PREFIX + "subscript";
    public static final String PREFERENCES_TEXT_FONT_SUPERSCRIPT = PREFERENCES_TEXT_FONT_PREFIX + "superscript";

    private final Preferences preferences;

    public TextFontPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean isUseDefaultFont() {
        return preferences.getBoolean(PREFERENCES_TEXT_FONT_DEFAULT, true);
    }

    @Override
    public void setUseDefaultFont(boolean defaultFont) {
        preferences.putBoolean(PREFERENCES_TEXT_FONT_DEFAULT, defaultFont);
    }

    @Nonnull
    @Override
    public Font getFont(Font initialFont) {
        Map<TextAttribute, Object> attribs = getFontAttributes();
        Font font = initialFont.deriveFont(attribs);
        return font;
    }

    @Nonnull
    @Override
    public Map<TextAttribute, Object> getFontAttributes() {
        Map<TextAttribute, Object> attribs = new HashMap<>();
        Optional<String> fontFamily = preferences.get(PREFERENCES_TEXT_FONT_FAMILY);
        if (fontFamily.isPresent()) {
            attribs.put(TextAttribute.FAMILY, fontFamily.get());
        }
        Optional<String> fontSize = preferences.get(PREFERENCES_TEXT_FONT_SIZE);
        if (fontSize.isPresent()) {
            attribs.put(TextAttribute.SIZE, Integer.valueOf(fontSize.get()).floatValue());
        }
        if (preferences.getBoolean(PREFERENCES_TEXT_FONT_UNDERLINE, false)) {
            attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        }
        if (preferences.getBoolean(PREFERENCES_TEXT_FONT_STRIKETHROUGH, false)) {
            attribs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (preferences.getBoolean(PREFERENCES_TEXT_FONT_STRONG, false)) {
            attribs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        if (preferences.getBoolean(PREFERENCES_TEXT_FONT_ITALIC, false)) {
            attribs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (preferences.getBoolean(PREFERENCES_TEXT_FONT_SUBSCRIPT, false)) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        }
        if (preferences.getBoolean(PREFERENCES_TEXT_FONT_SUPERSCRIPT, false)) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
        }
        return attribs;
    }

    public void setFont(Font font) {
        if (font != null) {
            Map<TextAttribute, ?> attribs = font.getAttributes();
            setFontAttributes((Map<TextAttribute, Object>) attribs);
        } else {
            preferences.remove(PREFERENCES_TEXT_FONT_FAMILY);
            preferences.remove(PREFERENCES_TEXT_FONT_SIZE);
            preferences.remove(PREFERENCES_TEXT_FONT_UNDERLINE);
            preferences.remove(PREFERENCES_TEXT_FONT_STRIKETHROUGH);
            preferences.remove(PREFERENCES_TEXT_FONT_STRONG);
            preferences.remove(PREFERENCES_TEXT_FONT_ITALIC);
            preferences.remove(PREFERENCES_TEXT_FONT_SUBSCRIPT);
            preferences.remove(PREFERENCES_TEXT_FONT_SUPERSCRIPT);
        }
    }

    @Override
    public void setFontAttributes(Map<TextAttribute, Object> attribs) {
        String value = (String) attribs.get(TextAttribute.FAMILY);
        if (value != null) {
            preferences.put(PREFERENCES_TEXT_FONT_FAMILY, value);
        } else {
            preferences.remove(PREFERENCES_TEXT_FONT_FAMILY);
        }
        Float fontSize = (Float) attribs.get(TextAttribute.SIZE);
        if (fontSize != null) {
            preferences.put(PREFERENCES_TEXT_FONT_SIZE, Integer.toString((int) (float) fontSize));
        } else {
            preferences.remove(PREFERENCES_TEXT_FONT_SIZE);
        }
        preferences.putBoolean(PREFERENCES_TEXT_FONT_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL.equals(attribs.get(TextAttribute.UNDERLINE)));
        preferences.putBoolean(PREFERENCES_TEXT_FONT_STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON.equals(attribs.get(TextAttribute.STRIKETHROUGH)));
        preferences.putBoolean(PREFERENCES_TEXT_FONT_STRONG, TextAttribute.WEIGHT_BOLD.equals(attribs.get(TextAttribute.WEIGHT)));
        preferences.putBoolean(PREFERENCES_TEXT_FONT_ITALIC, TextAttribute.POSTURE_OBLIQUE.equals(attribs.get(TextAttribute.POSTURE)));
        preferences.putBoolean(PREFERENCES_TEXT_FONT_SUBSCRIPT, TextAttribute.SUPERSCRIPT_SUB.equals(attribs.get(TextAttribute.SUPERSCRIPT)));
        preferences.putBoolean(PREFERENCES_TEXT_FONT_SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER.equals(attribs.get(TextAttribute.SUPERSCRIPT)));
    }
}
