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
package org.exbin.bined.netbeans.debug;

import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.data.PageProvider;

/**
 * Debugger value dual page data source.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2019/09/02
 */
@ParametersAreNonnullByDefault
public class DebugViewData implements BinaryData {

    public static final int PAGE_SIZE = 2048;

    private final PageProvider pageProvider;

    private final CachePage[] pages = new CachePage[2];
    private int nextPage = 0;

    public DebugViewData(PageProvider pageProvider) {
        this.pageProvider = pageProvider;
        pages[0] = new CachePage();
        pages[1] = new CachePage();
    }

    @Override
    public boolean isEmpty() {
        return pageProvider.getDocumentSize() == 0;
    }

    @Override
    public long getDataSize() {
        return pageProvider.getDocumentSize();
    }

    @Override
    public byte getByte(long position) {
        long pageIndex = position / PAGE_SIZE;
        int pageOffset = (int) (position % PAGE_SIZE);
        CachePage page;

        if (pages[0].index == pageIndex && pages[0].data != null) {
            page = pages[0];
        } else if (pages[1].index == pageIndex && pages[1].data != null) {
            page = pages[1];
        } else {
            byte[] data = pageProvider.getPage(pageIndex);
            if (data == null) {
                return -1;
            }

            pages[nextPage].data = data;
            pages[nextPage].index = pageIndex;
            page = pages[nextPage];
            nextPage = 1 - nextPage;
        }

        if (pageOffset >= page.data.length) {
            return -1;
        }

        return page.data[pageOffset];
    }

    @Nonnull
    @Override
    public BinaryData copy() {
        return copy(0, getDataSize());
    }

    @Nonnull
    @Override
    public BinaryData copy(long startFrom, long length) {
        ByteArrayEditableData result = new ByteArrayEditableData();
        result.insertUninitialized(0, length);
        int offset = 0;

        while (length > 0) {
            long pageIndex = startFrom / PAGE_SIZE;
            int pageOffset = (int) (startFrom % PAGE_SIZE);
            CachePage page;

            if (pages[0].index == pageIndex && pages[0].data != null) {
                page = pages[0];
            } else if (pages[1].index == pageIndex && pages[1].data != null) {
                page = pages[1];
            } else {
                byte[] data = pageProvider.getPage(pageIndex);
                if (data == null) {
                    throw createOfOutBoundsException();
                }

                pages[nextPage].data = data;
                pages[nextPage].index = pageIndex;
                page = pages[nextPage];
                nextPage = 1 - nextPage;
            }

            if (pageOffset >= page.data.length) {
                throw createOfOutBoundsException();
            }

            int copyLength = length > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) length;
            if (pageOffset + copyLength > page.data.length) {
                copyLength = page.data.length - pageOffset;
            }
            if (copyLength == 0) {
                throw createOfOutBoundsException();
            }

            result.replace(offset, page.data, pageOffset, copyLength);
            startFrom += copyLength;
            offset += copyLength;
            length -= copyLength;
        }

        return result;
    }

    @Override
    public void copyToArray(long startFrom, byte[] target, int offset, int length) {
        while (length > 0) {
            long pageIndex = startFrom / PAGE_SIZE;
            int pageOffset = (int) (startFrom % PAGE_SIZE);
            CachePage page;

            if (pages[0].index == pageIndex && pages[0].data != null) {
                page = pages[0];
            } else if (pages[1].index == pageIndex && pages[1].data != null) {
                page = pages[1];
            } else {
                byte[] data = pageProvider.getPage(pageIndex);
                if (data == null) {
                    throw createOfOutBoundsException();
                }

                pages[nextPage].data = data;
                pages[nextPage].index = pageIndex;
                page = pages[nextPage];
                nextPage = 1 - nextPage;
            }

            if (pageOffset >= page.data.length) {
                throw createOfOutBoundsException();
            }

            int copyLength = length;
            if (pageOffset + copyLength > page.data.length) {
                copyLength = page.data.length - pageOffset;
            }
            if (copyLength == 0) {
                throw createOfOutBoundsException();
            }

            System.arraycopy(page.data, pageOffset, target, offset, copyLength);
            startFrom += copyLength;
            offset += copyLength;
            length -= copyLength;
        }
    }

    @Override
    public void saveToStream(OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException("Save to stream is not supported");
    }

    @Nonnull
    @Override
    public InputStream getDataInputStream() {
        throw new UnsupportedOperationException("Data input stream is not supported");
    }

    @Override
    public void dispose() {

    }

    private class CachePage {

        long index = 0;
        byte[] data = null;
    }

    @Nonnull
    private static IndexOutOfBoundsException createOfOutBoundsException() {
        return new IndexOutOfBoundsException("Requested data out of bounds");
    }
}
