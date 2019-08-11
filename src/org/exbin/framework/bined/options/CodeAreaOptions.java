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
package org.exbin.framework.bined.options;

import java.awt.Font;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.CodeAreaViewMode;
import org.exbin.bined.CodeCharactersCase;
import org.exbin.bined.CodeType;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.capability.RowWrappingCapable;

/**
 * Code area options.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface CodeAreaOptions {

    @Nonnull
    CodeCharactersCase getCodeCharactersCase();

    @Nonnull
    Font getCodeFont();

    @Nonnull
    CodeType getCodeType();

    int getMaxBytesPerRow();

    int getMaxRowPositionLength();

    int getMinRowPositionLength();

    @Nonnull
    PositionCodeType getPositionCodeType();

    @Nonnull
    RowWrappingCapable.RowWrappingMode getRowWrappingMode();

    @Nonnull
    CodeAreaViewMode getViewMode();

    boolean isCodeColorization();

    boolean isShowUnprintables();

    boolean isUseDefaultFont();

    void setCodeCharactersCase(CodeCharactersCase codeCharactersCase);

    void setCodeColorization(boolean codeColorization);

    void setCodeFont(Font codeFont);

    void setCodeType(CodeType codeType);

    void setMaxBytesPerRow(int maxBytesPerRow);

    void setMaxRowPositionLength(int maxRowPositionLength);

    void setMinRowPositionLength(int minRowPositionLength);

    void setPositionCodeType(PositionCodeType positionCodeType);

    void setRowWrappingMode(RowWrappingCapable.RowWrappingMode rowWrappingMode);

    void setShowUnprintables(boolean showUnprintables);

    void setUseDefaultFont(boolean useDefaultFont);

    void setViewMode(CodeAreaViewMode viewMode);
}
