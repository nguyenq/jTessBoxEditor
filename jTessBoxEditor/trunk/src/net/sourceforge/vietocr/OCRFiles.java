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
package net.sourceforge.vietocr;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.vietocr.util.Utils;

/**
 * Invokes Tesseract executable via command-line.
 */
public class OCRFiles extends OCR<File> {

    private final String LANG_OPTION = "-l";
    private final String PSM_OPTION = "-psm";
//    private final String TESSDATA_DIR = "--tessdata-dir"; // Tess 3.04
    private final String tessPath;
    final static String OUTPUT_FILE_NAME = "TessOutput";
    final static String TEXTFILE_EXTENSION = ".txt";

    /**
     * Creates a new instance of OCR
     *
     * @param tessPath
     */
    public OCRFiles(String tessPath) {
        this.tessPath = tessPath;
    }

    /**
     * Recognizes TIFF files.
     *
     * @param tiffFiles
     * @return recognized text
     * @throws Exception
     */
    @Override
    public String recognizeText(final List<File> tiffFiles) throws Exception {
        File tempTessOutputFile = File.createTempFile(OUTPUT_FILE_NAME, TEXTFILE_EXTENSION);
        String outputFileName = Utils.stripExtension(tempTessOutputFile.getPath()); // chop the file extension

        List<String> cmd = new ArrayList<String>();
        cmd.add(tessPath + "/tesseract");
        cmd.add(""); // placeholder for inputfile
        cmd.add(outputFileName);
//        cmd.add(TESSDATA_DIR);
//        cmd.add(getDatapath());
        cmd.add(LANG_OPTION);
        cmd.add(getLanguage());
        cmd.add(PSM_OPTION);
        cmd.add(getPageSegMode());
//        cmd.add(CONFIGS_FILE);

        ProcessBuilder pb = new ProcessBuilder();
        Map<String, String> env = pb.environment();
        env.put("TESSDATA_PREFIX", getDatapath());
        pb.directory(new File(tessPath));
        pb.redirectErrorStream(true);

        StringBuilder result = new StringBuilder();

        for (File tiffFile : tiffFiles) {
            cmd.set(1, tiffFile.getPath());
            pb.command(cmd);
//            System.out.println(cmd);
            Process process = pb.start();
            // any error message?
            // this has become unneccesary b/c the standard error is already merged with the standard output
//            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
//            errorGobbler.start();
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
            outputGobbler.start();

            int w = process.waitFor();
            System.out.println("Exit value = " + w);

            if (w == 0) {
                String str = Utils.readTextFile(tempTessOutputFile);
                result.append(str);
            } else {
                tempTessOutputFile.delete();
                String msg = outputGobbler.getMessage(); // get actual message from the engine;
                if (msg.trim().length() == 0) {
                    msg = "Errors occurred.";
                }
                throw new RuntimeException(msg);
            }
        }

        tempTessOutputFile.delete();
        return result.toString();
    }

    /**
     * Processes OCR for input file with specified output format.
     *
     * @param inputImage
     * @param outputFile
     * @throws Exception
     */
    @Override
    public void processPages(File inputImage, File outputFile) throws Exception {
        // not used
    }
}

/**
 * When Runtime.exec() won't.
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 */
class StreamGobbler extends Thread {

    InputStream is;
    StringBuilder outputMessage = new StringBuilder();

    private final static Logger logger = Logger.getLogger(StreamGobbler.class.getName());

    StreamGobbler(InputStream is) {
        this.is = is;
    }

    String getMessage() {
        return outputMessage.toString();
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                outputMessage.append(line).append("\n");
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }
}
