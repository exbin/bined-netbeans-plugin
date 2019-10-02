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
import org.exbin.bined.netbeans.BinaryDebugAction;
import org.netbeans.api.debugger.Watch;
import org.netbeans.spi.debugger.DebuggerServiceRegistration;
import org.netbeans.spi.viewmodel.Models;
import org.netbeans.spi.viewmodel.NodeActionsProvider;
import org.netbeans.spi.viewmodel.NodeActionsProviderFilter;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Register view as binary action for debugger tree nodes.
 *
 * @author ExBin Project (http://exbin.org)
 * @version 0.2.2 2019/10/01
 */
@DebuggerServiceRegistration(path = "netbeans-JPDASession/ResultsView",
        types = NodeActionsProviderFilter.class,
        position = 720)
@ParametersAreNonnullByDefault
public class BinedResultsActionsProviderFilter implements NodeActionsProviderFilter {

    private final Action BINARY_DEBUG_ACTION = Models.createAction("View as Binary...", new Models.ActionPerformer() {
        @Override
        public boolean isEnabled(Object node) {
            return true;
        }

        @Override
        public void perform(Object[] arg0) {
            if (arg0 == null || arg0.length == 0 || arg0[0] == null) {
                return;
            }

            String viewName = BinaryDebugAction.isWatchesViewNested() ? "localsView" : "watchesView";
            TopComponent watchesView = WindowManager.getDefault().findTopComponent(viewName);
            BinaryDebugAction.actionPerformed(watchesView, arg0[0]);
        }
    }, Models.MULTISELECTION_TYPE_EXACTLY_ONE);

    public BinedResultsActionsProviderFilter() {
    }

    @Override
    public void performDefaultAction(NodeActionsProvider original, Object node) throws UnknownTypeException {
        original.performDefaultAction(node);
    }

    @Override
    public Action[] getActions(NodeActionsProvider original, Object node) throws UnknownTypeException {
        Action[] originalActions = original.getActions(node);
        if (node == TreeModel.ROOT || (node instanceof Watch && (((Watch) node).getExpression() == null || ((Watch) node).getExpression().isEmpty()))) {
            return originalActions;
        }

        Action[] actions = new Action[originalActions.length + 1];
        System.arraycopy(originalActions, 0, actions, 0, originalActions.length);
        actions[actions.length - 1] = BINARY_DEBUG_ACTION;
        return actions;
    }
}
