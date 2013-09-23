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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TessTrainer {
    ProcessBuilder pb;
    String tessFolder;
    String dataFolder;
    String lang;
    String bootstrapLang;
    
    public TessTrainer(String tessFolder, String dataFolder, String lang, String bootstrapLang) {
        pb = new ProcessBuilder();
        pb.directory(new File(System.getProperty("user.home")));
        pb.redirectErrorStream(true);
        
        this.tessFolder = tessFolder;
        this.dataFolder = dataFolder;
        this.lang = lang;
        this.bootstrapLang = bootstrapLang;
    }

    /**
     * Generates data based on selection of training mode.
     * @param mode 1: Generate Boxes Only; 2: Train with Existing Boxes; 3: Train without Boxes
     * @throws Exception 
     */
    public void generate(int mode) throws Exception {
        runCommand(lang);
    }
    
    void generateBox() throws Exception {
        runCommand(lang);
    }

    void generateTraineddata(boolean skipBoxGeneration) throws Exception {
        runCommand(lang);
    }

    void runCommand(String cmd) throws Exception {
        System.out.println("Execute command: " + cmd);
        pb.command(cmd);
        Process process = pb.start();

        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
        outputGobbler.start();

        int w = process.waitFor();
        System.out.println("Exit value = " + w);

        if (w == 0) {
            
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