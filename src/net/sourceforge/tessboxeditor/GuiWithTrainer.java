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
import java.beans.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import net.sourceforge.vietpad.components.SimpleFilter;

public class GuiWithTrainer extends GuiWithGenerator {

    protected static final String DIALOG_TITLE = "Train Tesseract";
    protected String tessDirectory;
    protected String trainDataDirectory;
    private JFileChooser jFileChooserTrainingData;
    private JFileChooser jFileChooserTessExecutables;

    private TrainingWorker trainWorker;

    public GuiWithTrainer() {
        initComponents();
    }

    private void initComponents() {
        tessDirectory = prefs.get("tessDirectory", new File(System.getProperty("user.dir"), "tesseract-ocr").getPath());
        jTextFieldTessDir.setText(tessDirectory);

        jFileChooserTessExecutables = new javax.swing.JFileChooser();
        jFileChooserTessExecutables.setApproveButtonText("Set");
        jFileChooserTessExecutables.setDialogTitle("Set Location of Tesseract Executables");
        jFileChooserTessExecutables.setCurrentDirectory(tessDirectory == null ? null : new File(tessDirectory));

        trainDataDirectory = prefs.get("trainDataDirectory", new File(System.getProperty("user.dir"), "samples/vie").getPath());
        jTextFieldDataDir.setText(trainDataDirectory);

        jFileChooserTrainingData = new JFileChooser();
        jFileChooserTrainingData.setAcceptAllFileFilterUsed(false);
        jFileChooserTrainingData.setApproveButtonText("Set");
        jFileChooserTrainingData.setDialogTitle("Set Location of Source Training Data");
        FileFilter allTrainingFilter = new SimpleFilter("bmp;jpg;jpeg;png;tif;tiff;box;font_properties;frequent_words_list;words_list;unicharambigs", "Source Training Data");
        jFileChooserTrainingData.setFileFilter(allTrainingFilter);
        jFileChooserTrainingData.setCurrentDirectory(trainDataDirectory == null ? null : new File(trainDataDirectory));

        jTextFieldLang.setText(prefs.get("trainnedLanguage", null));
        jTextFieldBootstrapLang.setText(prefs.get("bootstrapLanguage", null));
        jComboBoxOps.setSelectedIndex(prefs.getInt("trainingMode", 0));
        jCheckBoxRTL.setSelected(prefs.getBoolean("trainingRTL", false));
    }

    @Override
    void jButtonBrowseTessActionPerformed(java.awt.event.ActionEvent evt) {
        if (jFileChooserTessExecutables.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            tessDirectory = jFileChooserTessExecutables.getCurrentDirectory().getPath();
            this.jTextFieldTessDir.setText(tessDirectory);
        }
    }

    @Override
    void jButtonBrowseDataActionPerformed(java.awt.event.ActionEvent evt) {
        if (jFileChooserTrainingData.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            trainDataDirectory = jFileChooserTrainingData.getCurrentDirectory().getPath();
            this.jTextFieldDataDir.setText(trainDataDirectory);
        }
    }

    @Override
    void jButtonTrainActionPerformed(java.awt.event.ActionEvent evt) {
        String msg = "";

        TrainingMode selectedMode = TrainingMode.getValueByDesc(this.jComboBoxOps.getSelectedItem().toString());
        if (this.jTextFieldTessDir.getText().length() == 0 || this.jTextFieldDataDir.getText().length() == 0) {
            msg = "Input is not complete.";
        } else if (this.jTextFieldLang.getText().trim().length() == 0) {
            msg = "Language is required.";
        } else if (selectedMode == TrainingMode.HeaderText) {
            msg = "Please select a Training Mode.";
        }

        if (msg.length() > 0) {
            JOptionPane.showMessageDialog(this, msg);
            return;
        }

        // make sure all required data files exist before training
        if (selectedMode == TrainingMode.Train_with_Existing_Box || selectedMode == TrainingMode.Dictionary || selectedMode == TrainingMode.Train_from_Scratch) {
            final String lang = jTextFieldLang.getText();
            boolean otherFilesExist = new File(trainDataDirectory, lang + ".font_properties").exists() && new File(trainDataDirectory, lang + ".frequent_words_list").exists() && new File(trainDataDirectory, lang + ".words_list").exists();

            if (!otherFilesExist) {
                msg = String.format("The required file %1$s.font_properties, %1$s.frequent_words_list, or %1$s.words_list does not exist.", lang);
                JOptionPane.showMessageDialog(this, msg, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String[] boxFiles = new File(trainDataDirectory).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".box");
            }
        });

