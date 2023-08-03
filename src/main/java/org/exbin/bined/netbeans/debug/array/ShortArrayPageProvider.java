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
package org.exbin.bined.netbeans.debug.array;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.objectdata.PageProvider;
import org.exbin.bined.netbeans.debug.DebugViewData;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.ObjectVariable;

/**
 * Short array data source for debugger view.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ShortArrayPageProvider implements PageProvider {

    private final ObjectVariable arrayRef;

    public ShortArrayPageProvider(ObjectVariable arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        int pageSize = DebugViewData.PAGE_SIZE / 2;
        int startPos = (int) (pageIndex * pageSize);
        int length = pageSize;
        if (arrayRef.getFieldsCount() - startPos < pageSize) {
            length = arrayRef.getFieldsCount() - startPos;
        }
        final Field[] values = arrayRef.getFields(startPos, startPos + length);
        byte[] result = new byte[length * 2];
        for (int i = 0; i < values.length; i++) {
            Field rawValue = values[i];
            short value = Short.parseShort(rawValue.getValue());
            result[i * 2] = (byte) (value >> 8);
            result[i * 2 + 1] = (byte) (value & 0xff);
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return arrayRef.getFieldsCount() * 2;
    }
}
