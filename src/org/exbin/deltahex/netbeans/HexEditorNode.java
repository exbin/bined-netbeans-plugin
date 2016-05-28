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
package org.exbin.deltahex.netbeans;

import java.io.IOException;
import java.io.InputStream;
import org.exbin.deltahex.Hexadecimal;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;

/**
 * Hexadecimal editor node.
 *
 * @version 0.1.0 2016/05/28
 * @author ExBin Project (http://exbin.org)
 */
public class HexEditorNode extends AbstractNode {

    private final Hexadecimal hexadecimal;

    public HexEditorNode(Hexadecimal hexadecimal) {
        super(Children.LEAF);
        this.hexadecimal = hexadecimal;
    }

    public void openFile(DataObject dataObject) {
        InputStream stream = null;
        try {
            stream = dataObject.getPrimaryFile().getInputStream();
            if (stream != null) {
                ((EditableBinaryData) hexadecimal.getData()).loadFromStream(stream);
                hexadecimal.setEditable(dataObject.getPrimaryFile().canWrite());
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