        // warn about potential box overwrite
        if (selectedMode == TrainingMode.Make_Box_File_Only || selectedMode == TrainingMode.Train_from_Scratch) {
            if (boxFiles.length > 0) {
                int option = JOptionPane.showConfirmDialog(this,
                        "There are existing box files. Continuing may overwrite them.\nDo you want to proceed?",
                        DIALOG_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (option == JOptionPane.NO_OPTION) {
                    return;
                }
            }
        } else {
            if (boxFiles.length == 0) {
                JOptionPane.showMessageDialog(this, "There are no existing box files.", DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        this.jButtonTrain.setEnabled(false);
        this.jButtonCancel.setEnabled(true);
        this.jProgressBar1.setIndeterminate(true);
        this.jProgressBar1.setString("Training...");
        this.jProgressBar1.setVisible(true);
        this.jLabelTime.setText(null);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        trainWorker = new TrainingWorker();
        trainWorker.execute();
    }

    @Override
    void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        if (trainWorker != null && !trainWorker.isDone()) {
            trainWorker.cancel(true);
        }
        this.jButtonCancel.setEnabled(false);
    }

    @Override
    void jButtonSaveLogActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTextAreaOutput.getDocument().getLength() == 0) {
            return;
        }

        try {
            File outFile = new File(trainDataDirectory, "training.log");
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), UTF8))) {
                jTextAreaOutput.write(out);
            }
            JOptionPane.showMessageDialog(this, String.format("Log has been saved as \"%s\".", outFile.getPath()), DIALOG_TITLE, JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            //ignore
        }
    }

    @Override
    void jButtonClearLogActionPerformed(java.awt.event.ActionEvent evt) {
        this.jTextAreaOutput.setText(null);
    }

    @Override
    void quit() {
        if (tessDirectory != null) {
            prefs.put("tessDirectory", tessDirectory);
        }
        if (trainDataDirectory != null) {
            prefs.put("trainDataDirectory", trainDataDirectory);
        }
        prefs.put("trainnedLanguage", this.jTextFieldLang.getText());
        prefs.put("bootstrapLanguage", this.jTextFieldBootstrapLang.getText());
        prefs.putInt("trainingMode", this.jComboBoxOps.getSelectedIndex());
        prefs.putBoolean("trainingRTL", this.jCheckBoxRTL.isSelected());

        super.quit();
    }

    /**
     * A worker class for training process.
     */
    public class TrainingWorker extends SwingWorker<Void, Void> {

        TessTrainer trainer;
        long startTime;

        public TrainingWorker() {
            trainer = new TessTrainer(tessDirectory, trainDataDirectory, jTextFieldLang.getText(), jTextFieldBootstrapLang.getText(), jCheckBoxRTL.isSelected());
            trainer.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    if ("value".equals(evt.getPropertyName())) {
                        jTextAreaOutput.append(evt.getNewValue().toString());
                        jTextAreaOutput.setCaretPosition(jTextAreaOutput.getDocument().getLength());
                    }
                }
            });
        }

        @Override
        protected Void doInBackground() throws Exception {
            startTime = System.currentTimeMillis();
            trainer.generate(TrainingMode.getValueByDesc(jComboBoxOps.getSelectedItem().toString()));
            return null;
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method            
                jProgressBar1.setString("Training completed.");
                long millis = System.currentTimeMillis() - startTime;
                jLabelTime.setText("Elapsed time: " + getDisplayTime(millis));
            } catch (InterruptedException ignore) {
                //ignore
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    why = cause.getMessage();
                } else {
                    why = e.getMessage();
                }
//                    e.printStackTrace();
                if (why.trim().length() == 0) {
                    // if empty, display a generic error message
                    why = "An error has occurred. Input files could be missing.";
                }
                JOptionPane.showMessageDialog(GuiWithTrainer.this, why, DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
                jProgressBar1.setVisible(false);
                jProgressBar1.setString(null);
            } catch (java.util.concurrent.CancellationException e) {
                jProgressBar1.setString("Training cancelled.");
                long millis = System.currentTimeMillis() - startTime;
                jLabelTime.setText("Elapsed time: " + getDisplayTime(millis));
            } finally {
                jButtonTrain.setEnabled(true);
                jButtonCancel.setEnabled(false);
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
            }
        }
    }

    public static String getDisplayTime(long millis) {
        String elapsedTime = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        );
        return elapsedTime;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new GuiWithTrainer().setVisible(true);
            }
        });
    }
}
