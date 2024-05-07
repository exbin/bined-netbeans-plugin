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
package org.exbin.bined.netbeans.options.impl;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.bined.netbeans.preferences.IntegrationPreferences;
import org.exbin.framework.options.api.OptionsData;

/**
 * Binary integration preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class IntegrationOptionsImpl implements OptionsData, IntegrationOptions {

    private Locale languageLocale;
    private boolean registerFileMenuOpenAsBinary = true;
    private boolean registerOpenFileAsBinaryViaToolbar = true;
    private boolean registerContextOpenAsBinary = false;
    private boolean registerContextToolsOpenAsBinary = true;
    private boolean registerBinaryMultiview = false;
    private boolean registerDebugViewAsBinary = true;
    private boolean registerByteToByteDiffTool = true;

    private boolean registerEditAsBinaryForDbColumn = true;

    @Nonnull
    @Override
    public Locale getLanguageLocale() {
        return languageLocale;
    }

    @Override
    public void setLanguageLocale(Locale languageLocale) {
        this.languageLocale = languageLocale;
    }

    @Override
    public boolean isRegisterFileMenuOpenAsBinary() {
        return registerFileMenuOpenAsBinary;
    }

    public void setRegisterFileMenuOpenAsBinary(boolean registerFileMenuOpenAsBinary) {
        this.registerFileMenuOpenAsBinary = registerFileMenuOpenAsBinary;
    }

    @Override
    public boolean isRegisterOpenFileAsBinaryViaToolbar() {
        return registerOpenFileAsBinaryViaToolbar;
    }

    public void setRegisterOpenFileAsBinaryViaToolbar(boolean registerOpenFileAsBinaryViaToolbar) {
        this.registerOpenFileAsBinaryViaToolbar = registerOpenFileAsBinaryViaToolbar;
    }

    @Override
    public boolean isRegisterContextOpenAsBinary() {
        return registerContextOpenAsBinary;
    }

    public void setRegisterContextOpenAsBinary(boolean registerContextOpenAsBinary) {
        this.registerContextOpenAsBinary = registerContextOpenAsBinary;
    }

    @Override
    public boolean isRegisterContextToolsOpenAsBinary() {
        return registerContextToolsOpenAsBinary;
    }

    public void setRegisterContextToolsOpenAsBinary(boolean registerContextToolsOpenAsBinary) {
        this.registerContextToolsOpenAsBinary = registerContextToolsOpenAsBinary;
    }

    @Override
    public boolean isRegisterBinaryMultiview() {
        return registerBinaryMultiview;
    }

    public void setRegisterBinaryMultiview(boolean registerBinaryMultiview) {
        this.registerBinaryMultiview = registerBinaryMultiview;
    }

    @Override
    public boolean isRegisterDebugViewAsBinary() {
        return registerDebugViewAsBinary;
    }

    public void setRegisterDebugViewAsBinary(boolean registerDebugViewAsBinary) {
        this.registerDebugViewAsBinary = registerDebugViewAsBinary;
    }

    public boolean isRegisterByteToByteDiffTool() {
        return registerByteToByteDiffTool;
    }

    public void setRegisterByteToByteDiffTool(boolean registerByteToByteDiffTool) {
        this.registerByteToByteDiffTool = registerByteToByteDiffTool;
    }

    @Override
    public boolean isRegisterEditAsBinaryForDbColumn() {
        return registerEditAsBinaryForDbColumn;
    }

    public void setRegisterEditAsBinaryForDbColumn(boolean registerEditAsBinaryForDbColumn) {
        this.registerEditAsBinaryForDbColumn = registerEditAsBinaryForDbColumn;
    }

    public void loadFromPreferences(IntegrationPreferences preferences) {
        languageLocale = preferences.getLanguageLocale();
        registerFileMenuOpenAsBinary = preferences.isRegisterFileMenuOpenAsBinary();
        registerOpenFileAsBinaryViaToolbar = preferences.isRegisterOpenFileAsBinaryViaToolbar();
        registerContextOpenAsBinary = preferences.isRegisterContextOpenAsBinary();
        registerContextToolsOpenAsBinary = preferences.isRegisterContextToolsOpenAsBinary();
        registerBinaryMultiview = preferences.isRegisterBinaryMultiview();
        registerDebugViewAsBinary = preferences.isRegisterDebugViewAsBinary();
        registerByteToByteDiffTool = preferences.isRegisterByteToByteDiffTool();
        registerEditAsBinaryForDbColumn = preferences.isRegisterEditAsBinaryForDbColumn();
    }

    public void saveToPreferences(IntegrationPreferences preferences) {
        preferences.setLanguageLocale(languageLocale);
        preferences.setRegisterFileMenuOpenAsBinary(registerFileMenuOpenAsBinary);
        preferences.setRegisterOpenFileAsBinaryViaToolbar(registerOpenFileAsBinaryViaToolbar);
        preferences.setRegisterContextOpenAsBinary(registerContextOpenAsBinary);
        preferences.setRegisterContextToolsOpenAsBinary(registerContextToolsOpenAsBinary);
        preferences.setRegisterBinaryMultiview(registerBinaryMultiview);
        preferences.setRegisterDebugViewAsBinary(registerDebugViewAsBinary);
        preferences.setRegisterByteToByteDiffTool(registerByteToByteDiffTool);
        preferences.setRegisterEditAsBinaryForDbColumn(registerEditAsBinaryForDbColumn);
    }

    public void setOptions(IntegrationOptionsImpl options) {
        languageLocale = options.getLanguageLocale();
        registerFileMenuOpenAsBinary = options.isRegisterFileMenuOpenAsBinary();
        registerOpenFileAsBinaryViaToolbar = options.isRegisterOpenFileAsBinaryViaToolbar();
        registerContextOpenAsBinary = options.isRegisterContextOpenAsBinary();
        registerContextToolsOpenAsBinary = options.isRegisterContextToolsOpenAsBinary();
        registerBinaryMultiview = options.isRegisterBinaryMultiview();
        registerDebugViewAsBinary = options.isRegisterDebugViewAsBinary();
        registerByteToByteDiffTool = options.isRegisterByteToByteDiffTool();
        registerEditAsBinaryForDbColumn = options.isRegisterEditAsBinaryForDbColumn();
    }
}
