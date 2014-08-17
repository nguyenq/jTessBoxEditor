/**
 * Copyright @ 2011 Quan Nguyen
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
package net.sourceforge.tessboxeditor;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JOptionPane;
import net.sourceforge.tessboxeditor.datamodel.TessBox;

public class GuiWithEdit extends GuiWithMRU {

    @Override
    void jMenuItemMergeActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Please select more than one box for Merge operation.");
            return;
        }

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;

        String chrs = "";
        short page = 0;
        int index = 0;

        for (TessBox box : selected) {
            chrs += box.getChrs();
            page = box.getPage();
            index = this.boxes.toList().indexOf(box);
            Rectangle rect = box.getRect();
            minX = Math.min(minX, rect.x);
            minY = Math.min(minY, rect.y);
            maxX = Math.max(maxX, rect.x + rect.width);
            maxY = Math.max(maxY, rect.y + rect.height);
            this.boxes.remove(box);
        }

        if (chrs.length() > 0) {
            TessBox newBox = new TessBox(chrs, new Rectangle(minX, minY, maxX - minX, maxY - minY), page);
            newBox.setSelected(true);
            boxes.add(index, newBox);
            int tableIndex = this.boxes.toList().indexOf(newBox);
            tableModel.setDataVector(boxes.getTableDataList().toArray(new String[0][5]), headers);
            this.jTable.setRowSelectionInterval(tableIndex, tableIndex);
            Rectangle rect = this.jTable.getCellRect(tableIndex, 0, true);
            this.jTable.scrollRectToVisible(rect);
        }

        this.jLabelImage.repaint();
        updateSave(true);
    }

    @Override
    void jMenuItemSplitActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a box to split.");
            return;
        } else if (selected.size() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one box for Split operation.");
            return;
        }

        boolean modifierKeyPressed = false;
        int modifiers = evt.getModifiers();
        if ((modifiers & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK
                || (modifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK
                || (modifiers & ActionEvent.META_MASK) == ActionEvent.META_MASK) {
            modifierKeyPressed = true;
        }

        TessBox box = selected.get(0);
        int index = this.boxes.toList().indexOf(box);
        Rectangle rect = box.getRect();
        if (!modifierKeyPressed) {
            rect.width /= 2;
            tableModel.setValueAt(String.valueOf(rect.width), index, 3);
        } else {
            rect.height /= 2;
            tableModel.setValueAt(String.valueOf(rect.height), index, 4);
        }
        
        TessBox newBox = new TessBox(box.getChrs(), new Rectangle(rect), box.getPage());
        newBox.setSelected(true);
        boxes.add(index + 1, newBox);
        Rectangle newRect = newBox.getRect();
        if (!modifierKeyPressed) {
            newRect.x += newRect.width;
        } else {
            newRect.y += newRect.height;
        }
        
        Object[] newRow = {newBox.getChrs(), newRect.x, newRect.y, newRect.width, newRect.height};
        tableModel.insertRow(index + 1, newRow);
        jTable.setRowSelectionInterval(index, index + 1);
        resetReadout();
        this.jLabelImage.repaint();
        updateSave(true);
    }

    @Override
    void jMenuItemInsertActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select the box to insert after.");
            return;
        } else if (selected.size() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one box for Insert operation.");
            return;
        }

        TessBox box = selected.get(0);
        int index = this.boxes.toList().indexOf(box);
        index++;
        TessBox newBox = new TessBox(box.getChrs(), new Rectangle(box.getRect()), box.getPage());
        newBox.setSelected(true);
        boxes.add(index, newBox);
        Rectangle newRect = newBox.getRect();
        newRect.x += 15; // offset the new box 15 pixel from the base one
        Object[] newRow = {newBox.getChrs(), newRect.x, newRect.y, newRect.width, newRect.height};
        tableModel.insertRow(index, newRow);
        jTable.setRowSelectionInterval(index, index);
        this.jLabelImage.repaint();
        updateSave(true);
    }

    @Override
    void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a box or more to delete.");
            return;
        }

        for (TessBox box : selected) {
            int index = this.boxes.toList().indexOf(box);
            this.boxes.remove(box);
            tableModel.removeRow(index);
        }

        resetReadout();
        this.jLabelImage.repaint();
        updateSave(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GuiWithEdit().setVisible(true);
            }
        });
    }
}
