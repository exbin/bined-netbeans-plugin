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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.exbin.framework.api.Preferences;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.basic.EnterKeyHandlingMode;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.FileHandlingMode;

/**
 * Hexadecimal editor preferences.
 *
 * @version 0.2.1 2019/07/17
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EditorPreferences {

    public static final String PREFERENCES_FILE_HANDLING_MODE = "fileHandlingMode";
    public static final String PREFERENCES_SHOW_VALUES_PANEL = "valuesPanel";
    public static final String PREFERENCES_MEMORY_MODE = "memoryMode";
    public static final String PREFERENCES_ENTER_KEY_HANDLING_MODE = "enterKeyHandlingMode";

    private final Preferences preferences;

    public EditorPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Nonnull
    public FileHandlingMode getFileHandlingMode() {
        FileHandlingMode defaultFileHandlingMode = FileHandlingMode.DELTA;
        try {
            return FileHandlingMode.valueOf(preferences.get(PREFERENCES_FILE_HANDLING_MODE, defaultFileHandlingMode.name()));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EditorPreferences.class.getName()).log(Level.SEVERE, null, ex);
            return defaultFileHandlingMode;
        }
    }

    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        preferences.put(PREFERENCES_FILE_HANDLING_MODE, fileHandlingMode.name());
    }

    public boolean isShowValuesPanel() {
        return preferences.getBoolean(PREFERENCES_SHOW_VALUES_PANEL, true);
    }

    public void setShowValuesPanel(boolean showValuesPanel) {
        preferences.putBoolean(PREFERENCES_SHOW_VALUES_PANEL, showValuesPanel);
    }

    @Nonnull
    public String getMemoryMode() {
        return preferences.get(PREFERENCES_MEMORY_MODE, BinaryStatusApi.MemoryMode.DELTA_MODE.getPreferencesValue());
    }

    public void setMemoryMode(String memoryMode) {
        preferences.put(PREFERENCES_MEMORY_MODE, memoryMode);
    }

    @Nonnull
    public EnterKeyHandlingMode getEnterKeyHandlingMode() {
        EnterKeyHandlingMode defaultValue = EnterKeyHandlingMode.PLATFORM_SPECIFIC;
        try {
            return EnterKeyHandlingMode.valueOf(preferences.get(PREFERENCES_ENTER_KEY_HANDLING_MODE, defaultValue.name()));
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    public void setEnterKeyHandlingMode(EnterKeyHandlingMode enterKeyHandlingMode) {
        preferences.put(PREFERENCES_ENTER_KEY_HANDLING_MODE, enterKeyHandlingMode.name());
    }
}
