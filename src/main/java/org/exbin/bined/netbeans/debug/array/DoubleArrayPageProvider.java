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

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.objectdata.PageProvider;
import org.exbin.bined.netbeans.debug.DebugViewData;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.ObjectVariable;


/**
 * Double array data source for debugger view.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DoubleArrayPageProvider implements PageProvider {

    private final byte[] valuesCache = new byte[8];
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(valuesCache);

    private final ObjectVariable arrayRef;

    public DoubleArrayPageProvider(ObjectVariable arrayRef) {
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
            double value = Double.parseDouble(rawValue.getValue());

            byteBuffer.rewind();
            byteBuffer.putDouble(value);
            System.arraycopy(valuesCache, 0, result, i * 8, 8);
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return arrayRef.getFieldsCount() * 8;
    }
}
