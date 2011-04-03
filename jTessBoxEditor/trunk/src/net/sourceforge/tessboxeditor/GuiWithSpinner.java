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

import java.util.List;
import javax.swing.JOptionPane;

public class GuiWithSpinner extends GuiWithAction {

    @Override
    void stateChanged(javax.swing.event.ChangeEvent evt) {
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            return;
        } else if (selected.size() > 1) {
            JOptionPane.showMessageDialog(this, "Select only one box for Spinner operation.");
            return;
        }

        TessBox box = selected.get(0);
        int index = this.boxes.toList().indexOf(box);

//        box.chrs = this.jTextFieldChar.getText();
//        tableModel.setValueAt(box.chrs, index, 0);
        
        if (evt.getSource() == this.jSpinnerX) {
            box.rect.x = (Integer) this.jSpinnerX.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.x), index, 1);
        } else if (evt.getSource() == this.jSpinnerY) {
            box.rect.y = (Integer) this.jSpinnerY.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.y), index, 2);
        } else if (evt.getSource() == this.jSpinnerW) {
            box.rect.width = (Integer) this.jSpinnerW.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.x + box.rect.width), index, 3);
        } else if (evt.getSource() == this.jSpinnerH) {
            box.rect.height = (Integer) this.jSpinnerH.getValue();
            tableModel.setValueAt(String.valueOf(box.rect.y + box.rect.height), index, 4);
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
