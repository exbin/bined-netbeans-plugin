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

import org.exbin.framework.api.Preferences;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.framework.editor.text.preferences.TextFontPreferences;

/**
 * Code area preferences.
 *
 * @version 0.2.1 2019/07/12
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CodeAreaPreferences {

    public static final String PREFERENCES_CODE_TYPE = "codeType";
    public static final String PREFERENCES_SHOW_UNPRINTABLES = "showNonpritables";
    public static final String PREFERENCES_BYTES_PER_LINE = "bytesPerLine";
    public static final String PREFERENCES_LINE_NUMBERS_LENGTH_TYPE = "lineNumbersLengthType";
    public static final String PREFERENCES_LINE_NUMBERS_LENGTH = "lineNumbersLength";
    public static final String PREFERENCES_VIEW_MODE = "viewMode";
    public static final String PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND = "showLineNumbersBackground";
    public static final String PREFERENCES_POSITION_CODE_TYPE = "positionCodeType";
    public static final String PREFERENCES_HEX_CHARACTERS_CASE = "hexCharactersCase";
    public static final String PREFERENCES_CODE_COLORIZATION = "codeColorization";
    public static final String PREFERENCES_ROW_WRAPPING_MODE = "rowWrappingMode";
    public static final String PREFERENCES_MAX_BYTES_PER_ROW = "maxBytesPerRow";
    public static final String PREFERENCES_MIN_ROW_POSITION_LENGTH = "minRowPositionLength";
    public static final String PREFERENCES_MAX_ROW_POSITION_LENGTH = "maxRowPositionLength";

    private final Preferences preferences;

    public CodeAreaPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Nonnull
    public Font getCodeFont(Font initialFont) {
        String value;
        Map<TextAttribute, Object> attribs = new HashMap<>();
        value = preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_FAMILY);
        if (value != null) {
            attribs.put(TextAttribute.FAMILY, value);
        }
        value = preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_SIZE);
        if (value != null) {
            attribs.put(TextAttribute.SIZE, new Integer(value).floatValue());
        }
        if (Boolean.valueOf(preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_UNDERLINE, null))) {
            attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
        }
        if (Boolean.valueOf(preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_STRIKETHROUGH, null))) {
            attribs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        }
        if (Boolean.valueOf(preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_STRONG, null))) {
            attribs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }
        if (Boolean.valueOf(preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_ITALIC, null))) {
            attribs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        if (Boolean.valueOf(preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_SUBSCRIPT, null))) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        }
        if (Boolean.valueOf(preferences.get(TextFontPreferences.PREFERENCES_TEXT_FONT_SUPERSCRIPT, null))) {
            attribs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
        }
        Font font = initialFont.deriveFont(attribs);
        return font;
    }

    public void setCodeFont(Font font) {
        if (font != null) {
            Map<TextAttribute, ?> attribs = font.getAttributes();
            String value = (String) attribs.get(TextAttribute.FAMILY);
            if (value != null) {
                preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_FAMILY, value);
            } else {
                preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_FAMILY);
            }
            Float fontSize = (Float) attribs.get(TextAttribute.SIZE);
            if (fontSize != null) {
                preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_SIZE, Integer.toString((int) (float) fontSize));
            } else {
                preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_SIZE);
            }
            preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_UNDERLINE, Boolean.toString(TextAttribute.UNDERLINE_LOW_ONE_PIXEL.equals(attribs.get(TextAttribute.UNDERLINE))));
            preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_STRIKETHROUGH, Boolean.toString(TextAttribute.STRIKETHROUGH_ON.equals(attribs.get(TextAttribute.STRIKETHROUGH))));
            preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_STRONG, Boolean.toString(TextAttribute.WEIGHT_BOLD.equals(attribs.get(TextAttribute.WEIGHT))));
            preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_ITALIC, Boolean.toString(TextAttribute.POSTURE_OBLIQUE.equals(attribs.get(TextAttribute.POSTURE))));
            preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_SUBSCRIPT, Boolean.toString(TextAttribute.SUPERSCRIPT_SUB.equals(attribs.get(TextAttribute.SUPERSCRIPT))));
            preferences.put(TextFontPreferences.PREFERENCES_TEXT_FONT_SUPERSCRIPT, Boolean.toString(TextAttribute.SUPERSCRIPT_SUPER.equals(attribs.get(TextAttribute.SUPERSCRIPT))));
        } else {
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_FAMILY);
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_SIZE);
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_UNDERLINE);
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_STRIKETHROUGH);
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_STRONG);
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_ITALIC);
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_SUBSCRIPT);
            preferences.remove(TextFontPreferences.PREFERENCES_TEXT_FONT_SUPERSCRIPT);
        }
    }

    @Nonnull
    public CodeType getCodeType() {
        CodeType defaultCodeType = CodeType.HEXADECIMAL;
        try {
            return CodeType.valueOf(preferences.get(PREFERENCES_CODE_TYPE, defaultCodeType.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaPreferences.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCodeType;
        }
    }

    public void setCodeType(CodeType codeType) {
        preferences.put(PREFERENCES_CODE_TYPE, codeType.name());
    }

    public boolean isShowNonprintables() {
        return preferences.getBoolean(PREFERENCES_SHOW_UNPRINTABLES, false);
    }

    public void setShowUnprintables(boolean showUnprintables) {
        preferences.putBoolean(PREFERENCES_SHOW_UNPRINTABLES, showUnprintables);
    }

    @Nonnull
    public CodeCharactersCase getCodeCharactersCase() {
        CodeCharactersCase defaultCharactersCase = CodeCharactersCase.UPPER;
        try {
            return CodeCharactersCase.valueOf(preferences.get(PREFERENCES_HEX_CHARACTERS_CASE, defaultCharactersCase.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaPreferences.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCharactersCase;
        }
    }

    public void setCodeCharactersCase(CodeCharactersCase codeCharactersCase) {
        preferences.put(PREFERENCES_HEX_CHARACTERS_CASE, codeCharactersCase.name());
    }

    @Nonnull
    public PositionCodeType getPositionCodeType() {
        PositionCodeType defaultCodeType = PositionCodeType.HEXADECIMAL;
        try {
            return PositionCodeType.valueOf(preferences.get(PREFERENCES_POSITION_CODE_TYPE, defaultCodeType.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaPreferences.class.getName()).log(Level.SEVERE, null, ex);
            return defaultCodeType;
        }
    }

    public void setPositionCodeType(PositionCodeType positionCodeType) {
        preferences.put(PREFERENCES_POSITION_CODE_TYPE, positionCodeType.name());
    }

    @Nonnull
    public CodeAreaViewMode getViewMode() {
        CodeAreaViewMode defaultMode = CodeAreaViewMode.DUAL;
        try {
            return CodeAreaViewMode.valueOf(preferences.get(PREFERENCES_VIEW_MODE, defaultMode.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CodeAreaPreferences.class.getName()).log(Level.SEVERE, null, ex);
            return defaultMode;
        }
    }

    public void setViewMode(CodeAreaViewMode viewMode) {
        preferences.put(PREFERENCES_VIEW_MODE, viewMode.name());
    }

    public boolean isPaintRowPosBackground() {
        return preferences.getBoolean(PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND, true);
    }

    public void setPaintRowPosBackground(boolean paintRowPosBackground) {
        preferences.putBoolean(PREFERENCES_PAINT_LINE_NUMBERS_BACKGROUND, paintRowPosBackground);
    }

    public boolean isCodeColorization() {
        return preferences.getBoolean(PREFERENCES_CODE_COLORIZATION, true);
    }

    public void setCodeColorization(boolean codeColorization) {
        preferences.putBoolean(PREFERENCES_CODE_COLORIZATION, codeColorization);
    }

    public boolean isUseDefaultFont() {
        return preferences.getBoolean(TextFontPreferences.PREFERENCES_TEXT_FONT_DEFAULT, true);
    }

    public void setUseDefaultFont(boolean useDefaultFont) {
        preferences.putBoolean(TextFontPreferences.PREFERENCES_TEXT_FONT_DEFAULT, useDefaultFont);
    }

    @Nonnull
    public RowWrappingCapable.RowWrappingMode getRowWrappingMode() {
        RowWrappingCapable.RowWrappingMode defaultMode = RowWrappingCapable.RowWrappingMode.NO_WRAPPING;
        try {
            return RowWrappingCapable.RowWrappingMode.valueOf(preferences.get(PREFERENCES_ROW_WRAPPING_MODE, defaultMode.name()));
        } catch (Exception ex) {
            Logger.getLogger(CodeAreaPreferences.class.getName()).log(Level.SEVERE, null, ex);
            return defaultMode;
        }
    }

    public void setRowWrappingMode(RowWrappingCapable.RowWrappingMode rowWrappingMode) {
        preferences.put(PREFERENCES_ROW_WRAPPING_MODE, rowWrappingMode.name());
    }

    public int getMaxBytesPerRow() {
        return preferences.getInt(PREFERENCES_MAX_BYTES_PER_ROW, 16);
    }

    public void setMaxBytesPerRow(int maxBytesPerRow) {
        preferences.putInt(PREFERENCES_MAX_BYTES_PER_ROW, maxBytesPerRow);
    }

    public int getMinRowPositionLength() {
        return preferences.getInt(PREFERENCES_MIN_ROW_POSITION_LENGTH, 0);
    }

    public void setMinRowPositionLength(int minRowPositionLength) {
        preferences.putInt(PREFERENCES_MIN_ROW_POSITION_LENGTH, minRowPositionLength);
    }

    public int getMaxRowPositionLength() {
        return preferences.getInt(PREFERENCES_MAX_ROW_POSITION_LENGTH, 0);
    }

    public void setMaxRowPositionLength(int maxRowPositionLength) {
        preferences.putInt(PREFERENCES_MAX_ROW_POSITION_LENGTH, maxRowPositionLength);
    }
}
