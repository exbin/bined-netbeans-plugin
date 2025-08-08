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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.SwingUtilities;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * Open file in binary editor tools menu action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ActionID(category = "File", id = OpenAsBinaryToolsAction.ACTION_ID)
@ActionRegistration(displayName = "#CTL_OpenAsBinaryToolsAction", lazy = false)
@ActionReferences({
    @ActionReference(path = OpenAsBinaryToolsAction.ACTION_PATH_TOOLS, position = OpenAsBinaryToolsAction.ACTION_POSITION_TOOLS)
})
@NbBundle.Messages("CTL_OpenAsBinaryToolsAction=Open as Binary")
@ParametersAreNonnullByDefault
public final class OpenAsBinaryToolsAction extends NodeAction {

    public static final String ACTION_ID = "org.exbin.bined.OpenAsBinaryToolsAction";
    public static final String ACTION_PATH_TOOLS = "UI/ToolActions/Files";
    public static final int ACTION_POSITION_TOOLS = 2200;

    private static boolean enabled = true;

    public OpenAsBinaryToolsAction() {
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
    }

    @Override
    protected void performAction(Node[] nodes) {
        if (nodes.length != 1) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            final Mode editorMode = WindowManager.getDefault().findMode("editor");
            if (editorMode == null) {
                return;
            }

            final BinaryEditorTopComponent binaryEditor = new BinaryEditorTopComponent();
            editorMode.dockInto(binaryEditor);

            Lookup lookup = nodes[0].getLookup();
            DataObject dataObject = lookup.lookup(DataObject.class);

            if (dataObject instanceof DataShadow) {
                dataObject = ((DataShadow) dataObject).getOriginal();
            }

            if (dataObject != null) {
                binaryEditor.openDataObject(dataObject);
                binaryEditor.open();
                binaryEditor.requestActive();
            }
        });
    }

    @Override
    protected boolean enable(Node[] nodes) {
        if (!enabled || nodes.length != 1) {
            return false;
        }

        Lookup lookup = nodes[0].getLookup();
        DataObject dataObject = lookup.lookup(DataObject.class);

        if (dataObject instanceof DataShadow) {
            dataObject = ((DataShadow) dataObject).getOriginal();
        }

        if (dataObject == null) {
            return false;
        }

        FileObject primaryFile = dataObject.getPrimaryFile();
        return primaryFile != null && !primaryFile.isFolder();
    }

    @Nonnull
    @Override
    public String getName() {
        return NbBundle.getMessage(OpenAsBinaryToolsAction.class, "CTL_OpenAsBinaryAction");
    }

    @Nonnull
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {

            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
                enabled = integrationOptions.isRegisterContextToolsOpenAsBinary();
            }

            @Override
            public void uninstallIntegration() {
                enabled = false;
            }
        });
    }
}
