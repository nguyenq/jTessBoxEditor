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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.vietocr.util.Utils;
import net.sourceforge.vietpad.utilities.TextUtilities;

public class TessTrainer {

    private static final String cmdmake_box = "tesseract imageFile boxFile -l bootstrapLang batch.nochop makebox";
    private static final String cmdtess_train = "tesseract imageFile boxFile box.train";
    private static final String cmdunicharset_extractor = "unicharset_extractor"; // lang.fontname.exp0.box lang.fontname.exp1.box ...
    private static final String cmdshapeclustering = "shapeclustering -F %s.font_properties -U unicharset"; // lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private static final String cmdmftraining = "mftraining -F %1$s.font_properties -U unicharset -O %1$s.unicharset"; // lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private static final String cmdcntraining = "cntraining"; // lang.fontname.exp0.tr lang.fontname.exp1.tr ...";
    private static final String cmdwordlist2dawg = "wordlist2dawg %2$s %1$s.frequent_words_list %1$s.freq-dawg %1$s.unicharset";
    private static final String cmdwordlist2dawg2 = "wordlist2dawg %2$s %1$s.words_list %1$s.word-dawg %1$s.unicharset";
    private static final String cmdcombine_tessdata = "combine_tessdata %s.";

    ProcessBuilder pb;
    String tessDir;
    String inputDataDir;
    String lang;
    String bootstrapLang;
    boolean rtl;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final static Logger logger = Logger.getLogger(TessTrainer.class.getName());

    public TessTrainer(String tessDir, String inputDataDir, String lang, String bootstrapLang, boolean rtl) {
        pb = new ProcessBuilder();
//        pb.directory(new File(System.getProperty("user.home")));
        pb.directory(new File(inputDataDir));
        pb.redirectErrorStream(true);

        this.tessDir = tessDir;
        this.inputDataDir = inputDataDir;
        this.lang = lang;
        this.bootstrapLang = bootstrapLang;
        this.rtl = rtl;
    }

    /**
     * Adds listener for property change event.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Generates data based on selection of training mode.
     *
     * @param mode 1: Generate Boxes Only; 2: Train with Existing Boxes; 3:
     * Train without Boxes
     * @throws Exception
     */
    public void generate(TrainingMode mode) throws Exception {
        switch (mode) {
            case Make_Box_File_Only:
                makeBox();
                break;
            case Train_with_Existing_Box:
                generateTraineddata(true);
                break;
            case Shape_Clustering:
                runShapeClustering();
                break;
            case Dictionary:
                runDictionary();
                break;
            case Train_from_Scratch:
                generateTraineddata(false);
                break;
            default:
                break;
        }
    }

    /**
     * Makes box files.
     *
     * @throws Exception
     */
    void makeBox() throws Exception {
        //cmdmake_box
        List<String> cmd = getCommand(cmdmake_box);

        // if no bootstrap
        if (bootstrapLang.length() == 0) {
            cmd.remove(4);
            cmd.remove(3);
        } else {
            cmd.set(4, bootstrapLang);
        }

        String[] files = getImageFiles();

        if (files.length == 0) {
            throw new RuntimeException("There are no training images.");
        }

        logger.info("Make Box Files");
        writeMessage("** Make Box Files **");
        for (String file : files) {
            cmd.set(1, file);
            cmd.set(2, TextUtilities.stripExtension(file));
            runCommand(cmd);
        }
    }

