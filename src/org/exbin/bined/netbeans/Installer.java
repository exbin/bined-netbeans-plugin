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

import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 * Installer for binary editor.
 *
 * @version 0.2.0 2019/03/01
 * @author ExBin Project (http://exbin.org)
 */
public class Installer extends ModuleInstall {

    private static final String OPEN_AS_BINARY_ACTION_STRING = "org-exbin-bined-OpenAsBinaryAction.shadow";
    private static final String OPENIDE_OPEN_ACTION_STRING = "org-openide-actions-OpenAction.shadow";
    private static final String OPEN_ACTION_STRING = "OpenAction.shadow";
    private static final String CUT_TO_CLIPBOARD_ACTION_STRING = "CutAction.shadow";

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(new ActionInstaller());  
    }

    @Override
    public void uninstalled() {
        WindowManager.getDefault().invokeWhenUIReady(new ActionUninstaller());
    }

    /**
     * A Runnable that delegates all known filetypes to an abstract method for
     * further processing.
     */
    @ParametersAreNonnullByDefault
    private static abstract class FileTypeHandler implements Runnable {

        @Override
        public void run() {
            final FileObject loaders = FileUtil.getConfigFile("Loaders");
            final FileObject[] categories = loaders.getChildren();
            for (FileObject category : categories) {
                final FileObject[] fileTypes = category.getChildren();
                for (FileObject fileType : fileTypes) {
                    handleFileType(fileType);
                }
            }
        }

        protected abstract void handleFileType(FileObject fileType);
    }

    /**
     * Creates references to the 'Open As Binary' action for all known file
     * types.
     *
     * This is done when all modules are loaded and thus all file types have
     * been registered.
     */
    @ParametersAreNonnullByDefault
    private static final class ActionInstaller extends FileTypeHandler {

        @Override
        protected void handleFileType(FileObject fileType) {
            if (fileType.isFolder()) {
                try {
                    final FileObject actionsFolder = FileUtil.createFolder(fileType, "Actions");

                    /**
                     * Attempt to establish correct position in context menu for
                     * different mime types.
                     *
                     * TODO: check on different platforms and collisions with
                     * other modules / modes
                     */
                    int actionPosition = 175;
                    final FileObject openAction = actionsFolder.getFileObject(OPEN_ACTION_STRING);
                    final FileObject openIdeOpenAction = actionsFolder.getFileObject(OPENIDE_OPEN_ACTION_STRING);
                    final FileObject cutAction = actionsFolder.getFileObject(CUT_TO_CLIPBOARD_ACTION_STRING);
                    if (cutAction != null) {
                        Object position = cutAction.getAttribute("position");
                        if (position instanceof Integer) {
                            if ((Integer) position < actionPosition) {
                                actionPosition = (Integer) position - 25;
                            }
                        }
                    }
                    if (openAction != null) {
                        Object position = openAction.getAttribute("position");
                        if (position instanceof Integer) {
                            if ((Integer) position + 25 > actionPosition) {
                                actionPosition = (Integer) position + 25;
                            }
                        }
                    }
                    if (openIdeOpenAction != null) {
                        Object position = openIdeOpenAction.getAttribute("position");
                        if (position instanceof Integer) {
                            if ((Integer) position + 25 > actionPosition) {
                                actionPosition = (Integer) position + 25;
                            }
                        }
                    }

                    final FileObject openAsBinaryAction = actionsFolder.getFileObject(OPEN_AS_BINARY_ACTION_STRING);
                    if (openAsBinaryAction == null) {
                        final FileObject action = actionsFolder.createData(OPEN_AS_BINARY_ACTION_STRING);
                        action.setAttribute("originalFile", "Actions/File/org-exbin-bined-OpenAsBinaryAction.instance");
                        action.setAttribute("position", actionPosition);
                    } else {
                        openAsBinaryAction.setAttribute("position", actionPosition);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    /**
     * Drops all references to the 'Open As Binary' action.
     */
    @ParametersAreNonnullByDefault
    private static final class ActionUninstaller extends FileTypeHandler {

        @Override
        protected void handleFileType(FileObject fileType) {
            if (fileType.isFolder()) {
                try {
                    final FileObject actionsFolder = FileUtil.createFolder(fileType, "Actions");
                    final FileObject openAsBinaryAction = actionsFolder.getFileObject(OPEN_AS_BINARY_ACTION_STRING);
                    if (openAsBinaryAction != null) {
                        openAsBinaryAction.delete();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
