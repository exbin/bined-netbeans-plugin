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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;

/**
 * Text font preferences.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontPreferences {

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

    public boolean isUseDefaultFont() {
        return preferences.getBoolean(PREFERENCES_TEXT_FONT_DEFAULT, true);
    }

    public void setUseDefaultFont(boolean defaultFont) {
        preferences.putBoolean(PREFERENCES_TEXT_FONT_DEFAULT, defaultFont);
    }

    @Nonnull
    public Font getFont(Font initialFont) {
        Map<TextAttribute, Object> attribs = getFontAttribs();
        Font font = initialFont.deriveFont(attribs);
        return font;
    }

    @Nonnull
    public Map<TextAttribute, Object> getFontAttribs() {
        String value;
        Map<TextAttribute, Object> attribs = new HashMap<>();
        value = preferences.get(PREFERENCES_TEXT_FONT_FAMILY);
        if (value != null) {
            attribs.put(TextAttribute.FAMILY, value);
        }
        value = preferences.get(PREFERENCES_TEXT_FONT_SIZE);
        if (value != null) {
            attribs.put(TextAttribute.SIZE, Integer.valueOf(value).floatValue());
        }
        if (Boolean.valueOf(preferences.get(PREFERENCES_TEXT_FONT_UNDERLINE, null))) {
            attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        }
        if (Boolean.valueOf(preferences.get(PREFERENCES_TEXT_FONT_STRIKETHROUGH, null))) {
            attribs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (Boolean.valueOf(preferences.get(PREFERENCES_TEXT_FONT_STRONG, null))) {
            attribs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        if (Boolean.valueOf(preferences.get(PREFERENCES_TEXT_FONT_ITALIC, null))) {
            attribs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (Boolean.valueOf(preferences.get(PREFERENCES_TEXT_FONT_SUBSCRIPT, null))) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        }
        if (Boolean.valueOf(preferences.get(PREFERENCES_TEXT_FONT_SUPERSCRIPT, null))) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
        }
        return attribs;
    }

    public void setFont(Font font) {
        if (font != null) {
            Map<TextAttribute, ?> attribs = font.getAttributes();
            setFontAttribs(attribs);
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

    public void setFontAttribs(Map<TextAttribute, ?> attribs) {
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
        preferences.put(PREFERENCES_TEXT_FONT_UNDERLINE, Boolean.toString(TextAttribute.UNDERLINE_LOW_ONE_PIXEL.equals(attribs.get(TextAttribute.UNDERLINE))));
        preferences.put(PREFERENCES_TEXT_FONT_STRIKETHROUGH, Boolean.toString(TextAttribute.STRIKETHROUGH_ON.equals(attribs.get(TextAttribute.STRIKETHROUGH))));
        preferences.put(PREFERENCES_TEXT_FONT_STRONG, Boolean.toString(TextAttribute.WEIGHT_BOLD.equals(attribs.get(TextAttribute.WEIGHT))));
        preferences.put(PREFERENCES_TEXT_FONT_ITALIC, Boolean.toString(TextAttribute.POSTURE_OBLIQUE.equals(attribs.get(TextAttribute.POSTURE))));
        preferences.put(PREFERENCES_TEXT_FONT_SUBSCRIPT, Boolean.toString(TextAttribute.SUPERSCRIPT_SUB.equals(attribs.get(TextAttribute.SUPERSCRIPT))));
        preferences.put(PREFERENCES_TEXT_FONT_SUPERSCRIPT, Boolean.toString(TextAttribute.SUPERSCRIPT_SUPER.equals(attribs.get(TextAttribute.SUPERSCRIPT))));
    }
}
