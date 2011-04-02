package net.sourceforge.tessboxeditor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TessBoxCollection {

    List<TessBox> list;

    public TessBoxCollection() {
        list = new ArrayList<TessBox>();
    }

    void addBox(TessBox box) {
        list.add(box);
    }

    void addBox(int index, TessBox box) {
        list.add(index, box);
    }

    void deselectAll() {
        for (TessBox box : list) {
            box.setSelected(false);
        }
    }

    List<TessBox> toList() {
        return list;
    }

    void clear() {
        list.clear();
    }

    TessBox hitObject(Point p) {
        return hitObject(p.x, p.y);
    }

    TessBox hitObject(int x, int y) {
        for (TessBox box : list) {
            if (box.contains(x, y)) {
                return box;
            }
        }
        return null;
    }

    List<TessBox> getSelectedBoxes() {
        List<TessBox> selected = new ArrayList<TessBox>();
        for (TessBox box : list) {
            if (box.isSelected()) {
                selected.add(box);
            }
        }
        return selected;
    }

    boolean remove(TessBox box) {
        return list.remove(box);
    }

    List<String[]> getTableDataList() {
        List<String[]> dataList = new ArrayList<String[]>();
        for (TessBox box : list) {
            String[] item = new String[5];
            item[0] = box.ch;
            item[1] = String.valueOf(box.rect.x);
            item[2] = String.valueOf(box.rect.y);
            item[3] = String.valueOf(box.rect.x + box.rect.width);
            item[4] = String.valueOf(box.rect.y + box.rect.height);
            dataList.add(item);
        }
        return dataList;
    }
}
