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

import java.util.ResourceBundle;
import javax.swing.Action;

/**
 * Some simple static methods usable for actions, menus and toolbars.
 *
 * @version 0.1.4 2016/12/20
 * @author ExBin Project (http://exbin.org)
 */
public class ActionUtils {

    public static final String DIALOG_MENUITEM_EXT = "...";

    /**
     * Action type like or check, radio.
     *
     * Value is ActionType.
     */
    public static final String ACTION_TYPE = "type";
    /**
     * Radio group name value.
     *
     * Value is String.
     */
    public static final String ACTION_RADIO_GROUP = "radioGroup";
    /**
     * Action mode for actions opening dialogs.
     *
     * Value is Boolean.
     */
    public static final String ACTION_DIALOG_MODE = "dialogMode";

    public static final String ACTION_ID = "actionId";
    public static final String ACTION_NAME_POSTFIX = ".text";
    public static final String ACTION_SHORT_DESCRIPTION_POSTFIX = ".shortDescription";
    public static final String ACTION_SMALL_ICON_POSTFIX = ".smallIcon";
    public static final String ACTION_SMALL_LARGE_POSTFIX = ".largeIcon";

    /**
     * Sets action values according to values specified by resource bundle.
     *
     * @param action modified action
     * @param bundle source bundle
     * @param actionId action identifier and bundle key prefix
     */
    public static void setupAction(Action action, ResourceBundle bundle, String actionId) {
        action.putValue(Action.NAME, bundle.getString(actionId + ACTION_NAME_POSTFIX));
        action.putValue(ACTION_ID, actionId);

        // TODO keystroke from string with meta mask translation
        if (bundle.containsKey(actionId + ACTION_SHORT_DESCRIPTION_POSTFIX)) {
            action.putValue(Action.SHORT_DESCRIPTION, bundle.getString(actionId + ACTION_SHORT_DESCRIPTION_POSTFIX));
        }
        if (bundle.containsKey(actionId + ACTION_SMALL_ICON_POSTFIX)) {
            action.putValue(Action.SMALL_ICON, new javax.swing.ImageIcon(action.getClass().getResource(bundle.getString(actionId + ACTION_SMALL_ICON_POSTFIX))));
        }
        if (bundle.containsKey(actionId + ACTION_SMALL_LARGE_POSTFIX)) {
            action.putValue(Action.LARGE_ICON_KEY, new javax.swing.ImageIcon(action.getClass().getResource(bundle.getString(actionId + ACTION_SMALL_LARGE_POSTFIX))));
        }
    }

    /**
     * Enumeration of action types.
     */
    public enum ActionType {
        /**
         * Single click / activation action.
         */
        PUSH,
        /**
         * Checkbox type action.
         */
        CHECK,
        /**
         * Radion type checking, where only one item in radio group can be
         * checked.
         */
        RADIO
    }
}
