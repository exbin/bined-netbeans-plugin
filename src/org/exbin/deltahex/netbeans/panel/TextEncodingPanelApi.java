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
package org.exbin.deltahex.netbeans.panel;

import java.util.List;

/**
 * XB Text Editor Module.
 *
 * @version 0.1.4 2016/12/20
 * @author ExBin Project (http://exbin.org)
 */
public interface TextEncodingPanelApi {

    /**
     * Returns current encodings used in application frame.
     *
     * @return font
     */
    public List<String> getEncodings();

    /**
     * Gets selected encoding.
     *
     * @return selected encoding
     */
    public String getSelectedEncoding();

    /**
     * Sets current encodings used in application frame.
     *
     * @param encodings list of encodings
     */
    public void setEncodings(List<String> encodings);

    /**
     * Sets selected encoding.
     *
     * @param encoding encoding
     */
    public void setSelectedEncoding(String encoding);

}
