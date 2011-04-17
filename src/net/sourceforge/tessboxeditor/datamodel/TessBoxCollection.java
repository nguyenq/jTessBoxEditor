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

public class TessBoxCollection {

    private List<TessBox> list;

    public TessBoxCollection() {
        list = new ArrayList<TessBox>();
    }

    public void add(TessBox box) {
        list.add(box);
    }

    public void add(int index, TessBox box) {
        list.add(index, box);
    }

    public void deselectAll() {
        for (TessBox box : list) {
            box.setSelected(false);
        }
    }

    public List<TessBox> toList() {
        return list;
    }

    public TessBox select(TessBox findBox) {
        for (TessBox box : list) {
            if (box.getRect().equals(findBox.getRect())) {
                return box;
            }
        }
        return null;
    }

    public TessBox hitObject(Point p) {
        for (TessBox box : list) {
            if (box.contains(p)) {
                return box;
            }
        }
        return null;
    }

    public List<TessBox> getSelectedBoxes() {
        List<TessBox> selected = new ArrayList<TessBox>();
        for (TessBox box : list) {
            if (box.isSelected()) {
                selected.add(box);
            }
        }
        return selected;
    }

    public boolean remove(TessBox box) {
        return list.remove(box);
    }

    public TessBox remove(int index) {
        return list.remove(index);
    }

    /**
     * Gets coordinate data for each page.
     * @param page
     * @return
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
