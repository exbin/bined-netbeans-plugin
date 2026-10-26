/*
 * Copyright (C) ExBin Project, https://exbin.org
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
import java.io.InputStream;
import java.io.OutputStream;
import org.jspecify.annotations.NullMarked;
import org.exbin.jaguif.document.api.StreamDocumentSource;
import org.openide.filesystems.FileObject;

/**
 * File object document source.
 */
@NullMarked
public class FileObjectDocumentSource implements StreamDocumentSource {

    final FileObject fileObject;

    public FileObjectDocumentSource(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    @Override
    public String getDocumentTitle() {
        return fileObject.getName();
    }

    @Override
    public InputStream openInputStream() {
        try {
            return fileObject.getInputStream();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public OutputStream openOutputStream() {
        try {
            return fileObject.getOutputStream();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
