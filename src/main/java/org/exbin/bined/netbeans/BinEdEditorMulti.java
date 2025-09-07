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
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * BinEd multiview tab editor for all MIME types.
 *
 * @author ExBin Project (https://exbin.org)
 */
@MultiViewElement.Registration(
        displayName = "#BinEdEditor.displayName",
        mimeType = BinEdDataObject.MIME_TYPE,
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        preferredID = BinEdEditorMulti.ELEMENT_MULTI_ID,
        position = BinEdEditorMulti.POSITION_MULTI_ATTRIBUTE
)
@ParametersAreNonnullByDefault
public class BinEdEditorMulti extends BinEdEditor implements MultiViewElement {

    public static final String ELEMENT_MULTI_ID = "org.exbin.bined.netbeans.BinEdEditorMulti";
    public static final String ELEMENT_MULTI_NAME = "org-exbin-bined-netbeans-BinEdEditorMulti";
    public static final int POSITION_MULTI_ATTRIBUTE = 900006; // Between "Source" and "History"

//    private final DocumentListener documentListener;
    private static final String EDITORS_FOLDER = "Editors";
    private static final String MULTIVIEW_FOLDER = "MultiView";
    private static final String DYNAMIC_FILETYPE_PREFIX = "-nb";
    private static final String ELEMENT_INSTANCE = "Editors/" + BinEdDataObject.MIME_TYPE + "/" + MULTIVIEW_FOLDER + "/" + ELEMENT_NAME + ".instance";
    private static final String SHADOW_EXT = "shadow";
    private static final String ORIGINAL_FILE_ATTRIBUTE = "originalFile";

    
    public BinEdEditorMulti(Lookup lookup) {
        super(lookup);
    }
    
    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {
            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
//                if (integrationOptions.isRegisterBinaryMultiview()) {
//                    install();
//                } else {
//                    uninstall();
//                }
            }

            @Override
            public void uninstallIntegration() {
//                uninstall();
            }
        });
    }

    public static void install() {
        FileObject multiViewFolder = FileUtil.getSystemConfigFile("Editors/" + MULTIVIEW_FOLDER);
        FileObject binaryTypeFolder = FileUtil.getSystemConfigFile("Editors/" + BinEdDataObject.MIME_TYPE + "/" + MULTIVIEW_FOLDER);
        FileObject[] children = multiViewFolder.getChildren();
        for (FileObject fileObject : children) {
            System.out.println(fileObject.getName() + "." + fileObject.getExt());
        }
        try {
            FileObject allTypesObject = multiViewFolder.getFileObject(BinEdEditorMulti.ELEMENT_MULTI_NAME, "disabled");
            if (allTypesObject != null) {
                FileLock lock = null;
                try {
                    lock = allTypesObject.lock();
                    allTypesObject.rename(lock, BinEdEditorMulti.ELEMENT_MULTI_NAME, "instance");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                multiViewFolder.refresh();
            }
            FileObject binaryTypeObject = binaryTypeFolder.getFileObject(ELEMENT_MULTI_ID, "instance");
            if (binaryTypeObject != null) {
                FileLock lock = null;
                try {
                    lock = binaryTypeObject.lock();
                    binaryTypeObject.rename(lock, ELEMENT_MULTI_NAME, "disabled");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                binaryTypeFolder.refresh();
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        }

        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                installForMimeType(mimeSubType, type, subType);
            }
        }
        
        /*        FileObject targetFolder = FileUtil.getSystemConfigFile(EDITORS_FOLDER + "/" + MULTIVIEW_FOLDER);
        targetFolder.move(FileLock.NONE, targetFolder, ELEMENT_ID, SHADOW_EXT)
        FileObject elementRecord = targetFolder.getFileObject(ELEMENT_ID + "." + SHADOW_EXT);
        if (elementRecord == null) {
            try {
//                final FileObject record = targetFolder.createData(ELEMENT_ID + "." + SHADOW_EXT);
//                record.setAttribute(ORIGINAL_FILE_ATTRIBUTE, ELEMENT_INSTANCE);
//                record.setAttribute("position", POSITION_ATTRIBUTE);
//                targetFolder.refresh();
            } catch (IOException ex) {
                Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
            }
        } */
    }

    public static void uninstall() {
/*        FileObject multiViewFolder = FileUtil.getSystemConfigFile("Editors/" + MULTIVIEW_FOLDER);
        FileObject[] children = multiViewFolder.getChildren();
        for (FileObject fileObject : children) {
            System.out.println(fileObject.getName() + "." + fileObject.getExt());
        }
        FileObject fileObject = multiViewFolder.getFileObject(BinEdEditorMulti.ELEMENT_MULTI_NAME, "instance");
        if (fileObject == null) {
            fileObject = multiViewFolder.getFileObject(BinEdEditorMulti.ELEMENT_MULTI_NAME, "disabled");
        }
        if (fileObject != null && fileObject.isValid()) {
            try {
                fileObject.delete();
            } catch (IOException ex) {
                Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException();
        } */
        
        FileObject multiViewFolder = FileUtil.getSystemConfigFile("Editors/" + MULTIVIEW_FOLDER);
        FileObject binaryTypeFolder = FileUtil.getSystemConfigFile("Editors/application/octet-stream/" + MULTIVIEW_FOLDER);
        try {
            FileObject allTypesObject = multiViewFolder.getFileObject(BinEdEditorMulti.ELEMENT_MULTI_NAME, "instance");
            if (allTypesObject != null) {
                FileLock lock = null;
                try {
                    lock = allTypesObject.lock();
                    allTypesObject.rename(lock, BinEdEditorMulti.ELEMENT_MULTI_NAME, "disabled");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                multiViewFolder.refresh();
            }
            FileObject binaryTypeObject = binaryTypeFolder.getFileObject(BinEdEditor.ELEMENT_NAME, "disabled");
            if (binaryTypeObject != null) {
                FileLock lock = null;
                try {
                    lock = binaryTypeObject.lock();
                    binaryTypeObject.rename(lock, BinEdEditor.ELEMENT_NAME, "instance");
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
                binaryTypeFolder.refresh();
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*
        FileObject targetFolder = FileUtil.getSystemConfigFile(EDITORS_FOLDER + "/" + MULTIVIEW_FOLDER);
        FileObject elementRecord = targetFolder.getFileObject(ELEMENT_ID + "." + SHADOW_EXT);
        if (elementRecord != null) {
            try {
                elementRecord.delete();
                targetFolder.refresh();
            } catch (IOException ex) {
                Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
            }
        } */
    }

/*
    public static void install() {
        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                installForMimeType(mimeSubType, type, subType);
            }
        }
    }

    public static void uninstall() {
        final FileObject editors = FileUtil.getConfigFile(EDITORS_FOLDER);
        for (FileObject mimeType : editors.getChildren()) {
            String type = mimeType.getName();
            for (FileObject mimeSubType : mimeType.getChildren()) {
                String subType = mimeSubType.getName();
                uninstallForMimeType(mimeSubType, type, subType);
            }
        }
    }
*/
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

            final FileObject editorRecord = multiViewFolder.getFileObject(BinEdEditor.ELEMENT_ID + "." + SHADOW_EXT);
            if (editorRecord == null) {
                FileObject record = multiViewFolder.createData(BinEdEditor.ELEMENT_ID + "." + SHADOW_EXT);
                record.setAttribute(ORIGINAL_FILE_ATTRIBUTE, ELEMENT_INSTANCE);
                record.setAttribute("position", POSITION_ATTRIBUTE);
                record.setAttribute("persistenceType", TopComponent.PERSISTENCE_NEVER);
                
                // Register multiview
                record = multiViewFolder.createData(BinEdEditorMulti.ELEMENT_MULTI_ID);
                record.setAttribute("displayName", "Binary");
                record.setAttribute("mimeType", "");
                record.setAttribute("class", "org.exbin.bined.netbeans.BinEdEditorMulti");
                Method method = org.netbeans.core.spi.multiview.MultiViewFactory.class.getMethod("createMultiViewDescription", Map.class);
                record.setAttribute("instanceCreate", method);
                record.setAttribute("instanceClass", "org.netbeans.core.multiview.ContextAwareDescription");
                record.setAttribute("iconBase", "org/exbin/bined/netbeans/resources/icons/icon.png");
                record.setAttribute("preferredID", BinEdEditorMulti.ELEMENT_ID);
                record.setAttribute("position", BinEdEditorMulti.POSITION_ATTRIBUTE);
                record.setAttribute("persistenceType", TopComponent.PERSISTENCE_NEVER);
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void uninstallForMimeType(FileObject fileType, String mimeType, String mimeSubType) {
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
                return;
            }

            final FileObject editorRecord = multiViewFolder.getFileObject(ELEMENT_ID + "." + SHADOW_EXT);
            if (editorRecord != null) {
                editorRecord.delete();
                multiViewFolder.refresh();

                boolean hasAttributes = multiViewFolder.getAttributes().hasMoreElements();
                boolean hasChildren = multiViewFolder.getChildren().length > 0;
                if (!hasAttributes && !hasChildren) {
                    multiViewFolder.delete();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BinEdEditorMulti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
