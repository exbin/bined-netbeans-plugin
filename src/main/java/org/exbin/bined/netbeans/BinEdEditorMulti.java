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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.event.DocumentListener;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * BinEd multiview tab editor for all MIME types.
 *
 * @author ExBin Project (https://exbin.org)
 */
@MultiViewElement.Registration(
        displayName = "#BinEdEditor.displayName",
        mimeType = "",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        iconBase = "org/exbin/bined/netbeans/resources/icons/icon.png",
        preferredID = BinEdEditorMulti.ELEMENT_MULTI_ID,
        position = BinEdEditorMulti.POSITION_ALL_ATTRIBUTE
)
@ParametersAreNonnullByDefault
public class BinEdEditorMulti extends BinEdEditor implements MultiViewElement {

    public static final String ELEMENT_MULTI_ID = "org.exbin.bined.netbeans.BinEdEditorMulti";
    public static final String ELEMENT_MULTI_NAME = "org-exbin-bined-netbeans-BinEdEditorMulti";
    public static final int POSITION_ALL_ATTRIBUTE = 900006;

//    private final DocumentListener documentListener;
    
    public BinEdEditorMulti(Lookup lookup) {
        super(lookup);
    }
    
    public void openFile() {
        
    }
}
