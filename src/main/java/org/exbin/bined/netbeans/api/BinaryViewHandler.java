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
package org.exbin.bined.netbeans.api;

import org.exbin.auxiliary.binary_data.BinaryData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import java.util.Optional;

/**
 * BinEd View Data Handler.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface BinaryViewHandler {

    /**
     * Attempts to provide binary data wrapper for given instance.
     *
     * @param instance instance
     * @return binary data if supported
     */
    @Nonnull
    Optional<BinaryData> instanceToBinaryData(Object instance);

    /**
     * Creates panel for binary data.
     *
     * @param binaryData binary data
     * @return binary view panel
     */
    @Nonnull
    JComponent createBinaryViewPanel(@Nullable BinaryData binaryData);

    /**
     * creates panel for object instance if possible.
     *
     * @param instance class instance
     * @return binary view panel if instance supported
     */
    @Nonnull
    Optional<JComponent> createBinaryViewPanel(Object instance);

    /**
     * Shows dialog for binary data.
     *
     * @param binaryData binary data
     * @return binary view dialog
     */
//    @Nonnull
//    DialogWrapper createBinaryViewDialog(@Nullable BinaryData binaryData);

    /**
     * Shows dialog for object instance if possible.
     *
     * @param instance class instance
     * @return binary view dialog
     */
//    @Nonnull
//    DialogWrapper createBinaryViewDialog(Object instance);
}
