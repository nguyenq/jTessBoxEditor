/**
 * Copyright @ 2013 Quan Nguyen
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

import java.io.*;
import java.util.*;
import net.sourceforge.vietpad.utilities.TextUtilities;

public class TessTrainer {

    private final String cmdMakeBox = "tesseract imageFile boxFile -l bootstrapLang batch.nochop makebox";
    private final String cmdtesseract = "tesseract [lang].[fontname].exp[num].tif [lang].[fontname].exp[num] box.train";
    private final String cmdunicharset_extractor = "unicharset_extractor lang.fontname.exp0.box lang.fontname.exp1.box ...";
    private final String cmdshapeclustering = "shapeclustering -F font_properties -U unicharset lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private final String cmdmftraining = "mftraining -F font_properties -U unicharset -O lang.unicharset lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private final String cmdcntraining = "cntraining lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private final String cmdwordlist2dawg = "wordlist2dawg frequent_words_list lang.freq-dawg lang.unicharset";
    private final String cmdwordlist2dawg2 = "wordlist2dawg words_list lang.word-dawg lang.unicharset";
    private final String cmdcombine_tessdata = "combine_tessdata lang.";
    
    ProcessBuilder pb;
    String tessDir;
    String inputDataDir;
    String lang;
    String bootstrapLang;

    public TessTrainer(String tessDir, String inputDataDir, String lang, String bootstrapLang) {
        pb = new ProcessBuilder();
        pb.directory(new File(System.getProperty("user.home")));
        pb.redirectErrorStream(true);

        this.tessDir = tessDir;
        this.inputDataDir = inputDataDir;
        this.lang = lang;
        this.bootstrapLang = bootstrapLang;
    }

    /**
     * Generates data based on selection of training mode.
     *
     * @param mode 1: Generate Boxes Only; 2: Train with Existing Boxes; 3:
     * Train without Boxes
     * @throws Exception
     */
    public void generate(int mode) throws Exception {
        switch (mode) {
            case 1:
                generateBox();
                break;
            case 2:
                generateTraineddata(false);
                break;
            case 3:
                generateTraineddata(true);
                break;
            default:
                break;
        }
    }

    /**
     * Generates box file.
     * @throws Exception 
     */
    void generateBox() throws Exception {
        List<String> cmd = getCommand(cmdMakeBox);

        // if no bootstrap
        if (this.bootstrapLang.length() == 0) {
            cmd.remove(4);
            cmd.remove(3);
        }

        File[] files = new File(inputDataDir).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".tif") || filename.endsWith(".tiff") || filename.endsWith(".png");
            }
        });

        for (File f : files) {
            String fileName = f.getPath();
            cmd.set(1, fileName);
            cmd.set(2, TextUtilities.stripExtension(fileName));
            runCommand(cmd);
        }
    }

    /**
     * Generates traineddata file.
     * @param skipBoxGeneration
     * @throws Exception 
     */
    void generateTraineddata(boolean skipBoxGeneration) throws Exception {
        if (!skipBoxGeneration) {
            generateBox();
        }

        List<String> cmd = getCommand(cmdtesseract);

        runCommand(cmd);
    }

    /**
     * Gets command.
     * @param cmdStr
     * @return 
     */
    List<String> getCommand(String cmdStr) {
        List<String> cmd = new LinkedList<String>(Arrays.asList(cmdStr.split("\\s+")));
        cmd.set(0, tessDir + "/" + cmd.get(0));
        return cmd;
    }
    
    /**
     * Runs command.
     * @param cmd
     * @throws Exception 
     */
    void runCommand(List<String> cmd) throws Exception {
        System.out.println("Execute command: " + cmd);
        pb.command(cmd);
        Process process = pb.start();

        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
        outputGobbler.start();

        int w = process.waitFor();
        System.out.println("Exit value = " + w);

//        if (w != 0) {
//            throw new RuntimeException();
//        }
    }
}

/**
 * When Runtime.exec() won't.
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 */
class StreamGobbler extends Thread {

    InputStream is;
    StringBuilder outputMessage = new StringBuilder();

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
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                outputMessage.append(line).append("\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}