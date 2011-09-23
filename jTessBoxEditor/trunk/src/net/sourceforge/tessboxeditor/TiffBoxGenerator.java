/**
 * Copyright @ 2009 Quan Nguyen
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
package net.sourceforge.tessboxeditor;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sourceforge.tessboxeditor.datamodel.TessBox;
import net.sourceforge.tessboxeditor.datamodel.TessBoxCollection;
import net.sourceforge.vietocr.utilities.ImageIOHelper;

public class TiffBoxGenerator {

    static final String EOL = System.getProperty("line.separator");
    private LineBreakMeasurer lineMeasurer;
    private final HashMap<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
    private AttributedString astr;
    private final List<TessBoxCollection> boxPages = new ArrayList<TessBoxCollection>();
    private String text;
    private Font font;
    private int width, height;
    private final int margin = 50;
    private final List<ArrayList<TextLayout>> layouts = new ArrayList<ArrayList<TextLayout>>();
    private final List<BufferedImage> pages = new ArrayList<BufferedImage>();
    private String fileName = "test";
    
    TiffBoxGenerator(String text, Font font, int width, int height) {
        this.text = text;
        this.font = font;
        this.width = width;
        this.height = height;
    }

    public void create() {
        map.put(TextAttribute.FONT, font);
        astr = new AttributedString(text, map);

        this.breakLines();
        this.drawPages();
        this.saveMultipageTiff();
        makeBoxes();
        saveBoxFile();
    }

    void drawImage() {
        AttributedCharacterIterator paragraph = astr.getIterator();
        int paragraphStart = paragraph.getBeginIndex();

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = bi.createGraphics();

        // Create a new LineBreakMeasurer from the paragraph.
        lineMeasurer = new LineBreakMeasurer(paragraph, g2.getFontRenderContext());
        lineMeasurer.setPosition(paragraphStart);

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
    }

    String formatOutputString() {
        StringBuilder sb = new StringBuilder();
        for (short i = 0; i < pages.size(); i++) {
            for (TessBox box : boxPages.get(i).toList()) {
                Rectangle rect = box.getRect();
                sb.append(String.format("%s %d %d %d %d %d", box.getChrs(), rect.x, height - rect.y - rect.height, rect.x + rect.width, height - rect.y, i)).append(EOL);
            }
        }
//        if (isTess2_0Format) {
//            return sb.toString().replace(" 0" + EOL, EOL); // strip the ending zeroes
//        }
        return sb.toString();
    }

    void makeBoxes() {
        boxPages.clear();

        for (String str : text.split("\n")) {
            TessBoxCollection boxCol = new TessBoxCollection();

            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2 = bi.createGraphics();
            FontRenderContext renderContext = g2.getFontRenderContext();
            GlyphVector glyphVector = font.createGlyphVector(renderContext, str);
//        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
//        Rectangle pixelBounds = glyphVector.getPixelBounds(renderContext, drawPosY, drawPosY).getBounds();
            int num = glyphVector.getNumGlyphs();
            for (int i = 0; i < num; i++) {
                Point2D p = glyphVector.getGlyphPosition(i);
//            Shape s = glyphVector.getGlyphOutline(i);
//            Shape s = glyphVector.getGlyphLogicalBounds(i);
//             Shape s = glyphVector.getGlyphOutline(i, (float) p.getX(), (float) p.getY());
//            Rectangle s = glyphVector.getGlyphPixelBounds(i, null, (float) p.getX(), (float) p.getY());
                Shape s = glyphVector.getGlyphVisualBounds(i); // too wide
                GlyphMetrics metrics = glyphVector.getGlyphMetrics(i);
//            graphics2D.draw(s);
                int glyphX = (int) p.getX() + margin + (int) metrics.getLSB();
                int glyphY = (int) p.getY() + margin + s.getBounds().y;
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
        }
    }

    void saveBoxFile() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName + ".box"), "UTF8")); // save boxes
            out.write(formatOutputString());
            out.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Breaks input text into TextLayout lines.
     */
    void breakLines() {
        for (String str : text.split("\n")) {
            if (str.length() == 0) {
                str = " ";
            }
            final AttributedString attStr = new AttributedString(str, map);
            final LineBreakMeasurer measurer = new LineBreakMeasurer(attStr.getIterator(), new FontRenderContext(null, true, true));

            ArrayList<TextLayout> para = new ArrayList<TextLayout>();
            TextLayout line;

            while ((line = measurer.nextLayout(width - 2 * margin)) != null) {
                para.add(line);
            }
            layouts.add(para); // collection of paragraphs (long strings) of lines
        }
    }

    /**
     * Creates graphics with specific settings.
     * 
     * @param bi
     * @return 
     */
    Graphics2D createGraphics(BufferedImage bi, Font font) {
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, bi.getWidth(), bi.getHeight());
        g2.setColor(Color.black);
        g2.setFont(font);
        return g2;
    }

    /**
     * Draws TextLayout lines on pages of <code>BufferedImage</code>
     */
    void drawPages() {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        pages.add(bi);
        Graphics2D g2 = createGraphics(bi, font);

        int drawPosY = margin;

        for (ArrayList<TextLayout> para : layouts) {
            for (TextLayout line : para) {
                // Move y-coordinate by the ascent of the layout.
                drawPosY += line.getAscent();
                // Compute pen x position. If the paragraph is
                // right-to-left, we want to align the TextLayouts
                // to the right margin.
                float drawPosX = line.isLeftToRight()
                        ? margin : width - margin - line.getAdvance();
                // Draw the TextLayout at (drawPosX, drawPosY).
                line.draw(g2, drawPosX, drawPosY);
                // Move y-coordinate in preparation for next layout.
                drawPosY += line.getDescent() + line.getLeading();

                // Reach bottom margin?
                if (drawPosY > height - margin) { // - line.getAscent() ?
                    drawPosY = margin; // reset to top margin of next page
                    bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                    pages.add(bi);
                    g2.dispose();
                    g2 = createGraphics(bi, font);
                }
            }
        }

        g2.dispose();
    }

    /**
     * Creates a multi-page TIFF image.
     */
    void saveMultipageTiff() {
        try {
            ImageIOHelper.mergeTiff(pages.toArray(new BufferedImage[pages.size()]), new File(fileName + ".tif"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
