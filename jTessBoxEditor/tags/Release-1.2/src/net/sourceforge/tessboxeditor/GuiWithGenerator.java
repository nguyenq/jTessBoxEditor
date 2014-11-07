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

    private File selectedFile;
    private String trainDirectory;
    private Font fontGen;
    private final Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
    private JFileChooser jFileChooserInputText;

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

        jFileChooserInputText = new JFileChooser();
        FileFilter textFilter = new SimpleFilter("txt", "Text Files");
        jFileChooserInputText.addChoosableFileFilter(textFilter);
        jFileChooserInputText.setAcceptAllFileFilterUsed(false);
        trainDirectory = prefs.get("trainDirectory", null);
        jFileChooserInputText.setCurrentDirectory(trainDirectory == null ? null : new File(trainDirectory));
    }

    @Override
    void jButtonInputActionPerformed(java.awt.event.ActionEvent evt) {
        if (jFileChooserInputText.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            trainDirectory = jFileChooserInputText.getCurrentDirectory().getPath();
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getGlassPane().setVisible(true);

            try {
                selectedFile = jFileChooserInputText.getSelectedFile();
                openTextFile(selectedFile);
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
            JOptionPane.showMessageDialog(this, "Please load some text.");
            return;
        }

        TiffBoxGenerator generator = new TiffBoxGenerator(this.jTextAreaInput.getText(), this.jTextAreaInput.getFont(), (Integer) this.jSpinnerW1.getValue(), (Integer) this.jSpinnerH1.getValue());
        File outputFolder;

        if (selectedFile != null) {
            outputFolder = selectedFile.getParentFile();
        } else {
            outputFolder = new File(System.getProperty("user.home"));
        }
        generator.setOutputFolder(outputFolder);
        String prefix = this.jTextFieldPrefix.getText();
        if (prefix.trim().length() > 0) {
            prefix += ".";
        }
        generator.setFileName(prefix + this.jTextFieldFileName.getText());
        generator.setTracking((Float) this.jSpinnerTracking.getValue());
        generator.setNoiseAmount((Integer) this.jSpinnerNoise.getValue());
        generator.setAntiAliasing(this.jCheckBoxAntiAliasing.isSelected());

        this.jButtonGenerate.setEnabled(false);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        try {
            generator.create();
            JOptionPane.showMessageDialog(this, String.format("TIFF/Box files have been generated and saved in %s folder.", outputFolder.getPath()));
        } catch (OutOfMemoryError oome) {
            JOptionPane.showMessageDialog(this, "The input text was probably too large. Please reduce it to a more manageable amount.", "Out-Of-Memory Exception", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
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
