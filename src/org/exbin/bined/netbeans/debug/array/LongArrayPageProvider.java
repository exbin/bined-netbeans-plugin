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

/**
 * Long array data source for debugger view.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2019/09/02
 */
public class LongArrayPageProvider implements DebugViewDataSource.PageProvider {

    @Override
    public byte[] getPage(long pageIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getDocumentSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    private final ArrayReference arrayRef;
//
//    public LongArrayPageProvider(ArrayReference arrayRef) {
//        this.arrayRef = arrayRef;
//    }
//
//    @Override
//    public byte[] getPage(long pageIndex) {
//        int pageSize = DebugViewDataSource.PAGE_SIZE / 8;
//        int startPos = (int) (pageIndex * pageSize);
//        int length = pageSize;
//        if (arrayRef.length() - startPos < pageSize) {
//            length = arrayRef.length() - startPos;
//        }
//        final List<Value> values = arrayRef.getValues(startPos, length);
//        byte[] result = new byte[length * 8];
//        for (int i = 0; i < values.size(); i++) {
//            Value rawValue = values.get(i);
//            if (rawValue instanceof ObjectReference) {
//                Field field = ((ObjectReference) rawValue).referenceType().fieldByName("value");
//                rawValue = ((ObjectReference) rawValue).getValue(field);
//            }
//
//            long value = rawValue instanceof LongValue ? ((LongValue) rawValue).value() : 0;
//
//            BigInteger bigInteger = BigInteger.valueOf(value);
//            for (int bit = 0; bit < 7; bit++) {
//                BigInteger nextByte = bigInteger.and(ValuesPanel.BIG_INTEGER_BYTE_MASK);
//                result[i * 8 + 7 - bit] = nextByte.byteValue();
//                bigInteger = bigInteger.shiftRight(8);
//            }
//        }
//
//        return result;
//    }
//
//    @Override
//    public long getDocumentSize() {
//        return arrayRef.length() * 8;
//    }
}
