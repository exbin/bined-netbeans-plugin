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
package org.exbin.bined.netbeans.main;

import org.exbin.bined.PositionCodeType;
import org.exbin.bined.basic.BasicCodeAreaZone;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.bined.netbeans.action.OptionsAction;
import org.exbin.bined.netbeans.gui.BinEdToolbarPanel;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.about.gui.AboutPanel;
import org.exbin.framework.bined.FileHandlingMode;
import org.exbin.framework.bined.gui.BinEdComponentFileApi;
import org.exbin.framework.bined.preferences.BinaryEditorPreferences;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.CloseControlPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JTextField;
import org.exbin.auxiliary.binary_data.delta.DeltaDocument;
import org.exbin.bined.CodeAreaCaretPosition;
import org.exbin.bined.EditMode;
import org.exbin.bined.EditOperation;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.capability.EditModeCapable;
import org.exbin.bined.netbeans.options.IntegrationOptions;
import org.exbin.framework.bined.BinEdEditorComponent;
import org.exbin.framework.bined.BinEdFileHandler;
import org.exbin.framework.bined.BinEdFileManager;
import org.exbin.framework.bined.BinaryStatusApi;
import org.exbin.framework.bined.CodeAreaCommandHandlerProvider;
import org.exbin.framework.bined.action.EditSelectionAction;
import org.exbin.framework.bined.action.GoToPositionAction;
import org.exbin.framework.bined.bookmarks.BookmarksManager;
import org.exbin.framework.bined.bookmarks.action.ManageBookmarksAction;
import org.exbin.framework.bined.compare.action.CompareFilesAction;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.bined.gui.BinaryStatusPanel;
import org.exbin.framework.bined.inspector.BasicValuesPositionColorModifier;
import org.exbin.framework.bined.inspector.BinEdComponentInspector;
import org.exbin.framework.bined.inspector.options.DataInspectorOptions;
import org.exbin.framework.bined.macro.MacroManager;
import org.exbin.framework.bined.macro.action.ManageMacrosAction;
import org.exbin.framework.bined.macro.operation.CodeAreaMacroCommandHandler;
import org.exbin.framework.bined.operation.action.ConvertDataAction;
import org.exbin.framework.bined.operation.action.InsertDataAction;
import org.exbin.framework.bined.operation.api.ConvertDataMethod;
import org.exbin.framework.bined.operation.api.InsertDataMethod;
import org.exbin.framework.bined.operation.bouncycastle.component.ComputeHashDataMethod;
import org.exbin.framework.bined.operation.component.BitSwappingDataMethod;
import org.exbin.framework.bined.operation.component.RandomDataMethod;
import org.exbin.framework.bined.operation.component.SimpleFillDataMethod;
import org.exbin.framework.bined.options.CodeAreaColorOptions;
import org.exbin.framework.bined.options.CodeAreaLayoutOptions;
import org.exbin.framework.bined.options.CodeAreaOptions;
import org.exbin.framework.bined.options.CodeAreaThemeOptions;
import org.exbin.framework.bined.options.EditorOptions;
import org.exbin.framework.bined.options.StatusOptions;
import org.exbin.framework.bined.search.BinEdComponentSearch;
import org.exbin.framework.bined.search.action.FindReplaceActions;
import org.exbin.framework.bined.tool.content.action.ClipboardContentAction;
import org.exbin.framework.bined.tool.content.action.DragDropContentAction;
import org.exbin.framework.editor.api.EditorProvider;
import org.exbin.framework.editor.text.EncodingsHandler;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.TextFontApi;
import org.exbin.framework.editor.text.options.TextEncodingOptions;
import org.exbin.framework.editor.text.options.TextFontOptions;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.file.api.FileType;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Manager for binary editor.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdManager {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(BinEdManager.class);

    private static BinEdManager instance;

    private static final String FRAMEWORK_TANGO_ICON_THEME_PREFIX = "/org/exbin/framework/action/resources/icons/tango-icon-theme/16x16/actions/";
    private static final FileHandlingMode DEFAULT_FILE_HANDLING_MODE = FileHandlingMode.DELTA;
    private static final String ONLINE_HELP_URL = "https://bined.exbin.org/intellij-plugin/?manual";

    private final Application application = new Application();
    private final BinaryEditorPreferences preferences;
    private BinEdFileManager fileManager = new BinEdFileManager();

    private FindReplaceActions findReplaceActions;
    private BookmarksManager bookmarksManager;
    private MacroManager macroManager;
    private EncodingsHandler encodingsHandler = new EncodingsHandler();

    private final List<InsertDataMethod> insertDataComponents = new ArrayList<>();
    private final List<ConvertDataMethod> convertDataComponents = new ArrayList<>();
    private BasicValuesPositionColorModifier basicValuesColorModifier = new BasicValuesPositionColorModifier();

    private BinEdManager() {
        preferences = new BinaryEditorPreferences(application.getAppPreferences());
        fileManager.setApplication(application);
        bookmarksManager = new BookmarksManager();
        bookmarksManager.setApplication(application);
        ((ManageBookmarksAction) bookmarksManager.getManageBookmarksAction()).setBookmarksManager(bookmarksManager);
        macroManager = new MacroManager();
        macroManager.setApplication(application);
        ((ManageMacrosAction) macroManager.getManageMacrosAction()).setMacroManager(macroManager);
        findReplaceActions = new FindReplaceActions();
        findReplaceActions.setup(application, null, resourceBundle);
        encodingsHandler.setApplication(application);

        SimpleFillDataMethod simpleFillDataMethod = new SimpleFillDataMethod();
        simpleFillDataMethod.setApplication(this.application);
        addInsertDataComponent(simpleFillDataMethod);
        RandomDataMethod randomDataMethod = new RandomDataMethod();
        randomDataMethod.setApplication(this.application);
        addInsertDataComponent(randomDataMethod);
        BitSwappingDataMethod bitSwappingDataMethod = new BitSwappingDataMethod();
        bitSwappingDataMethod.setApplication(this.application);
        addConvertDataComponent(bitSwappingDataMethod);
        ComputeHashDataMethod computeHashDataMethod = new ComputeHashDataMethod();
        computeHashDataMethod.setApplication(this.application);
        addConvertDataComponent(computeHashDataMethod);

        fileManager.addPainterColorModifier(basicValuesColorModifier);
        fileManager.addBinEdComponentExtension(new BinEdFileManager.BinEdFileExtension() {
            @Nonnull
            @Override
            public Optional<BinEdComponentPanel.BinEdComponentExtension> createComponentExtension(BinEdComponentPanel component) {
                BinEdComponentInspector binEdComponentInspector = new BinEdComponentInspector();
                binEdComponentInspector.setBasicValuesColorModifier(basicValuesColorModifier);
                return Optional.of(binEdComponentInspector);
            }
        });

        fileManager.addActionStatusUpdateListener((codeArea) -> {
            findReplaceActions.updateForActiveFile();
        });
        fileManager.addBinEdComponentExtension((BinEdComponentPanel component) -> Optional.of(new BinEdComponentSearch()));

        fileManager.setCommandHandlerProvider((codeArea, undoHandler) -> new CodeAreaMacroCommandHandler(codeArea, undoHandler));
        findReplaceActions.addFindAgainListener();
    }

    @Nonnull
    public static synchronized BinEdManager getInstance() {
        if (instance == null) {
            instance = new BinEdManager();
            instance.init();
        }

        return instance;
    }

    private void init() {
        bookmarksManager.init();
        macroManager.init();
        encodingsHandler.init();
    }

    @Nonnull
    public void initFileHandler(FileHandler fileHandler) {
        BinEdEditorComponent editorComponent = ((BinEdComponentFileApi) fileHandler).getEditorComponent();
        initEditorComponent(editorComponent, fileHandler);
    }

    public void initEditorComponent(BinEdEditorComponent editorComponent) {
        initEditorComponent(editorComponent, null);
    }

    public void initEditorComponent(BinEdEditorComponent editorComponent, @Nullable FileHandler fileHandler) {
        BinEdComponentPanel componentPanel = editorComponent.getComponentPanel();
        ExtCodeArea codeArea = componentPanel.getCodeArea();

        codeArea.setComponentPopupMenu(new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                int clickedX = x;
                int clickedY = y;
                if (invoker instanceof JViewport) {
                    clickedX += invoker.getParent().getX();
                    clickedY += invoker.getParent().getY();
                }
                removeAll();
                createContextMenu(editorComponent, fileHandler, this, PopupMenuVariant.EDITOR, clickedX, clickedY);
                super.show(invoker, x, y);
            }
        });

        codeArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getModifiersEx() == ActionUtils.getMetaMask()) {
                    int keyCode = keyEvent.getKeyCode();
                    switch (keyCode) {
                    case KeyEvent.VK_F: {
                        findReplaceActions.getEditFindAction().actionPerformed(new ActionEvent(keyEvent.getSource(), keyEvent.getID(), ""));
                        break;
                    }
                    case KeyEvent.VK_G: {
                        if (codeArea.isEditable()) {
                            GoToPositionAction goToPositionAction = new GoToPositionAction();
                            goToPositionAction.setup(application, resourceBundle);
                            goToPositionAction.updateForActiveCodeArea(codeArea);
                            goToPositionAction.actionPerformed(new ActionEvent(keyEvent.getSource(), keyEvent.getID(), ""));
                        }
                        break;
                    }
                    case KeyEvent.VK_I: {
                        if (codeArea.isEditable()) {
                            InsertDataAction insertDataAction = new InsertDataAction();
                            insertDataAction.setup(application, resourceBundle);
                            insertDataAction.updateForActiveCodeArea(codeArea);
                            insertDataAction.setInsertDataComponents(insertDataComponents);
                            insertDataAction.actionPerformed(new ActionEvent(keyEvent.getSource(),
                                    keyEvent.getID(),
                                    ""));
                        }
                        break;
                    }
                    case KeyEvent.VK_M: {
                        if (codeArea.isEditable()) {
                            ConvertDataAction convertDataAction = new ConvertDataAction();
                            convertDataAction.setup(application, resourceBundle);
                            convertDataAction.updateForActiveCodeArea(codeArea);
                            convertDataAction.setConvertDataComponents(convertDataComponents);
                            convertDataAction.actionPerformed(new ActionEvent(keyEvent.getSource(),
                                    keyEvent.getID(),
                                    ""));
                        }
                        break;
                    }
                    }
                }
            }
        });

        BinaryStatusPanel statusPanel = editorComponent.getStatusPanel();
        statusPanel.setStatusControlHandler(new BinaryStatusPanel.StatusControlHandler() {
            @Override
            public void changeEditOperation(EditOperation editOperation) {
                editorComponent.getCodeArea().setEditOperation(editOperation);
            }

            @Override
            public void changeCursorPosition() {
                createGoToAction(codeArea).actionPerformed(null);
            }

            @Override
            public void cycleEncodings() {
                if (encodingsHandler != null) {
                    encodingsHandler.cycleEncodings();
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
                if (fileHandler instanceof BinEdFileHandler) {
                    BinEdFileHandler binEdFileHandler = (BinEdFileHandler) fileHandler;
                    FileHandlingMode newHandlingMode = memoryMode == BinaryStatusApi.MemoryMode.DELTA_MODE ?
                            FileHandlingMode.DELTA :
                            FileHandlingMode.MEMORY;
                    FileHandlingMode fileHandlingMode = binEdFileHandler.getFileHandlingMode();
                    if (newHandlingMode != fileHandlingMode) {
                        binEdFileHandler.switchFileHandlingMode(newHandlingMode);
                        preferences.getEditorPreferences().setFileHandlingMode(newHandlingMode);
                        updateCurrentMemoryMode(editorComponent);
                    }
                }
            }
        });
        registerBinaryStatus(editorComponent, (BinEdComponentFileApi) fileHandler);
        registerEncodingStatus(statusPanel, editorComponent);
        bookmarksManager.registerBookmarksComponentActions(editorComponent.getComponentPanel());

        BinEdToolbarPanel toolbarPanel = editorComponent.getToolbarPanel();
        toolbarPanel.setOptionsAction(createOptionsAction(editorComponent, fileHandler));
        toolbarPanel.setOnlineHelpAction(createOnlineHelpAction());

        editorComponent.applyOptions(new BinEdApplyOptions() {
            @Nonnull
            @Override
            public IntegrationOptions getIntegrationOptions() {
                return preferences.getIntegrationPreferences();
            }

            @Nonnull
            @Override
            public CodeAreaOptions getCodeAreaOptions() {
                return preferences.getCodeAreaPreferences();
            }

            @Nonnull
            @Override
            public TextEncodingOptions getEncodingOptions() {
                return preferences.getEncodingPreferences();
            }

            @Nonnull
            @Override
            public TextFontOptions getFontOptions() {
                return preferences.getFontPreferences();
            }

            @Nonnull
            @Override
            public EditorOptions getEditorOptions() {
                return preferences.getEditorPreferences();
            }

            @Nonnull
            @Override
            public StatusOptions getStatusOptions() {
                return preferences.getStatusPreferences();
            }

            @Nonnull
            @Override
            public DataInspectorOptions getDataInspectorOptions() {
                return preferences.getDataInspectorPreferences();
            }

            @Nonnull
            @Override
            public CodeAreaLayoutOptions getLayoutOptions() {
                return preferences.getLayoutPreferences();
            }

            @Nonnull
            @Override
            public CodeAreaColorOptions getColorOptions() {
                return preferences.getColorPreferences();
            }

            @Nonnull
            @Override
            public CodeAreaThemeOptions getThemeOptions() {
                return preferences.getThemePreferences();
            }
        }, encodingsHandler, fileHandler == null ? new JTextField().getFont() : ((TextFontApi) fileHandler).getDefaultFont());
    }

    @Nonnull
    public BinEdFileManager getFileManager() {
        return fileManager;
    }

    @Nonnull
    public Application getApplication() {
        return application;
    }

    @Nonnull
    public EncodingsHandler getEncodingsHandler() {
        return encodingsHandler;
    }

    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi, BinEdEditorComponent editorComponent) {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        encodingsHandler.setSelectedEncoding(codeArea.getCharset().name());
        encodingsHandler.setParentComponent(editorComponent.getComponent());
        encodingsHandler.setTextEncodingStatus(new TextEncodingStatusApi() {
            @Nonnull
            @Override
            public String getEncoding() {
                return codeArea.getCharset().name();
            }

            @Override
            public void setEncoding(String encodingName) {
                codeArea.setCharset(Charset.forName(encodingName));
                encodingStatusApi.setEncoding(encodingName);
                // TODO preferences.getEncodingPreferences().setSelectedEncoding(encodingName);
                //charsetChangeListener.charsetChanged();
            }
        });
    }

    public void createContextMenu(BinEdEditorComponent editorComponent, @Nullable FileHandler fileHandler, final JPopupMenu menu, PopupMenuVariant variant, int x, int y) {
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        BasicCodeAreaZone positionZone = codeArea.getPainter().getPositionZone(x, y);
        EditorProvider editorProvider = new EditorProviderImpl(fileHandler);
        findReplaceActions.setup(application, editorProvider, resourceBundle);
        findReplaceActions.updateForActiveFile();
        bookmarksManager.setEditorProvider(editorProvider);
        macroManager.setEditorProvider(editorProvider);

        if (variant == PopupMenuVariant.EDITOR) {
            switch (positionZone) {
                case TOP_LEFT_CORNER:
                case HEADER:
                case ROW_POSITIONS: {
                    break;
                }
                default: {
                    JMenu showMenu = new JMenu("Show");
                    showMenu.add(createShowHeaderMenuItem(codeArea));
                    showMenu.add(createShowRowPositionMenuItem(codeArea));
                    showMenu.add(createShowInspectorPanel(editorComponent.getComponentPanel()));
                    menu.add(showMenu);
                    menu.addSeparator();
                }
            }
        }

        switch (positionZone) {
            case TOP_LEFT_CORNER:
            case HEADER: {
                if (variant != PopupMenuVariant.BASIC) {
                    menu.add(createShowHeaderMenuItem(codeArea));
                    menu.add(createPositionCodeTypeMenuItem(codeArea));
                }
                break;
            }
            case ROW_POSITIONS: {
                if (variant != PopupMenuVariant.BASIC) {
                    menu.add(createShowRowPositionMenuItem(codeArea));
                    menu.add(createPositionCodeTypeMenuItem(codeArea));
                    menu.add(new JSeparator());
                    menu.add(ActionUtils.actionToMenuItem(createGoToAction(codeArea)));
                }

                break;
            }
            default: {
                final JMenuItem cutMenuItem = new JMenuItem("Cut");
                ImageIcon cutMenuItemIcon = new ImageIcon(getClass().getResource(FRAMEWORK_TANGO_ICON_THEME_PREFIX + "edit-cut.png"));
                cutMenuItem.setIcon(cutMenuItemIcon);
                cutMenuItem.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(cutMenuItemIcon.getImage())));
                cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionUtils.getMetaMask()));
                cutMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
                cutMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.cut();
                    menu.setVisible(false);
                });
                menu.add(cutMenuItem);

                final JMenuItem copyMenuItem = new JMenuItem("Copy");
                ImageIcon copyMenuItemIcon = new ImageIcon(getClass().getResource(FRAMEWORK_TANGO_ICON_THEME_PREFIX + "edit-copy.png"));
                copyMenuItem.setIcon(copyMenuItemIcon);
                copyMenuItem.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(copyMenuItemIcon.getImage())));
                copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionUtils.getMetaMask()));
                copyMenuItem.setEnabled(codeArea.hasSelection());
                copyMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.copy();
                    menu.setVisible(false);
                });
                menu.add(copyMenuItem);

                final JMenuItem copyAsCodeMenuItem = new JMenuItem("Copy as Code");
                copyAsCodeMenuItem.setEnabled(codeArea.hasSelection());
                copyAsCodeMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.copyAsCode();
                    menu.setVisible(false);
                });
                menu.add(copyAsCodeMenuItem);

                final JMenuItem pasteMenuItem = new JMenuItem("Paste");
                ImageIcon pasteMenuItemIcon = new ImageIcon(getClass().getResource(FRAMEWORK_TANGO_ICON_THEME_PREFIX + "edit-paste.png"));
                pasteMenuItem.setIcon(pasteMenuItemIcon);
                pasteMenuItem.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(pasteMenuItemIcon.getImage())));
                pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionUtils.getMetaMask()));
                pasteMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
                pasteMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.paste();
                    menu.setVisible(false);
                });
                menu.add(pasteMenuItem);

                final JMenuItem pasteFromCodeMenuItem = new JMenuItem("Paste from Code");
                pasteFromCodeMenuItem.setEnabled(codeArea.canPaste() && codeArea.isEditable());
                pasteFromCodeMenuItem.addActionListener((ActionEvent e) -> {
                    try {
                        codeArea.pasteFromCode();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(codeArea, ex.getMessage(), "Unable to Paste Code", JOptionPane.ERROR_MESSAGE);
                    }
                    menu.setVisible(false);
                });
                menu.add(pasteFromCodeMenuItem);

                final JMenuItem deleteMenuItem = new JMenuItem("Delete");
                ImageIcon deleteMenuItemIcon = new ImageIcon(getClass().getResource(FRAMEWORK_TANGO_ICON_THEME_PREFIX + "edit-delete.png"));
                deleteMenuItem.setIcon(deleteMenuItemIcon);
                deleteMenuItem.setDisabledIcon(new ImageIcon(GrayFilter.createDisabledImage(deleteMenuItemIcon.getImage())));
                deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                deleteMenuItem.setEnabled(codeArea.hasSelection() && codeArea.isEditable());
                deleteMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.delete();
                    menu.setVisible(false);
                });
                menu.add(deleteMenuItem);

                final JMenuItem selectAllMenuItem = new JMenuItem("Select All");
                selectAllMenuItem.setIcon(new ImageIcon(getClass().getResource(FRAMEWORK_TANGO_ICON_THEME_PREFIX + "edit-select-all.png")));
                selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionUtils.getMetaMask()));
                selectAllMenuItem.addActionListener((ActionEvent e) -> {
                    codeArea.selectAll();
                    menu.setVisible(false);
                });
                menu.add(selectAllMenuItem);

                menu.add(ActionUtils.actionToMenuItem(createEditSelectionAction(codeArea)));

                menu.add(ActionUtils.actionToMenuItem(createInsertDataAction(editorComponent)));
                menu.add(ActionUtils.actionToMenuItem(createConvertDataAction(editorComponent)));

                menu.addSeparator();

                menu.add(ActionUtils.actionToMenuItem(createGoToAction(codeArea)));

                menu.add(ActionUtils.actionToMenuItem(findReplaceActions.getEditFindAction()));
                menu.add(ActionUtils.actionToMenuItem(findReplaceActions.getEditReplaceAction()));

                JMenu bookmarksMenu = bookmarksManager.getBookmarksMenu();
                bookmarksManager.updateBookmarksMenu();
                menu.add(bookmarksMenu);

                JMenu macrosMenu = macroManager.getMacrosMenu();
                macroManager.updateMacrosMenu();
                macroManager.notifyMacroRecordingChange(codeArea);
                menu.add(macrosMenu);
            }
        }

        menu.addSeparator();

        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(ActionUtils.actionToMenuItem(createCompareFilesAction(codeArea, editorProvider)));
        toolsMenu.add(ActionUtils.actionToMenuItem(createClipboardContentAction(editorProvider)));
        toolsMenu.add(ActionUtils.actionToMenuItem(createDragDropContentAction(editorProvider)));
        menu.add(toolsMenu);

        if (editorComponent != null) {
            if (fileHandler instanceof BinEdFileHandler || fileHandler instanceof BinEdFileDataWrapper) {
                JMenuItem reloadFileMenuItem = createReloadFileMenuItem(fileHandler, editorProvider);
                menu.add(reloadFileMenuItem);
            }
        }

        if (editorComponent != null) {
            final JMenuItem optionsMenuItem = new JMenuItem("Options...");
            optionsMenuItem.setIcon(new ImageIcon(getClass().getResource(
                    "/org/exbin/framework/options/gui/resources/icons/Preferences16.gif")));
            optionsMenuItem.addActionListener(createOptionsAction(editorComponent, fileHandler));
            menu.add(optionsMenuItem);
        }

        switch (positionZone) {
            case TOP_LEFT_CORNER:
            case HEADER:
            case ROW_POSITIONS: {
                break;
            }
            default: {
                menu.addSeparator();

                final JMenuItem onlineHelpMenuItem = new JMenuItem("Online Help...");
                onlineHelpMenuItem.setIcon(new ImageIcon(getClass().getResource("/org/exbin/framework/bined/resources/icons/open_icon_library/icons/png/16x16/actions/help.png")));
                onlineHelpMenuItem.addActionListener(createOnlineHelpAction());
                menu.add(onlineHelpMenuItem);

                final JMenuItem aboutMenuItem = new JMenuItem("About...");
                aboutMenuItem.addActionListener((ActionEvent e) -> {
                    AboutPanel aboutPanel = new AboutPanel();
                    aboutPanel.setupFields();
                    CloseControlPanel closeControlPanel = new CloseControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(aboutPanel, closeControlPanel);
                    WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, (Component) e.getSource(), "About Plugin", Dialog.ModalityType.APPLICATION_MODAL);
                    closeControlPanel.setHandler(() -> {
                        dialog.close();
                    });
                    //            dialog.setSize(650, 460);
                    dialog.showCentered((Component) e.getSource());
                });
                menu.add(aboutMenuItem);
            }
        }
    }

    @Nonnull
    private OptionsAction createOptionsAction(BinEdEditorComponent editorComponent, FileHandler fileHandler) {
        return new OptionsAction(editorComponent.getComponentPanel(), fileHandler, preferences);
    }

    @Nonnull
    private AbstractAction createOnlineHelpAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DesktopUtils.openDesktopURL(ONLINE_HELP_URL);
            }
        };
    }

    @Nonnull
    private GoToPositionAction createGoToAction(ExtCodeArea codeArea) {
        GoToPositionAction goToPositionAction = new GoToPositionAction();
        goToPositionAction.setup(application, resourceBundle);
        goToPositionAction.updateForActiveCodeArea(codeArea);
        return goToPositionAction;
    }

    @Nonnull
    private EditSelectionAction createEditSelectionAction(ExtCodeArea codeArea) {
        EditSelectionAction editSelectionAction = new EditSelectionAction();
        editSelectionAction.setup(application, resourceBundle);
        editSelectionAction.updateForActiveCodeArea(codeArea);
        return editSelectionAction;
    }

    @Nonnull
    private InsertDataAction createInsertDataAction(BinEdEditorComponent editorComponent) {
        InsertDataAction insertDataAction = new InsertDataAction();
        insertDataAction.setup(application, resourceBundle);
        insertDataAction.updateForActiveCodeArea(editorComponent.getCodeArea());
        insertDataAction.setInsertDataComponents(insertDataComponents);
        return insertDataAction;
    }

    @Nonnull
    private ConvertDataAction createConvertDataAction(BinEdEditorComponent editorComponent) {
        ConvertDataAction convertDataAction = new ConvertDataAction();
        convertDataAction.setup(application, resourceBundle);
        convertDataAction.updateForActiveCodeArea(editorComponent.getCodeArea());
        convertDataAction.setConvertDataComponents(convertDataComponents);
        return convertDataAction;
    }

    @Nonnull
    private CompareFilesAction createCompareFilesAction(ExtCodeArea codeArea, EditorProvider editorProvider) {
        CompareFilesAction compareFilesAction = new CompareFilesAction();
        compareFilesAction.setup(application, editorProvider, resourceBundle);
        compareFilesAction.setCodeArea(codeArea);
        return compareFilesAction;
    }

    @Nonnull
    private JMenuItem createReloadFileMenuItem(FileHandler fileHandler, EditorProvider editorProvider) {
        final JMenuItem reloadFileMenuItem = new JMenuItem("Reload File");
        reloadFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionUtils.getMetaMask() + KeyEvent.ALT_DOWN_MASK));
        reloadFileMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorProvider.releaseFile(fileHandler)) {
                    if (fileHandler instanceof BinEdFileHandler) {
                        ((BinEdFileHandler) fileHandler).reloadFile();
                    } else if (fileHandler instanceof BinEdFileDataWrapper) {
                        // TODO ((BinEdFileDataWrapper) fileHandler).reloadFile();
                    }
                }
            }
        });
        return reloadFileMenuItem;
    }

    @Nonnull
    private JMenuItem createShowHeaderMenuItem(ExtCodeArea codeArea) {
        final JCheckBoxMenuItem showHeader = new JCheckBoxMenuItem("Show Header");
        showHeader.setSelected(codeArea.getLayoutProfile().isShowHeader());
        showHeader.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = codeArea.getLayoutProfile();
                if (layoutProfile == null) {
                    throw new IllegalStateException();
                }
                boolean showHeader = layoutProfile.isShowHeader();
                layoutProfile.setShowHeader(!showHeader);
                codeArea.setLayoutProfile(layoutProfile);
            }
        });
        return showHeader;
    }

    @Nonnull
    private JMenuItem createShowRowPositionMenuItem(ExtCodeArea codeArea) {
        final JCheckBoxMenuItem showRowPosition = new JCheckBoxMenuItem("Show Row Position");
        showRowPosition.setSelected(codeArea.getLayoutProfile().isShowRowPosition());
        showRowPosition.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = codeArea.getLayoutProfile();
                if (layoutProfile == null) {
                    throw new IllegalStateException();
                }
                boolean showRowPosition = layoutProfile.isShowRowPosition();
                layoutProfile.setShowRowPosition(!showRowPosition);
                codeArea.setLayoutProfile(layoutProfile);
            }
        });
        return showRowPosition;
    }

    @Nonnull
    private JMenuItem createPositionCodeTypeMenuItem(ExtCodeArea codeArea) {
        JMenu menu = new JMenu("Position Code Type");
        PositionCodeType codeType = codeArea.getPositionCodeType();

        final JRadioButtonMenuItem octalCodeTypeMenuItem = new JRadioButtonMenuItem("Octal");
        octalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.OCTAL);
        octalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.OCTAL);
                preferences.getCodeAreaPreferences().setPositionCodeType(PositionCodeType.OCTAL);
            }
        });
        menu.add(octalCodeTypeMenuItem);

        final JRadioButtonMenuItem decimalCodeTypeMenuItem = new JRadioButtonMenuItem("Decimal");
        decimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.DECIMAL);
        decimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.DECIMAL);
                preferences.getCodeAreaPreferences().setPositionCodeType(PositionCodeType.DECIMAL);
            }
        });
        menu.add(decimalCodeTypeMenuItem);

        final JRadioButtonMenuItem hexadecimalCodeTypeMenuItem = new JRadioButtonMenuItem("Hexadecimal");
        hexadecimalCodeTypeMenuItem.setSelected(codeType == PositionCodeType.HEXADECIMAL);
        hexadecimalCodeTypeMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setPositionCodeType(PositionCodeType.HEXADECIMAL);
                preferences.getCodeAreaPreferences().setPositionCodeType(PositionCodeType.HEXADECIMAL);
            }
        });
        menu.add(hexadecimalCodeTypeMenuItem);

        return menu;
    }

    @Nonnull
    public ClipboardContentAction createClipboardContentAction(EditorProvider editorProvider) {
        ClipboardContentAction clipboardContentAction = new ClipboardContentAction();
        clipboardContentAction.setup(application, resourceBundle);
        return clipboardContentAction;
    }

    @Nonnull
    public DragDropContentAction createDragDropContentAction(EditorProvider editorProvider) {
        DragDropContentAction dragDropContentAction = new DragDropContentAction();
        dragDropContentAction.setup(application, resourceBundle);
        return dragDropContentAction;
    }

    @Nonnull
    public JMenuItem createShowInspectorPanel(BinEdComponentPanel binEdComponentPanel) {
        JCheckBoxMenuItem clipboardContentMenuItem = new JCheckBoxMenuItem("Inspector Panel");
//        clipboardContentMenuItem.setSelected(inspectorSupport.isShowParsingPanel(binEdComponentPanel));
//        clipboardContentMenuItem.addActionListener(event -> {
//            inspectorSupport.showParsingPanelAction(binEdComponentPanel).actionPerformed(event);
//        });
        return clipboardContentMenuItem;
    }

    public void addInsertDataComponent(InsertDataMethod insertDataComponent) {
        insertDataComponents.add(insertDataComponent);
    }

    public void addConvertDataComponent(ConvertDataMethod convertDataComponent) {
        convertDataComponents.add(convertDataComponent);
    }

    private void registerCodeAreaCommandHandlerProvider(CodeAreaCommandHandlerProvider commandHandlerProvider) {
        fileManager.setCommandHandlerProvider(commandHandlerProvider);
    }

    public void registerBinaryStatus(BinEdEditorComponent editorComponent, @Nullable BinEdComponentFileApi fileHandler) {
        BinaryStatusPanel statusPanel = editorComponent.getStatusPanel();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        codeArea.addDataChangedListener(() -> {
            //fileHandler.getComponent().notifyDataChanged();
            //            if (editorModificationListener != null) {
            //                editorModificationListener.modified();
            //            }
            long documentOriginalSize = fileHandler == null ? 0 : fileHandler.getDocumentOriginalSize();
            updateCurrentDocumentSize(editorComponent, documentOriginalSize);
        });

        codeArea.addSelectionChangedListener(() -> {
            statusPanel.setSelectionRange(codeArea.getSelection());
            // updateClipboardActionsStatus();
        });

        codeArea.addCaretMovedListener((CodeAreaCaretPosition caretPosition) -> {
            statusPanel.setCursorPosition(caretPosition);
        });

        codeArea.addEditModeChangedListener((EditMode mode, EditOperation operation) -> {
            statusPanel.setEditMode(mode, operation);
        });

        long documentOriginalSize = fileHandler == null ? 0 : fileHandler.getDocumentOriginalSize();
        updateStatus(editorComponent, documentOriginalSize);
    }

    public void updateStatus(BinEdEditorComponent editorComponent, long documentOriginalSize) {
        updateCurrentDocumentSize(editorComponent, documentOriginalSize);
        updateCurrentCaretPosition(editorComponent);
        updateCurrentSelectionRange(editorComponent);
        updateCurrentMemoryMode(editorComponent);
        updateCurrentEditMode(editorComponent);
    }

    private void updateCurrentDocumentSize(BinEdEditorComponent editorComponent, long documentOriginalSize) {
        BinaryStatusPanel statusPanel = editorComponent.getStatusPanel();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        long dataSize = codeArea.getDataSize();
        statusPanel.setCurrentDocumentSize(dataSize, documentOriginalSize);
    }

    private void updateCurrentCaretPosition(BinEdEditorComponent editorComponent) {
        BinaryStatusPanel statusPanel = editorComponent.getStatusPanel();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        CodeAreaCaretPosition caretPosition = codeArea.getCaretPosition();
        statusPanel.setCursorPosition(caretPosition);
    }

    private void updateCurrentSelectionRange(BinEdEditorComponent editorComponent) {
        BinaryStatusPanel statusPanel = editorComponent.getStatusPanel();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        SelectionRange selectionRange = codeArea.getSelection();
        statusPanel.setSelectionRange(selectionRange);
    }

    private void updateCurrentMemoryMode(BinEdEditorComponent editorComponent) {
        BinaryStatusPanel statusPanel = editorComponent.getStatusPanel();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        BinaryStatusApi.MemoryMode newMemoryMode = BinaryStatusApi.MemoryMode.RAM_MEMORY;
        if (((EditModeCapable) codeArea).getEditMode() == EditMode.READ_ONLY) {
            newMemoryMode = BinaryStatusApi.MemoryMode.READ_ONLY;
        } else if (codeArea.getContentData() instanceof DeltaDocument) {
            newMemoryMode = BinaryStatusApi.MemoryMode.DELTA_MODE;
        }

        statusPanel.setMemoryMode(newMemoryMode);
    }

    private void updateCurrentEditMode(BinEdEditorComponent editorComponent) {
        BinaryStatusPanel statusPanel = editorComponent.getStatusPanel();
        ExtCodeArea codeArea = editorComponent.getCodeArea();
        statusPanel.setEditMode(codeArea.getEditMode(), codeArea.getActiveOperation());
    }

    public boolean releaseFile(FileHandler fileHandler) {
        while (fileHandler.isModified() && ((BinEdComponentFileApi) fileHandler).isSaveSupported()) {
            Object[] options = {
                    "Save",
                    "Discard",
                    "Cancel"
            };
            int result = JOptionPane.showOptionDialog(fileHandler.getComponent(),
                    "Document was modified! Do you wish to save it?",
                    "Save File?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (result == JOptionPane.NO_OPTION) {
                return true;
            }
            if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                return false;
            }

            fileHandler.saveFile();
        }

        return true;
    }

    @Nonnull
    public BinaryEditorPreferences getPreferences() {
        return preferences;
    }

    public enum PopupMenuVariant {
        BASIC, NORMAL, EDITOR
    }

    @ParametersAreNonnullByDefault
    private static class EditorProviderImpl implements EditorProvider {

        private FileHandler fileHandler;

        public EditorProviderImpl(@Nullable FileHandler fileHandler) {
            this.fileHandler = fileHandler;
        }

        @Override
        public JComponent getEditorComponent() {
            return fileHandler.getComponent();
        }

        @Override
        public Optional<FileHandler> getActiveFile() {
            return Optional.ofNullable(fileHandler);
        }

        @Override
        public String getWindowTitle(String parentTitle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void openFile(URI fileUri, FileType fileType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setModificationListener(EditorProvider.EditorModificationListener editorModificationListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void newFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void openFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void saveAsFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canSave() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean releaseFile(FileHandler fileHandler) {
            BinEdManager binedManager = BinEdManager.getInstance();
            return binedManager.releaseFile(fileHandler);
        }

        @Override
        public boolean releaseAllFiles() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void loadFromFile(String fileName) throws URISyntaxException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void loadFromFile(URI fileUri, FileType fileType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<File> getLastUsedDirectory() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLastUsedDirectory(File directory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRecentFilesList(URI fileUri, FileType fileType) {
            throw new UnsupportedOperationException();
        }
    }
}
