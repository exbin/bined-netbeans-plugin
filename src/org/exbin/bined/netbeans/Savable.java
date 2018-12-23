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
package org.exbin.bined.netbeans;

import java.io.IOException;
import java.util.Objects;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.loaders.DataObject;

/**
 * Saving capability for hexadecimal editor.
 *
 * @version 0.2.0 2018/09/10
 * @author ExBin Project (http://exbin.org)
 */
class Savable extends AbstractSavable {

    private DataObject dataObject;
    private final BinaryEditorTopComponent component;
    private final ExtCodeArea codeArea;

    public Savable(BinaryEditorTopComponent component, ExtCodeArea codeArea) {
        this.component = component;
        this.codeArea = codeArea;
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

    @Override
    protected String findDisplayName() {
        return dataObject == null ? "<unknown file>" : dataObject.getPrimaryFile().getName();
    }

    @Override
    public boolean equals(Object obj) {
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
        return Objects.equals(this.component, other.component);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    protected void handleSave() throws IOException {
        if (dataObject == null) {
            return;
        }

        component.saveDataObject(dataObject);
    }
}
