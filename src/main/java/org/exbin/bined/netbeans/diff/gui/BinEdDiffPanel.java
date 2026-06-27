/*
 * Copyright (C) ExBin Project, https://exbin.org
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import org.jspecify.annotations.NullMarked;
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
import org.exbin.jaguif.App;
import org.exbin.bined.jaguif.BinEdCodeAreaAssessor;
import org.exbin.bined.jaguif.BinaryStatusApi;
import org.exbin.bined.jaguif.component.BinedComponentModule;
import org.exbin.bined.jaguif.component.action.GoToPositionAction;
import org.exbin.bined.jaguif.editor.options.BinaryEditorOptions;
import org.exbin.bined.jaguif.gui.BinaryStatusPanel;
import org.exbin.bined.jaguif.handler.CodeAreaPopupMenuHandler;
import org.exbin.bined.jaguif.options.StatusOptions;
import org.exbin.bined.jaguif.theme.options.CodeAreaColorOptions;
import org.exbin.bined.jaguif.theme.options.CodeAreaLayoutOptions;
import org.exbin.bined.jaguif.theme.options.CodeAreaThemeOptions;
import org.exbin.bined.jaguif.viewer.options.CodeAreaOptions;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.options.action.OptionsAction;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.options.api.OptionsStorage;
import org.exbin.jaguif.text.encoding.EncodingsHandler;
import org.exbin.jaguif.text.encoding.TextEncodingStatusApi;
import org.exbin.jaguif.text.encoding.settings.TextEncodingOptions;
import org.exbin.jaguif.text.encoding.EncodingsManager;
import org.exbin.jaguif.text.font.options.TextFontOptions;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarModuleApi;
import org.exbin.jaguif.utils.DesktopUtils;

/**
 * BinEd diff panel to compare binary files.
 */
@NullMarked
public class BinEdDiffPanel extends JPanel {

    private final SectCodeAreaDiffPanel diffPanel = new SectCodeAreaDiffPanel();

    private final Font defaultFont;
    private final SectionCodeAreaLayoutProfile defaultLayoutProfile;
    private final SectionCodeAreaThemeProfile defaultThemeProfile;
    private final CodeAreaColorsProfile defaultColorProfile;

    private final BinEdToolbarPanel toolbarPanel;
    private final StatusBar leftStatusBar;
    private final StatusBar rightStatusBar;
    private EncodingsManager encodingsHandler;
    private GoToPositionAction goToPositionAction = new GoToPositionAction();

    public BinEdDiffPanel() {
        setLayout(new java.awt.BorderLayout());

        defaultFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        SectCodeArea leftCodeArea = diffPanel.getLeftCodeArea();
        SectCodeArea rightCodeArea = diffPanel.getRightCodeArea();

        CodeAreaPainter leftPainter = leftCodeArea.getPainter();
        BinEdCodeAreaAssessor codeAreaAssessor = new BinEdCodeAreaAssessor(((ColorAssessorPainterCapable) leftPainter).getColorAssessor(), ((CharAssessorPainterCapable) leftPainter).getCharAssessor());
        ((ColorAssessorPainterCapable) leftPainter).setColorAssessor(codeAreaAssessor);
        ((CharAssessorPainterCapable) leftPainter).setCharAssessor(codeAreaAssessor);
        CodeAreaPainter rightPainter = rightCodeArea.getPainter();
        codeAreaAssessor = new BinEdCodeAreaAssessor(((ColorAssessorPainterCapable) rightPainter).getColorAssessor(), ((CharAssessorPainterCapable) rightPainter).getCharAssessor());
        ((ColorAssessorPainterCapable) rightPainter).setColorAssessor(codeAreaAssessor);
        ((CharAssessorPainterCapable) rightPainter).setCharAssessor(codeAreaAssessor);

        defaultLayoutProfile = leftCodeArea.getLayoutProfile();
        defaultThemeProfile = leftCodeArea.getThemeProfile();
        defaultColorProfile = leftCodeArea.getColorsProfile();
        toolbarPanel = new BinEdToolbarPanel();
        leftStatusBar = new BinaryStatusPanel();
        leftStatusBar.setMinimumSize(new Dimension(0, getMinimumSize().height));
        rightStatusBar = new BinaryStatusPanel();
        rightStatusBar.setMinimumSize(new Dimension(0, getMinimumSize().height));
        toolbarPanel.setTargetComponent(diffPanel);
        toolbarPanel.setCodeAreaControl(new BinEdToolbarPanel.Control() {
            @Override
            public CodeType getCodeType() {
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
        AbstractAction wrapperAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionsAction.actionPerformed(e);
                // TODO Options are not applied due to no active file handler is present
                toolbarPanel.applyFromCodeArea();
                leftStatusBar.updateStatus();
                rightStatusBar.updateStatus();
            }
        };
        toolbarPanel.setOptionsAction(wrapperAction);
        toolbarPanel.setOnlineHelpAction(createOnlineHelpAction());

        init();
    }

    private void init() {
        this.add(toolbarPanel, BorderLayout.NORTH);
        encodingsHandler = new EncodingsHandler();
        encodingsHandler.init();
        encodingsHandler.setTextEncodingStatus(new TextEncodingStatusApi() {
            @Override
            public String getEncoding() {
                return leftStatusBar.getEncoding();
            }

            @Override
            public void setEncoding(String encodingName) {
                diffPanel.getLeftCodeArea().setCharset(Charset.forName(encodingName));
                diffPanel.getRightCodeArea().setCharset(Charset.forName(encodingName));
                leftStatusBar.setEncoding(encodingName);
                rightStatusBar.setEncoding(encodingName);
                new TextEncodingOptions(preferences).setSelectedEncoding(encodingName);
            }
        });
        goToPositionAction.setup(App.getModule(LanguageModuleApi.class).getBundle(BinedModule.class));

        registerBinaryStatus(leftStatusBar, diffPanel.getLeftCodeArea());
        registerBinaryStatus(rightStatusBar, diffPanel.getRightCodeArea());

        initialLoadFromPreferences();
        BinedComponentModule binedComponentModule = App.getModule(BinedComponentModule.class);
        CodeAreaPopupMenuHandler codeAreaPopupMenuHandler = binedComponentModule.createCodeAreaPopupMenuHandler(BinedComponentModule.PopupMenuVariant.BASIC);
        diffPanel.getLeftCodeArea().setComponentPopupMenu(createPopupMenu(codeAreaPopupMenuHandler, "compareLeft"));
        diffPanel.getRightCodeArea().setComponentPopupMenu(createPopupMenu(codeAreaPopupMenuHandler, "compareRight"));

        diffPanel.getLeftPanel().add(leftStatusBar, BorderLayout.SOUTH);
        diffPanel.getRightPanel().add(rightStatusBar, BorderLayout.SOUTH);
        this.add(diffPanel, BorderLayout.CENTER);
        diffPanel.revalidate();
        diffPanel.repaint();
        revalidate();
        repaint();
    }

    public void registerBinaryStatus(BinaryStatusApi binaryStatus, SectCodeArea codeArea) {
        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            binaryStatus.setCursorPosition(caretPosition);
        });
        codeArea.addSelectionChangedListener(() -> {
            binaryStatus.setSelectionRange(codeArea.getSelection());
        });

