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
package org.exbin.bined.netbeans.diff.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.CodeAreaUtils;
import org.exbin.bined.CodeType;
import org.exbin.bined.EditOperation;
import org.exbin.bined.capability.CharsetCapable;
import org.exbin.bined.highlight.swing.NonprintablesCodeAreaAssessor;
import org.exbin.bined.netbeans.gui.BinEdToolbarPanel;
import org.exbin.bined.netbeans.options.BinEdApplyOptions;
import org.exbin.bined.operation.swing.CodeAreaOperationCommandHandler;
import org.exbin.bined.section.layout.SectionCodeAreaLayoutProfile;
import org.exbin.bined.swing.CodeAreaPainter;
import org.exbin.bined.swing.CodeAreaSwingUtils;
import org.exbin.bined.swing.basic.color.CodeAreaColorsProfile;
import org.exbin.bined.swing.capability.CharAssessorPainterCapable;
import org.exbin.bined.swing.capability.ColorAssessorPainterCapable;
import org.exbin.bined.swing.capability.FontCapable;
import org.exbin.bined.swing.section.SectCodeArea;
import org.exbin.bined.swing.section.diff.SectCodeAreaDiffPanel;
import org.exbin.bined.swing.section.theme.SectionCodeAreaThemeProfile;
import org.exbin.framework.App;
import org.exbin.framework.bined.BinEdCodeAreaAssessor;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.bined.editor.options.BinaryEditorOptions;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.bined.handler.CodeAreaPopupMenuHandler;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.bined.theme.options.CodeAreaColorOptions;
import org.exbin.framework.bined.theme.options.CodeAreaLayoutOptions;
import org.exbin.framework.bined.theme.options.CodeAreaThemeOptions;
import org.exbin.framework.bined.viewer.options.CodeAreaOptions;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.action.OptionsAction;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.text.encoding.EncodingsHandler;
import org.exbin.framework.text.encoding.TextEncodingStatusApi;
import org.exbin.framework.text.encoding.options.TextEncodingOptions;
import org.exbin.framework.text.font.options.TextFontOptions;
import org.exbin.framework.utils.DesktopUtils;

