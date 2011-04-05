/**
 * Copyright @ 20011 Quan Nguyen
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
package net.sourceforge.tessboxeditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 *
 */
public class JImageLabel extends JLabel {

    private TessBoxCollection boxes;
    short page;
    private JTable table;
    private boolean boxClickAction;

    /** Creates a new instance of JImageLabel */
    public JImageLabel() {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                if (boxes == null) {
                    return;
                }

                TessBox box = boxes.hitObject(me.getPoint(), page);
                if (box == null) {
                    if (!me.isControlDown()) {
                        boxes.deselectAll();
                        repaint();
                        table.clearSelection();
                    }
                } else {
                    if (!me.isControlDown()) {
                        boxes.deselectAll();
                        table.clearSelection();
                    }
                    box.setSelected(!box.isSelected()); // toggle selection
                    repaint();
                    int index = boxes.toList(page).indexOf(box);
                    boxClickAction = true;
                    table.setRowSelectionInterval(index, index);
                    boxClickAction = false;
                    Rectangle rect = table.getCellRect(index, 0, true);
                    table.scrollRectToVisible(rect);
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        // automatically called when repaint
        super.paintComponent(g);

        if (boxes == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        boolean resetColor = false;
//        int height = getHeight();

        for (TessBox box : boxes.toList(page)) {
            if (box.isSelected()) {
                g2d.setColor(Color.RED);
                resetColor = true;
            }
            Rectangle rect = box.rect;
            g2d.draw(rect);
//            g2d.drawRect(rect.x, height - rect.y - rect.height, rect.width, rect.height);
            if (resetColor) {
                g2d.setColor(Color.BLUE);
                resetColor = false;
            }
        }
    }

    public void setBoxes(TessBoxCollection boxes) {
        this.boxes = boxes;
    }

    public void setPage(short page) {
        this.page = page;
        repaint();
    }

    /**
     * @param table the table to set
     */
    public void setTable(JTable table) {
        this.table = table;
    }

    /**
     * @return the boxClickAction
     */
    public boolean isBoxClickAction() {
        return boxClickAction;
    }
}
