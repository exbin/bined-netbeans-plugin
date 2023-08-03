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

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.framework.bined.objectdata.ObjectValueConvertor;
import org.exbin.bined.netbeans.api.BinaryViewHandler;
import org.exbin.bined.netbeans.debug.gui.DebugViewPanel;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.util.lookup.ServiceProvider;

/**
 * BinEd View Data service.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@ServiceProvider(service = BinaryViewHandler.class)
public class BinaryViewDataService implements BinaryViewHandler {

    private final ObjectValueConvertor valueConvertor = new ObjectValueConvertor();

    @Nonnull
    @Override
    public Optional<BinaryData> instanceToBinaryData(Object instance) {
        return valueConvertor.process(instance);
    }

    @Nonnull
    @Override
    public JComponent createBinaryViewPanel(@Nullable BinaryData binaryData) {
        DebugViewPanel viewPanel = new DebugViewPanel();
        viewPanel.setContentData(binaryData);
        return viewPanel;
    }

    @Nonnull
    @Override
    public Optional<JComponent> createBinaryViewPanel(Object instance) {
        Optional<BinaryData> binaryData = valueConvertor.process(instance);
        DebugViewPanel viewPanel = new DebugViewPanel();
        if (binaryData.isPresent()) {
            viewPanel.setContentData(binaryData.get());
            return Optional.of(viewPanel);
        }

        return Optional.empty();
    }

    @Nonnull
    @Override
    public DialogWrapper createBinaryViewDialog(@Nullable BinaryData binaryData) {
        throw new UnsupportedOperationException("Not supported yet.");
//        Project project = ProjectManager.getInstance().getDefaultProject();
//        DataDialog dialog = new DataDialog(project, binaryData);
//        dialog.setTitle("View Binary Data");
//        return dialog;
    }

    @Nonnull
    @Override
    public DialogWrapper createBinaryViewDialog(Object instance) {
        Optional<BinaryData> binaryData = valueConvertor.process(instance);
        return createBinaryViewDialog(binaryData.orElse(null));
    }

//    @ParametersAreNonnullByDefault
//    private static class DataDialog extends DialogWrapper {
//
//        private final byte[] valuesCache = new byte[8];
//        private final ByteBuffer byteBuffer = ByteBuffer.wrap(valuesCache);
//
//        private final BinEdComponentPanel viewPanel;
//        private BinaryData binaryData;
//
//        private DataDialog(Project project, @Nullable BinaryData binaryData) {
//            super(project, false);
//            this.binaryData = binaryData;
//            setModal(false);
//            setCancelButtonText("Close");
//            getOKAction().setEnabled(false);
//            setCrossClosesWindow(true);
//
//            viewPanel = new BinEdComponentPanel();
//            viewPanel.setFileApi(new BinEdComponentFileApi() {
//                @Override
//                public boolean isSaveSupported() {
//                    return false;
//                }
//
//                @Override
//                public void saveDocument() {
//                }
//
//                @Override
//                public void switchFileHandlingMode(FileHandlingMode newHandlingMode) {
//                }
//
//                @Override
//                public void closeData() {
//                }
//            });
//            viewPanel.setContentData(binaryData);
//            init();
//        }
//
//        @Override
//        protected void doOKAction() {
//            super.doOKAction();
//        }
//
//        @Nonnull
//        @Override
//        protected Action[] createActions() {
//            return new Action[]{getCancelAction()};
//        }
//
//        @Nullable
//        @Override
//        public JComponent getPreferredFocusedComponent() {
//            return viewPanel;
//        }
//
//        @Override
//        protected String getDimensionServiceKey() {
//            return "#org.exbin.bined.intellij.debug.ViewBinaryAction";
//        }
//
//        @Override
//        protected JComponent createCenterPanel() {
//            BorderLayoutPanel panel = JBUI.Panels.simplePanel(viewPanel);
//            panel.setPreferredSize(JBUI.size(600, 400));
//            return panel;
//        }
//    }
}
