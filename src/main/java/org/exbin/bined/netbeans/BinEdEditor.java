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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.text.CloneableEditor;
import static org.openide.windows.TopComponent.PERSISTENCE_NEVER;

/**
 * BinEd native NetBeans editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
@MultiViewElement.Registration(
        displayName = "#BinEdEditor.displayName",
        mimeType = BinEdDataObject.MIME,
        persistenceType = PERSISTENCE_NEVER,
        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        preferredID = BinEdEditor.ID,
        position = 1
)
public class BinEdEditor extends CloneableEditor implements MultiViewElement {

    public static final String ID = "bined-editor"; //NOI18N

    @Override
    public JComponent getVisualRepresentation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JComponent getToolbarRepresentation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CloseOperationState canCloseElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
    }
}
