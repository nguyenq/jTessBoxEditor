package net.sourceforge.tessboxeditor;

import java.awt.Rectangle;

public class CharEntity {

    String ch;
    Rectangle rect;
    short page;

    CharEntity(String ch, Rectangle rect, short page) {
        this.ch = ch;
        this.rect = rect;
        this.page = page;
    }
}
