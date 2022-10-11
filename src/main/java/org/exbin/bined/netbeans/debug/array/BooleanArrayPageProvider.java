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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.data.PageProvider;
import org.exbin.bined.netbeans.debug.DebugViewData;

import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.ObjectVariable;

/**
 * Boolean array data source for debugger view.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.2 2020/01/18
 */
@ParametersAreNonnullByDefault
public class BooleanArrayPageProvider implements PageProvider {

    private final ObjectVariable arrayRef;

    public BooleanArrayPageProvider(ObjectVariable arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        int startPos = (int) (pageIndex * DebugViewData.PAGE_SIZE * 8);
        int length = DebugViewData.PAGE_SIZE * 8;
        long documentSize = getDocumentSize() * 8;
        if (documentSize - startPos < DebugViewData.PAGE_SIZE * 8) {
            length = (int) (documentSize - startPos);
        }
        final Field[] values = arrayRef.getFields(startPos, startPos + length);
        byte[] result = new byte[(length + 7) / 8];
        int bitMask = 0x80;
        int bytePos = 0;
        for (int i = 0; i < values.length; i++) {
            Field rawValue = values[i];
            boolean value = Boolean.parseBoolean(rawValue.getValue());
//            if (rawValue instanceof ObjectVariable) {
//                rawValue = ((ObjectVariable) rawValue).getFields(0, 0)[0];
//            }

            if (value) {
                result[bytePos] += bitMask;
            }
            if (bitMask == 1) {
                bitMask = 0x80;
                bytePos++;
            } else {
                bitMask = bitMask >> 1;
            }
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return (arrayRef.getFieldsCount() + 7) / 8;
    }
}
