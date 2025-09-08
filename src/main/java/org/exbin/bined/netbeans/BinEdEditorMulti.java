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
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * BinEd multiview tab editor for all MIME types.
 *
 * @author ExBin Project (https://exbin.org)
 */
@MultiViewElement.Registration(
        displayName = "#BinEdEditor.displayName",
        mimeType = BinEdEditorMulti.FAKE_MIME_TYPE,
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        preferredID = BinEdEditorMulti.ELEMENT_MULTI_ID,
        position = BinEdEditorMulti.POSITION_MULTI_ATTRIBUTE
)
@ParametersAreNonnullByDefault
public class BinEdEditorMulti extends BinEdEditor implements MultiViewElement {

    public static final String FAKE_MIME_TYPE = "application/x-bined-fake";
    public static final String ELEMENT_MULTI_ID = "org.exbin.bined.netbeans.BinEdEditorMulti";
    public static final String ELEMENT_MULTI_NAME = "org-exbin-bined-netbeans-BinEdEditorMulti";
    public static final int POSITION_MULTI_ATTRIBUTE = 900006; // Between "Source" and "History"

//    private final DocumentListener documentListener;
    private static final String EDITORS_FOLDER = "Editors";
    private static final String MULTIVIEW_FOLDER = "MultiView";
    private static final String DYNAMIC_FILETYPE_PREFIX = "-nb";
    private static final String ELEMENT_INSTANCE = "Editors/" + FAKE_MIME_TYPE + "/" + MULTIVIEW_FOLDER + "/" + ELEMENT_MULTI_NAME + ".instance";
    private static final String SHADOW_EXT = "shadow";
    private static final String ORIGINAL_FILE_ATTRIBUTE = "originalFile";

    public BinEdEditorMulti(Lookup lookup) {
        super(lookup);
    }

    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {
            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
                if (integrationOptions.isRegisterBinaryMultiview()) {
                    install();
                } else {
                    uninstall();
                }
            }

            @Override
            public void uninstallIntegration() {
                uninstall();
            }
        });
    }

    public static void install() {
        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                String fullMimeType = type + "/" + subType;
                if (FAKE_MIME_TYPE.equals(fullMimeType) || BinEdDataObject.MIME_TYPE.equals(fullMimeType)) {
                    continue;
                }

                try {
                    Lookup mimeLookup = MimeLookup.getLookup(fullMimeType);
                    if (mimeLookup != null) {
                        installForMimeType(mimeSubType, type, subType);
                    }
                } catch (IllegalArgumentException ex) {
                    // not a valid MIME type
                }
            }
        }
    }

    public static void uninstall() {
        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                if (BinEdDataObject.MIME_TYPE.equals(type + "/" + subType)) {
                    continue;
                }

                FileObject elementRecord = mimeType.getFileObject(BinEdEditorMulti.ELEMENT_MULTI_NAME + "." + SHADOW_EXT);
                if (elementRecord != null) {
                    try {
                        elementRecord.delete();
                        mimeType.refresh();
                    } catch (IOException ex) {
                        Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private static void installForMimeType(FileObject fileType, String mimeType, String mimeSubType) {
        if (!fileType.isFolder()) {
            return;
        }

        // It seems that NetBeans registers types with -nb postfix for dynamically loaded plugins
        if (mimeType.endsWith(DYNAMIC_FILETYPE_PREFIX) || BinEdDataObject.MIME_TYPE.equals(mimeType + "/" + mimeSubType)) {
            return;
        }

        try {
            FileObject multiViewFolder = fileType.getFileObject(MULTIVIEW_FOLDER);
            if (multiViewFolder == null) {
                multiViewFolder = FileUtil.createFolder(fileType, MULTIVIEW_FOLDER);
            }

            final FileObject editorRecord = multiViewFolder.getFileObject(ELEMENT_MULTI_NAME, SHADOW_EXT);
            if (editorRecord == null) {
                FileObject record = multiViewFolder.createData(ELEMENT_MULTI_NAME, SHADOW_EXT);
                record.setAttribute(ORIGINAL_FILE_ATTRIBUTE, ELEMENT_INSTANCE);
                record.setAttribute("position", POSITION_MULTI_ATTRIBUTE);
                record.setAttribute("persistenceType", TopComponent.PERSISTENCE_NEVER);
                multiViewFolder.refresh();
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
