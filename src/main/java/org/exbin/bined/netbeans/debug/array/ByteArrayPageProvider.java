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

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.debug.DebugViewData;

import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.ObjectVariable;
// import org.netbeans.modules.debugger.jpda.models.FieldVariable;

/**
 * Byte array data source for debugger view.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2019/09/04
 */
@ParametersAreNonnullByDefault
public class ByteArrayPageProvider implements DebugViewData.PageProvider {

    private final ObjectVariable arrayRef;

    public ByteArrayPageProvider(ObjectVariable arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Override
    public byte[] getPage(long pageIndex) {
        int startPos = (int) (pageIndex * DebugViewData.PAGE_SIZE);
        int length = DebugViewData.PAGE_SIZE;
        if (arrayRef.getFieldsCount() - startPos < DebugViewData.PAGE_SIZE) {
            length = arrayRef.getFieldsCount() - startPos;
        }
        final Field[] values = arrayRef.getFields(startPos, startPos + length);
        byte[] result = new byte[length];
        for (int i = 0; i < values.length; i++) {
            Field rawValue = values[i];
            if (rawValue instanceof ObjectVariable) {
                rawValue = ((ObjectVariable) rawValue).getFields(0, 1)[0];
            }

            byte value = Byte.valueOf(rawValue.getValue());
            result[i] = value;
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return arrayRef.getFieldsCount();
    }
}
