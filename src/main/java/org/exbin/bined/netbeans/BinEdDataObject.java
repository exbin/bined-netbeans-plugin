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
import javax.annotation.ParametersAreNonnullByDefault;
import org.netbeans.api.actions.Savable;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 * BinEd data object.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@MIMEResolver.ExtensionRegistration(displayName = "#BinEdDataObject.extensionDisplayName", mimeType = BinEdDataObject.MIME_TYPE, extension = {BinEdDataObject.MMD_EXT}, showInFileChooser = {"#BinEdDataObject.extensionDisplayName"})
//@MIMEResolver.Registration(displayName = "#BinEdDataObject.extensionDisplayName", resource = "mime-resolver.xml", showInFileChooser = {"#BinEdDataObject.extensionDisplayName"})
// Doesn't work
//<?xml version="1.0" encoding="UTF-8"?>
//<!DOCTYPE MIME-resolver PUBLIC "-//NetBeans//DTD MIME Resolver 1.1//EN" "http://www.netbeans.org/dtds/mime-resolver-1_1.dtd">
//<MIME-resolver>
//    <file>
//        <name name="" substring="true"/>
//        <resolver mime="application/octet-stream"/>
//    </file>
//</MIME-resolver>
@DataObject.Registration(displayName = "#BinEdDataObject.displayName", mimeType = BinEdDataObject.MIME_TYPE, iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png")
public class BinEdDataObject extends MultiDataObject implements Savable {

    public static final String MIME_TYPE = "application/octet-stream"; //NOI18N
    public static final String MMD_EXT = "bin"; //NOI18N
    
    private BinEdEditor visualEditor;

    public BinEdDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
        registerEditor(MIME_TYPE, true);
    }

    public void setVisualEditor(BinEdEditor visualEditor) {
        this.visualEditor = visualEditor;
    }

    @Override
    public void save() throws IOException {
        if (visualEditor != null) {
            visualEditor.save();
        }
    }
}
