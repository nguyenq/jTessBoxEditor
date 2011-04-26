package net.sourceforge.vietocr.utilities;

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


import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;

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
}
