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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.auxiliary.paged_data.BinaryData;

/**
 * Data source for debugging.
 *
 * @version 0.2.0 2020/01/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultDebugViewDataProvider implements DebugViewDataProvider {

    private final String name;
    private final BinaryData data;

    public DefaultDebugViewDataProvider(String name, BinaryData data) {
        this.name = name;
        this.data = data;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    @Nonnull
    public BinaryData getData() {
        return data;
    }
}
