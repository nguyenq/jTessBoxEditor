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
}
