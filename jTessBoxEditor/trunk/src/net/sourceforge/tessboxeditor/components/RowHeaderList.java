/**
 * Copyright @ 2013 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.tessboxeditor.components;

import java.awt.*;
import javax.swing.*;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RowHeaderList extends JList {

    JTable table;
    RowHeaderRenderer render;

    @SuppressWarnings("unchecked")
    public RowHeaderList(final JTable table) {
        this.table = table;
        setFixedCellWidth(50);
        setFixedCellHeight(table.getRowHeight());
        render = new RowHeaderRenderer();
        setCellRenderer(render);
        setSelectionModel(table.getSelectionModel());
        addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = RowHeaderList.this.getSelectedIndex();
                    Rectangle rect = table.getCellRect(index, 0, true);
                    table.scrollRectToVisible(rect);
                }
            }
        });
        
        ListModel lm = new AbstractListModel() {
            @Override
            public int getSize() {
                return table.getRowCount();
            }

            @Override
            public Object getElementAt(int index) {
                return index + 1;
            }
        };
        setModel(lm);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        FontMetrics metrics = getFontMetrics(font);
        setFixedCellWidth(metrics.stringWidth("4444") + 8); // 4-digit length + padding
        if (table != null) {
            setFixedCellHeight(table.getRowHeight());
        }

        if (render != null) {
            render.setFont(font);
        }
    }

    class RowHeaderRenderer extends JLabel implements ListCellRenderer {

        public RowHeaderRenderer() {
            this.setOpaque(true);
            this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            this.setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
