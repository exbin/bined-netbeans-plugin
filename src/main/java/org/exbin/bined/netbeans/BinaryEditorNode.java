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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.EditationMode;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.auxiliary.paged_data.PagedData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.auxiliary.paged_data.delta.FileDataSource;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.exbin.framework.bined.FileHandlingMode;

/**
 * Binary editor node.
 *
 * @version 0.2.1 2019/07/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorNode extends AbstractNode {

    private final BinaryEditorTopComponent editorTopComponent;

    public BinaryEditorNode(BinaryEditorTopComponent editorTopComponent) {
        super(Children.LEAF);
        this.editorTopComponent = editorTopComponent;
    }

    public void openFile(DataObject dataObject) {
        ExtCodeArea codeArea = editorTopComponent.getCodeArea();
        boolean editable = dataObject.getPrimaryFile().canWrite();
        SegmentsRepository segmentsRepository = BinaryEditorTopComponent.getSegmentsRepository();
        URI fileUri = dataObject.getPrimaryFile().toURI();
        if (fileUri == null) {
            InputStream stream = null;
            try {
                stream = dataObject.getPrimaryFile().getInputStream();
                if (stream != null) {
                    ((EditableBinaryData) codeArea.getContentData()).loadFromStream(stream);
                    codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
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
                BinaryData oldData = codeArea.getContentData();
                codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
                File file = new File(fileUri);
                if (editorTopComponent.getFileHandlingMode() == FileHandlingMode.DELTA) {
                    FileDataSource fileSource = segmentsRepository.openFileSource(file, editable ? FileDataSource.EditationMode.READ_WRITE : FileDataSource.EditationMode.READ_ONLY);
                    DeltaDocument document = segmentsRepository.createDocument(fileSource);
                    codeArea.setContentData(document);
                    oldData.dispose();
                } else {
                    try (FileInputStream fileStream = new FileInputStream(file)) {
                        BinaryData data = codeArea.getContentData();
                        if (!(data instanceof PagedData)) {
                            data = new PagedData();
                            oldData.dispose();
                        }
                        ((EditableBinaryData) data).loadFromStream(fileStream);
                        codeArea.setContentData(data);
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void saveFile(DataObject dataObject) {
        ExtCodeArea codeArea = editorTopComponent.getCodeArea();
        SegmentsRepository segmentsRepository = BinaryEditorTopComponent.getSegmentsRepository();
        BinaryData data = codeArea.getContentData();
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
                    codeArea.getContentData().saveToStream(stream);
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