        codeArea.addEditModeChangedListener(binaryStatus::setEditMode);

        codeArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateBinaryStatus(binaryStatus, codeArea);
            }
        });

        updateBinaryStatus(binaryStatus, codeArea);

        ((BinaryStatusPanel) binaryStatus).setController(new BinaryStatusController());
    }

    private void updateBinaryStatus(BinaryStatusApi binaryStatus, SectCodeArea codeArea) {
        binaryStatus.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
        binaryStatus.setCursorPosition(codeArea.getActiveCaretPosition());
        binaryStatus.setSelectionRange(codeArea.getSelection());
        long dataSize = codeArea.getDataSize();
        binaryStatus.setCurrentDocumentSize(dataSize, dataSize);
        goToPositionAction.setCodeArea(codeArea);
    }

    private void initialLoadFromPreferences() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsStorage preferences = optionsModule.getAppOptions();

        applyOptions(preferences, diffPanel.getLeftCodeArea());
        applyOptions(preferences, diffPanel.getRightCodeArea());

//        encodingsHandler.loadFromOptions(new TextEncodingOptions(preferences));
//        leftStatusBar.loadFromOptions(new StatusOptions(preferences));
//        rightStatusBar.loadFromOptions(new StatusOptions(preferences));
        toolbarPanel.applyFromCodeArea();
        toolbarPanel.loadFromOptions(preferences);

//        BinaryStatusApi.MemoryMode memoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
//        leftStatusBar.setMemoryMode(memoryMode);
//        rightStatusBar.setMemoryMode(memoryMode);
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
        ((FontCapable) codeArea).setCodeFont(
                applyOptions.getFontOptions().isUseDefaultFont() ? defaultFont : applyOptions.getFontOptions().getFont(defaultFont)
        );

        BinaryEditorOptions editorOptions = applyOptions.getEditorOptions();
        //        switchShowValuesPanel(editorOptions.isShowValuesPanel());
        if (codeArea.getCommandHandler() instanceof CodeAreaOperationCommandHandler) {
            ((CodeAreaOperationCommandHandler) codeArea.getCommandHandler()).setEnterKeyHandlingMode(editorOptions.getEnterKeyHandlingMode());
        }

        StatusOptions statusOptions = applyOptions.getStatusOptions();
        leftStatusBar.setStatusOptions(statusOptions);
        rightStatusBar.setStatusOptions(statusOptions);
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
        updateBinaryStatus(leftStatusBar, diffPanel.getLeftCodeArea());
    }

    public void setRightContentData(BinaryData contentData) {
        diffPanel.setRightContentData(contentData);
        updateBinaryStatus(rightStatusBar, diffPanel.getRightCodeArea());
    }

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
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                }

                SectCodeArea codeArea = invoker instanceof SectCodeArea ? (SectCodeArea) invoker
                        : (SectCodeArea) invoker.getParent().getParent();

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
}
