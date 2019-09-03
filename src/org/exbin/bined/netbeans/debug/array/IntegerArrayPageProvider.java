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
package org.exbin.bined.netbeans.debug.array;

import org.exbin.bined.netbeans.debug.DebugViewDataSource;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.ObjectVariable;

/**
 * Integer array data source for debugger view.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2019/09/03
 */
public class IntegerArrayPageProvider implements DebugViewDataSource.PageProvider {

    private final ObjectVariable arrayRef;

    public IntegerArrayPageProvider(ObjectVariable arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Override
    public byte[] getPage(long pageIndex) {
        int pageSize = DebugViewDataSource.PAGE_SIZE / 4;
        int startPos = (int) (pageIndex * pageSize);
        int length = pageSize;
        if (arrayRef.getFieldsCount() - startPos < pageSize) {
            length = arrayRef.getFieldsCount() - startPos;
        }
        final Field[] values = arrayRef.getFields(startPos, startPos + length);
        byte[] result = new byte[length * 4];
        for (int i = 0; i < values.length; i++) {
            Field rawValue = values[i];
            int value = Integer.valueOf(rawValue.getValue());

            result[i * 4] = (byte) (value >> 24);
            result[i * 4 + 1] = (byte) ((value >> 16) & 0xff);
            result[i * 4 + 2] = (byte) ((value >> 8) & 0xff);
            result[i * 4 + 3] = (byte) (value & 0xff);
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return arrayRef.getFieldsCount() * 4;
    }
}
