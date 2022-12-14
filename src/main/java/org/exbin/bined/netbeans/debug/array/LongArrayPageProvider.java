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

import java.math.BigInteger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.data.PageProvider;
import org.exbin.bined.netbeans.debug.DebugViewData;
import org.exbin.framework.bined.gui.ValuesPanel;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.ObjectVariable;

/**
 * Long array data source for debugger view.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LongArrayPageProvider implements PageProvider {

    private final ObjectVariable arrayRef;

    public LongArrayPageProvider(ObjectVariable arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        int pageSize = DebugViewData.PAGE_SIZE / 8;
        int startPos = (int) (pageIndex * pageSize);
        int length = pageSize;
        if (arrayRef.getFieldsCount() - startPos < pageSize) {
            length = arrayRef.getFieldsCount() - startPos;
        }
        final Field[] values = arrayRef.getFields(startPos, startPos + length);
        byte[] result = new byte[length * 8];
        for (int i = 0; i < values.length; i++) {
            Field rawValue = values[i];
            long value = Long.parseLong(rawValue.getValue());

            BigInteger bigInteger = BigInteger.valueOf(value);
            for (int bit = 0; bit < 7; bit++) {
                BigInteger nextByte = bigInteger.and(ValuesPanel.BIG_INTEGER_BYTE_MASK);
                result[i * 8 + 7 - bit] = nextByte.byteValue();
                bigInteger = bigInteger.shiftRight(8);
            }
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return arrayRef.getFieldsCount() * 8;
    }
}
