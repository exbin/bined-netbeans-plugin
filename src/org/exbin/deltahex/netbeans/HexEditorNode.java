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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import org.exbin.deltahex.delta.DeltaDocument;
import org.exbin.deltahex.delta.FileDataSource;
import org.exbin.deltahex.delta.SegmentsRepository;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;
import org.exbin.utils.binary_data.PagedData;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;

/**
 * Hexadecimal editor node.
 *
 * @version 0.1.4 2017/01/06
 * @author ExBin Project (http://exbin.org)
 */
public class HexEditorNode extends AbstractNode {

    private final HexEditorTopComponent hexEditorTopComponent;

    public HexEditorNode(HexEditorTopComponent hexEditorTopComponent) {
        super(Children.LEAF);
        this.hexEditorTopComponent = hexEditorTopComponent;
    }

    public void openFile(DataObject dataObject) {
        CodeArea codeArea = hexEditorTopComponent.getCodeArea();
        SegmentsRepository segmentsRepository = HexEditorTopComponent.getSegmentsRepository();
        URI fileUri = dataObject.getPrimaryFile().toURI();
        if (fileUri == null) {
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
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } else {
            try {
                BinaryData oldData = codeArea.getData();
                File file = new File(fileUri);
                if (hexEditorTopComponent.isDeltaMemoryMode()) {
                    FileDataSource fileSource = segmentsRepository.openFileSource(file);
                    DeltaDocument document = segmentsRepository.createDocument(fileSource);
                    codeArea.setData(document);
                    oldData.dispose();
                } else {
                    try (FileInputStream fileStream = new FileInputStream(file)) {
                        BinaryData data = codeArea.getData();
                        if (!(data instanceof PagedData)) {
                            data = new PagedData();
                            oldData.dispose();
                        }
                        ((EditableBinaryData) data).loadFromStream(fileStream);
                        codeArea.setData(data);
                    }
                }
                codeArea.setEditable(dataObject.getPrimaryFile().canWrite());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void saveFile(DataObject dataObject) {
        CodeArea codeArea = hexEditorTopComponent.getCodeArea();
        SegmentsRepository segmentsRepository = HexEditorTopComponent.getSegmentsRepository();
        BinaryData data = codeArea.getData();
        if (data instanceof DeltaDocument) {
            try {
                segmentsRepository.saveDocument((DeltaDocument) data);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            OutputStream stream;
            try {
                stream = dataObject.getPrimaryFile().getOutputStream();
                try {
                    codeArea.getData().saveToStream(stream);
                    stream.flush();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
