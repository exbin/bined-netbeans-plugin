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
package org.exbin.deltahex.netbeans.panel;

import java.awt.event.MouseEvent;
import org.exbin.deltahex.EditationMode;

/**
 * Hexadecimal editor status interface.
 *
 * @version 0.1.4 2016/12/20
 * @author ExBin Project (http://exbin.org)
 */
public interface HexStatusApi {

    /**
     * Reports cursor position.
     *
     * @param cursorPosition cursor position
     */
    void setCursorPosition(String cursorPosition);

    /**
     * Reports currently active editation mode.
     *
     * @param editationMode editation mode
     */
    void setEditationMode(EditationMode editationMode);

    /**
     * Sets control handler for status operations.
     *
     * @param statusControlHandler status control handler
     */
    void setControlHandler(StatusControlHandler statusControlHandler);

    /**
     * Sets current document size.
     *
     * @param documentSize document size
     */
    void setCurrentDocumentSize(String documentSize);

    /**
     * Sets current memory mode.
     *
     * @param memoryMode memory mode
     */
    void setMemoryMode(String memoryMode);

    public static interface StatusControlHandler {

        /**
         * Requests change of editation mode from given mode.
         *
         * @param editationMode editation mode
         */
        void changeEditationMode(EditationMode editationMode);

        /**
         * Requests change of cursor position using go-to dialog.
         */
        void changeCursorPosition();

        /**
         * Switches to next encoding in defined list.
         */
        void cycleEncodings();

        /**
         * Handles encodings popup menu.
         *
         * @param mouseEvent mouse event
         */
        void popupEncodingsMenu(MouseEvent mouseEvent);
    }
}
