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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.panel.StatusCursorPositionFormat;
import org.exbin.framework.bined.panel.StatusDocumentSizeFormat;
import org.exbin.framework.bined.preferences.StatusParameters;

/**
 * Status panel options.
 *
 * @version 0.2.0 2019/03/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusOptions {
    
    private StatusCursorPositionFormat statusCursorPositionFormat = new StatusCursorPositionFormat();
    private StatusDocumentSizeFormat statusDocumentSizeFormat = new StatusDocumentSizeFormat();
    private int octalSpaceGroupSize = 0;
    private int decimalSpaceGroupSize = 0;
    private int hexadecimalSpaceGroupSize = 0;

    @Nonnull
    public StatusCursorPositionFormat getStatusCursorPositionFormat() {
        return statusCursorPositionFormat;
    }

    public void setStatusCursorPositionFormat(StatusCursorPositionFormat statusCursorPositionFormat) {
        this.statusCursorPositionFormat = statusCursorPositionFormat;
    }

    @Nonnull
    public StatusDocumentSizeFormat getStatusDocumentSizeFormat() {
        return statusDocumentSizeFormat;
    }

    public void setStatusDocumentSizeFormat(StatusDocumentSizeFormat statusDocumentSizeFormat) {
        this.statusDocumentSizeFormat = statusDocumentSizeFormat;
    }

    public int getOctalSpaceGroupSize() {
        return octalSpaceGroupSize;
    }

    public void setOctalSpaceGroupSize(int octalSpaceGroupSize) {
        this.octalSpaceGroupSize = octalSpaceGroupSize;
    }

    public int getDecimalSpaceGroupSize() {
        return decimalSpaceGroupSize;
    }

    public void setDecimalSpaceGroupSize(int decimalSpaceGroupSize) {
        this.decimalSpaceGroupSize = decimalSpaceGroupSize;
    }

    public int getHexadecimalSpaceGroupSize() {
        return hexadecimalSpaceGroupSize;
    }

    public void setHexadecimalSpaceGroupSize(int hexadecimalSpaceGroupSize) {
        this.hexadecimalSpaceGroupSize = hexadecimalSpaceGroupSize;
    }

    public void loadFromParameters(StatusParameters parameters) {
        statusCursorPositionFormat.setCodeType(parameters.getCursorPositionCodeType());
        statusCursorPositionFormat.setShowOffset(parameters.isCursorShowOffset());
        statusDocumentSizeFormat.setCodeType(parameters.getDocumentSizeCodeType());
        statusDocumentSizeFormat.setShowRelative(parameters.isDocumentSizeShowRelative());
        octalSpaceGroupSize = parameters.getOctalSpaceSize();
        decimalSpaceGroupSize = parameters.getDecimalSpaceSize();
        hexadecimalSpaceGroupSize = parameters.getHexadecimalSpaceSize();
    }

    public void saveToParameters(StatusParameters parameters) {
        parameters.setCursorPositionCodeType(statusCursorPositionFormat.getCodeType());
        parameters.setCursorShowOffset(statusCursorPositionFormat.isShowOffset());
        parameters.setDocumentSizeCodeType(statusDocumentSizeFormat.getCodeType());
        parameters.setDocumentSizeShowRelative(statusDocumentSizeFormat.isShowRelative());
        parameters.setOctalSpaceSize(octalSpaceGroupSize);
        parameters.setDecimalSpaceSize(decimalSpaceGroupSize);
        parameters.setHexadecimalSpaceSize(hexadecimalSpaceGroupSize);
    }
}
