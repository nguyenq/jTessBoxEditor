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

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.vietpad.utilities.TextUtilities;

public class TessTrainer {
    private final String LANG_OPTION = "-l";
    
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
     * @param mode 1: Generate Boxes Only; 2: Train with Existing Boxes; 3: Train without Boxes
     * @throws Exception 
     */
    public void generate(int mode) throws Exception {
        switch (mode) {
            case 1: generateBox(); break;
            case 2: generateTraineddata(false); break;
            case 3: generateTraineddata(true); break;
            default: break;
        }
    }
    
    void generateBox() throws Exception {
//        String cmdStr = "tesseract imageFile boxFile -l bootstrapLang batch.nochop makebox";
        List<String> cmd = new ArrayList<String>();
        cmd.add(tessDir + "/tesseract");
        cmd.add(""); // placeholder for input image file
        cmd.add(""); // placeholder for output box file
        if (this.bootstrapLang.length() > 0) {
            cmd.add(LANG_OPTION);
            cmd.add(this.bootstrapLang);
        }
        cmd.add("batch.nochop");
        cmd.add("makebox");
        
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
    
    void generateTraineddata(boolean skipBoxGeneration) throws Exception {
        if (!skipBoxGeneration) {
            generateBox();
        }
        
        //unicharset_extractor lang.fontname.exp0.box lang.fontname.exp1.box ...
        List<String> cmd = new ArrayList<String>();
        cmd.add(tessDir + "/unicharset_extractor");
        cmd.add(""); // placeholder for box files

        runCommand(cmd);
    }

    void runCommand(List<String> cmd) throws Exception {
        System.out.println("Execute command: " + cmd);
        pb.command(cmd);
        Process process = pb.start();

        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
        outputGobbler.start();

        int w = process.waitFor();
        System.out.println("Exit value = " + w);

        if (w == 0) {
            // successful
        }
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