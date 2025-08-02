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
import javax.annotation.Nonnull;
import org.exbin.bined.netbeans.Installer;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.bined.swing.section.diff.SectCodeAreaDiffPanel;
import org.netbeans.api.diff.Difference;

/**
 * Binary files comparator difference visualizer.
 *
 * @author ExBin Project (https://exbin.org)
 */
//@org.openide.util.lookup.ServiceProvider(service = org.netbeans.spi.diff.DiffVisualizer.class)
public class BinaryDiffVisualizer extends org.netbeans.spi.diff.DiffVisualizer implements Serializable {

    private boolean contextMode = true;

    public BinaryDiffVisualizer() {
    }

    /**
     * Returns the display name of this diff visualizer.
     *
     * @return name
     */
    @Nonnull
    public String getDisplayName() {
        return "Binary";
    }

    /**
     * Returns a short description of this diff visualizer.
     *
     * @return description
     */
    @Nonnull
    public String getShortDescription() {
        return "Binary";
    }

    @Nonnull
    @Override
    public Component createView(Difference[] diffs, String name1, String title1, Reader r1, String name2, String title2, Reader r2, String MIMEType) throws IOException {
        SectCodeAreaDiffPanel panel = new SectCodeAreaDiffPanel();
        return panel;
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

    public static void registerIntegration() {
        Installer.addIntegrationOptionsListener(new Installer.IntegrationOptionsListener() {
            @Override
            public void integrationInit(IntegrationOptions integrationOptions) {
                if (integrationOptions.isRegisterByteToByteDiffTool()) {
                    install();
                } else {
                    uninstall();
                }
            }

            @Override
            public void uninstallIntegration() {
                uninstall();
            }
        });
    }

    public static void install() {

    }

    public static void uninstall() {

    }
}
