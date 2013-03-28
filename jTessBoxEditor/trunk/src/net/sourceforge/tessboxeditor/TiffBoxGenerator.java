/**
 * Copyright @ 2009 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tessboxeditor;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sourceforge.tessboxeditor.datamodel.TessBox;
import net.sourceforge.tessboxeditor.datamodel.TessBoxCollection;
import net.sourceforge.vietocr.utilities.ImageIOHelper;
import net.sourceforge.vietocr.utilities.Utilities;
import net.sourceforge.vietpad.utilities.TextUtilities;

public class TiffBoxGenerator {

    static final String EOL = System.getProperty("line.separator");
    private final HashMap<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
    private final List<TessBoxCollection> boxPages = new ArrayList<TessBoxCollection>();
    private String text;
    private Font font;
    private int width, height;
    private final int margin = 100;
    private final List<ArrayList<TextLayout>> layouts = new ArrayList<ArrayList<TextLayout>>();
    private final List<BufferedImage> pages = new ArrayList<BufferedImage>();
    private String fileName = "fontname.exp0";
    private File outputFolder;
    private final int COLOR_WHITE = Color.WHITE.getRGB();
    private float tracking = TextAttribute.TRACKING_LOOSE; // 0.04
    private boolean isAntiAliased;
    private final File baseDir = Utilities.getBaseDir(TiffBoxGenerator.this);

    public TiffBoxGenerator(String text, Font font, int width, int height) {
        this.text = text;
        this.font = font;
        this.width = width;
        this.height = height;
    }

    public void create() {
        map.put(TextAttribute.FAMILY, font.getName());
        map.put(TextAttribute.SIZE, font.getSize());
        if (font.getStyle() == (Font.BOLD | Font.ITALIC)) {
            map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            map.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        } else if (font.getStyle() == Font.BOLD) {
            map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        } else if (font.getStyle() == Font.ITALIC) {
            map.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }
        map.put(TextAttribute.TRACKING, tracking);

        this.breakLines();
        this.drawPages();
        this.saveMultipageTiff();
        this.saveBoxFile();
    }

//    private void drawImage() {
//        AttributedCharacterIterator paragraph = astr.getIterator();
//        int paragraphStart = paragraph.getBeginIndex();
//
//        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//        Graphics2D g2 = bi.createGraphics();
//
//        // Create a new LineBreakMeasurer from the paragraph.
//        lineMeasurer = new LineBreakMeasurer(paragraph, g2.getFontRenderContext());
//        lineMeasurer.setPosition(paragraphStart);
//
//        boxPages.clear();
//        TessBoxCollection boxCol = new TessBoxCollection();
//
//        // get the visual center of the component.
//        int centerX = bi.getWidth() / 2;
//        int centerY = bi.getHeight() / 2;
//
//// get the bounds of the string to draw.
//        FontMetrics fontMetrics = g2.getFontMetrics();
//        Rectangle stringBounds = fontMetrics.getStringBounds(text, g2).getBounds();
//
//// get the visual bounds of the text using a GlyphVector.
//
//        FontRenderContext renderContext = g2.getFontRenderContext();
//        GlyphVector glyphVector = font.createGlyphVector(renderContext, text);
//        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
////        Rectangle pixelBounds = glyphVector.getPixelBounds(renderContext, drawPosY, drawPosY).getBounds();
//        int num = glyphVector.getNumGlyphs();
//
//// calculate the lower left point at which to draw the string. note that this we
//// give the graphics context the y corridinate at which we want the baseline to
//// be placed. use the visual bounds height to center on in conjuction with the
//// position returned in the visual bounds. the vertical position given back in the
//// visualBounds is a negative offset from the basline of the text.
//        int textX = centerX - stringBounds.width / 2;
//        int textY = centerY - visualBounds.height / 2 - visualBounds.y;
//
//        for (int i = 0; i < num; i++) {
//            Point2D p = glyphVector.getGlyphPosition(i);
////            Shape s = glyphVector.getGlyphOutline(i);
////            Shape s = glyphVector.getGlyphLogicalBounds(i);
////             Shape s = glyphVector.getGlyphOutline(i, (float) p.getX(), (float) p.getY());
////            Rectangle s = glyphVector.getGlyphPixelBounds(i, null, (float) p.getX(), (float) p.getY());
//            Shape s = glyphVector.getGlyphVisualBounds(i); // too wide
//            GlyphMetrics metrics = glyphVector.getGlyphMetrics(i);
////            graphics2D.draw(s);
//            int glyphX = (int) p.getX() + textX + (int) metrics.getLSB();
//            int glyphY = (int) p.getY() + textY + s.getBounds().y;
//            int glyphW = (int) metrics.getBounds2D().getWidth();
//            int glyphH = (int) metrics.getBounds2D().getHeight();
//            short page = 0;
//            String chrs = String.valueOf(text.charAt(i));
//
//            g2.drawRect(glyphX, glyphY, glyphW, glyphH);
////            graphics2D.drawRect((int)p.getX()+textX, (int)p.getY() + textY -s.getBounds().height, s.getBounds().width, s.getBounds().height);
//
//            if (!chrs.equals(" ")) {
//                boxCol.add(new TessBox(chrs, new Rectangle(glyphX, glyphY, glyphW, glyphH), page));
////                boxess.add(String.format("%s %d %d %d %d 0", chrs, glyphX, size.height - glyphY - glyphH, glyphX + glyphW, size.height - glyphY));
//            }
//        }
//
//        this.boxPages.add(boxCol);
////        g2.drawString(text, textX, textY);
////        graphics2D.drawRect(textX, textY - (int) pixelBounds.getHeight(), (int) pixelBounds.getWidth(), (int) pixelBounds.getHeight());
//        g2.dispose();
//    }
    
    /**
     * Formats box content.
     * 
     * @return 
     */
    private String formatOutputString() {
        StringBuilder sb = new StringBuilder();
        String combiningSymbols = readCombiningSymbols(); //"\u0c82\u0c83\u0cbe\u0cbf\u0cc0-\u0ccc";
        for (short i = 0; i < pages.size(); i++) {
            TessBoxCollection boxCol = boxPages.get(i);
            boxCol.setCombiningSymbols(combiningSymbols); 
            boxCol.combineBoxes();
            for (TessBox box : boxCol.toList()) {
                Rectangle rect = box.getRect();
                sb.append(String.format("%s %d %d %d %d %d", box.getChrs(), rect.x, height - rect.y - rect.height, rect.x + rect.width, height - rect.y, i)).append(EOL);
            }
        }
//        if (isTess2_0Format) {
//            return sb.toString().replace(" 0" + EOL, EOL); // strip the ending zeroes
//        }
        return sb.toString();
    }
    
    /**
     * Reads in combining symbols.
     * 
     * @return 
     */
    private String readCombiningSymbols() {
        String str = null;
        try {
            File xmlFile = new File(baseDir, "data/combiningsymbols.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(xmlFile), "UTF8"));
            while ((str = in.readLine()) != null) { // while loop begins here
                if (!str.trim().startsWith("#")) {
                    break;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        
        str = str.replaceAll("[ \\[\\]]", "");
        return TextUtilities.convertNCR(str); // convert escaped sequences to Unicode
    }

    /**
     * Tightens bounding box in four directions b/c Java cannot produce bounding
     * boxes as tight as Tesseract can. Exam only the first pixel on each side.
     *
     * @param rect
     * @param bi
     */
    private void tightenBoundingBox(Rectangle rect, BufferedImage bi) {
        // left
        int endX = rect.x + 2;
        outerLeft:
        for (int x = rect.x; x < endX; x++) {
            for (int y = rect.y; y < rect.y + rect.height; y++) {
                int color = bi.getRGB(x, y);
                if (color != COLOR_WHITE) {
                    break outerLeft;
                }
            }
            rect.x++;
            rect.width--;
        }

        // right
        endX = rect.x + rect.width - 4;
        outerRight:
        for (int x = rect.x + rect.width - 1; x > endX; x--) {
            for (int y = rect.y; y < rect.y + rect.height; y++) {
                int color = bi.getRGB(x, y);
                if (color != COLOR_WHITE) {
                    break outerRight;
                }
            }
            rect.width--;
        }

        //TODO: Need to account for Java's incorrect over-tightening the top of the bounding box
        // Need to move the top up by 1px and increase the height by 1px
        // top
        int endY = rect.y + 3;
        int startY = rect.y - 1;
        outerTop:
        for (int y = startY; y < endY; y++) {
            for (int x = rect.x; x < rect.x + rect.width; x++) {
                int color = bi.getRGB(x, y);
                if (color != COLOR_WHITE) {
                    if (y == startY) {
                        rect.y--;
                        rect.height++;
                        continue outerTop;
                    } else {
                        break outerTop;
                    }
                }
            }
            if (y != startY) {
                rect.y++;
            }
        }

        // bottom
        endY = rect.y + rect.height - 4;
        outerBottom:
        for (int y = rect.y + rect.height - 1; y > endY; y--) {
            for (int x = rect.x; x < rect.x + rect.width; x++) {
                int color = bi.getRGB(x, y);
                if (color != COLOR_WHITE) {
                    break outerBottom;
                }
            }
            rect.height--;
        }
    }

//    private void makeBoxes() {
//        boxPages.clear();
//
//        for (String str : text.split("\n")) {
//            TessBoxCollection boxCol = new TessBoxCollection();
//
//            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//            Graphics2D g2 = bi.createGraphics();
//            FontRenderContext renderContext = g2.getFontRenderContext();
//            GlyphVector glyphVector = font.createGlyphVector(renderContext, str);
////        Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();
////        Rectangle pixelBounds = glyphVector.getPixelBounds(renderContext, drawPosY, drawPosY).getBounds();
//            int num = glyphVector.getNumGlyphs();
//            for (int i = 0; i < num; i++) {
//                Point2D p = glyphVector.getGlyphPosition(i);
////            Shape s = glyphVector.getGlyphOutline(i);
////            Shape s = glyphVector.getGlyphLogicalBounds(i);
////             Shape s = glyphVector.getGlyphOutline(i, (float) p.getX(), (float) p.getY());
////            Rectangle s = glyphVector.getGlyphPixelBounds(i, null, (float) p.getX(), (float) p.getY());
//                Shape s = glyphVector.getGlyphVisualBounds(i); // too wide
//                GlyphMetrics metrics = glyphVector.getGlyphMetrics(i);
////            graphics2D.draw(s);
//                int glyphX = (int) p.getX() + margin + (int) metrics.getLSB();
//                int glyphY = (int) p.getY() + margin + s.getBounds().y;
//                int glyphW = (int) metrics.getBounds2D().getWidth();
//                int glyphH = (int) metrics.getBounds2D().getHeight();
//                short page = 0;
//                String chrs = String.valueOf(text.charAt(i));
//
//                g2.drawRect(glyphX, glyphY, glyphW, glyphH);
////            graphics2D.drawRect((int)p.getX()+textX, (int)p.getY() + textY -s.getBounds().height, s.getBounds().width, s.getBounds().height);
//
//                if (!chrs.equals(" ")) {
//                    boxCol.add(new TessBox(chrs, new Rectangle(glyphX, glyphY, glyphW, glyphH), page));
////                boxess.add(String.format("%s %d %d %d %d 0", chrs, glyphX, size.height - glyphY - glyphH, glyphX + glyphW, size.height - glyphY));
//                }
//            }
////            this.boxPages.add(boxCol);
//        }
//    }
    
    /**
     * Creates box file.
     */
    private void saveBoxFile() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFolder, fileName + ".box")), "UTF8")); // save boxes
            out.write(formatOutputString());
            out.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Breaks input text into TextLayout lines.
     */
    private void breakLines() {
        // Bug ID: 6598756 - Tracking textattribute is not properly handled by LineBreakMeasurer
        // Line breaks do not change with letter tracking.
        float wrappingWidth = width - 2 * margin - (float) (150 * (tracking / 0.04)); // the last operand was added to compensate for LineBreakMeasurer's failure to adjust for letter tracking
        
        for (String str : text.split("\n")) {
            if (str.length() == 0) {
                str = " ";
            }
            final AttributedString attStr = new AttributedString(str, map);
            final LineBreakMeasurer measurer = new LineBreakMeasurer(attStr.getIterator(), new FontRenderContext(null, isAntiAliased, true));

            ArrayList<TextLayout> para = new ArrayList<TextLayout>();
            TextLayout line;

            while ((line = measurer.nextLayout(wrappingWidth)) != null) {
                para.add(line);
            }
            layouts.add(para); // collection of paragraphs (long strings) of lines
        }
    }

    /**
     * Creates graphics object with specific settings.
     *
     * @param bi image
     * @param font font
     * @return
     */
    private Graphics2D createGraphics(BufferedImage bi, Font font) {
        Graphics2D g2 = bi.createGraphics();
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, bi.getWidth(), bi.getHeight());
        g2.setColor(Color.black);
        g2.setFont(font);
        return g2;
    }

    /**
     * Draws TextLayout lines on pages of
     * <code>BufferedImage</code>
     */
    private void drawPages() {
        BufferedImage bi = new BufferedImage(width, height, isAntiAliased ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_BYTE_BINARY);
        pages.add(bi);
        Graphics2D g2 = createGraphics(bi, font);

        TessBoxCollection boxCol = new TessBoxCollection(); // for each page
        boxPages.add(boxCol);
        short pageNum = 0;
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

                // TextLayout API does not expose a way to access the underlying string.
                String lineText = line.toString();
                int startPos = lineText.indexOf("chars:\"") + "chars:\"".length();
                lineText = lineText.substring(startPos, lineText.indexOf("\",", startPos));
                String[] chars = lineText.split("\\s+");

                // get bounding box for each character on a line
                int c = line.getCharacterCount();
                for (int i = 0; i < c; i++) {
                    Shape shape = line.getBlackBoxBounds(i, i + 1);
                    Rectangle rect = shape.getBounds();
                    if (rect.width == 0 || rect.height == 0) {
                        continue;
                    }
                    rect.x += drawPosX;
                    rect.y += drawPosY;

                    try {
                        tightenBoundingBox(rect, bi);
                    } catch (java.lang.ArrayIndexOutOfBoundsException aie) {
                        // ignore
                    }
                    char ch = (char) Integer.parseInt(chars[i], 16);
                    boxCol.add(new TessBox(String.valueOf(ch), rect, pageNum));
                }

                // Move y-coordinate in preparation for next layout.
                drawPosY += 2 * line.getDescent() + line.getLeading(); // factor 2 for larger line spacing

                // Reach bottom margin?
                if (drawPosY > height - margin) { // - line.getAscent() ?
                    drawPosY = margin; // reset to top margin of next page
                    bi = new BufferedImage(width, height, isAntiAliased ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_BYTE_BINARY);
                    pages.add(bi);
                    boxCol = new TessBoxCollection();
                    boxPages.add(boxCol);
                    pageNum++;
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
    private void saveMultipageTiff() {
        try {
            File tiffFile = new File(outputFolder, fileName + ".tif");
            tiffFile.delete();
            ImageIOHelper.mergeTiff(pages.toArray(new BufferedImage[pages.size()]), tiffFile);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sets output filename.
     * 
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        if (fileName != null && fileName.length() > 0) {
            int index = fileName.lastIndexOf(".");
            this.fileName = index > -1 ? fileName.substring(0, index) : fileName;
        }
    }

    /**
     * Sets letter tracking.
     * 
     * @param tracking the tracking to set
     */
    public void setTracking(float tracking) {
        this.tracking = tracking;
    }

    /**
     * Sets output folder.
     * 
     * @param outputFolder the outputFolder to set
     */
    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * Enables text anti-aliasing.
     * 
     * @param enabled on or off
     */
    public void setAntiAliasing(boolean enabled) {
        this.isAntiAliased = enabled;
    }
}
