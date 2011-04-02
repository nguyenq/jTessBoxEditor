/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.tessboxeditor;

import java.awt.Rectangle;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class GuiWithAction extends GuiWithLaF {

    @Override
    void mergeAction() {
        mergeBoxes(boxes.getSelectedBoxes());
    }

    void mergeBoxes(List<TessBox> selected) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;

        String ch = null;
        short page = 0;
        int index = 0;

        for (TessBox box : selected) {
            ch = box.ch;
            page = box.page;
            index = this.boxes.list.indexOf(box);
            minX = Math.min(minX, box.rect.x);
            minY = Math.min(minY, box.rect.y);
            maxX = Math.max(maxX, box.rect.x + box.rect.width);
            maxY = Math.max(maxY, box.rect.y + box.rect.height);
            this.boxes.remove(box);
        }

        if (ch != null) {
            this.boxes.addBox(index, new TessBox(ch, new Rectangle(minX, minY, maxX - minX, maxY - minY), page));
        }

        DefaultTableModel model = (DefaultTableModel) this.jTable1.getModel();
        // need to recreate the tabledata here!
        model.setDataVector(boxes.getTableDataList().toArray(new String[0][5]), headers);
        ((JImageLabel) this.jLabelImage).setBoxes(this.boxes);
    }

    @Override
    void deleteAction() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GuiWithAction().setVisible(true);
            }
        });
    }
}
