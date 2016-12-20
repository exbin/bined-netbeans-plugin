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
package org.exbin.deltahex.netbeans.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Utility static methods usable for windows and dialogs.
 *
 * @version 0.1.4 2016/12/04
 * @author ExBin Project (http://exbin.org)
 */
public class WindowUtils {

    private static final int BUTTON_CLICK_TIME = 150;
    private static LookAndFeel lookAndFeel = null;

    private WindowUtils() {
    }

    public static void invokeWindow(final Window window) {
        if (lookAndFeel != null) {
            try {
                javax.swing.UIManager.setLookAndFeel(lookAndFeel);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(WindowUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (window instanceof JDialog) {
                    ((JDialog) window).setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                }

                window.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                window.setVisible(true);
            }
        });
    }

    public static void initWindow(Window window) {
//        if (window.getParent() instanceof XBEditorFrame) {
//            window.setIconImage(((XBEditorFrame) window.getParent()).getMainFrameManagement().getFrameIcon());
//        }
    }

    public static LookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }

    public static void setLookAndFeel(LookAndFeel lookAndFeel) {
        WindowUtils.lookAndFeel = lookAndFeel;
    }

    public static void closeWindow(Window window) {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    public static JDialog createBasicDialog() {
        JDialog dialog = new JDialog(new javax.swing.JFrame(), true);
        dialog.setSize(640, 480);
        dialog.setLocationByPlatform(true);
        return dialog;
    }

    /**
     * Find frame component for given component.
     *
     * @param component instantiated component
     * @return frame instance if found
     */
    public static Frame getFrame(Component component) {
        Component parentComponent = SwingUtilities.getWindowAncestor(component);
        while (!(parentComponent == null || parentComponent instanceof Frame)) {
            parentComponent = parentComponent.getParent();
        }
        return (Frame) parentComponent;
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param closeButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Container component, final JButton closeButton) {
        assignGlobalKeyListener(component, closeButton, closeButton);
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param okButton button which will be used for default ENTER
     * @param cancelButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Container component, final JButton okButton, final JButton cancelButton) {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    boolean performOkAction = true;

                    if (evt.getSource() instanceof JButton) {
                        ((JButton) evt.getSource()).doClick(BUTTON_CLICK_TIME);
                        performOkAction = false;
                    } else if (evt.getSource() instanceof JTextArea) {
                        performOkAction = !((JTextArea) evt.getSource()).isEditable();
                    } else if (evt.getSource() instanceof JTextPane) {
                        performOkAction = !((JTextPane) evt.getSource()).isEditable();
                    } else if (evt.getSource() instanceof JEditorPane) {
                        performOkAction = !((JEditorPane) evt.getSource()).isEditable();
                    }

                    if (okButton != null && performOkAction) {
                        okButton.doClick(BUTTON_CLICK_TIME);
                    }

                } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelButton.doClick(BUTTON_CLICK_TIME);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        assignGlobalKeyListener(component, keyListener);
    }

    /**
     * Assign key listener for all focusable components recursively.
     *
     * @param component target component
     * @param keyListener key lisneter
     */
    public static void assignGlobalKeyListener(Container component, KeyListener keyListener) {
        Component[] comps = component.getComponents();
        for (Component item : comps) {
            if (item.isFocusable()) {
                item.addKeyListener(keyListener);
            }

            if (item instanceof Container) {
                assignGlobalKeyListener((Container) item, keyListener);
            }
        }
    }

    /**
     * Performs visually visible click on the button component.
     *
     * @param button button component
     */
    public static void doButtonClick(JButton button) {
        button.doClick(BUTTON_CLICK_TIME);
    }

    public static WindowPosition getWindowPosition(Window window) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        int windowX = window.getX();
        int windowY = window.getY();
        int screenX = 0;
        int screenY = 0;
        int screenWidth = 0;
        int screenHeight = 0;
        int screenIndex = 0;
        for (GraphicsDevice screen : screenDevices) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            if (bounds.contains(windowX, windowY)) {
                screenX = bounds.x;
                screenY = bounds.y;
                screenWidth = bounds.width;
                screenHeight = bounds.height;
                break;
            }
            screenIndex++;
        }
        WindowPosition position = new WindowPosition();
        position.setScreenIndex(screenIndex);
        position.setScreenWidth(screenWidth);
        position.setScreenHeight(screenHeight);
        position.setRelativeX(window.getX() - screenX);
        position.setRelativeY(window.getY() - screenY);
        position.setWidth(window.getWidth());
        position.setHeight(window.getHeight());
        position.setMaximized(window instanceof Frame ? (((Frame) window).getExtendedState() & JFrame.MAXIMIZED_BOTH) > 0 : false);
        return position;
    }

    public static void setWindowPosition(Window window, WindowPosition position) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        GraphicsDevice device;
        if (screenDevices.length > position.getScreenIndex()) {
            device = screenDevices[position.getScreenIndex()];
        } else {
            device = ge.getDefaultScreenDevice();
        }
        Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
        double absoluteX = position.getScreenWidth() > 0
                ? screenBounds.x + position.getRelativeX() * screenBounds.width / position.getScreenWidth()
                : screenBounds.x + position.getRelativeX();
        double absoluteY = position.getScreenHeight() > 0
                ? screenBounds.y + position.getRelativeY() * screenBounds.height / position.getScreenHeight()
                : screenBounds.y + position.getRelativeY();
        double widthX = position.getScreenWidth() > 0
                ? position.getWidth() * screenBounds.width / position.getScreenWidth()
                : position.getWidth();
        double widthY = position.getScreenHeight() > 0
                ? position.getHeight() * screenBounds.height / position.getScreenHeight()
                : position.getHeight();
        if (position.isMaximized()) {
            window.setLocation((int) absoluteX, (int) absoluteY);
            if (window instanceof Frame) {
                ((Frame) window).setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                // TODO if (window instanceof JDialog) 
            }
        } else {
            window.setBounds((int) absoluteX, (int) absoluteY, (int) widthX, (int) widthY);
        }
    }

    public static JPanel createDialogPanel(JPanel mainPanel, JPanel controlPanel) {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(mainPanel, BorderLayout.CENTER);
        dialogPanel.add(controlPanel, BorderLayout.SOUTH);
        return dialogPanel;
    }
}
