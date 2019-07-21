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
package org.exbin.framework.bined.preferences;

import org.exbin.framework.api.Preferences;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.capability.RowWrappingCapable;
import org.exbin.bined.swing.extended.layout.DefaultExtendedCodeAreaLayoutProfile;
import org.exbin.bined.swing.extended.layout.ExtendedCodeAreaDecorations;
import org.exbin.bined.swing.extended.theme.ExtendedCodeAreaThemeProfile;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.editor.text.preferences.TextEncodingPreferences;

/**
 * Binary editor preferences.
 *
 * @version 0.2.1 2019/07/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryEditorPreferences {

    private final static String PREFERENCES_VERSION = "version";
    private final static String PREFERENCES_VERSION_VALUE = "0.2.0";

    private final Preferences preferences;

    private final EditorPreferences editorParameters;
    private final StatusPreferences statusParameters;
    private final CodeAreaPreferences codeAreaParameters;
    private final TextEncodingPreferences encodingParameters;
    private final CodeAreaLayoutPreferences layoutParameters;
    private final CodeAreaThemePreferences themeParameters;
    private final CodeAreaColorPreferences colorParameters;

    public BinaryEditorPreferences(Preferences preferences) {
        this.preferences = preferences;

        editorParameters = new EditorPreferences(preferences);
        statusParameters = new StatusPreferences(preferences);
        codeAreaParameters = new CodeAreaPreferences(preferences);
        encodingParameters = new TextEncodingPreferences(preferences);
        layoutParameters = new CodeAreaLayoutPreferences(preferences);
        themeParameters = new CodeAreaThemePreferences(preferences);
        colorParameters = new CodeAreaColorPreferences(preferences);

        final String legacyDef = "LEGACY";
        String storedVersion = preferences.get(PREFERENCES_VERSION, legacyDef);
        if (legacyDef.equals(storedVersion)) {
            try {
                importLegacyPreferences();
            } finally {
                preferences.put(PREFERENCES_VERSION, PREFERENCES_VERSION_VALUE);
                preferences.flush();
            }
        }
    }

    @Nonnull
    public Preferences getPreferences() {
        return preferences;
    }

    @Nonnull
    public EditorPreferences getEditorPreferences() {
        return editorParameters;
    }

    @Nonnull
    public StatusPreferences getStatusPreferences() {
        return statusParameters;
    }

    @Nonnull
    public CodeAreaPreferences getCodeAreaPreferences() {
        return codeAreaParameters;
    }

    public TextEncodingPreferences getEncodingPreferences() {
        return encodingParameters;
    }

    @Nonnull
    public CodeAreaLayoutPreferences getLayoutPreferences() {
        return layoutParameters;
    }

    @Nonnull
    public CodeAreaThemePreferences getThemePreferences() {
        return themeParameters;
    }

    @Nonnull
    public CodeAreaColorPreferences getColorPreferences() {
        return colorParameters;
    }

    private void importLegacyPreferences() {
        LegacyPreferences legacyPreferences = new LegacyPreferences(preferences);
        codeAreaParameters.setUseDefaultFont(legacyPreferences.isUseDefaultFont());
        codeAreaParameters.setCodeFont(legacyPreferences.getCodeFont(CodeAreaOptions.DEFAULT_FONT));
        codeAreaParameters.setCodeType(legacyPreferences.getCodeType());
        codeAreaParameters.setRowWrappingMode(legacyPreferences.isLineWrapping() ? RowWrappingCapable.RowWrappingMode.WRAPPING : RowWrappingCapable.RowWrappingMode.NO_WRAPPING);
        codeAreaParameters.setShowUnprintables(legacyPreferences.isShowNonprintables());
        codeAreaParameters.setCodeCharactersCase(legacyPreferences.getCodeCharactersCase());
        codeAreaParameters.setPositionCodeType(legacyPreferences.getPositionCodeType());
        codeAreaParameters.setViewMode(legacyPreferences.getViewMode());
        codeAreaParameters.setPaintRowPosBackground(legacyPreferences.isPaintRowPosBackground());
        codeAreaParameters.setCodeColorization(legacyPreferences.isCodeColorization());

        editorParameters.setFileHandlingMode(legacyPreferences.isDeltaMemoryMode() ? FileHandlingMode.DELTA : FileHandlingMode.MEMORY);
        editorParameters.setShowValuesPanel(legacyPreferences.isShowValuesPanel());

        List<String> layoutProfiles = new ArrayList<>();
        layoutProfiles.add("Imported profile");
        DefaultExtendedCodeAreaLayoutProfile layoutProfile = new DefaultExtendedCodeAreaLayoutProfile();
        layoutProfile.setShowHeader(legacyPreferences.isShowHeader());
        layoutProfile.setShowRowPosition(legacyPreferences.isShowLineNumbers());
        layoutProfile.setSpaceGroupSize(legacyPreferences.getByteGroupSize());
        layoutProfile.setDoubleSpaceGroupSize(legacyPreferences.getSpaceGroupSize());
        layoutParameters.setLayoutProfile(0, layoutProfile);
        layoutParameters.setLayoutProfilesList(layoutProfiles);

        List<String> themeProfiles = new ArrayList<>();
        themeProfiles.add("Imported profile");
        ExtendedCodeAreaThemeProfile themeProfile = new ExtendedCodeAreaThemeProfile();
        themeProfile.setBackgroundPaintMode(legacyPreferences.getBackgroundPaintMode());
        themeProfile.setPaintRowPosBackground(legacyPreferences.isPaintRowPosBackground());
        themeProfile.setDecoration(ExtendedCodeAreaDecorations.HEADER_LINE, legacyPreferences.isDecorationHeaderLine());
        themeProfile.setDecoration(ExtendedCodeAreaDecorations.ROW_POSITION_LINE, legacyPreferences.isDecorationLineNumLine());
        themeProfile.setDecoration(ExtendedCodeAreaDecorations.SPLIT_LINE, legacyPreferences.isDecorationPreviewLine());
        themeProfile.setDecoration(ExtendedCodeAreaDecorations.BOX_LINES, legacyPreferences.isDecorationBox());
        themeParameters.setThemeProfile(0, themeProfile);
        themeParameters.setThemeProfilesList(themeProfiles);

        encodingParameters.setSelectedEncoding(legacyPreferences.getSelectedEncoding());
        encodingParameters.setEncodings(new ArrayList<>(legacyPreferences.getEncodings()));
        Collection<String> legacyEncodings = legacyPreferences.getEncodings();
        List<String> encodings = new ArrayList<>(legacyEncodings);
        if (!encodings.isEmpty() && !encodings.contains(TextEncodingPreferences.ENCODING_UTF8)) {
            encodings.add(TextEncodingPreferences.ENCODING_UTF8);
        }
        encodingParameters.setEncodings(encodings);

        preferences.flush();
    }
}
