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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.sourceforge.vietpad.components.FontDialog;
import net.sourceforge.vietpad.components.SimpleFilter;

public class GuiWithGenerator extends GuiWithTools {

    private File inputTextFile;
    private String trainDirectory;
    private Font fontGen;
    private String fontFolder;
    protected String tessDirectory;
    protected String trainDataDirectory;
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
                prefs.getInt("trainfontSize", 36));

        attributes.put(TextAttribute.TRACKING, this.jSpinnerTracking.getValue());
        fontGen = fontGen.deriveFont(attributes);
        this.jTextAreaInput.setFont(fontGen);
        this.jButtonFont.setText(fontDesc(fontGen));
        this.jTextFieldFileName.setText(createFileName(fontGen) + ".exp0.tif");

        trainDirectory = prefs.get("trainDirectory", System.getProperty("user.home"));
        this.jTextFieldOuputDir.setText(trainDirectory);

        fontFolder = prefs.get("fontFolder", getFontFolder());
        this.jTextFieldFontFolder.setText(fontFolder);

        jFileChooserInputText = new JFileChooser();
        FileFilter textFilter = new SimpleFilter("txt", "Text Files");
        jFileChooserInputText.addChoosableFileFilter(textFilter);
        jFileChooserInputText.setAcceptAllFileFilterUsed(false);
        jFileChooserInputText.setCurrentDirectory(new File(trainDirectory));

        jFileChooserOutputDir = new JFileChooser();
        jFileChooserOutputDir.setApproveButtonText("Set");
        jFileChooserOutputDir.setDialogTitle("Set Output Directory");
        jFileChooserOutputDir.setAcceptAllFileFilterUsed(false);
        jFileChooserOutputDir.setCurrentDirectory(new File(trainDirectory));
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
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), "UTF8"))) {
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
            trainDirectory = jFileChooserOutputDir.getSelectedFile().getPath();
            this.jTextFieldOuputDir.setText(trainDirectory);
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
                this.jTextAreaInput.setFont(fontGen);
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
        }

        this.jButtonGenerate.setEnabled(false);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        try {
            String prefix = this.jTextFieldPrefix.getText();
            if (prefix.trim().length() > 0) {
                prefix += ".";
            }

            if (this.jCheckBoxText2Image.isSelected()) {
                // execute Text2Image
                TessTrainer trainer = new TessTrainer(tessDirectory, trainDataDirectory, jTextFieldLang.getText(), jTextFieldBootstrapLang.getText(), jCheckBoxRTL.isSelected());
                String outputbase = jTextFieldFileName.getText();
                if (outputbase.endsWith(".tif")) {
                    outputbase = outputbase.substring(0, outputbase.lastIndexOf(".tif"));
                }
                trainer.text2image(inputTextFile.getPath(), prefix + outputbase, fontGen.getFontName(), jTextFieldFontFolder.getText());
            } else {
                TiffBoxGenerator generator = new TiffBoxGenerator(this.jTextAreaInput.getText(), this.jTextAreaInput.getFont(), (Integer) this.jSpinnerW1.getValue(), (Integer) this.jSpinnerH1.getValue());
                generator.setOutputFolder(new File(trainDirectory));

                generator.setFileName(prefix + this.jTextFieldFileName.getText());
                generator.setTracking((Float) this.jSpinnerTracking.getValue());
                generator.setNoiseAmount((Integer) this.jSpinnerNoise.getValue());
                generator.setAntiAliasing(this.jCheckBoxAntiAliasing.isSelected());
                long lastModified = 0;
                File fontpropFile = new File(trainDirectory, this.jTextFieldPrefix.getText() + ".font_properties");
                if (fontpropFile.exists()) {
                    lastModified = fontpropFile.lastModified();
                }
                generator.create();
                String msg = "";
                if (fontpropFile.exists() && lastModified != fontpropFile.lastModified()) {
                    msg = "\nBe sure to check the entries in font_properties file for accuracy.";
                }
                JOptionPane.showMessageDialog(this, String.format("TIFF/Box files have been generated and saved in %s folder.%s", trainDirectory, msg));
            }
        } catch (OutOfMemoryError oome) {
            JOptionPane.showMessageDialog(this, "The input text was probably too large. Please reduce it to a more manageable amount.", "Out-Of-Memory Exception", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } finally {
            jButtonGenerate.setEnabled(true);
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            getGlassPane().setVisible(false);
        }
    }

    @Override
    void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {
        this.jTextAreaInput.setText(null);
    }

    @Override
    void quit() {
        if (trainDirectory != null) {
            prefs.put("trainDirectory", trainDirectory);
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
