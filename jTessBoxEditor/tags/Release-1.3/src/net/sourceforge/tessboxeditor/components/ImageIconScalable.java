package net.sourceforge.tessboxeditor.components;

/**
 * Core Java Technologies Tech Tips, February 20, 2003: Providing a Scalable Image Icon
 * http://java.sun.com/developer/JDCTechTips/2003/tt0220.html#2
 */
import java.awt.*;
import javax.swing.*;
import java.net.*;

public class ImageIconScalable extends ImageIcon {

    private int width = -1;
    private int height = -1;

    public ImageIconScalable() {
        super();
    }

    public ImageIconScalable(byte imageData[]) {
        super(imageData);
    }

    public ImageIconScalable(byte imageData[], String description) {
        super(imageData, description);
    }

    public ImageIconScalable(Image image) {
        super(image);
    }

    public ImageIconScalable(Image image, String description) {
        super(image, description);
    }

    public ImageIconScalable(String filename) {
        super(filename);
    }

    public ImageIconScalable(String filename, String description) {
        super(filename, description);
    }

    public ImageIconScalable(URL location) {
        super(location);
    }

    public ImageIconScalable(URL location, String description) {
        super(location, description);
    }

    @Override
    public int getIconHeight() {
        int returnValue;
        if (height == -1) {
            returnValue = super.getIconHeight();
        } else {
            returnValue = height;
        }
        return returnValue;
    }

    @Override
    public int getIconWidth() {
        int returnValue;
        if (width == -1) {
            returnValue = super.getIconWidth();
        } else {
            returnValue = width;
        }
        return returnValue;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Image image = this.getImage();
        if (image == null) {
            return;
        }
        if ((width == -1) && (height == -1)) {
            g.drawImage(image, x, y, c);
        } else {
            g.drawImage(image, x, y, width, height, c);
        }
    }

    public void setScaledFactor(int factor) {
        this.width = getIconWidth() * factor;
        this.height = getIconHeight() * factor;
    }
}
