/**
 * Copyright @ 2011 Quan Nguyen
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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageIOHelper;
import static net.sourceforge.tessboxeditor.Gui.APP_NAME;
import static net.sourceforge.tessboxeditor.Gui.prefs;
import net.sourceforge.tessboxeditor.datamodel.TessBox;
import net.sourceforge.tessboxeditor.datamodel.TessBoxCollection;

public class GuiWithEdit extends GuiWithMRU implements PropertyChangeListener {

    protected String tessDirectory;
    protected String tessdataPath;
    private OcrSegmentWorker ocrSegmentWorker;
    private OcrSegmentBulkWorker ocrSegmentBulkWorker;
    private ProgressMonitor progressMonitor;

    private final static Logger logger = Logger.getLogger(GuiWithEdit.class.getName());

    public GuiWithEdit() {
        tessDirectory = prefs.get("tessDirectory", WINDOWS ? new File(System.getProperty("user.dir"), "tesseract-ocr").getPath() : "/usr/bin");
        tessdataPath = WINDOWS ? tessDirectory + "/tessdata" : "/usr/share/tesseract-ocr/4.00/tessdata";
    }

    @Override
    void jMenuItemMergeActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Please select more than one box for Merge operation.");
            return;
        }

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;

        String chrs = "";
        short page = 0;
        int index = 0;

        for (TessBox box : selected) {
            chrs += box.getChrs();
            page = box.getPage();
            index = this.boxes.toList().indexOf(box);
            Rectangle rect = box.getRect();
            minX = Math.min(minX, rect.x);
            minY = Math.min(minY, rect.y);
            maxX = Math.max(maxX, rect.x + rect.width);
            maxY = Math.max(maxY, rect.y + rect.height);
            this.boxes.remove(box);
        }

        if (chrs.length() > 0) {
            TessBox newBox = new TessBox(chrs, new Rectangle(minX, minY, maxX - minX, maxY - minY), page);
            newBox.setSelected(true);
            boxes.add(index, newBox);
            int tableIndex = this.boxes.toList().indexOf(newBox);
            tableModel.setDataVector(boxes.getTableDataList().toArray(new String[0][5]), headers);
            this.jTable.setRowSelectionInterval(tableIndex, tableIndex);
            Rectangle rect = this.jTable.getCellRect(tableIndex, 0, true);
            this.jTable.scrollRectToVisible(rect);
        }

        this.jLabelImage.repaint();
        updateSave(true);
    }

    @Override
    void jMenuItemSplitActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a box to split.");
            return;
        } else if (selected.size() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one box for Split operation.");
            return;
        }

        boolean modifierKeyPressed = false;
        int modifiers = evt.getModifiers();
        if ((modifiers & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK
                || (modifiers & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK
                || (modifiers & ActionEvent.META_MASK) == ActionEvent.META_MASK) {
            modifierKeyPressed = true;
        }

        TessBox box = selected.get(0);
        int index = this.boxes.toList().indexOf(box);
        Rectangle rect = box.getRect();
        if (!modifierKeyPressed) {
            rect.width /= 2;
            tableModel.setValueAt(String.valueOf(rect.width), index, 3);
        } else {
            rect.height /= 2;
            tableModel.setValueAt(String.valueOf(rect.height), index, 4);
        }

        TessBox newBox = new TessBox(box.getChrs(), new Rectangle(rect), box.getPage());
        newBox.setSelected(true);
        boxes.add(index + 1, newBox);
        Rectangle newRect = newBox.getRect();
        if (!modifierKeyPressed) {
            newRect.x += newRect.width;
        } else {
            newRect.y += newRect.height;
        }

        Object[] newRow = {newBox.getChrs(), newRect.x, newRect.y, newRect.width, newRect.height};
        tableModel.insertRow(index + 1, newRow);
        jTable.setRowSelectionInterval(index, index + 1);
        resetReadout();
        this.jLabelImage.repaint();
        updateSave(true);
    }

    @Override
    void jMenuItemInsertActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select the box to insert after.");
            return;
        } else if (selected.size() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one box for Insert operation.");
            return;
        }

        TessBox box = selected.get(0);
        int index = this.boxes.toList().indexOf(box);
        index++;
        TessBox newBox = new TessBox(" ", new Rectangle(box.getRect()), box.getPage());
        newBox.setSelected(true);
        boxes.add(index, newBox);
        Rectangle newRect = newBox.getRect();
        newRect.x += 15; // offset the new box 15 pixel from the base one
        Object[] newRow = {newBox.getChrs(), newRect.x, newRect.y, newRect.width, newRect.height};
        tableModel.insertRow(index, newRow);
        jTable.setRowSelectionInterval(index, index);
        this.jLabelImage.repaint();
        updateSave(true);
    }

    @Override
    void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a box or more to delete.");
            return;
        }

        for (TessBox box : selected) {
            int index = this.boxes.toList().indexOf(box);
            this.boxes.remove(box);
            tableModel.removeRow(index);
        }

        resetReadout();
        this.jLabelImage.repaint();
        updateSave(true);
    }

    @Override
    void jMenuItemMarkEOLActionPerformed(java.awt.event.ActionEvent evt) {
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        // instantiate SwingWorker for OCR
        ocrSegmentWorker = new OcrSegmentWorker(imageList, boxPages);
        ocrSegmentWorker.execute();
    }

    @Override
    void jMenuItemMarkEOLBulkActionPerformed(java.awt.event.ActionEvent evt) {
        jFileChooserInputImage.setMultiSelectionEnabled(true);
        if (jFileChooserInputImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getGlassPane().setVisible(true);

            progressMonitor = new ProgressMonitor(GuiWithEdit.this, "Marking EOL with tab...", "", 0, 100);
            progressMonitor.setMillisToDecideToPopup(0);
            progressMonitor.setProgress(0);

            // instantiate SwingWorker for OCR
            ocrSegmentBulkWorker = new OcrSegmentBulkWorker(jFileChooserInputImage.getSelectedFiles());
            ocrSegmentBulkWorker.addPropertyChangeListener(this);
            ocrSegmentBulkWorker.execute();
            jMenuItemMarkEOLBulk.setEnabled(false);
        }
        jFileChooserInputImage.setMultiSelectionEnabled(false);
    }

    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
            if (progressMonitor.isCanceled() || ocrSegmentBulkWorker.isDone()) {
                if (progressMonitor.isCanceled()) {
                    ocrSegmentBulkWorker.cancel(true);
                }
                jMenuItemMarkEOLBulk.setEnabled(true);
            }
        }
    }

    /**
     * A worker class for managing OCR process.
     */
    class OcrSegmentWorker extends SwingWorker<Void, Void> {

        List<BufferedImage> imageList;
        List<TessBoxCollection> boxPages;

        OcrSegmentWorker(List<BufferedImage> imageList, List<TessBoxCollection> boxPages) {
            this.imageList = imageList;
            this.boxPages = boxPages;
        }

        @Override
        protected Void doInBackground() throws Exception {
            Tesseract instance = new Tesseract();
            instance.setDatapath(tessdataPath);
            performSegment(imageList, boxPages, instance);
            return null;
        }

        @Override
        protected void done() {
            try {
                get(); // dummy method
                resetReadout();
                loadTable();
                jLabelImage.repaint();
                updateSave(true);
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
                JOptionPane.showMessageDialog(null, why, APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
            }
        }
    }

    void performSegment(final List<BufferedImage> imageList, final List<TessBoxCollection> boxPages, final ITesseract instance) throws Exception {
        short pageIndex = 0;
        for (BufferedImage image : imageList) {
            // Perform text-line segmentation
            List<Rectangle> regions = instance.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);
            TessBoxCollection boxesPerPage = boxPages.get(pageIndex); // boxes per page
            for (Rectangle rect : regions) { // process each line
                TessBox lastBox = boxesPerPage.toList().stream().filter((r) -> {
                    return rect.contains(r.getRect());
                }).reduce((first, second) -> second).orElse(null);

                if (lastBox == null) {
                    continue;
                }

                int index = boxesPerPage.toList().indexOf(lastBox);
                Rectangle nRect = new Rectangle(lastBox.getRect());
                nRect.x += nRect.width + 10;
                boxesPerPage.add(index + 1, new TessBox("\t", nRect, pageIndex));
            }
            pageIndex++;
        }
    }

    /**
     * A worker class for managing OCR process.
     */
    class OcrSegmentBulkWorker extends SwingWorker<Void, String> {

        File[] files;

        OcrSegmentBulkWorker(File[] files) {
            this.files = files;
        }

        @Override
        protected Void doInBackground() throws Exception {
            int progress = 0;
            setProgress(0);
            Tesseract instance = new Tesseract();
            instance.setDatapath(tessdataPath);

            int tick = (int) Math.ceil(100f / files.length);

            for (File imageFile : files) {
                int lastDot = imageFile.getName().lastIndexOf(".");
                File boxFile = new File(imageFile.getParentFile(), imageFile.getName().substring(0, lastDot) + ".box");
                if (!boxFile.exists()) {
                    continue;
                }

                List<BufferedImage> imageList = ImageIOHelper.getImageList(imageFile);
                String str = readBoxFile(boxFile);
                List<TessBoxCollection> boxPages = parseBoxString(str, imageList);
                performSegment(imageList, boxPages, instance);

                // save boxes
                try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(boxFile), StandardCharsets.UTF_8))) {
                    out.write(formatOutputString(imageList, boxPages));
                }

                progress += tick;
                setProgress(Math.min(progress, 100));
                publish(imageFile.getName());
            }

            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            if (isCancelled()) {
                return;
            }

            for (String message : chunks) {
                progressMonitor.setNote(message);
            }
        }

        @Override
        protected void done() {
            try {
                get(); // dummy method
                JOptionPane.showMessageDialog(null, "EOL tab characters have been inserted in selected box files.", APP_NAME, JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(null, why, APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new GuiWithEdit().setVisible(true);
            }
        });
    }
}
