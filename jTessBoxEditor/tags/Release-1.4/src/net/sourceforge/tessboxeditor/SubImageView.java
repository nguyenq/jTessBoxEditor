package net.sourceforge.tessboxeditor;

import net.sourceforge.tessboxeditor.components.ImageIconScalable;

import javax.swing.*;
import java.awt.*;

/**
 * View of individual box.
 *
 * @author A2K
 */
public class SubImageView extends JLabel {

    public static final String TAG = SubImageView.class.getName();

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        if (g == null || g2d == null) {
            return;
        }

        g2d.setColor(Color.GREEN);

        ImageIconScalable icon = (ImageIconScalable) this.getIcon();

        if (icon == null) {
            return;
        }

        int iconActualWidth = icon.getIconWidth() / Gui.scaleFactor;
        int scaledPixelSize = icon.getIconWidth() / iconActualWidth;
        int offset = scaledPixelSize * Gui.iconMargin;

        // top left
        int x1 = (this.getWidth() - icon.getIconWidth()) / 2;
        int y1 = (this.getHeight() - icon.getIconHeight()) / 2;

        // bottom right
        int x2 = (this.getWidth() + this.getIcon().getIconWidth()) / 2;
        int y2 = (this.getHeight() + this.getIcon().getIconHeight()) / 2;

//        System.out.println("icon pos: " + Gui.iconPosX);

        if (Gui.iconPosX < Gui.iconMargin) {
            int shift = (Gui.iconMargin - Gui.iconPosX) * Gui.scaleFactor;
            x1 -= shift;
            x2 -= shift;
        }

        if (Gui.iconPosY < Gui.iconMargin) {
            int shift = (Gui.iconMargin - Gui.iconPosY) * Gui.scaleFactor;
            y1 -= shift;
            y2 -= shift;
        }

//        System.out.println("icon pos: " + Gui.iconPosX + ", width=" + Gui.iconWidth + ", image width=" + Gui.imageWidth);

        if ((Gui.imageWidth - (Gui.iconPosX + Gui.iconWidth) - 1) <= Gui.iconMargin) {
            int shift = (Gui.iconMargin - (Gui.imageWidth - (Gui.iconPosX + Gui.iconWidth)) + 2) * Gui.scaleFactor;
            //x1 += shift;
            x2 += shift;
        }

        if ((Gui.imageHeight - (Gui.iconPosY + Gui.iconHeight) - 1) <= Gui.iconMargin) {
            int shift = (Gui.iconMargin - (Gui.imageHeight - (Gui.iconPosY + Gui.iconHeight)) + 2) * Gui.scaleFactor;
            //x1 += shift;
            y2 += shift;
        }


        /*
         if (Gui.iconPosY == 0) {
         x1 -= 2;
         x2 -= 2;
         }
         if (Gui.iconPosX == 1) {
         x1 -= 1;
         x2 -= 1;
         }
         */
        // left
        g2d.drawLine(x1 + offset, y1 + offset, x1 + offset, y2 - offset);
        // top
        g2d.drawLine(x1 + offset, y1 + offset, x2 - offset, y1 + offset);
        // right
        g2d.drawLine(x2 - offset, y1 + offset, x2 - offset, y2 - offset);
        // bottom
        g2d.drawLine(x1 + offset, y2 - offset, x2 - offset, y2 - offset);

        //System.out.println("insets: left=" + this.getInsets().left
        //                + " right=" + this.getInsets().right);
        //        g.drawLine(0, 0, 100, 100);
    }
}
