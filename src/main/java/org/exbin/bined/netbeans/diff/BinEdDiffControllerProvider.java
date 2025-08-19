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

import org.netbeans.spi.diff.DiffControllerProvider;
import org.netbeans.spi.diff.DiffControllerImpl;
import org.netbeans.api.diff.StreamSource;
import org.openide.util.Lookup;

import java.io.IOException;

/**
 * BinEd alternative diff controller provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
//@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.diff.DiffControllerProvider.class)
public class BinEdDiffControllerProvider extends DiffControllerProvider {
    
    private static BinEdDiffControllerProvider instance = null;

    public static BinEdDiffControllerProvider getInstance() {
        if (instance == null) {
            instance = new BinEdDiffControllerProvider();
        }
        return instance;
    }

    @Override
    public DiffControllerImpl createDiffController(StreamSource base, StreamSource modified) throws IOException {
        return new EditableDiffView(base, modified);
    }

    @Override
    public DiffControllerImpl createEnhancedDiffController(StreamSource base, StreamSource modified) throws IOException {
        if (Boolean.getBoolean("netbeans.diff.default.compact")) {
            return createDiffController(base, modified);
        }
        return new EditableDiffView(base, modified, true);
    }
    
    public static void install() {
        Lookup.getDefault().lookup(org.netbeans.spi.diff.DiffControllerProvider.class);
    }

    public static void uninstall() {
        
    }
}
