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
import org.exbin.deltahex.CodeArea;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;

/**
 * Hexadecimal editor node.
 *
 * @version 0.1.1 2016/06/15
 * @author ExBin Project (http://exbin.org)
 */
public class HexEditorNode extends AbstractNode {

    private final CodeArea codeArea;

    public HexEditorNode(CodeArea codeArea) {
        super(Children.LEAF);
        this.codeArea = codeArea;
    }

    public void openFile(DataObject dataObject) {
        InputStream stream = null;
        try {
            stream = dataObject.getPrimaryFile().getInputStream();
            if (stream != null) {
                ((EditableBinaryData) codeArea.getData()).loadFromStream(stream);
                codeArea.setEditable(dataObject.getPrimaryFile().canWrite());
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
