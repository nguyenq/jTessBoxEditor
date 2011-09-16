/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.tessboxeditor;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.imageio.ImageIO;
import net.sourceforge.tessboxeditor.datamodel.TessBox;
import net.sourceforge.tessboxeditor.datamodel.TessBoxCollection;

public class TiffBoxGenerator {

    static final String EOL = System.getProperty("line.separator");
    private LineBreakMeasurer lineMeasurer;
    // the first character in the paragraph.
    private int paragraphStart;
    // the first character after the end of the paragraph.
    private int paragraphEnd;
    private final Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
    private AttributedString astr;
    private final List<TessBoxCollection> boxPages = new ArrayList<TessBoxCollection>();
    private final List<BufferedImage> imageList = new ArrayList<BufferedImage>();
    String text;
    Font font;
    int width, height;
    private final int margin = 20;
    
    TiffBoxGenerator(String text, Font font, int width, int height) {
        this.text = text;
        this.font = font;
        this.width = width;
        this.height = height;
    }

    public void create() {
        map.put(TextAttribute.FONT, font);
        astr = new AttributedString(text, map);
        
//        imageList.clear();
        drawImage();
        saveImageBox();
    }

    void drawImage() {
        AttributedCharacterIterator paragraph = astr.getIterator();
        paragraphStart = paragraph.getBeginIndex();
        paragraphEnd = paragraph.getEndIndex();

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = bi.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); //VALUE_TEXT_ANTIALIAS_LCD_HRGB
        // Set formatting width to width of Component.
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, bi.getWidth(), bi.getHeight());
        g2.setColor(Color.black);
        g2.setFont(font);
        float formatWidth = bi.getWidth(); // - 2 * margin;
        float drawPosY = margin;
        // Create a new LineBreakMeasurer from the paragraph.
        lineMeasurer = new LineBreakMeasurer(paragraph, g2.getFontRenderContext());
        lineMeasurer.setPosition(paragraphStart);

        // Get lines from lineMeasurer until the entire
        // paragraph has been displayed.
        while (lineMeasurer.getPosition() < paragraphEnd) {
            // Retrieve next layout.
            TextLayout layout = lineMeasurer.nextLayout(formatWidth);
            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();
            // Compute pen x position. If the paragraph is
            // right-to-left, we want to align the TextLayouts
            // to the right edge of the panel.
            float drawPosX = layout.isLeftToRight()
                    ? margin : formatWidth - layout.getAdvance();
            // Draw the TextLayout at (drawPosX, drawPosY).
            layout.draw(g2, drawPosX, drawPosY);
            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }

        boxPages.clear();
        TessBoxCollection boxCol = new TessBoxCollection();

        // get the visual center of the component.
        int centerX = bi.getWidth() / 2;
        int centerY = bi.getHeight() / 2;

// get the bounds of the string to draw.
        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle stringBounds = fontMetrics.getStringBounds(text, g2).getBounds();

// get the visual bounds of the text using a GlyphVector.

        FontRenderContext renderContext = g2.getFontRenderContext();
        GlyphVector glyphVector = font.createGlyphVector(renderContext, text);
        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
//        Rectangle pixelBounds = glyphVector.getPixelBounds(renderContext, drawPosY, drawPosY).getBounds();
        int num = glyphVector.getNumGlyphs();

// calculate the lower left point at which to draw the string. note that this we
// give the graphics context the y corridinate at which we want the baseline to
// be placed. use the visual bounds height to center on in conjuction with the
// position returned in the visual bounds. the vertical position given back in the
// visualBounds is a negative offset from the basline of the text.
        int textX = centerX - stringBounds.width / 2;
        int textY = centerY - visualBounds.height / 2 - visualBounds.y;

        for (int i = 0; i < num; i++) {
            Point2D p = glyphVector.getGlyphPosition(i);
//            Shape s = glyphVector.getGlyphOutline(i);
//            Shape s = glyphVector.getGlyphLogicalBounds(i);
//             Shape s = glyphVector.getGlyphOutline(i, (float) p.getX(), (float) p.getY());
//            Rectangle s = glyphVector.getGlyphPixelBounds(i, null, (float) p.getX(), (float) p.getY());
            Shape s = glyphVector.getGlyphVisualBounds(i); // too wide
            GlyphMetrics metrics = glyphVector.getGlyphMetrics(i);
//            graphics2D.draw(s);
            int glyphX = (int) p.getX() + textX + (int) metrics.getLSB();
            int glyphY = (int) p.getY() + textY + s.getBounds().y;
            int glyphW = (int) metrics.getBounds2D().getWidth();
            int glyphH = (int) metrics.getBounds2D().getHeight();
            short page = 0;
            String chrs = String.valueOf(text.charAt(i));

            g2.drawRect(glyphX, glyphY, glyphW, glyphH);
//            graphics2D.drawRect((int)p.getX()+textX, (int)p.getY() + textY -s.getBounds().height, s.getBounds().width, s.getBounds().height);

            if (!chrs.equals(" ")) {
                boxCol.add(new TessBox(chrs, new Rectangle(glyphX, glyphY, glyphW, glyphH), page));
//                boxess.add(String.format("%s %d %d %d %d 0", chrs, glyphX, size.height - glyphY - glyphH, glyphX + glyphW, size.height - glyphY));
            }
        }
        
        this.boxPages.add(boxCol);
//        g2.drawString(text, textX, textY);
//        graphics2D.drawRect(textX, textY - (int) pixelBounds.getHeight(), (int) pixelBounds.getWidth(), (int) pixelBounds.getHeight());
        g2.dispose();
        imageList.add(bi);
    }

    String formatOutputString() {
        StringBuilder sb = new StringBuilder();
        for (short i = 0; i < imageList.size(); i++) {
            BufferedImage bi = imageList.get(i);
            int pageHeight = bi.getHeight(); // each page (in an image) can have different height
            for (TessBox box : boxPages.get(i).toList()) {
                Rectangle rect = box.getRect();
//                tightenBoundingBox(rect, bi);
                sb.append(String.format("%s %d %d %d %d %d", box.getChrs(), rect.x, pageHeight - rect.y - rect.height, rect.x + rect.width, pageHeight - rect.y, i)).append(EOL);
            }
        }
//        if (isTess2_0Format) {
//            return sb.toString().replace(" 0" + EOL, EOL); // strip the ending zeroes
//        }
        return sb.toString();
    }

    void saveImageBox() {
        BufferedImage bi = imageList.get(0);

        try {
            ImageIO.write(bi, "png", new File("test.png"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.box"), "UTF8"));
            out.write(formatOutputString());
            out.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
