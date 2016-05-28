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
package org.exbin.utils.binary_data;

import java.io.IOException;

/**
 * Interface for finishable stream.
 *
 * @version 0.1.0 2016/05/24
 * @author ExBin Project (http://exbin.org)
 */
public interface FinishableStream {

    /**
     * Reads remaining data and returns size of all data processed by this
     * stream.
     *
     * @return size of data in bytes
     * @throws IOException if input/output error occurs
     */
    public long finish() throws IOException;

    /**
     * Returns size of data processed so far.
     *
     * @return size of data in bytes
     */
    public long getLength();
}
