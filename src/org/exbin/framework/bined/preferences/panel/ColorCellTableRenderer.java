/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exbin.framework.bined.preferences.panel;

import java.awt.Color;
import java.awt.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;

/**
 * Table model for Color profile panel.
 *
 * @version 0.2.0 2019/01/17
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ColorCellTableRenderer implements TableCellRenderer {

    public ColorCellTableRenderer() {
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return new ColorCellPanel((Color) value);
    }
}
