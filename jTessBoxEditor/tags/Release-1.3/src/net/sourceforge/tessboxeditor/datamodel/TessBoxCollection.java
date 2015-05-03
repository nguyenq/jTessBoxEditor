/**
 * Copyright @ 2011 Quan Nguyen
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
package net.sourceforge.tessboxeditor.datamodel;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Operations on collection of boxes.
 */
public class TessBoxCollection {

    private String appendingSymbols;
    private String prependingSymbols;
    private final List<TessBox> list;

    public TessBoxCollection() {
        list = new ArrayList<TessBox>();
    }

    /**
     * Adds box to list.
     * 
     * @param box 
     */
    public void add(TessBox box) {
        list.add(box);
    }

    /**
     * Adds box to list at index.
     * 
     * @param index
     * @param box 
     */
    public void add(int index, TessBox box) {
        list.add(index, box);
    }

    /**
     * Deselects all boxes.
     */
    public void deselectAll() {
        for (TessBox box : list) {
            box.setSelected(false);
        }
    }

    /**
     * Gets list of boxes.
     * 
     * @return 
     */
    public List<TessBox> toList() {
        return list;
    }

    /**
     * Finds the box that matches a given coordinate.
     * 
     * @param findBox
     * @return 
     */
    public TessBox select(TessBox findBox) {
        for (TessBox box : list) {
            if (box.getRect().equals(findBox.getRect())) {
                return box;
            }
        }
        return null;
    }

    /**
     * Finds the box that matches a given character value.
     * 
     * @param findBox
     * @return 
     */
    public TessBox selectByChars(TessBox findBox) {
        List<TessBox> selectedBoxes = getSelectedBoxes();
        List<TessBox> searchList;

        if (selectedBoxes.isEmpty()) {
            searchList = list;
        } else {
            TessBox lastSelectedBox = selectedBoxes.get(selectedBoxes.size() - 1);
            int index = list.indexOf(lastSelectedBox);
            searchList = list.subList(index + 1, list.size());
        }

        for (TessBox box : searchList) {
            if (box.getChrs().contains(findBox.getChrs())) {
                return box;
            }
        }
        return null;
    }

    /**
     * Gets the box hit by mouse click.
     * 
     * @param p where mouse clicks
     * @return 
     */
    public TessBox hitObject(Point p) {
        for (TessBox box : list) {
            if (box.contains(p)) {
                return box;
            }
        }
        return null;
    }

    /**
     * Gets selected boxes.
     * 
     * @return 
     */
    public List<TessBox> getSelectedBoxes() {
        List<TessBox> selected = new ArrayList<TessBox>();
        for (TessBox box : list) {
            if (box.isSelected()) {
                selected.add(box);
            }
        }
        return selected;
    }
    
    /**
     * Sets combining symbols.
     * 
     * @param combiningSymbols 
     */
    public void setCombiningSymbols(String combiningSymbols) {
        if (combiningSymbols == null) {
            return;
        }
        String[] str = combiningSymbols.split(";");
        if (str.length > 0) {
            this.appendingSymbols = str[0];
        }
        if (str.length > 1) {
            this.prependingSymbols = str[1];
        }
    }
    
    /**
     * Combines boxes that have the same coordinates or combining symbols with main/base character.
     * The new resultant value will be the combined values.
     */
    public void combineBoxes() {
        TessBox prev = null; 
        for (TessBox box : list.toArray(new TessBox[list.size()])) {
            if (prev != null && (box.getRect().equals(prev.getRect()) || prev.getRect().contains(box.getRect()))) {
                list.remove(box);
                prev.setChrs(prev.getChrs() + box.getChrs());
            } else if (prev != null && ((appendingSymbols != null && appendingSymbols.trim().length() > 0 && box.getChrs().matches("[" + appendingSymbols + "]"))
                                     || (prependingSymbols != null && prependingSymbols.trim().length() > 0 && prev.getChrs().matches("[" + prependingSymbols + "]")))) {
                list.remove(box);
                prev.setChrs(prev.getChrs() + box.getChrs());
                Rectangle prevRect = prev.getRect();
                Rectangle curRect = box.getRect();
                int minX = Math.min(prevRect.x, curRect.x);
                int minY = Math.min(prevRect.y, curRect.y);
                int maxX = Math.max(prevRect.x + prevRect.width, curRect.x + curRect.width);
                int maxY = Math.max(prevRect.y + prevRect.height, curRect.y + curRect.height);
                prev.setRect(new Rectangle(minX, minY, maxX - minX, maxY - minY));
            } else {
                prev = box;
            }
        }
    }

    /**
     * Removes a box from list.
     * 
     * @param box
     * @return 
     */
    public boolean remove(TessBox box) {
        return list.remove(box);
    }

    /**
     * Removes a box from list by index.
     * 
     * @param index
     * @return 
     */
    public TessBox remove(int index) {
        return list.remove(index);
    }

    /**
     * Gets coordinate data for each page.
     * 
     * @return table data list
     */
    public List<String[]> getTableDataList() {
        List<String[]> dataList = new ArrayList<String[]>();
        for (TessBox box : list) {
            String[] item = new String[5];
            item[0] = box.getChrs();
            Rectangle rect = box.getRect();
            item[1] = String.valueOf(rect.x);
            item[2] = String.valueOf(rect.y);
            item[3] = String.valueOf(rect.width);
            item[4] = String.valueOf(rect.height);
            dataList.add(item);
        }
        return dataList;
    }
}
