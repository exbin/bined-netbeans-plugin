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
package org.exbin.bined.netbeans.data.list;

import org.exbin.bined.netbeans.data.PageProvider;
import org.exbin.bined.netbeans.data.PageProviderBinaryData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Character list data as binary data provider.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.6 2022/05/18
 */
@ParametersAreNonnullByDefault
public class CharListPageProvider implements PageProvider {

    private final List<Character> listRef;

    public CharListPageProvider(List<Character> listRef) {
        this.listRef = listRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        int pageSize = PageProviderBinaryData.PAGE_SIZE / 2;
        int startPos = (int) (pageIndex * pageSize);
        int length = Math.min(listRef.size() - startPos, pageSize);
        byte[] result = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            int value = (int) (listRef.get(startPos + i));

            result[i * 2 ] = (byte) ((value >> 8) & 0xff);
            result[i * 2 + 1] = (byte) (value & 0xff);
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return listRef.size() * 2L;
    }
}
