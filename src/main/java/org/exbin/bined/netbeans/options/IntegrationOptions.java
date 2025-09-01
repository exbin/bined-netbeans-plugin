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
package org.exbin.bined.netbeans.options;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * BinEd plugin preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class IntegrationOptions implements OptionsData {

    public static final String KEY_LOCALE_LANGUAGE = "locale.language";
    public static final String KEY_LOCALE_COUNTRY = "locale.country";
    public static final String KEY_LOCALE_VARIANT = "locale.variant";
    public static final String KEY_LOCALE_TAG = "locale.tag";
    public static final String KEY_ICONSET = "iconset";
    public static final String KEY_REGISTER_FILE_MENU_OPEN_AS_BINARY = "registerFileMenuOpenAsBinary";
    public static final String KEY_REGISTER_OPEN_FILE_AS_BINARY_VIA_DIALOG = "registerOpenFileAsBinaryViaDialog";
    public static final String KEY_REGISTER_CONTEXT_OPEN_AS_BINARY = "registerContextOpenAsBinary";
    public static final String KEY_REGISTER_CONTEXT_TOOLS_OPEN_AS_BINARY = "registerContextToolsBinaryEditor";
    public static final String KEY_REGISTER_BINARY_MULTIVIEW = "registerBinaryMultiview";
    public static final String KEY_REGISTER_DEBUG_VIEW_AS_BINARY = "registerDebugVariablesAsBinary";
    public static final String KEY_REGISTER_BYTE_TO_BYTE_DIFF_TOOL = "registerByteToByteDiffTool";

    public static final String KEY_REGISTER_EDIT_AS_BINARY_FOR_DB_COLUMN = "registerEditAsBinaryForDbColumn";

    private final OptionsStorage storage;

    public IntegrationOptions(OptionsStorage storage) {
        this.storage = storage;
    }

    @Nonnull
    public String getLocaleLanguage() {
        return storage.get(KEY_LOCALE_LANGUAGE, "");
    }

    @Nonnull
    public String getLocaleCountry() {
        return storage.get(KEY_LOCALE_COUNTRY, "");
    }

    @Nonnull
    public String getLocaleVariant() {
        return storage.get(KEY_LOCALE_VARIANT, "");
    }

    @Nonnull
    public String getLocaleTag() {
        return storage.get(KEY_LOCALE_TAG, "");
    }

    @Nonnull
    public Locale getLanguageLocale() {
        String localeTag = getLocaleTag();
        if (!localeTag.trim().isEmpty()) {
            try {
                return Locale.forLanguageTag(localeTag);
            } catch (SecurityException ex) {
                // Ignore it in java webstart
            }
        }

        String localeLanguage = getLocaleLanguage();
        String localeCountry = getLocaleCountry();
        String localeVariant = getLocaleVariant();
        try {
            return new Locale(localeLanguage, localeCountry, localeVariant);
        } catch (SecurityException ex) {
            // Ignore it in java webstart
        }

        return Locale.ROOT;
    }

    public void setLocaleLanguage(String language) {
        storage.put(KEY_LOCALE_LANGUAGE, language);
    }

    public void setLocaleCountry(String country) {
        storage.put(KEY_LOCALE_COUNTRY, country);
    }

    public void setLocaleVariant(String variant) {
        storage.put(KEY_LOCALE_VARIANT, variant);
    }

    public void setLocaleTag(String variant) {
        storage.put(KEY_LOCALE_TAG, variant);
    }

    public void setLanguageLocale(Locale locale) {
        setLocaleTag(locale.toLanguageTag());
        setLocaleLanguage(locale.getLanguage());
        setLocaleCountry(locale.getCountry());
        setLocaleVariant(locale.getVariant());
    }

    @Nonnull
    public String getIconSet() {
        return storage.get(KEY_ICONSET, "");
    }

    public void setIconSet(String iconSet) {
        storage.put(KEY_ICONSET, iconSet);
    }

    public boolean isRegisterFileMenuOpenAsBinary() {
        return storage.getBoolean(KEY_REGISTER_FILE_MENU_OPEN_AS_BINARY, true);
    }

    public void setRegisterFileMenuOpenAsBinary(boolean registerFileMenuOpenAsBinary) {
        storage.putBoolean(KEY_REGISTER_FILE_MENU_OPEN_AS_BINARY, registerFileMenuOpenAsBinary);
    }

    public boolean isRegisterOpenFileAsBinaryViaDialog() {
        return storage.getBoolean(KEY_REGISTER_OPEN_FILE_AS_BINARY_VIA_DIALOG, false);
    }

    public void setRegisterOpenFileAsBinaryViaDialog(boolean registerOpenFileAsBinaryViaDialog) {
        storage.putBoolean(KEY_REGISTER_OPEN_FILE_AS_BINARY_VIA_DIALOG, registerOpenFileAsBinaryViaDialog);
    }

    public boolean isRegisterContextOpenAsBinary() {
        return storage.getBoolean(KEY_REGISTER_CONTEXT_OPEN_AS_BINARY, false);
    }

    public void setRegisterContextOpenAsBinary(boolean registerContextOpenAsBinary) {
        storage.putBoolean(KEY_REGISTER_CONTEXT_OPEN_AS_BINARY, registerContextOpenAsBinary);
    }

    public boolean isRegisterContextToolsOpenAsBinary() {
        return storage.getBoolean(KEY_REGISTER_CONTEXT_TOOLS_OPEN_AS_BINARY, true);
    }

    public void setRegisterContextToolsOpenAsBinary(boolean registerContextToolsOpenAsBinary) {
        storage.putBoolean(KEY_REGISTER_CONTEXT_TOOLS_OPEN_AS_BINARY, registerContextToolsOpenAsBinary);
    }

    public boolean isRegisterBinaryMultiview() {
        return storage.getBoolean(KEY_REGISTER_BINARY_MULTIVIEW, true);
    }

    public void setRegisterBinaryMultiview(boolean registerBinaryMultiview) {
        storage.putBoolean(KEY_REGISTER_BINARY_MULTIVIEW, registerBinaryMultiview);
    }

    public boolean isRegisterDebugViewAsBinary() {
        return storage.getBoolean(KEY_REGISTER_DEBUG_VIEW_AS_BINARY, true);
    }

    public void setRegisterDebugViewAsBinary(boolean registerDebugViewAsBinary) {
        storage.putBoolean(KEY_REGISTER_DEBUG_VIEW_AS_BINARY, registerDebugViewAsBinary);
    }

    public boolean isRegisterByteToByteDiffTool() {
        return storage.getBoolean(KEY_REGISTER_BYTE_TO_BYTE_DIFF_TOOL, true);
    }

    public void setRegisterByteToByteDiffTool(boolean registerByteToByteDiffTool) {
        storage.putBoolean(KEY_REGISTER_BYTE_TO_BYTE_DIFF_TOOL, registerByteToByteDiffTool);
    }

    public boolean isRegisterEditAsBinaryForDbColumn() {
        return storage.getBoolean(KEY_REGISTER_EDIT_AS_BINARY_FOR_DB_COLUMN, true);
    }

    public void setRegisterEditAsBinaryForDbColumn(boolean registerEditAsBinaryForDbColumn) {
        storage.putBoolean(KEY_REGISTER_EDIT_AS_BINARY_FOR_DB_COLUMN, registerEditAsBinaryForDbColumn);
    }

    @Override
    public void copyTo(OptionsData options) {
        IntegrationOptions with = (IntegrationOptions) options;
        with.setLanguageLocale(getLanguageLocale());
        with.setIconSet(getIconSet());
        with.setRegisterFileMenuOpenAsBinary(isRegisterFileMenuOpenAsBinary());
        with.setRegisterOpenFileAsBinaryViaDialog(isRegisterOpenFileAsBinaryViaDialog());
        with.setRegisterContextOpenAsBinary(isRegisterContextOpenAsBinary());
        with.setRegisterContextToolsOpenAsBinary(isRegisterContextToolsOpenAsBinary());
        with.setRegisterBinaryMultiview(isRegisterBinaryMultiview());
        with.setRegisterDebugViewAsBinary(isRegisterDebugViewAsBinary());
        with.setRegisterByteToByteDiffTool(isRegisterByteToByteDiffTool());
        with.setRegisterEditAsBinaryForDbColumn(isRegisterEditAsBinaryForDbColumn());
    }
}
