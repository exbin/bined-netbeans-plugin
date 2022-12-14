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

import javax.annotation.ParametersAreNonnullByDefault;
import org.openide.loaders.DataObject;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;

/**
 * BinEd editor support.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdEditorSupport extends DataEditorSupport {

    public BinEdEditorSupport(DataObject obj, Env env) {
        super(obj, env);
    }

    public BinEdEditorSupport(DataObject obj, Lookup lkp, Env env) {
        super(obj, lkp, env);
    }

}