/**
 * BinEd diff support provider to compare binary files.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdDiffPanel extends JPanel {

    private final OptionsStorage preferences;
    private final SectCodeAreaDiffPanel diffPanel = new SectCodeAreaDiffPanel();

    private final Font defaultFont;
    private final SectionCodeAreaLayoutProfile defaultLayoutProfile;
    private final SectionCodeAreaThemeProfile defaultThemeProfile;
    private final CodeAreaColorsProfile defaultColorProfile;

    private final BinEdToolbarPanel toolbarPanel;
    private final BinaryStatusPanel statusPanel;
    private EncodingsHandler encodingsHandler;
    private BinaryStatusApi binaryStatus;
    private TextEncodingStatusApi encodingStatus;
    private GoToPositionAction goToPositionAction = new GoToPositionAction();
    
    public BinEdDiffPanel() {
        setLayout(new java.awt.BorderLayout());

        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        preferences = preferencesModule.getAppPreferences();
        defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        SectCodeArea leftCodeArea = diffPanel.getLeftCodeArea();
        SectCodeArea rightCodeArea = diffPanel.getRightCodeArea();

        CodeAreaPainter leftPainter = leftCodeArea.getPainter();
        BinEdCodeAreaAssessor codeAreaAssessor = new BinEdCodeAreaAssessor(((ColorAssessorPainterCapable)leftPainter).getColorAssessor(), ((CharAssessorPainterCapable)leftPainter).getCharAssessor());
        ((ColorAssessorPainterCapable)leftPainter).setColorAssessor(codeAreaAssessor);
        ((CharAssessorPainterCapable)leftPainter).setCharAssessor(codeAreaAssessor);
        CodeAreaPainter rightPainter = rightCodeArea.getPainter();
        codeAreaAssessor = new BinEdCodeAreaAssessor(((ColorAssessorPainterCapable)rightPainter).getColorAssessor(), ((CharAssessorPainterCapable)rightPainter).getCharAssessor());
        ((ColorAssessorPainterCapable)rightPainter).setColorAssessor(codeAreaAssessor);
        ((CharAssessorPainterCapable)rightPainter).setCharAssessor(codeAreaAssessor);

        defaultLayoutProfile = leftCodeArea.getLayoutProfile();
        defaultThemeProfile = leftCodeArea.getThemeProfile();
        defaultColorProfile = leftCodeArea.getColorsProfile();
        toolbarPanel = new BinEdToolbarPanel();
        toolbarPanel.setTargetComponent(diffPanel);
        toolbarPanel.setCodeAreaControl(new BinEdToolbarPanel.Control() {
            @Nonnull
            @Override public CodeType getCodeType() {
                return leftCodeArea.getCodeType();
            }

            @Override
            public void setCodeType(CodeType codeType) {
                leftCodeArea.setCodeType(codeType);
                rightCodeArea.setCodeType(codeType);
            }

            @Override
            public boolean isShowNonprintables() {
                ColorAssessorPainterCapable painter = (ColorAssessorPainterCapable) leftCodeArea.getPainter();
                NonprintablesCodeAreaAssessor nonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(painter, NonprintablesCodeAreaAssessor.class);
                return CodeAreaUtils.requireNonNull(nonprintablesCodeAreaAssessor).isShowNonprintables();
            }

            @Override
            public void setShowNonprintables(boolean showNonprintables) {
                ColorAssessorPainterCapable leftPainter = (ColorAssessorPainterCapable) leftCodeArea.getPainter();
                NonprintablesCodeAreaAssessor leftNonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(leftPainter, NonprintablesCodeAreaAssessor.class);
                CodeAreaUtils.requireNonNull(leftNonprintablesCodeAreaAssessor).setShowNonprintables(showNonprintables);
                ColorAssessorPainterCapable rightPainter = (ColorAssessorPainterCapable) rightCodeArea.getPainter();
                NonprintablesCodeAreaAssessor rightNonprintablesCodeAreaAssessor = CodeAreaSwingUtils.findColorAssessor(rightPainter, NonprintablesCodeAreaAssessor.class);
                CodeAreaUtils.requireNonNull(rightNonprintablesCodeAreaAssessor).setShowNonprintables(showNonprintables);
            }

            @Override
            public void repaint() {
                diffPanel.repaint();
            }
        });
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsAction optionsAction = (OptionsAction) optionsModule.createOptionsAction();
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        optionsAction.setDialogParentComponent(() -> frameModule.getFrame());
        toolbarPanel.setOptionsAction(optionsAction);
        toolbarPanel.setOnlineHelpAction(createOnlineHelpAction());
        statusPanel = new BinaryStatusPanel();

        init();
    }

    private void init() {
        this.add(toolbarPanel, BorderLayout.NORTH);
        registerEncodingStatus(statusPanel);
        encodingsHandler = new EncodingsHandler();
        encodingsHandler.init();
        encodingsHandler.setTextEncodingStatus(new TextEncodingStatusApi() {
            @Nonnull
            @Override
            public String getEncoding() {
                return encodingStatus.getEncoding();
            }

            @Override
            public void setEncoding(String encodingName) {
                diffPanel.getLeftCodeArea().setCharset(Charset.forName(encodingName));
                diffPanel.getRightCodeArea().setCharset(Charset.forName(encodingName));
                encodingStatus.setEncoding(encodingName);
                new TextEncodingOptions(preferences).setSelectedEncoding(encodingName);
            }
        });
        goToPositionAction.setup(App.getModule(LanguageModuleApi.class).getBundle(BinedModule.class));

        registerBinaryStatus(statusPanel);

        initialLoadFromPreferences();
        BinedModule binedModule = App.getModule(BinedModule.class);
        CodeAreaPopupMenuHandler codeAreaPopupMenuHandler = binedModule.createCodeAreaPopupMenuHandler(BinedModule.PopupMenuVariant.BASIC);
        diffPanel.getLeftCodeArea().setComponentPopupMenu(createPopupMenu(codeAreaPopupMenuHandler, "compareLeft"));
        diffPanel.getRightCodeArea().setComponentPopupMenu(createPopupMenu(codeAreaPopupMenuHandler, "compareRight"));
 
        this.add(statusPanel, BorderLayout.SOUTH);
        this.add(diffPanel, BorderLayout.CENTER);
        diffPanel.revalidate();
        diffPanel.repaint();
        revalidate();
        repaint();
    }
    
    public void registerBinaryStatus(BinaryStatusApi binaryStatusApi) {
        this.binaryStatus = binaryStatusApi;
        SectCodeArea leftCodeArea = diffPanel.getLeftCodeArea();
        SectCodeArea rightCodeArea = diffPanel.getRightCodeArea();
        leftCodeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });
        rightCodeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });
        leftCodeArea.addSelectionChangedListener(() -> {
            binaryStatus.setSelectionRange(leftCodeArea.getSelection());
        });
        rightCodeArea.addSelectionChangedListener(() -> {
            binaryStatus.setSelectionRange(rightCodeArea.getSelection());
        });

        leftCodeArea.addEditModeChangedListener(binaryStatus::setEditMode);
        rightCodeArea.addEditModeChangedListener(binaryStatus::setEditMode);

        leftCodeArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateBinaryStatus(leftCodeArea);
            }
        });
        rightCodeArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateBinaryStatus(rightCodeArea);
            }
        });

        updateBinaryStatus(leftCodeArea);

        ((BinaryStatusPanel) binaryStatus).setController(new BinaryStatusController());
    }

    private void updateBinaryStatus(SectCodeArea codeArea) {
        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
        binaryStatus.setCursorPosition(codeArea.getActiveCaretPosition());
        binaryStatus.setSelectionRange(codeArea.getSelection());
        long dataSize = codeArea.getDataSize();
        binaryStatus.setCurrentDocumentSize(dataSize, dataSize);
        goToPositionAction.setCodeArea(codeArea);
    }

    private void initialLoadFromPreferences() {
        applyOptions(new BinEdApplyOptions() {
            @Nonnull
            @Override
            public CodeAreaOptions getCodeAreaOptions() {
                return new CodeAreaOptions(preferences);
            }

            @Nonnull
            @Override
            public TextEncodingOptions getEncodingOptions() {
                return new TextEncodingOptions(preferences);
            }

            @Nonnull
            @Override
            public TextFontOptions getFontOptions() {
                return new TextFontOptions(preferences);
            }

            @Nonnull
            @Override
            public BinaryEditorOptions getEditorOptions() {
                return new BinaryEditorOptions(preferences);
            }

            @Nonnull
            @Override
            public StatusOptions getStatusOptions() {
                return new StatusOptions(preferences);
            }

            @Nonnull
            @Override
            public CodeAreaLayoutOptions getLayoutOptions() {
                return new CodeAreaLayoutOptions(preferences);
            }

            @Nonnull
            @Override
            public CodeAreaColorOptions getColorOptions() {
                return new CodeAreaColorOptions(preferences);
            }

            @Nonnull
            @Override
            public CodeAreaThemeOptions getThemeOptions() {
                return new CodeAreaThemeOptions(preferences);
            }
        });

        encodingsHandler.loadFromOptions(new TextEncodingOptions(preferences));
        statusPanel.loadFromOptions(new StatusOptions(preferences));
        toolbarPanel.loadFromOptions(preferences);

        updateCurrentMemoryMode();
    }

    private void updateCurrentMemoryMode() {
        BinaryStatusApi.MemoryMode memoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;

        if (binaryStatus != null) {
            binaryStatus.setMemoryMode(memoryMode);
        }
    }

    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        // TODO
//        setCharsetChangeListener(() -> {
//            String selectedEncoding = diffPanel.getLeftCodeArea().getCharset().name();
//            encodingStatus.setEncoding(selectedEncoding);
//        });
    }

    private void applyOptions(BinEdApplyOptions applyOptions) {
        applyOptions(applyOptions, diffPanel.getLeftCodeArea());
        applyOptions(applyOptions, diffPanel.getRightCodeArea());
    }

    private void applyOptions(BinEdApplyOptions applyOptions, SectCodeArea codeArea) {
        CodeAreaOptions.applyToCodeArea(applyOptions.getCodeAreaOptions(), codeArea);

        ((CharsetCapable) codeArea).setCharset(Charset.forName(applyOptions.getEncodingOptions()
                .getSelectedEncoding()));
        encodingsHandler.setEncodings(applyOptions.getEncodingOptions().getEncodings());
        ((FontCapable) codeArea).setCodeFont(applyOptions.getFontOptions().isUseDefaultFont() ?
                defaultFont :
                applyOptions.getFontOptions().getFont(defaultFont));

        BinaryEditorOptions editorOptions = applyOptions.getEditorOptions();
        //        switchShowValuesPanel(editorOptions.isShowValuesPanel());
        if (codeArea.getCommandHandler() instanceof CodeAreaOperationCommandHandler) {
            ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).setEnterKeyHandlingMode(editorOptions.getEnterKeyHandlingMode());
        }

        StatusOptions statusOptions = applyOptions.getStatusOptions();
        statusPanel.setStatusOptions(statusOptions);
        toolbarPanel.applyFromCodeArea();

        CodeAreaLayoutOptions layoutOptions = applyOptions.getLayoutOptions();
        int selectedLayoutProfile = layoutOptions.getSelectedProfile();
        if (selectedLayoutProfile >= 0) {
            codeArea.setLayoutProfile(layoutOptions.getLayoutProfile(selectedLayoutProfile));
        } else {
            codeArea.setLayoutProfile(defaultLayoutProfile);
        }

        CodeAreaThemeOptions themeOptions = applyOptions.getThemeOptions();
        int selectedThemeProfile = themeOptions.getSelectedProfile();
        if (selectedThemeProfile >= 0) {
            codeArea.setThemeProfile(themeOptions.getThemeProfile(selectedThemeProfile));
        } else {
            codeArea.setThemeProfile(defaultThemeProfile);
        }

        CodeAreaColorOptions colorOptions = applyOptions.getColorOptions();
        int selectedColorProfile = colorOptions.getSelectedProfile();
        if (selectedColorProfile >= 0) {
            codeArea.setColorsProfile(colorOptions.getColorsProfile(selectedColorProfile));
        } else {
            codeArea.setColorsProfile(defaultColorProfile);
        }
    }

    @Nonnull
    private AbstractAction createOnlineHelpAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LanguageModuleApi languageModuleApi = App.getModule(LanguageModuleApi.class);
                DesktopUtils.openDesktopURL(languageModuleApi.getAppBundle().getString("online_help_url"));
            }
        };
    }
    
    public void setLeftContentData(BinaryData contentData) {
        diffPanel.setLeftContentData(contentData);
    }

    public void setRightContentData(BinaryData contentData) {
        diffPanel.setRightContentData(contentData);
    }

    @Nonnull
    private JPopupMenu createPopupMenu(final CodeAreaPopupMenuHandler codeAreaPopupMenuHandler, String popupMenuId) {
        return new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                if (codeAreaPopupMenuHandler == null || invoker == null) {
                    return;
                }

                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += ((JViewport) invoker).getParent().getX();
                    clickedY += ((JViewport) invoker).getParent().getY();
                }

                SectCodeArea codeArea = invoker instanceof SectCodeArea ? (SectCodeArea) invoker
                        : (SectCodeArea) ((JViewport) invoker).getParent().getParent();

                JPopupMenu popupMenu = codeAreaPopupMenuHandler.createPopupMenu(codeArea, popupMenuId, clickedX, clickedY);
                popupMenu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }

                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        codeAreaPopupMenuHandler.dropPopupMenu(popupMenuId);
                    }

                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                popupMenu.show(invoker, x, y);
            }
        };
    }
    
    @ParametersAreNonnullByDefault
    private class BinaryStatusController implements BinaryStatusPanel.Controller, BinaryStatusPanel.EncodingsController, BinaryStatusPanel.MemoryModeController {
        @Override
        public void changeEditOperation(EditOperation editOperation) {
            SectCodeArea leftCodeArea = diffPanel.getLeftCodeArea();
            SectCodeArea rightCodeArea = diffPanel.getRightCodeArea();
            leftCodeArea.setEditOperation(editOperation);
            rightCodeArea.setEditOperation(editOperation);
        }

        @Override
        public void changeCursorPosition() {
            goToPositionAction.actionPerformed(new ActionEvent(BinEdDiffPanel.this, 0, ""));
        }

        @Override
        public void cycleNextEncoding() {
            if (encodingsHandler != null) {
                encodingsHandler.cycleNextEncoding();
            }
        }

        @Override
        public void cyclePreviousEncoding() {
            if (encodingsHandler != null) {
                encodingsHandler.cyclePreviousEncoding();
            }
        }

        @Override
        public void encodingsPopupEncodingsMenu(MouseEvent mouseEvent) {
            if (encodingsHandler != null) {
                encodingsHandler.popupEncodingsMenu(mouseEvent);
            }
        }

        @Override
        public void changeMemoryMode(BinaryStatusApi.MemoryMode memoryMode) {
            // Ignore
        }
    }
}
