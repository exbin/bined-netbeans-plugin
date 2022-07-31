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
package org.exbin.framework.utils;

import java.awt.Component;
import java.awt.Container;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Recursive interface for panels creating lazy components.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RecursiveLazyComponentListener implements LazyComponentListener {

    private final LazyComponentListener listener;

    public RecursiveLazyComponentListener(LazyComponentListener listener) {
        this.listener = listener;
    }

    @Override
    public void componentCreated(Component component) {
        fireListener(component);
    }

    protected void fireListener(Component component) {
        listener.componentCreated(component);

        if (component instanceof Container) {
            Component[] comps = ((Container) component).getComponents();
            for (Component child : comps) {
                fireListener(child);
            }
        }

        if (component instanceof LazyComponentsIssuable) {
            ((LazyComponentsIssuable) component).addChildComponentListener(this);
        }
    }
}
