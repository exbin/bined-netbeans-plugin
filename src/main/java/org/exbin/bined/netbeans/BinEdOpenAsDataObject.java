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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.netbeans.api.actions.Savable;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * BinEd data object.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@MIMEResolver.Registration(displayName = "#BinEdDataObject.extensionDisplayName", resource = "mime-resolver.xml", showInFileChooser = {"#BinEdDataObject.extensionDisplayName"})
@DataObject.Registration(displayName = "#BinEdDataObject.displayName", mimeType = BinEdOpenAsDataObject.MIME_TYPE, iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png")
public class BinEdOpenAsDataObject extends MultiDataObject implements Savable {

    public static final String MIME_TYPE = "application/x-bined-openas"; //NOI18N

    private BinaryEditorTopComponent editorComponent;

    public BinEdOpenAsDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);

        final Mode editorMode = WindowManager.getDefault().findMode("editor");
        if (editorMode == null) {
            return;
        }

        try {
            editorComponent = new BinaryEditorTopComponent();
            editorMode.dockInto(editorComponent);
            DataObject dataObject = DataObject.find(fo);
            editorComponent.openDataObject(dataObject);
            editorComponent.open();
            editorComponent.requestActive();
        } catch (DataObjectNotFoundException ex) {
            Logger.getLogger(BinEdOpenAsDataObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void save() throws IOException {
        editorComponent.saveFile();
    }

    @Nonnull
    @Override
    public Lookup getLookup() {
        return editorComponent.getLookup();
    }

    @Nonnull
    public UndoRedo.Manager getUndoRedoManager() {
        return (UndoRedo.Manager) editorComponent.getUndoRedo();
    }

    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {
            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
                if (!integrationOptions.isRegisterOpenFileAsBinaryViaDialog()) {
                    uninstall();
                }
            }

            @Override
            public void uninstallIntegration() {
                uninstall();
            }
        });
    }

    public static void uninstall() {
        FileObject mimeResolverFolder = FileUtil.getSystemConfigFile("Services/MIMEResolver");
        FileObject mimeFileObject = mimeResolverFolder.getFileObject("org-exbin-bined-netbeans-BinEdOpenAsDataObject-Registration.xml");
        if (mimeFileObject != null && mimeFileObject.isValid()) {
            try {
                mimeFileObject.delete();
            } catch (IOException ex) {
                Logger.getLogger(BinEdOpenAsDataObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
