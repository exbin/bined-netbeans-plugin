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

import java.awt.event.ActionEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 * Debug view action.
 *
 * @version 0.2.1 2019/09/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public final class VariablesActionProvider implements org.netbeans.spi.viewmodel.NodeActionsProvider {

    @Override
    public void performDefaultAction(Object node) throws UnknownTypeException {
    }

    @Override
    public Action[] getActions(Object node) throws UnknownTypeException {
        return new Action[]{
            new AbstractAction("Test") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Test");
                }
            }
        };
    }
}
