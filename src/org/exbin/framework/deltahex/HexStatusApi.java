/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.deltahex;

import java.awt.event.MouseEvent;
import org.exbin.deltahex.EditationMode;

/**
 * Hexadecimal editor status interface.
 *
 * @version 0.2.0 2016/12/20
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
