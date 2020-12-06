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
package org.exbin.framework.editor.text.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.AbstractTableModel;

/**
 * Table model for encoding / character sets.
 *
 * @version 0.2.1 2019/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class EncodingsTableModel extends AbstractTableModel {

    private final Map<String, EncodingRecord> encodings = new HashMap<>();
    private final List<String> filtered = new ArrayList<>();
    private final Set<String> usedEncodings = new HashSet<>();
    private String nameFilter = "";
    private String countryFilter = "";

    public EncodingsTableModel() {
        initEncodings();
    }

    private void initEncodings() {
        Charset.availableCharsets().entrySet().stream().map((entry) -> entry.getValue()).forEachOrdered((charset) -> {
            EncodingRecord record = new EncodingRecord();
            record.name = charset.name();
            try {
                record.maxBytes = (int) charset.newEncoder().maxBytesPerChar();
            } catch (UnsupportedOperationException ex) {
                // ignore
            }
            encodings.put(charset.name().toLowerCase(), record);
        });

        try (InputStream stream = this.getClass().getResourceAsStream("/org/exbin/framework/editor/text/resources/encodingsMap.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String encoding;
            do {
                encoding = reader.readLine();
                if (encoding != null) {
                    EncodingRecord record = encodings.get(encoding.toLowerCase());
                    if (record != null) {
                        record.description = reader.readLine();
                        record.countries = reader.readLine();
                    } else {
                        reader.readLine();
                        reader.readLine();
                    }
                }
            } while (encoding != null);
        } catch (IOException ex) {
            Logger.getLogger(EncodingsTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Nullable
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "Description";
            case 2:
                return "Country Codes";
            case 3:
                return "Max Bytes";
        }

        return null;
    }

    private void rebuildFiltered() {
        filtered.clear();
        encodings.entrySet().forEach((record) -> {
            String name = record.getKey();
            if (!(usedEncodings.contains(name))) {
                if (!(!nameFilter.isEmpty() && !name.contains(nameFilter.toLowerCase()))) {
                    if (!(!countryFilter.isEmpty() && (record.getValue().countries == null || !record.getValue().countries.toLowerCase().contains(countryFilter.toLowerCase())))) {
                        filtered.add(name);
                    }
                }
            }
        });
        Collections.sort(filtered);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return filtered.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Nullable
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String name = filtered.get(rowIndex);
        EncodingRecord record = encodings.get(name);
        switch (columnIndex) {
            case 0: {
                return record.name;
            }
            case 1: {
                return record.description;
            }
            case 2: {
                return record.countries;
            }
            case 3: {
                return record.maxBytes;
            }
        }

        return null;
    }

    public void setUsedEncodings(List<String> encodings) {
        usedEncodings.clear();
        encodings.forEach((name) -> {
            usedEncodings.add(name.toLowerCase());
        });
        rebuildFiltered();
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
        rebuildFiltered();
    }

    public void setCountryFilter(String countryFilter) {
        this.countryFilter = countryFilter;
        rebuildFiltered();
    }

    /**
     * POJO structure for single record.
     */
    private static class EncodingRecord {

        String name;
        String description;
        /**
         * Space separated country codes.
         */
        String countries;
        int maxBytes;
    }
}
