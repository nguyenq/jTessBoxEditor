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

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.font.TextAttribute;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.sourceforge.tessboxeditor.utilities.Utils;
import net.sourceforge.vietpad.components.FontDialog;
import net.sourceforge.vietpad.components.SimpleFilter;

public class GuiWithGenerator extends GuiWithTools {

    private File inputTextFile;
    private String outputDirectory;
    private Font fontGen;
    private String fontFolder;
    private final Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
    private JFileChooser jFileChooserInputText;
    private JFileChooser jFileChooserOutputDir;
    private JFileChooser jFileChooserFontFolder;

    private final static Logger logger = Logger.getLogger(GuiWithGenerator.class.getName());

    public GuiWithGenerator() {
        initComponents();

        // DnD support
        new DropTarget(this.jTextAreaInput, new FileDropTargetListener(GuiWithGenerator.this, this.jTextAreaInput));
    }

    private void initComponents() {
        // Set fontGen
        fontGen = new Font(
                prefs.get("trainfontName", this.jTextAreaInput.getFont().getName()),
                prefs.getInt("trainfontStyle", Font.PLAIN),
                prefs.getInt("trainfontSize", 12));

        attributes.put(TextAttribute.TRACKING, this.jSpinnerTracking.getValue());
        fontGen = fontGen.deriveFont(attributes);
        this.jTextAreaInput.setFont(fontGen.deriveFont(fontGen.getSize2D() * 3)); // adjustment
        this.jButtonFont.setText(fontDesc(fontGen));
        this.jTextFieldFileName.setText(createFileName(fontGen) + ".exp0.tif");

        outputDirectory = prefs.get("outputDirectory", new File(System.getProperty("user.dir"), "samples/vie").getPath());
        this.jTextFieldOuputDir.setText(outputDirectory);

        fontFolder = prefs.get("fontFolder", getFontFolder());
        this.jTextFieldFontFolder.setText(fontFolder);

        jFileChooserInputText = new JFileChooser();
        FileFilter textFilter = new SimpleFilter("txt", "Text Files");
        jFileChooserInputText.addChoosableFileFilter(textFilter);
        jFileChooserInputText.setAcceptAllFileFilterUsed(false);
        jFileChooserInputText.setCurrentDirectory(new File(outputDirectory));

        jFileChooserOutputDir = new JFileChooser();
        jFileChooserOutputDir.setApproveButtonText("Set");
        jFileChooserOutputDir.setDialogTitle("Set Output Directory");
        jFileChooserOutputDir.setAcceptAllFileFilterUsed(false);
        jFileChooserOutputDir.setCurrentDirectory(new File(outputDirectory));
        jFileChooserOutputDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        jFileChooserFontFolder = new JFileChooser();
        jFileChooserFontFolder.setApproveButtonText("Set");
        jFileChooserFontFolder.setDialogTitle("Set Font Folder");
        jFileChooserFontFolder.setAcceptAllFileFilterUsed(false);
        jFileChooserFontFolder.setCurrentDirectory(new File(fontFolder));
        jFileChooserFontFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    /**
     * Gets default system font folder.
     *
     * @return
     */
    String getFontFolder() {
        String folder;
        if (WINDOWS) {
            folder = "C:\\Windows\\Fonts";
        } else if (MAC_OS_X) {
            folder = "/Library/Fonts/";
        } else {
            folder = "/usr/share/fonts"; // assume Linux
        }
        return folder;
    }

    @Override
    void jButtonInputActionPerformed(java.awt.event.ActionEvent evt) {
        if (jFileChooserInputText.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getGlassPane().setVisible(true);

            try {
                inputTextFile = jFileChooserInputText.getSelectedFile();
                openTextFile(inputTextFile);
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            } finally {
                SwingUtilities.invokeLater(
                        new Runnable() {
                    @Override
                    public void run() {
                        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        getGlassPane().setVisible(false);
                    }
                });
            }
        }
    }

    /**
     * Opens input text file.
     *
     * @param selectedFile
     */
    void openTextFile(final File selectedFile) {
        if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
            return; // not text file
        }
        try {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), StandardCharsets.UTF_8))) {
                this.jTextAreaInput.read(in, null);
            }
            Document doc = jTextAreaInput.getDocument();
            if (doc.getText(0, 1).equals("\uFEFF")) {
                doc.remove(0, 1); // remove BOM
            }
        } catch (IOException | BadLocationException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    void jButtonBrowseOutputDirActionPerformed(java.awt.event.ActionEvent evt) {
        if (jFileChooserOutputDir.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = jFileChooserOutputDir.getSelectedFile().getPath();
            this.jTextFieldOuputDir.setText(outputDirectory);
            jFileChooserInputText.setCurrentDirectory(new File(outputDirectory));
        }
    }

    @Override
    void jButtonBrowseFontFolderActionPerformed(java.awt.event.ActionEvent evt) {
        if (jFileChooserFontFolder.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fontFolder = jFileChooserFontFolder.getSelectedFile().getPath();
            this.jTextFieldFontFolder.setText(fontFolder);
        }
    }

    @Override
    void jButtonFontActionPerformed(java.awt.event.ActionEvent evt) {
        FontDialog dlg = new FontDialog(this);
        dlg.setAttributes(fontGen);
        dlg.setVisible(true);

        if (dlg.succeeded()) {
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getGlassPane().setVisible(true);

            try {
                fontGen = dlg.getFont().deriveFont(attributes);
                this.jTextAreaInput.setFont(fontGen.deriveFont(fontGen.getSize2D() * 3));
                this.jTextAreaInput.validate();
                this.jButtonFont.setText(fontDesc(fontGen));
                String curFontName = this.jTextFieldFileName.getText();
                String ext = curFontName.substring(curFontName.lastIndexOf(".exp"));
                String newFontName = createFileName(fontGen) + ext;
                this.jTextFieldFileName.setText(newFontName);
                if (newFontName.length() > curFontName.length()) {
                    pack(); // re-adjust window width
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            } finally {
                SwingUtilities.invokeLater(
                        new Runnable() {
                    @Override
                    public void run() {
                        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        getGlassPane().setVisible(false);
                    }
                });
            }
        }
    }

    @Override
    void jSpinnerTrackingStateChanged(javax.swing.event.ChangeEvent evt) {
        attributes.put(TextAttribute.TRACKING, this.jSpinnerTracking.getValue());
        this.jTextAreaInput.setFont(this.jTextAreaInput.getFont().deriveFont(attributes));
    }

    @Override
    void jButtonGenerateActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.jTextAreaInput.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Please load training text.");
            return;
        } else if (inputTextFile == null && this.jCheckBoxText2Image.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please select an input file.");
            return;
        }

        this.jButtonGenerate.setEnabled(false);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        SwingWorker generateWorker = new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {
                return generateTiffBox();
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    JOptionPane.showMessageDialog(GuiWithGenerator.this, result, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ignore) {
                    logger.log(Level.WARNING, ignore.getMessage(), ignore);
                } catch (java.util.concurrent.ExecutionException e) {
                    String why;
                    boolean oome = false;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        if (cause instanceof OutOfMemoryError) {
                            oome = true;
                            why = "The input text was probably too large. Please reduce it to a more manageable amount.";
                        } else {
                            why = cause.getMessage() != null ? cause.getMessage() : e.getMessage();
                        }
                    } else {
                        why = e.getMessage();
                    }
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    JOptionPane.showMessageDialog(GuiWithGenerator.this, why, oome ? "Out-Of-Memory Exception" : APP_NAME, JOptionPane.ERROR_MESSAGE);
                } finally {
                    jButtonGenerate.setEnabled(true);
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                }
            }
        };

        generateWorker.execute();
    }

    String generateTiffBox() throws Exception {
        String prefix = this.jTextFieldPrefix.getText();
        if (prefix.trim().length() > 0) {
            prefix += ".";
        }

        long lastModified = 0;
        File fontpropFile = new File(outputDirectory, prefix + "font_properties");
        if (fontpropFile.exists()) {
            lastModified = fontpropFile.lastModified();
        }

        if (this.jCheckBoxText2Image.isSelected()) {
            // execute Text2Image
            TessTrainer trainer = new TessTrainer(tessDirectory, outputDirectory, jTextFieldLang.getText(), jTextFieldBootstrapLang.getText(), jCheckBoxRTL.isSelected());
            String outputbase = jTextFieldFileName.getText();
            if (outputbase.endsWith(".tif")) {
                outputbase = outputbase.substring(0, outputbase.lastIndexOf(".tif"));
            }
            outputbase = outputDirectory + "/" + prefix + outputbase;
            trainer.text2image(inputTextFile.getPath(), outputbase, fontGen, jTextFieldFontFolder.getText(), (Integer) jSpinnerExposure.getValue(), (Float) this.jSpinnerTracking.getValue(), (Integer) this.jSpinnerLeading.getValue(), (Integer) this.jSpinnerW1.getValue(), (Integer) this.jSpinnerH1.getValue());
//            Utils.removeEmptyBoxes(new File(outputbase + ".box"));
        } else {
            TiffBoxGenerator generator = new TiffBoxGenerator(this.jTextAreaInput.getText(), fontGen, (Integer) this.jSpinnerW1.getValue(), (Integer) this.jSpinnerH1.getValue());
            generator.setOutputFolder(new File(outputDirectory));
            generator.setFileName(prefix + this.jTextFieldFileName.getText());
            generator.setTracking((Float) this.jSpinnerTracking.getValue());
            generator.setLeading((Integer) this.jSpinnerLeading.getValue());
            generator.setNoiseAmount((Integer) this.jSpinnerNoise.getValue());
            generator.setAntiAliasing(this.jCheckBoxAntiAliasing.isSelected());
            generator.create();
        }

        // updates font_properties file
        Utils.updateFontProperties(new File(outputDirectory), prefix + this.jTextFieldFileName.getText(), fontGen);
        String msg = String.format("TIFF/Box files have been generated and saved in %s folder.", outputDirectory);

        if (fontpropFile.exists() && lastModified != fontpropFile.lastModified()) {
            msg = msg.concat("\nBe sure to check the entries in font_properties file for accuracy.");
        }

        return msg;
    }

    @Override
    void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {
        this.jTextAreaInput.setText(null);
    }

    @Override
    void quit() {
        if (outputDirectory != null) {
            prefs.put("outputDirectory", outputDirectory);
        }
        if (fontFolder != null) {
            prefs.put("fontFolder", fontFolder);
        }

        prefs.put("trainLanguage", jTextFieldPrefix.getText());
        prefs.put("trainfontName", fontGen.getName());
        prefs.putInt("trainfontSize", fontGen.getSize());
        prefs.putInt("trainfontStyle", fontGen.getStyle());

        super.quit();
    }

    /**
     * Gets font description.
     *
     * @param font selected font
     * @return font description
     */
    String fontDesc(Font font) {
        return font.getName() + (font.isBold() ? " Bold" : "") + (font.isItalic() ? " Italic" : "") + " " + font.getSize() + "pt";
    }

    /**
     * Creates file name.
     *
     * @param font
     * @return file name
     */
    String createFileName(Font font) {
        return font.getName().replace(" ", "").toLowerCase() + (font.isBold() ? "b" : "") + (font.isItalic() ? "i" : "");
    }
}
