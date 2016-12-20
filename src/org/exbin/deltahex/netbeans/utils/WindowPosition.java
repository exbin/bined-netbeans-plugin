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
package org.exbin.deltahex.netbeans.utils;

import java.util.prefs.Preferences;

/**
 * Structure for window position.
 *
 * @version 0.1.4 2016/12/20
 * @author ExBin Project (http://exbin.org)
 */
public class WindowPosition {

    public static final String PREFERENCES_SCREEN_INDEX = "screenIndex";
    public static final String PREFERENCES_SCREEN_WIDTH = "screenWidth";
    public static final String PREFERENCES_SCREEN_HEIGHT = "screenHeight";
    public static final String PREFERENCES_POSITION_X = "positionX";
    public static final String PREFERENCES_POSITION_Y = "positionY";
    public static final String PREFERENCES_WIDTH = "width";
    public static final String PREFERENCES_HEIGHT = "height";
    public static final String PREFERENCES_MAXIMIZED = "maximized";

    private int screenIndex;
    private int screenWidth;
    private int screenHeight;
    private int relativeX;
    private int relativeY;
    private int width;
    private int height;
    private boolean maximized = false;

    public int getScreenIndex() {
        return screenIndex;
    }

    public void setScreenIndex(int screenIndex) {
        this.screenIndex = screenIndex;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getRelativeX() {
        return relativeX;
    }

    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    public void saveToPreferences(Preferences pref, String prefix) {
        pref.put(prefix + PREFERENCES_SCREEN_INDEX, String.valueOf(screenIndex));
        pref.put(prefix + PREFERENCES_SCREEN_WIDTH, String.valueOf(screenWidth));
        pref.put(prefix + PREFERENCES_SCREEN_HEIGHT, String.valueOf(screenHeight));
        pref.put(prefix + PREFERENCES_POSITION_X, String.valueOf(relativeX));
        pref.put(prefix + PREFERENCES_POSITION_Y, String.valueOf(relativeY));
        pref.put(prefix + PREFERENCES_WIDTH, String.valueOf(width));
        pref.put(prefix + PREFERENCES_HEIGHT, String.valueOf(height));
        pref.put(prefix + PREFERENCES_MAXIMIZED, String.valueOf(maximized));
    }
    
    public void loadFromPreferences(Preferences pref, String prefix) {
        screenIndex = Integer.valueOf(pref.get(prefix + PREFERENCES_SCREEN_INDEX, "0"));
        screenWidth = Integer.valueOf(pref.get(prefix + PREFERENCES_SCREEN_WIDTH, "0"));
        screenHeight = Integer.valueOf(pref.get(prefix + PREFERENCES_SCREEN_HEIGHT, "0"));
        relativeX = Integer.valueOf(pref.get(prefix + PREFERENCES_POSITION_X, "0"));
        relativeY = Integer.valueOf(pref.get(prefix + PREFERENCES_POSITION_Y, "0"));
        width = Integer.valueOf(pref.get(prefix + PREFERENCES_WIDTH, "0"));
        height = Integer.valueOf(pref.get(prefix + PREFERENCES_HEIGHT, "0"));
        maximized = Boolean.valueOf(pref.get(prefix + PREFERENCES_MAXIMIZED, Boolean.toString(Boolean.FALSE)));
    }

    public boolean preferencesExists(Preferences pref, String prefix) {
        return pref.get(prefix + PREFERENCES_SCREEN_INDEX, null) != null;
    }
}
