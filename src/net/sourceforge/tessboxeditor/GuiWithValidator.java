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

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import static net.sourceforge.tessboxeditor.GuiWithTrainer.DIALOG_TITLE;
import net.sourceforge.vietocr.OCR;
import net.sourceforge.vietocr.OCRFiles;
import net.sourceforge.vietpad.components.SimpleFilter;

public class GuiWithValidator extends GuiWithTrainer {

    private JFileChooser jFileChooserValidatingImage;
    private OcrWorker ocrWorker;

    private final static Logger logger = Logger.getLogger(GuiWithValidator.class.getName());

    public GuiWithValidator() {
        initComponents();
    }

    private void initComponents() {
        jFileChooserValidatingImage = new JFileChooser();
        jFileChooserValidatingImage.setAcceptAllFileFilterUsed(false);
        jFileChooserValidatingImage.setApproveButtonText("Select");
        jFileChooserValidatingImage.setDialogTitle("Select Image File");
        FileFilter allImagesFilter = new SimpleFilter("bmp;jpg;jpeg;png;tif;tiff", "All Images");
        jFileChooserValidatingImage.setFileFilter(allImagesFilter);
        jFileChooserValidatingImage.setCurrentDirectory(trainDataDirectory == null ? null : new File(trainDataDirectory));
    }

    @Override
    void jButtonValidateActionPerformed(java.awt.event.ActionEvent evt) {
        String lang = this.jTextFieldLang.getText();
        File tessdata = new File(trainDataDirectory, "tessdata");
        File traineddata = new File(tessdata, lang + ".traineddata");
        if (!traineddata.exists()) {
            String message = String.format("%s.traineddata does not exist in %s. Be sure to run training first.", lang, tessdata.getPath());
            JOptionPane.showMessageDialog(this, message, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // perform OCR on the training image
        if (jFileChooserValidatingImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            jButtonValidate.setEnabled(false);
            this.jTextAreaValidationResult.setText(null);
            jLabelStatus.setText(bundle.getString("OCR_running..."));
            jProgressBar1.setIndeterminate(true);
            jProgressBar1.setString(bundle.getString("OCR_running..."));
            jProgressBar1.setVisible(true);
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getGlassPane().setVisible(true);

            File imageFile = jFileChooserValidatingImage.getSelectedFile();
            List<File> files = new ArrayList<File>();
            files.add(imageFile);

            // instantiate SwingWorker for OCR
            ocrWorker = new OcrWorker(files);
            ocrWorker.execute();
        }
    }

    @Override
    void jButtonCloseDialogActionPerformed(java.awt.event.ActionEvent evt) {
        this.jDialogValidationResult.setVisible(false);
    }

    /**
     * A worker class for managing OCR process.
     */
    class OcrWorker extends SwingWorker<Void, String> {

        List<File> files;

        OcrWorker(List<File> files) {
            this.files = files;
        }

        @Override
        protected Void doInBackground() throws Exception {
            OCR<File> ocrEngine = new OCRFiles(tessDirectory);
            ocrEngine.setDatapath(trainDataDirectory);
            ocrEngine.setLanguage(jTextFieldLang.getText());

            for (int i = 0; i < files.size(); i++) {
                if (!isCancelled()) {
                    String result = ocrEngine.recognizeText(files.subList(i, i + 1));
                    publish(result); // interim result
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
            for (String str : results) {
                jTextAreaValidationResult.append(str);
                jTextAreaValidationResult.setCaretPosition(jTextAreaValidationResult.getDocument().getLength());
            }
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonValidate.setEnabled(true);
                jDialogValidationResult.setVisible(true);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new GuiWithValidator().setVisible(true);
            }
        });
    }
}
