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

    private boolean registerFileMenuOpenAsBinary = false;
    private boolean registerOpenFileAsBinaryViaToolbar = true;
    private boolean registerContextOpenAsBinary = false;
    private boolean registerContextOpenInBinaryEditor = true;
    private boolean registerDebugViewAsBinary = true;
    private boolean registerByteToByteDiffTool = true;

    private boolean registerEditAsBinaryForDbColumn = true;

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
    public boolean isRegisterContextOpenInBinaryEditor() {
        return registerContextOpenInBinaryEditor;
    }

    public void setRegisterContextOpenInBinaryEditor(boolean registerContextOpenInBinaryEditor) {
        this.registerContextOpenInBinaryEditor = registerContextOpenInBinaryEditor;
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
        registerFileMenuOpenAsBinary = preferences.isRegisterFileMenuOpenAsBinary();
        registerOpenFileAsBinaryViaToolbar = preferences.isRegisterOpenFileAsBinaryViaToolbar();
        registerContextOpenAsBinary = preferences.isRegisterContextOpenAsBinary();
        registerContextOpenInBinaryEditor = preferences.isRegisterContextOpenInBinaryEditor();
        registerDebugViewAsBinary = preferences.isRegisterDebugViewAsBinary();
        registerByteToByteDiffTool = preferences.isRegisterByteToByteDiffTool();
        registerEditAsBinaryForDbColumn = preferences.isRegisterEditAsBinaryForDbColumn();
    }

    public void saveToPreferences(IntegrationPreferences preferences) {
        preferences.setRegisterFileMenuOpenAsBinary(registerFileMenuOpenAsBinary);
        preferences.setRegisterOpenFileAsBinaryViaToolbar(registerOpenFileAsBinaryViaToolbar);
        preferences.setRegisterContextOpenAsBinary(registerContextOpenAsBinary);
        preferences.setRegisterContextOpenInBinaryEditor(registerContextOpenInBinaryEditor);
        preferences.setRegisterDebugViewAsBinary(registerDebugViewAsBinary);
        preferences.setRegisterByteToByteDiffTool(registerByteToByteDiffTool);
        preferences.setRegisterEditAsBinaryForDbColumn(registerEditAsBinaryForDbColumn);
    }

    public void setOptions(IntegrationOptionsImpl options) {
        registerFileMenuOpenAsBinary = options.isRegisterFileMenuOpenAsBinary();
        registerOpenFileAsBinaryViaToolbar = options.isRegisterOpenFileAsBinaryViaToolbar();
        registerContextOpenAsBinary = options.isRegisterContextOpenAsBinary();
        registerContextOpenInBinaryEditor = options.isRegisterContextOpenInBinaryEditor();
        registerDebugViewAsBinary = options.isRegisterDebugViewAsBinary();
        registerByteToByteDiffTool = options.isRegisterByteToByteDiffTool();
        registerEditAsBinaryForDbColumn = options.isRegisterEditAsBinaryForDbColumn();
    }
}