    /**
     * Generates traineddata file.
     *
     * @param skipBoxGeneration
     * @throws Exception
     */
    void generateTraineddata(boolean skipBoxGeneration) throws Exception {
        if (!skipBoxGeneration) {
            makeBox();
        }

        String[] files = getImageFiles();

        if (files.length == 0) {
            throw new RuntimeException("There are no training images.");
        }

        logger.info("Run Tesseract for Training");
        writeMessage("** Run Tesseract for Training **");
        //cmdtess_train
        List<String> cmd = getCommand(cmdtess_train);
        for (String file : files) {
            cmd.set(1, file);
            cmd.set(2, TextUtilities.stripExtension(file));
            runCommand(cmd);
        }

        logger.info("Compute the Character Set");
        writeMessage("** Compute the Character Set **");
        //cmdunicharset_extractor
        cmd = getCommand(cmdunicharset_extractor);
        files = new File(inputDataDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".box");
            }
        });
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);

        if (rtl) {
            //fix Unicode character directionality in unicharset
            logger.info("Fixed unicharset's Unicode character directionality.");
            writeMessage("Fixed unicharset's Unicode character directionality.\n");
            fixUniCharDirectionality();
        }

        runShapeClustering();
    }

    /**
     * Perform training from shape clustering on...
     *
     * @throws Exception
     */
    void runShapeClustering() throws Exception {
        String[] files = new File(inputDataDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".tr");
            }
        });

        if (files.length == 0) {
            throw new RuntimeException("There are no .tr files. Need to train Tesseract first.");
        }

        logger.info("Shape Clustering");
        writeMessage("** Shape Clustering **");
        //cmdshapeclustering
        List<String> cmd = getCommand(String.format(cmdshapeclustering, lang));
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);

        logger.info("MF Training");
        writeMessage("** MF Training **");
        //cmdmftraining
        cmd = getCommand(String.format(cmdmftraining, lang));
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);

        logger.info("CN Training");
        writeMessage("** CN Training **");
        //cmdcntraining
        cmd = getCommand(cmdcntraining);
        cmd.addAll(Arrays.asList(files));
        runCommand(cmd);

        logger.info("Rename files");
        renameFile("inttemp");
        renameFile("pffmtable");
        renameFile("normproto");
        renameFile("shapetable");

        runDictionary();
    }

    /**
     * Perform training from dictionary on...
     *
     * @throws Exception
     */
    void runDictionary() throws Exception {
        if (!new File(inputDataDir, lang + ".unicharset").exists()) {
            String msg = String.format("There is no %1$s.unicharset. Need to train Tesseract first.", lang);
            throw new RuntimeException(msg);
        }

        logger.info("Dictionary Data");
        writeMessage("** Dictionary Data **");
        //cmdwordlist2dawg
        List<String> cmd = getCommand(String.format(cmdwordlist2dawg, lang, (rtl ? "-r 1" : "")));
        runCommand(cmd);

        //cmdwordlist2dawg2
        cmd = getCommand(String.format(cmdwordlist2dawg2, lang, (rtl ? "-r 1" : "")));
        runCommand(cmd);

        logger.info("Combine Data Files");
        writeMessage("** Combine Data Files **");
        //cmdcombine_tessdata
        cmd = getCommand(String.format(cmdcombine_tessdata, lang));
        runCommand(cmd);

        String traineddata = lang + ".traineddata";
        logger.info("Moving generated traineddata file to tessdata folder");
        writeMessage("** Moving generated traineddata file to tessdata folder **");
        File tessdata = new File(inputDataDir, "tessdata");
        if (!tessdata.exists()) {
            tessdata.mkdir();
        }
        boolean success = new File(inputDataDir, traineddata).renameTo(new File(tessdata, traineddata));

        logger.info("Training Completed");
        writeMessage("** Training Completed **");
    }

    /**
     * Fixes Unicode Character Directionality in <code>unicharset</code> file.
     *
     * http://tesseract-ocr.googlecode.com/svn/trunk/doc/unicharset.5.html
     */
    void fixUniCharDirectionality() throws IOException {
        Path path = FileSystems.getDefault().getPath(inputDataDir, "unicharset");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(" ");
            if (parts.length < 8) {
                continue;
            }

            boolean change = false;
            int codePoint = parts[0].codePointAt(0);
            String scriptName = Character.UnicodeScript.of(codePoint).toString();
            if (parts[3].equals("NULL")) {
                parts[3] = Utils.capitalize(scriptName);
                change = true;
            }

            byte diValue = Character.getDirectionality(codePoint);
            diValue = customRuleOverride(diValue);

            String diVal = String.valueOf(diValue);
            if (!parts[5].equals(diVal)) {
                parts[5] = diVal;
                change = true;
            }

            if (change) {
                lines.set(i, Utils.join(Arrays.asList(parts), " "));
            }
        }
        Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Custom rules for overriding directionality, mainly for RTL scripts.
     *
     * @param diVal
     * @return
     */
    byte customRuleOverride(byte diVal) {
        switch (diVal) {
            case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
            case Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
            case Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
                diVal = Character.DIRECTIONALITY_RIGHT_TO_LEFT;
                break;
        }

        return diVal;
    }

    /**
     * Gets training image files.
     *
     * @return
     */
    String[] getImageFiles() {
        String[] files = new File(inputDataDir).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().matches(".*\\.(tif|tiff|jpg|jpeg|png|bmp)$");
            }
        });

        return files;
    }

    /**
     * Prefixes filename with language code
     *
     * @param fileName
     */
    void renameFile(String fileName) {
        File file = new File(inputDataDir, fileName);
        if (file.exists()) {
            File fileWithPrefix = new File(inputDataDir, lang + "." + fileName);
            fileWithPrefix.delete();
            boolean result = file.renameTo(fileWithPrefix);
            String msg = (result ? "Successful" : "Unsuccessful") + " rename of " + fileName;
            writeMessage(msg);
        }
    }

    /**
     * Gets a training command.
     *
     * @param cmdStr
     * @return
     */
    List<String> getCommand(String cmdStr) {
        List<String> cmd = new LinkedList<>(Arrays.asList(cmdStr.split("\\s+")));
        cmd.set(0, tessDir + "/" + cmd.get(0));
        return cmd;
    }

    /**
     * Runs given command.
     *
     * @param cmd
     * @throws Exception
     */
    void runCommand(List<String> cmd) throws Exception {
        logger.log(Level.INFO, "Command: {0}", cmd.toString());
        writeMessage(cmd.toString());
        pb.command(cmd);
        Process process = pb.start();

        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
        outputGobbler.start();

        int w = process.waitFor();
        logger.log(Level.INFO, "Exit value = {0}", w);
        writeMessage(outputGobbler.getMessage());

        if (w != 0) {
            String msg;
            if (cmd.get(0).contains("shapeclustering")) {
                msg = "An error has occurred. font_properties could be missing a font entry.";
            } else {
                msg = outputGobbler.getMessage();
            }
            throw new RuntimeException(msg);
        }
    }

    /**
     * Writes a message.
     *
     * @param message
     */
    void writeMessage(String message) {
        this.pcs.firePropertyChange("value", null, message + "\n");
//        System.out.println(message);
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
            InputStreamReader isr = new InputStreamReader(is, "UTF8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                outputMessage.append(line).append("\n");
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }
}
