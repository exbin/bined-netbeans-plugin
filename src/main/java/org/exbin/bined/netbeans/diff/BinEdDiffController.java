/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.exbin.bined.netbeans.diff;

import org.netbeans.spi.diff.DiffControllerImpl;
import org.netbeans.spi.diff.DiffControllerProvider;
import org.openide.util.Lookup;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.StreamSource;

/**
 * Imported from NetBeans diff module.
 * <p>
 * Encapsulates a single Diff window that displays differences between two files (sources).
 * 
 * @author Maros Sandor
 * @since 1.18
 */
public final class BinEdDiffController {

    /**
     * Property change that indicates that set of differences OR the current difference changed. Current difference
     * changes as the user navigates in the view and set of differences may change if the view is editable or a source
     * changes programatically. 
     * Clients should update their state that depends on the current difference index or total number of differences. 
     */
    public static final String PROP_DIFFERENCES = "(void) differencesChanged"; // NOI18N

    /**
     * Enumerates Base (left) and Modified (right) panes of a Diff view for setLocation() method
     */
    public enum DiffPane { Base, Modified };

    /**
     * Enumerates types of location for setLocation() method. 
     */
    public enum LocationType { LineNumber, DifferenceIndex };

    private final DiffControllerImpl impl;

    /**
     * Creates a Diff Controller for supplied left and right sources.
     * 
     * @param base defines content of the Base Diff pane
     * @param modified defines content of the Modified (possibly editable) Diff pane
     * @return DiffController implementation of the DiffController class
     * @throws java.io.IOException when the reading from input streams fails.
     */
    public static BinEdDiffController create(StreamSource base, StreamSource modified) throws IOException {
        // DiffControllerProvider provider = Lookup.getDefault().lookup(DiffControllerProvider.class);
        BinEdDiffControllerProvider provider = BinEdDiffControllerProvider.getInstance();
        if (provider != null) {
            return new BinEdDiffController(provider.createDiffController(base, modified));
        } else {
            DiffView view = Diff.getDefault().createDiff(base, modified);
            return new BinEdDiffController(new DiffControllerViewBridge(view));
        }
    }

    /**
     * Creates a Diff Controller for supplied left and right sources capable of creating enhanced UI.
     *
     * @param base defines content of the Base Diff pane
     * @param modified defines content of the Modified (possibly editable) Diff pane
     * @return DiffController implementation of the DiffController class
     * @throws java.io.IOException when the reading from input streams fails.
     * @since 1.27
     */
    public static BinEdDiffController createEnhanced (StreamSource base, StreamSource modified) throws IOException {
        // DiffControllerProvider provider = Lookup.getDefault().lookup(DiffControllerProvider.class);
        BinEdDiffControllerProvider provider = BinEdDiffControllerProvider.getInstance();
        if (provider != null) {
            return new BinEdDiffController(provider.createEnhancedDiffController(base, modified));
        } else {
            DiffView view = Diff.getDefault().createDiff(base, modified);
            return new BinEdDiffController(new DiffControllerViewBridge(view));
        }
    }
        
    private BinEdDiffController(DiffControllerImpl impl) {
        this.impl = impl;
    }
    
    /**
     * Ensure the requested location in the Diff view is visible on screen. Diff view can be requested to jump to
     * a given line in either source or to a given Difference.
     * Diff controller may ignore the request if it does not support this functionality.
     * This method must be called from AWT thread.
     * 
     * @param pane defines which pane the location parameter refers to
     * @param type defines the location parameter, see below
     * @param location depending on the type parameter this defines either a line number or a Difference index, both 0-based
     * @throws IllegalArgumentException if location parameter is out of range for the given pane and location type
     */
    public void setLocation(org.netbeans.api.diff.DiffController.DiffPane pane, org.netbeans.api.diff.DiffController.LocationType type, int location) {
        impl.setLocation(pane, type, location);
    }

    /**
     * Intializes the Controller and creates visual presenter of the Diff.
     * 
     * @return JComponent component to be embedded into client UI
     */
    public JComponent getJComponent() {
        return impl.getJComponent();
    }

    /**
     * Gets total number of Differences between sources currently displayed in the Diff view.
     * 
     * @return total number of Differences in sources, an integer >= 0
     */
    public int getDifferenceCount() {
        return impl.getDifferenceCount();
    }

    /**
     * Gets the current (highlighted) difference in the Diff view.
     * 
     * @return current difference index or -1 of there is no Current difference
     */
    public int getDifferenceIndex() {
        return impl.getDifferenceIndex();
    }

    /**
     * Adds a property change listener.
     * 
     * @param listener property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        impl.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes a property change listener.
     * 
     * @param listener property change listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        impl.removePropertyChangeListener(listener);
    }

    /**
     * If there is no registered DiffControllerProvider, this provides a bridge from DiffView to DiffControllerProvider.
     */
    private static class DiffControllerViewBridge extends DiffControllerImpl {
        
        private final DiffView view;

        DiffControllerViewBridge(DiffView view) {
            this.view = view;
        }

        public void setLocation(BinEdDiffController.DiffPane pane, BinEdDiffController.LocationType type, int location) {
            if (type == BinEdDiffController.LocationType.DifferenceIndex) {
                view.setCurrentDifference(location);
            }
        }

        public JComponent getJComponent() {
            return (JComponent) view.getComponent();
        }

        public int getDifferenceCount() {
            return view.getDifferenceCount();
        }
    }
}
