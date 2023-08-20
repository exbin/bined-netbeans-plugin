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
package org.exbin.bined.netbeans;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.bined.gui.BinEdComponentFileApi;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.loaders.DataObject;

/**
 * Saving capability for binary editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
class Savable extends AbstractSavable {

    private DataObject dataObject;
    private final BinEdComponentFileApi fileApi;

    public Savable(BinEdComponentFileApi fileApi) {
        this.fileApi = fileApi;
    }

    public void activate() {
        register();
    }

    public void deactivate() {
        unregister();
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Nonnull
    @Override
    protected String findDisplayName() {
        return dataObject == null ? "<unknown file>" : dataObject.getPrimaryFile().getName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Savable other = (Savable) obj;
        return Objects.equals(this.fileApi, other.fileApi);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    protected void handleSave() throws IOException {
        fileApi.saveDocument();
    }
}
