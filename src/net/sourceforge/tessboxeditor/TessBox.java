package net.sourceforge.tessboxeditor;

import java.awt.Rectangle;

public class TessBox {

    String ch;
    Rectangle rect;
    short page;
    private boolean selected;

    TessBox(String ch, Rectangle rect, short page) {
        this.ch = ch;
        this.rect = rect;
        this.page = page;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    boolean contains(int x, int y) {
        return this.rect.contains(x, y);
    }

}
