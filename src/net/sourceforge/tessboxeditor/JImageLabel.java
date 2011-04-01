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
import java.util.List;
import javax.swing.JLabel;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 *
 */
public class JImageLabel extends JLabel {

    private List<CharEntity> chrs;
    short s;

    /** Creates a new instance of JImageLabel */
    public JImageLabel() {
    }

    public void setRects(List<CharEntity> chrs) {
        this.chrs = chrs;
    }

    public void setPage(short s) {
        this.s = s;
    }

    @Override
    public void paintComponent(Graphics g) {
        // automatically called when repaint
        super.paintComponent(g);

        if (chrs != null) {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.BLUE);

            for (CharEntity ch : chrs) {
                if (ch.page == s) {
                    Rectangle rect = ch.rect;
                    g2d.draw(rect);
                }
            }
        }
    }
}
