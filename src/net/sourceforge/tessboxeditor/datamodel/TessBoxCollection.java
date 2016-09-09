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
