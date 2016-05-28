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
import java.io.OutputStream;

/**
 * Byte array data output stream.
 *
 * @version 0.1.0 2016/05/24
 * @author ExBin Project (http://exbin.org)
 */
public class ByteArrayDataOutputStream extends OutputStream implements SeekableStream, FinishableStream {

    private final ByteArrayEditableData data;
    private long position = 0;

    public ByteArrayDataOutputStream(ByteArrayEditableData data) {
        this.data = data;
    }

    @Override
    public void write(int value) throws IOException {
        long dataSize = data.getDataSize();
        if (position == dataSize) {
            dataSize++;
            data.setDataSize(dataSize);
        }
        data.setByte(position++, (byte) value);
    }

    @Override
    public void write(byte[] input, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }

        long dataSize = data.getDataSize();
        if (position + len > dataSize) {
            data.setDataSize(position + len);
        }

        byte[] byteArray = data.getData();
        System.arraycopy(input, off, byteArray, (int) position, len);
        position += len;
    }

    @Override
    public void seek(long position) throws IOException {
        this.position = position;
    }

    @Override
    public long getStreamSize() {
        return data.getDataSize();
    }

    @Override
    public long getLength() {
        return position;
    }

    @Override
    public void close() throws IOException {
        finish();
    }

    @Override
    public long finish() throws IOException {
        position = data.getDataSize();
        return position;
    }
}
