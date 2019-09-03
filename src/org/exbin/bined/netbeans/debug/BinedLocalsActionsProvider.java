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
package org.exbin.bined.netbeans.debug;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.Models;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 * Register view as binary action for debugger tree nodes.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2019/09/03
 */
@DebuggerServiceRegistration(path = "netbeans-JPDASession/LocalsView",
        types = NodeActionsProvider.class,
        position = 720)
@ParametersAreNonnullByDefault
public class BinedLocalsActionsProvider implements NodeActionsProvider {

    private final Action BINARY_DEBUG_ACTION = Models.createAction("View as Binary Data...", new Models.ActionPerformer() {
        @Override
        public boolean isEnabled(Object node) {
            return true;
        }

        @Override
        public void perform(Object[] arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }, Models.MULTISELECTION_TYPE_EXACTLY_ONE);

    public BinedLocalsActionsProvider() {
    }

    @Override
    public void performDefaultAction(Object node) throws UnknownTypeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Action[] getActions(Object node) throws UnknownTypeException {
        return new Action[]{BINARY_DEBUG_ACTION};
    }
}
