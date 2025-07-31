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
package org.exbin.bined.netbeans.diff;

import java.awt.Component;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import javax.swing.JPanel;
import org.netbeans.api.diff.Difference;

/**
 * Binary editor options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@org.openide.util.lookup.ServiceProvider(service = org.netbeans.spi.diff.DiffVisualizer.class)
public class BinaryDiffVisualizer extends org.netbeans.spi.diff.DiffVisualizer implements Serializable {

    private boolean contextMode = true;
    
    static final long serialVersionUID = -2481513747957146262L;

    /**
     * Creates a new instance of TextDiffVisualizer
     */
    public BinaryDiffVisualizer() {
    }

    /**
     * Get the display name of this diff visualizer.
     *
     * @return name
     */
    public String getDisplayName() {
        return "Binary";
    }

    /**
     * Get a short description of this diff visualizer.
     *
     * @return description
     */
    public String getShortDescription() {
        return "Binary";
    }

    @Override
    public Component createView(Difference[] diffs, String name1, String title1, Reader r1, String name2, String title2, Reader r2, String MIMEType) throws IOException {
        return new JPanel();
    }

    /**
     * Getter for property contextMode.
     *
     * @return Value of property contextMode.
     */
    public boolean isContextMode() {
        return contextMode;
    }

    /**
     * Setter for property contextMode.
     *
     * @param contextMode New value of property contextMode.
     */
    public void setContextMode(boolean contextMode) {
        this.contextMode = contextMode;
    }
}
