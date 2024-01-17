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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.bined.netbeans.debug.DebugViewData;
import org.exbin.bined.netbeans.debug.array.BooleanArrayPageProvider;
import org.exbin.bined.netbeans.debug.array.ByteArrayPageProvider;
import org.exbin.bined.netbeans.debug.array.CharArrayPageProvider;
import org.exbin.bined.netbeans.debug.array.DoubleArrayPageProvider;
import org.exbin.bined.netbeans.debug.array.FloatArrayPageProvider;
import org.exbin.bined.netbeans.debug.array.IntegerArrayPageProvider;
import org.exbin.bined.netbeans.debug.array.LongArrayPageProvider;
import org.exbin.bined.netbeans.debug.array.ShortArrayPageProvider;
import org.exbin.bined.netbeans.debug.gui.DebugViewPanel;
import org.exbin.framework.bined.inspector.gui.BasicValuesPanel;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.gui.CloseControlPanel;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.ByteArrayData;
import org.exbin.bined.netbeans.debug.DebugViewDataProvider;
import org.exbin.bined.netbeans.debug.DefaultDebugViewDataProvider;
import org.netbeans.api.debugger.jpda.ClassVariable;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.JPDAArrayType;
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
 * @author ExBin Project (https://exbin.org)
 */
@ActionID(
        category = "Debug",
        id = "org.exbin.bined.netbeans.debug.BinaryDebugAction"
)
@ActionRegistration(
        displayName = "#CTL_BinaryDebugAction"
)
//@Messages("CTL_BinaryDebugAction=Show as Binary")
@ParametersAreNonnullByDefault
public final class BinaryDebugAction implements ActionListener {

    public static final String SHOW_WATCHES = "show_watches"; // NOI18N

    public static final String PREFERENCES_NAME = "variables_view"; // NOI18N

    private static final byte[] valuesCache = new byte[8];
    private static final ByteBuffer byteBuffer = ByteBuffer.wrap(valuesCache);

    public BinaryDebugAction() {
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
        actionPerformed((Component) e.getSource(), selectedObject);
    }

    public static void actionPerformed(Component parent, Object variableObject) {
        DebugViewPanel debugViewPanel = new DebugViewPanel();
        debugViewPanel.setPreferredSize(new Dimension(800, 500));
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(debugViewPanel, controlPanel);
        WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parent, "View as Binary", Dialog.ModalityType.APPLICATION_MODAL);

        BinaryData data;
        if (variableObject instanceof ObjectVariable) {
            JPDAClassType classType = ((ObjectVariable) variableObject).getClassType();
            ClassVariable classObject = classType == null ? null : classType.classObject();
//            JPDAClassType reflectedType = classObject.getReflectedType();

            if (classObject instanceof JPDAArrayType) {
                data = processArrayValue((ObjectVariable) variableObject, (JPDAArrayType) classObject);
                if (data != null) {
                    debugViewPanel.addProvider(new DefaultDebugViewDataProvider("binary sequence from array", data));
                }
            } else if (classObject != null && classObject.getReflectedType() instanceof JPDAArrayType) {
                data = processArrayValue((ObjectVariable) variableObject, (JPDAArrayType) classObject.getReflectedType());
                if (data != null) {
                    debugViewPanel.addProvider(new DefaultDebugViewDataProvider("native binary sequence from array", data));
                }
            } else if (classType instanceof JPDAArrayType) {
// TODO support for java.lang.*[] wrappers for native types
// (Throws java.lang.AssertionError: Debugger communication in AWT Event Queue!)
//                data = processArrayValue((ObjectVariable) variableObject, (JPDAArrayType) classType);
//                if (data != null) {
//                    fallback = false;
//                    debugViewPanel.setData(data);
//                }
            } else {
                // classObject.getToStringValue();
// TODO support for java.lang.* wrappers for native types
//                int fieldsCount = ((ObjectVariable) variableObject).getFieldsCount();
//                if (fieldsCount == 1) {
//                    Field[] fields = ((ObjectVariable) variableObject).getFields(0, 0);
//                    Field field = fields[0];
//                    data = processSimpleValue(field.getDeclaredType(), ((ObjectVariable) variableObject).getValue());
//                    if (data != null) {
//                        fallback = false;
//                        debugViewPanel.setData(data);
//                    }
//                }
            }
        }

        if (variableObject instanceof Variable) {
            String variableValue = ((Variable) variableObject).getValue();
            String variableType = ((Variable) variableObject).getType();
            data = processSimpleValue(variableType, variableValue);
            if (data != null) {
                debugViewPanel.addProvider(new DefaultDebugViewDataProvider("binary value", data));
            }

            final String value = ((Variable) variableObject).getValue();
            debugViewPanel.addProvider(new DebugViewDataProvider() {
                @Override
                public String getName() {
                    return "toString()";
                }

                @Override
                public BinaryData getData() {
                    if (value != null) {
                        return new ByteArrayData(value.getBytes(Charset.defaultCharset()));
                    } else {
                        return new ByteArrayData();
                    }
                }
            });
        }

        controlPanel.setHandler(() -> {
            dialog.close();
        });
        dialog.show();
    }

    @Nullable
    private static BinaryData processSimpleValue(String variableType, String variableValue) {
        switch (variableType) {
            case "byte": {
                byte[] byteArray = new byte[1];
                byte value = Byte.parseByte(variableValue);
                byteArray[0] = value;
                return new ByteArrayData(byteArray);
            }
            case "short": {
                byte[] byteArray = new byte[2];
                short value = Short.parseShort(variableValue);
                byteArray[0] = (byte) (value >> 8);
                byteArray[1] = (byte) (value & 0xff);
                return new ByteArrayData(byteArray);
            }
            case "int": {
                byte[] byteArray = new byte[4];
                int value = Integer.parseInt(variableValue);
                byteArray[0] = (byte) (value >> 24);
                byteArray[1] = (byte) ((value >> 16) & 0xff);
                byteArray[2] = (byte) ((value >> 8) & 0xff);
                byteArray[3] = (byte) (value & 0xff);
                return new ByteArrayData(byteArray);
            }
            case "long": {
                byte[] byteArray = new byte[8];
                long value = Long.parseLong(variableValue);
                BigInteger bigInteger = BigInteger.valueOf(value);
                for (int bit = 0; bit < 7; bit++) {
                    BigInteger nextByte = bigInteger.and(BasicValuesPanel.BIG_INTEGER_BYTE_MASK);
                    byteArray[7 - bit] = nextByte.byteValue();
                    bigInteger = bigInteger.shiftRight(8);
                }
                return new ByteArrayData(byteArray);
            }
            case "float": {
                byte[] byteArray = new byte[4];
                float value = Float.parseFloat(variableValue);
                byteBuffer.rewind();
                byteBuffer.putFloat(value);
                System.arraycopy(valuesCache, 0, byteArray, 0, 4);
                return new ByteArrayData(byteArray);
            }
            case "double": {
                byte[] byteArray = new byte[8];
                double value = Double.parseDouble(variableValue);
                byteBuffer.rewind();
                byteBuffer.putDouble(value);
                System.arraycopy(valuesCache, 0, byteArray, 0, 8);
                return new ByteArrayData(byteArray);
            }
            case "char": {
                if (variableValue.length() == 3) {
                    byte[] byteArray = new byte[2];
                    char value = variableValue.charAt(1);
                    byteBuffer.rewind();
                    byteBuffer.putChar(value);
                    System.arraycopy(valuesCache, 0, byteArray, 0, 2);
                    return new ByteArrayData(byteArray);
                }

                break;
            }
            case "string": {
                if (variableValue.length() > 2) {
                    return new ByteArrayData(variableValue.substring(1, variableValue.length() - 2).getBytes(Charset.defaultCharset()));
                }
            }
        }

        return null;
    }

    @Nullable
    private static BinaryData processArrayValue(ObjectVariable variableObject, JPDAArrayType arrayType) {
        String type = arrayType.getComponentTypeName();
        switch (type) {
            case "boolean": {
                return new DebugViewData(new BooleanArrayPageProvider(variableObject));
            }
//            case "java.lang.Byte": {
//                loadChildValues(variableObject);
//            }
            case "byte": {
                return new DebugViewData(new ByteArrayPageProvider(variableObject));
            }
            case "short": {
                return new DebugViewData(new ShortArrayPageProvider(variableObject));
            }
            case "int": {
                return new DebugViewData(new IntegerArrayPageProvider(variableObject));
            }
            case "long": {
                return new DebugViewData(new LongArrayPageProvider(variableObject));
            }
            case "float": {
                return new DebugViewData(new FloatArrayPageProvider(variableObject));
            }
            case "double": {
                return new DebugViewData(new DoubleArrayPageProvider(variableObject));
            }
            case "char": {
                return new DebugViewData(new CharArrayPageProvider(variableObject));
            }
        }

        return null;
    }

    private static void loadChildValues(ObjectVariable variableObject) {
        int fieldsCount = variableObject.getFieldsCount();
        for (int i = 0; i < fieldsCount; i++) {
            final Field[] values = variableObject.getFields(i, i + 1);
            Field rawValue = values[0];
            if (rawValue instanceof ObjectVariable) {
                rawValue = ((ObjectVariable) rawValue).getFields(0, 1)[0];
                rawValue.getValue();
            }
        }
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

    @Nonnull
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
