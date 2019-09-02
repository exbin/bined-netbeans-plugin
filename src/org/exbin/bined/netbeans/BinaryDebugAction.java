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
package org.exbin.bined.netbeans;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.exbin.bined.netbeans.panel.DebugViewPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.utils.binary_data.ByteArrayEditableData;
import org.netbeans.api.debugger.jpda.ClassVariable;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.spi.debugger.ContextProvider;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbPreferences;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Debug view action.
 *
 * @version 0.2.1 2019/09/02
 * @author ExBin Project (http://exbin.org)
 */
@ActionID(
        category = "Debug",
        id = "org.exbin.bined.netbeans.BinaryDebugAction"
)
@ActionRegistration(
        displayName = "#CTL_BinaryDebugAction"
)
//@Messages("CTL_BinaryDebugAction=Show as Binary")
public final class BinaryDebugAction implements ActionListener {

    public static final String SHOW_WATCHES = "show_watches"; // NOI18N

    public static final String PREFERENCES_NAME = "variables_view"; // NOI18N

    private final List<ObjectVariable> context;

    public BinaryDebugAction(List<ObjectVariable> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String viewName = isWatchesViewNested() ? "localsView" : "watchesView";
        TopComponent watchesView = WindowManager.getDefault().findTopComponent(viewName);

        // TODO There is probably better way how to handle this
        Object selectedObject = watchesView.getLookup().lookup(Object.class);

        // AccessibleContext accessibleContext = watchesView.getAccessibleContext();
        //OutlineTable outlineTable = watchesView.getLookup().lookup(OutlineTable.class);
        // OutlineTable outlineTable = accessibleContext.getAccessibleChild(0).getAccessibleChild(0).getAccessibleChild(0);
        // Node[] nodes = watchesView.getActivatedNodes();
//        DebuggerManager.getDebuggerManager().getCurrentSession().
//        final Mode editorMode = WindowManager.getDefault().findMode(viewName);
//        TopComponent localsView = WindowManager.getDefault().findTopComponent("LocalsView");
//        TopComponent breakpointsView = WindowManager.getDefault().findTopComponent("BreakpointsView");
//        TopComponent activeTC = TopComponent.getRegistry().getActivated();
//        DebuggingView debuggingView = DebuggingView.getDefault();
//        TopComponent debuggingViewTC = debuggingView.getViewTC();
        // VariablesView variablesView = VariablesView.
//        DebuggerManager.getDebuggerManager().get
//        TopComponent outputWindow = WindowManager.getDefault().findTopComponent();
//        activeTC.ViewFactory viewFactory = activeTC.getLookup().lookup(ViewFactory.class);
//        viewFactory.View.LOCALS_VIEW_NAME JOptionPane optionPane = new JOptionPane("Test");
//        optionPane.setVisible(true);
//        VariablesViewButtons
        DebugViewPanel debugViewPanel = new DebugViewPanel();
        WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(debugViewPanel, (Component) e.getSource(), "View as Binary Data", Dialog.ModalityType.APPLICATION_MODAL);

        ByteArrayEditableData data = new ByteArrayEditableData();
        if (selectedObject instanceof ObjectVariable) {
//            JPDAClassType classType = ((ObjectVariable) selectedObject).getClassType();
//            ClassVariable classObject = classType.classObject();
            // classObject.getToStringValue();
//            int fieldsCount = ((ObjectVariable) selectedObject).getFieldsCount();

//            Field[] fields = ((ObjectVariable) selectedObject).getFields(0, 0);
            

            String value = ((ObjectVariable) selectedObject).getValue();
            if (value != null) {
                data.insert(0, value.getBytes());
                debugViewPanel.setData(data);
            }
        } else if (selectedObject instanceof Variable) {
//            String type = ((Variable) selectedObject).getType();
            String value = ((Variable) selectedObject).getValue();
            if (value != null) {
                data.insert(0, value.getBytes());
                debugViewPanel.setData(data);
            }
        }
        dialog.show();
    }

    private static void openWatchesView() {
        // open watches view
        TopComponent watchesView = WindowManager.getDefault().findTopComponent("watchesView"); // NOI18N
        if (watchesView != null && watchesView.isOpened()) {
            Mode mw = WindowManager.getDefault().findMode(watchesView);
            if (mw != null && mw.getSelectedTopComponent() == watchesView) {
                return; // Watches is already selected
            }
        }
        String viewName = isWatchesViewNested() ? "localsView" : "watchesView";
        openComponent(viewName, false).requestVisible();
    }

    static TopComponent openComponent(String viewName, boolean activate) {
        TopComponent view = WindowManager.getDefault().findTopComponent(viewName);
        if (view == null) {
            throw new IllegalArgumentException(viewName);
        }
        view.open();
        if (activate) {
            view.requestActive();
        }
        return view;
    }

    public static boolean isWatchesViewNested() {
        java.util.prefs.Preferences preferences = NbPreferences.forModule(ContextProvider.class).node(PREFERENCES_NAME); // NOI18N
        return preferences.getBoolean(SHOW_WATCHES, true);
    }
}
