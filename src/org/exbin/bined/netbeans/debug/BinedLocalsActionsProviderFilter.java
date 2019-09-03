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
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 * Register view as binary action for debugger tree nodes.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.1 2019/09/03
 */
@DebuggerServiceRegistration(path = "netbeans-JPDASession/LocalsView",
        types = NodeActionsProviderFilter.class,
        position = 720)
@ParametersAreNonnullByDefault
public class BinedLocalsActionsProviderFilter implements NodeActionsProviderFilter {

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

    public BinedLocalsActionsProviderFilter() {
    }

    @Override
    public void performDefaultAction(NodeActionsProvider original, Object node) throws UnknownTypeException {
        original.performDefaultAction(node);
    }

    @Override
    public Action[] getActions(NodeActionsProvider original, Object node) throws UnknownTypeException {
        Action[] originalActions = original.getActions(node);
        Action[] actions = new Action[originalActions.length + 1];
        System.arraycopy(originalActions, 0, actions, 0, originalActions.length);
        BinedLocalsActionsProvider binedActionsProvider = new BinedLocalsActionsProvider();
        Action[] binedActions = binedActionsProvider.getActions(node);
        actions[actions.length - 1] = binedActions[0];
        return actions;
    }
}
