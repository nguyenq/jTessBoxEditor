/**
 * Copyright @ 2013 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tessboxeditor.components;

import java.awt.Component;
import java.awt.Font;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import net.sourceforge.vietpad.utilities.TextUtilities;

public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    JTextComponent component = new JTextField();
    Font font;
       
    public void setFont(Font font) {
        this.font = font;
    } 

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        component.setText((String) value);
        component.setFont((font != null) ? font : table.getFont());
        
        return component;
    }

    @Override
    public Object getCellEditorValue() {
        return TextUtilities.convertNCR(component.getText());
    }
}
