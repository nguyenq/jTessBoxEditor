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

/**
 * Operations on a box.
 */
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
     * Whether the box is selected.
     * 
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Select a box.
     * 
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Whether the box contains a coordinate.
     * 
     * @param x
     * @param y
     * @return 
     */
    boolean contains(int x, int y) {
        return this.rect.contains(x, y);
    }

    /**
     * Whether the box contains a point.
     * 
     * @param p
     * @return 
     */
    boolean contains(Point p) {
        return this.rect.contains(p);
    }

    /**
     * A box information.
     * 
     * @return 
     */
    @Override
    public String toString() {
        return String.format("%s %d %d %d %d %d", chrs, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, page);
    }

    /**
     * Gets box bounding rectangle.
     * 
     * @return the rectangle
     */
    public Rectangle getRect() {
        return rect;
    }
    
    /**
     * Sets box bounding rectangle.
     * 
     * @param rect the rectangle to set
     */
    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    /**
     * Gets box character value.
     * 
     * @return the chrs
     */
    public String getChrs() {
        return chrs;
    }

    /**
     * Sets box character value.
     * 
     * @param chrs the chrs to set
     */
    public void setChrs(String chrs) {
        this.chrs = chrs;
    }

    /**
     * Gets the page the box is in.
     * 
     * @return the page
     */
    public short getPage() {
        return page;
    }

    /**
     * Sets the page the box is in.
     * 
     * @param page the page to set
     */
    public void setPage(short page) {
        this.page = page;
    }
}
