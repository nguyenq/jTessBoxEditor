/**
 * Copyright @ 2016 Quan Nguyen
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
package net.sourceforge.tessboxeditor.utilities;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.sourceforge.vietocr.util.Utils.readTextFile;
import static net.sourceforge.vietocr.util.Utils.writeTextFile;

public class Utils {

    private final static Logger logger = Logger.getLogger(Utils.class.getName());

    /**
     * Creates or updates font_properties file.
     * 
     * @param outputFolder
     * @param fileName
     * @param font
     */
    public static void updateFontProperties(File outputFolder, String fileName, Font font) {
        int index = fileName.indexOf(".");
        File fontpropFile = new File(outputFolder, fileName.substring(0, index) + ".font_properties");
        String fontName = fileName.substring(index + 1, fileName.lastIndexOf(".exp"));

        try {
            if (fontpropFile.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(fontpropFile.getPath()), Charset.defaultCharset());
//                boolean fontNameExist = lines.stream().anyMatch((s) -> s.startsWith(fontName + " ")); // Java 8
                for (String str : lines) {
                    if (str.startsWith(fontName + " ")) {
                        return; // font entry already exists, skip
                    }
                }
            } else {
                fontpropFile.getParentFile().mkdirs();
                fontpropFile.createNewFile();
            }

            //<fontname> <italic> <bold> <fixed> <serif> <fraktur>
            String entry = String.format("%s %s %s %s %s %s\n", fontName, font.isItalic() ? "1" : "0", font.isBold() ? "1" : "0", "0", "0", "0");
            Files.write(Paths.get(fontpropFile.getPath()), entry.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    /**
     * Creates stub empty file, as needed.
     * 
     * @param file
     * @return 
     */
    public static boolean createFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Removes empty boxes from box file created by text2image.
     *
     * @param boxFile
     */
    public static void removeEmptyBoxes(File boxFile) {
        try {
            writeTextFile(boxFile, readTextFile(boxFile).replaceAll("(?m)^\\s+.*\n", ""));
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
