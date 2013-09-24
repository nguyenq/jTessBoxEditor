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

    private final String cmdmake_box = "tesseract imageFile boxFile -l bootstrapLang batch.nochop makebox";
    private final String cmdtess_train = "tesseract imageFile boxFile box.train";
    private final String cmdunicharset_extractor = "unicharset_extractor"; // lang.fontname.exp0.box lang.fontname.exp1.box ...
    private final String cmdshapeclustering = "shapeclustering -F %s.font_properties -U unicharset"; // lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private final String cmdmftraining = "mftraining -F %1$s.font_properties -U unicharset -O %1$s.unicharset"; // lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private final String cmdcntraining = "cntraining"; // lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private final String cmdwordlist2dawg = "wordlist2dawg %1$s.frequent_words_list %1$s.freq-dawg %1$s.unicharset";
    private final String cmdwordlist2dawg2 = "wordlist2dawg %1$s.words_list %1$s.word-dawg %1$s.unicharset";
    private final String cmdcombine_tessdata = "combine_tessdata %s.";
    
    ProcessBuilder pb;
    String tessDir;
    String inputDataDir;
    String lang;
    String bootstrapLang;

    public TessTrainer(String tessDir, String inputDataDir, String lang, String bootstrapLang) {
        pb = new ProcessBuilder();
//        pb.directory(new File(System.getProperty("user.home")));
        pb.directory(new File(inputDataDir));
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
                generateTraineddata(true);
                break;
            case 3:
                generateTraineddata(false);
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
        //cmdmake_box
        List<String> cmd = getCommand(cmdmake_box);

        // if no bootstrap
        if (this.bootstrapLang.length() == 0) {
            cmd.remove(4);
            cmd.remove(3);
        }

        String[] files = new File(inputDataDir).list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".tif") || filename.endsWith(".tiff") || filename.endsWith(".png");
            }
        });

        System.out.println("** Make Box Files **");
        for (String file : files) {
            cmd.set(1, file);
            cmd.set(2, TextUtilities.stripExtension(file));
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

        List<String> cmd;
        String[] files;
        files = new File(inputDataDir).list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".tif") || filename.endsWith(".tiff") || filename.endsWith(".png");
            }
        });
        
        System.out.println("** Run Tesseract for Training **");
        //cmdtess_train
        cmd = getCommand(cmdtess_train);
        for (String file : files) {
            cmd.set(1, file);
            cmd.set(2, TextUtilities.stripExtension(file));
            runCommand(cmd);
        }
           
        System.out.println("** Compute the Character Set **");
        //cmdunicharset_extractor
        cmd = getCommand(cmdunicharset_extractor);
        files = new File(inputDataDir).list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".box");
            }
        });
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);
        
        System.out.println("** Shape Clustering **");
        //cmdshapeclustering
        cmd = getCommand(String.format(cmdshapeclustering, lang));
        files = new File(inputDataDir).list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".tr");
            }
        });
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);
        
        System.out.println("** MF Training **");
        //cmdmftraining
        cmd = getCommand(String.format(cmdmftraining, lang));
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);
        
        System.out.println("** CN Training **");
        //cmdcntraining
        cmd = getCommand(cmdcntraining);
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);
        
        System.out.println("** Dictionary Data **");
        //cmdwordlist2dawg
        cmd = getCommand(String.format(cmdwordlist2dawg, lang));
        runCommand(cmd);
        
        //cmdwordlist2dawg2
        cmd = getCommand(String.format(cmdwordlist2dawg2, lang));
        runCommand(cmd);
        
        System.out.println("** Combine **");
        //cmdcombine_tessdata
        cmd = getCommand(String.format(cmdcombine_tessdata, lang));
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
        System.out.println(cmd);
        pb.command(cmd);
        Process process = pb.start();

        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
        outputGobbler.start();

        int w = process.waitFor();
//        System.out.println("Exit value = " + w);

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