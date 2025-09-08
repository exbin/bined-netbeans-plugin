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
import javax.swing.Action;
import org.netbeans.api.actions.Savable;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;

/**
 * BinEd data object.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@MIMEResolver.ExtensionRegistration(displayName = "#BinEdDataObject.extensionDisplayName", mimeType = BinEdDataObject.MIME_TYPE, extension = {BinEdDataObject.MMD_EXT})
@DataObject.Registration(displayName = "#BinEdDataObject.displayName", mimeType = BinEdDataObject.MIME_TYPE, iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png")
public class BinEdDataObject extends MultiDataObject implements Savable {

    public static final String MIME_TYPE = "application/octet-stream"; //NOI18N
    public static final String MMD_EXT = "bin"; //NOI18N
    private Action saveAction = null;
    
    public BinEdDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
        // TODO
//        CookieSet cookieSet = getCookieSet();
//        OpenCookie openCookie = cookieSet.getCookie(OpenCookie.class);
//        Lookup lookup = cookieSet.getLookup();
//        DataEditorSupport dataEditorSupport = lookup.lookup(DataEditorSupport.class);
//        NbEditorDocument document = null;
//        if (dataEditorSupport.isDocumentLoaded()) {
//            document = (NbEditorDocument) dataEditorSupport.getDocument();
//        } else {
//            try {
//                document = (NbEditorDocument) dataEditorSupport.openDocument();
//            } catch (IOException ex) {
//                Logger.getLogger(BinEdDataObject.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        registerEditor(MIME_TYPE, true);
    }

    @Override
    public void save() throws IOException {
        if (saveAction != null) {
            saveAction.actionPerformed(null);
        } else {
            // Doesn't work at the moment
            DataEditorSupport dataEditorSupport = getLookup().lookup(DataEditorSupport.class);
            dataEditorSupport.saveDocument();
        }
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    public void setSaveAction(Action saveAction) {
        this.saveAction = saveAction;
    }
}
