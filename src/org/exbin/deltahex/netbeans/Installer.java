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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 * Installer for hexadecimal editor.
 *
 * @version 0.1.1 2016/06/15
 * @author ExBin Project (http://exbin.org)
 */
public class Installer extends ModuleInstall {

    private static final String OPEN_AS_HEX_ACTION_STRING = "org-exbin-deltahex-OpenAsHexAction.shadow";
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
     * Creates references to the 'Open As Hex' action for all known file types.
     *
     * This is done when all modules are loaded and thus all file types have
     * been registered.
     */
    private static final class ActionInstaller extends FileTypeHandler {

        @Override
        protected void handleFileType(FileObject fileType) {
            try {
                final FileObject actionsFolder = FileUtil.createFolder(fileType, "Actions");
                final FileObject openAsHexAction = actionsFolder.getFileObject(OPEN_AS_HEX_ACTION_STRING);
                if (openAsHexAction == null) {
                    System.out.println("Actions: " + fileType.getName());
                    for (FileObject fileObject : actionsFolder.getChildren()) {
                        System.out.println(fileObject.getName() + ": " + fileObject.getAttribute("position"));
                    }
                    System.out.println();
                    /**
                     * Attempt to establish correct position in context menu for
                     * different mime types.
                     *
                     * TODO: check on different platforms and collisions with
                     * other modules / modes
                     */
                    int actionPosition = 375;
                    final FileObject openAction = actionsFolder.getFileObject(OPEN_ACTION_STRING);
                    final FileObject openIdeOpenAction = actionsFolder.getFileObject(OPENIDE_OPEN_ACTION_STRING);
                    final FileObject cutAction = actionsFolder.getFileObject(CUT_TO_CLIPBOARD_ACTION_STRING);
                    if (openAction != null) {
                        Object position = openAction.getAttribute("position");
                        if (position instanceof Integer) {
                            if ((Integer) position == 100) {
                                actionPosition = 175;
                            } else if ((Integer) position > actionPosition) {
                                actionPosition = (Integer) position + 25;
                            }
                        }
                    }
                    if (openIdeOpenAction != null) {
                        Object position = openIdeOpenAction.getAttribute("position");
                        if (position instanceof Integer) {
                            if ((Integer) position == 100) {
                                actionPosition = 175;
                            } else if ((Integer) position > actionPosition) {
                                actionPosition = (Integer) position + 25;
                            }
                        }
                    }
                    if (cutAction != null) {
                        Object position = cutAction.getAttribute("position");
                        if (position instanceof Integer) {
                            if ((Integer) position < actionPosition) {
                                actionPosition = (Integer) position - 25;
                            }
                        }
                    }

                    final FileObject action = actionsFolder.createData(OPEN_AS_HEX_ACTION_STRING);
                    action.setAttribute("originalFile", "Actions/File/org-exbin-deltahex-OpenAsHexAction.instance");
                    action.setAttribute("position", actionPosition);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Creates references to the 'Open As Hex' action for all known file types.
     *
     * This is done when all modules are loaded and thus all file types have
     * been registered.
     */
    private static final class ActionUninstaller extends FileTypeHandler {

        @Override
        protected void handleFileType(FileObject fileType) {
            try {
                final FileObject actionsFolder = FileUtil.createFolder(fileType, "Actions");
                final FileObject openAsHexAction = actionsFolder.getFileObject(OPEN_AS_HEX_ACTION_STRING);
                if (openAsHexAction != null) {
                    openAsHexAction.delete();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
