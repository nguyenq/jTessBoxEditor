/**
 * Copyright @ 2008 Quan Nguyen
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
package net.sourceforge.vietocr.utilities;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import javax.imageio.metadata.IIOMetadata;
import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;

public class ImageIOHelper {

    final static String OUTPUT_FILE_NAME = "Tesstmp";
    final static String TIFF_EXT = ".tif";
    final static String TIFF_FORMAT = "tiff";

    /**
     * Gets a list of <code>BufferedImage</code> objects for an image file.
     *
     * @param imageFile input image file. It can be any of the supported formats, including TIFF, JPEG, GIF, PNG, BMP, JPEG
     * @return a list of <code>BufferedImage</code> objects
     * @throws Exception
     */
    public static List<BufferedImage> getImageList(File imageFile) throws IOException {
        ImageReader reader = null;
        ImageInputStream iis = null;

        try {
            List<BufferedImage> biList = new ArrayList<BufferedImage>();

            String imageFileName = imageFile.getName();
            String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
            reader = readers.next();

            if (reader == null) {
                throw new RuntimeException("Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
            }

            iis = ImageIO.createImageInputStream(imageFile);
            reader.setInput(iis);

            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
                BufferedImage bi = reader.read(i);
                biList.add(bi);
            }

            return biList;
        } finally {
            try {
                if (iis != null) {
                    iis.close();
                }
                if (reader != null) {
                    reader.dispose();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Gets a list of <code>IIOImage</code> objects for an image file.
     *
     * @param imageFile input image file. It can be any of the supported formats, including TIFF, JPEG, GIF, PNG, BMP, JPEG, and PDF if GPL Ghostscript is installed
     * @return a list of <code>IIOImage</code> objects
     * @throws Exception
     */
    public static List<IIOImage> getIIOImageList(File imageFile) throws IOException {
        ImageReader reader = null;
        ImageInputStream iis = null;

        try {
            List<IIOImage> iioImageList = new ArrayList<IIOImage>();

            String imageFileName = imageFile.getName();
            String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
            if (imageFormat.matches("(pbm|pgm|ppm)")) {
                imageFormat = "pnm";
            } else if (imageFormat.equals("jp2")) {
                imageFormat = "jpeg2000";
            }
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
            reader = readers.next();

            if (reader == null) {
                throw new RuntimeException("Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
            }

            iis = ImageIO.createImageInputStream(imageFile);
            reader.setInput(iis);

            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
//                IIOImage oimage = new IIOImage(reader.read(i), null, reader.getImageMetadata(i));
                IIOImage oimage = reader.readAll(i, reader.getDefaultReadParam());
                iioImageList.add(oimage);
            }

            return iioImageList;
        } finally {
            try {
                if (iis != null) {
                    iis.close();
                }
                if (reader != null) {
                    reader.dispose();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Merges multiple images into one TIFF image.
     * 
     * @param inputImages an array of image files
     * @param outputTiff the output TIFF file
     * @throws Exception
     */
    public static void mergeTiff(File[] inputImages, File outputTiff) throws IOException {
        List<IIOImage> imageList = new ArrayList<IIOImage>();

        for (int i = 0; i < inputImages.length; i++) {
            imageList.addAll(getIIOImageList(inputImages[i]));
        }

        if (imageList.isEmpty()) {
            // if no image
            return;
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        ImageWriter writer = writers.next();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

        //Get the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        ImageOutputStream ios = ImageIO.createImageOutputStream(outputTiff);
        writer.setOutput(ios);

        IIOImage firstIioImage = imageList.remove(0);
        writer.write(streamMetadata, firstIioImage, tiffWriteParam);

        int i = 1;
        for (IIOImage iioImage : imageList) {
            writer.writeInsert(i++, iioImage, tiffWriteParam);
        }
        ios.close();

        writer.dispose();
    }
}
