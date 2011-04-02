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

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 *
 */
public class JImageLabel extends JLabel {

    private TessBoxCollection boxes;
    short page;

    /** Creates a new instance of JImageLabel */
    public JImageLabel() {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {
                TessBox o = boxes.hitObject(me.getPoint());
                if (o == null) {
                    boxes.deselectAll();
                } else {
                    if (!me.isControlDown()) {
                        boxes.deselectAll();
                    }
                    o.setSelected(true);
                }
                repaint();
            }
        });
    }

    public void setBoxes(TessBoxCollection boxes) {
        this.boxes = boxes;
        repaint();
    }

    public void setPage(short page) {
        this.page = page;
    }

    @Override
    public void paintComponent(Graphics g) {
        // automatically called when repaint
        super.paintComponent(g);

        if (boxes != null) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.BLUE);
            boolean resetColor = false;

            for (TessBox box : boxes.toList()) {
                if (box.page == page) {
                    Rectangle rect = box.rect;
                    if (box.isSelected()) {
                        g2d.setColor(Color.RED);
                        resetColor = true;
                    }
                    g2d.draw(rect);
                    if (resetColor) {
                        g2d.setColor(Color.BLUE);
                        resetColor = false;
                    }
                }
            }
        }
    }
}
