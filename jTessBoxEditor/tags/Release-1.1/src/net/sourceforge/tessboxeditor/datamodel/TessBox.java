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

public class TessBox {

    private String chrs;
    private Rectangle rect;
    private short page;
    private boolean selected;

    public TessBox(String chrs, Rectangle rect, short page) {
        this.chrs = chrs;
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

    boolean contains(Point p) {
        return this.rect.contains(p);
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %d %d %d", chrs, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, page);
    }

    /**
     * @return the rectangle
     */
    public Rectangle getRect() {
        return rect;
    }
    
    /**
     * @param rect the rectangle to set
     */
    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    /**
     * @return the chrs
     */
    public String getChrs() {
        return chrs;
    }

    /**
     * @param chrs the chrs to set
     */
    public void setChrs(String chrs) {
        this.chrs = chrs;
    }

    /**
     * @return the page
     */
    public short getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(short page) {
        this.page = page;
    }
}
