/**
 * Copyright @ 2008 Quan Nguyen
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
package net.sourceforge.tess4j.util;

import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import javax.imageio.metadata.IIOMetadata;
import com.sun.media.imageio.plugins.tiff.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;

public class ImageIOHelper {

    final static String OUTPUT_FILE_NAME = "Tesstmp";
    final static String TIFF_EXT = ".tif";
    final static String TIFF_FORMAT = "tiff";
    final static String JAI_IMAGE_WRITER_MESSAGE = "Need to install JAI Image I/O package.\nhttps://java.net/projects/jai-imageio/";
    final static String JAI_IMAGE_READER_MESSAGE = "Unsupported image format. May need to install JAI Image I/O package.\nhttps://java.net/projects/jai-imageio/";

    /**
     * Gets a list of <code>BufferedImage</code> objects for an image file.
     *
     * @param imageFile input image file. It can be any of the supported
     * formats, including TIFF, JPEG, GIF, PNG, BMP, JPEG
     * @return a list of <code>BufferedImage</code> objects
     * @throws IOException
     */
    public static List<BufferedImage> getImageList(File imageFile) throws IOException {
        ImageReader reader = null;
        ImageInputStream iis = null;

        try {
            List<BufferedImage> biList = new ArrayList<BufferedImage>();

            String imageFileName = imageFile.getName();
            String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
            if (!readers.hasNext()) {
                throw new RuntimeException(JAI_IMAGE_READER_MESSAGE);
            }

            reader = readers.next();

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
     * @param imageFile input image file. It can be any of the supported
     * formats, including TIFF, JPEG, GIF, PNG, BMP, JPEG, and PDF if GPL
     * Ghostscript is installed
     * @return a list of <code>IIOImage</code> objects
     * @throws IOException
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
            if (!readers.hasNext()) {
                throw new RuntimeException(JAI_IMAGE_READER_MESSAGE);
            }

            reader = readers.next();

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
     * Creates a list of TIFF image files from an image file. It basically
     * converts images of other formats to TIFF format, or a multi-page TIFF
     * image to multiple TIFF image files.
     *
     * @param imageFile input image file
     * @param index an index of the page; -1 means all pages, as in a multi-page
     * TIFF image
     * @param preserve preserve compression mode
     * @return a list of TIFF image files
     * @throws IOException
     */
    public static List<File> createTiffFiles(File imageFile, int index, boolean preserve) throws IOException {
        List<File> tiffFiles = new ArrayList<File>();

        String imageFileName = imageFile.getName();
        String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);

        if (!readers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_READER_MESSAGE);
        }

        ImageReader reader = readers.next();

        ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
        reader.setInput(iis);
        //Read the stream metadata
//        IIOMetadata streamMetadata = reader.getStreamMetadata();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);

        if (!preserve) {
            tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED); // not preserve original sizes; decompress
        }

        //Get tif writer and set output to file
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);

        if (!writers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_WRITER_MESSAGE);
        }

        ImageWriter writer = writers.next();

        //Read the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        int imageTotal = reader.getNumImages(true);

        for (int i = 0; i < imageTotal; i++) {
            // all if index == -1; otherwise, only index-th
            if (index == -1 || i == index) {
//                BufferedImage bi = reader.read(i);
//                IIOImage oimage = new IIOImage(bi, null, reader.getImageMetadata(i));
                IIOImage oimage = reader.readAll(i, reader.getDefaultReadParam());
                File tiffFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
                ImageOutputStream ios = ImageIO.createImageOutputStream(tiffFile);
                writer.setOutput(ios);
                writer.write(streamMetadata, oimage, tiffWriteParam);
                ios.close();
                tiffFiles.add(tiffFile);
            }
        }
        writer.dispose();
        reader.dispose();

        return tiffFiles;
    }

    /**
     * Merges multiple images into one TIFF image.
     *
     * @param inputImages an array of image files
     * @param outputTiff the output TIFF file
     * @throws IOException
     */
    public static void mergeTiff(File[] inputImages, File outputTiff) throws IOException {
        List<IIOImage> imageList = new ArrayList<IIOImage>();

        for (File inputImage : inputImages) {
            imageList.addAll(getIIOImageList(inputImage));
        }

        mergeTiff(imageList, outputTiff);
    }

    /**
     * Merges multiple images into one TIFF image.
     *
     * @param inputImages an array of <code>BufferedImage</code>
     * @param outputTiff the output TIFF file
     * @throws IOException
     */
    public static void mergeTiff(BufferedImage[] inputImages, File outputTiff) throws IOException {
        List<IIOImage> imageList = new ArrayList<IIOImage>();

        for (BufferedImage inputImage : inputImages) {
            imageList.add(new IIOImage(inputImage, null, null));
        }

        mergeTiff(imageList, outputTiff);
    }

    /**
     * Merges multiple images into one TIFF image.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param outputTiff the output TIFF file
     * @throws IOException
     */
    public static void mergeTiff(List<IIOImage> imageList, File outputTiff) throws IOException {
        if (imageList == null || imageList.isEmpty()) {
            // if no image
            return;
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        if (!writers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_WRITER_MESSAGE);
        }

        ImageWriter writer = writers.next();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
//        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED); // comment out to preserve original sizes

        //Get the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        ImageOutputStream ios = ImageIO.createImageOutputStream(outputTiff);
        writer.setOutput(ios);

        int dpiX = 300;
        int dpiY = 300;

        for (IIOImage iioImage : imageList) {
            // Get the default image metadata.
            ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromRenderedImage(iioImage.getRenderedImage());
            IIOMetadata imageMetadata = writer.getDefaultImageMetadata(imageType, null);
            imageMetadata = setDPIViaAPI(imageMetadata, dpiX, dpiY);
            iioImage.setMetadata(imageMetadata);
        }

        IIOImage firstIioImage = imageList.remove(0);
        writer.write(streamMetadata, firstIioImage, tiffWriteParam);

        int i = 1;
        for (IIOImage iioImage : imageList) {
            writer.writeInsert(i++, iioImage, tiffWriteParam);
        }
        ios.close();

        writer.dispose();
    }

    /**
     * Set DPI using API.
     */
    private static IIOMetadata setDPIViaAPI(IIOMetadata imageMetadata, int dpiX, int dpiY)
            throws IIOInvalidTreeException {
        // Derive the TIFFDirectory from the metadata.
        TIFFDirectory dir = TIFFDirectory.createFromMetadata(imageMetadata);

        // Get {X,Y}Resolution tags.
        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
        TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
        TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

        // Create {X,Y}Resolution fields.
        TIFFField fieldXRes = new TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL,
                1, new long[][]{{dpiX, 1}});
        TIFFField fieldYRes = new TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL,
                1, new long[][]{{dpiY, 1}});

        // Append {X,Y}Resolution fields to directory.
        dir.addTIFFField(fieldXRes);
        dir.addTIFFField(fieldYRes);

        // Convert to metadata object.
        IIOMetadata metadata = dir.getAsMetadata();

        // Add other metadata.
        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(25.4f / dpiX));
        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(25.4f / dpiY));
        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);
        root.appendChild(dim);
        metadata.mergeTree("javax_imageio_1.0", root);

        return metadata;
    }
}
