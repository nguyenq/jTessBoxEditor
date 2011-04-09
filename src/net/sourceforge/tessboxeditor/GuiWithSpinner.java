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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;

public class GuiWithSpinner extends GuiWithEdit {

    @Override
    void stateChanged(javax.swing.event.ChangeEvent evt) {
        if (tableSelectAction) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            return;
        } else if (selected.size() > 1) {
//            JOptionPane.showMessageDialog(this, "Select only one box for Spinner operation.");
            return;
        }

        TessBox box = selected.get(0);
        int index = this.boxes.toList().indexOf(box);

        box.chrs = this.jTextFieldChar.getText();
        tableModel.setValueAt(box.chrs, index, 0);
        JSpinner sp = (JSpinner) evt.getSource();
        if (sp == this.jSpinnerX) {
            box.rect.x = (Integer) this.jSpinnerX.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.x), index, 1);
        } else if (sp == this.jSpinnerY) {
            box.rect.y = (Integer) this.jSpinnerY.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.y), index, 2);
        } else if (sp == this.jSpinnerW) {
            box.rect.width = (Integer) this.jSpinnerW.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.width), index, 3);
        } else if (sp == this.jSpinnerH) {
            box.rect.height = (Integer) this.jSpinnerH.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.height), index, 4);
        }

        Icon icon = jLabelImage.getIcon();
        try {
            Image subImage = ((BufferedImage) ((ImageIcon) icon).getImage()).getSubimage(box.rect.x, box.rect.y, box.rect.width, box.rect.height);
            jLabelSubimage.setIcon(new ImageIcon(subImage));
        } catch (Exception exc) {
            //ignore
        }
        this.jLabelImage.repaint();
        updateSave(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GuiWithSpinner().setVisible(true);
            }
        });
    }
}
