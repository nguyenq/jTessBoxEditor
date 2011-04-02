/**
 * Copyright @ 2011 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.tessboxeditor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import net.sourceforge.vietocr.utilities.ImageIOHelper;
import net.sourceforge.vietocr.utilities.Utilities;
import net.sourceforge.vietpad.components.HtmlPane;
import net.sourceforge.vietpad.components.SimpleFilter;

public class Gui extends javax.swing.JFrame {

    public static final String APP_NAME = "jTessBoxEditor";
    public static final String TO_BE_IMPLEMENTED = "To be implemented in subclass";
    static final boolean MAC_OS_X = System.getProperty("os.name").startsWith("Mac");
    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    static final String UTF8 = "UTF-8";
    ResourceBundle bundle;
    static final Preferences prefs = Preferences.userRoot().node("/net/sourceforge/tessboxeditor");
    private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    private int filterIndex;
    private FileFilter[] fileFilters;
    private File selectedFile, boxFile;
    private String currentDirectory;
    private String outputDirectory;
    private boolean boxChanged = false;
    private java.util.List<String> mruList = new java.util.ArrayList<String>();
    private String strClearRecentFiles;
    protected TessBoxCollection boxes;
    BufferedImage image;
    protected short imageIndex;
    private List<BufferedImage> imageList;
    int curIndex;
    String langCode = "eng";
    protected final File baseDir = Utilities.getBaseDir(Gui.this);
    final String[] headers = {"Char", "X", "Y", "Width", "Height"};
    DefaultTableModel model;

    /** Creates new form JTessBoxEditor */
    public Gui() {
        try {
            UIManager.setLookAndFeel(prefs.get("lookAndFeel", UIManager.getSystemLookAndFeelClassName()));
        } catch (Exception e) {
            // keep default LAF
        }
        bundle = ResourceBundle.getBundle("net.sourceforge.tessboxeditor.Gui"); // NOI18N
        initComponents();
        if (MAC_OS_X) {
            new MacOSXApplication(Gui.this);

            // remove Exit menuitem
            this.jMenuFile.remove(this.jSeparatorExit);
            this.jMenuFile.remove(this.jMenuItemExit);

            // remove About menuitem
            this.jMenuHelp.remove(this.jSeparatorAbout);
            this.jMenuHelp.remove(this.jMenuItemAbout);

//            // remove Options menuitem
//            this.jMenuSettings.remove(this.jSeparatorOptions);
//            this.jMenuSettings.remove(this.jMenuItemOptions);
        }

        model = (DefaultTableModel) this.jTable1.getModel();
        boxes = new TessBoxCollection();

        // DnD support
        new DropTarget(this.jLabelImage, new FileDropTargetListener(Gui.this));

        this.addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        quit();
                    }

                    @Override
                    public void windowOpened(WindowEvent e) {
//                        updateSave(false);
                        setExtendedState(prefs.getInt("windowState", Frame.NORMAL));
                        populateMRUList();
                    }
                });

        setSize(
                snap(prefs.getInt("frameWidth", 500), 300, screen.width),
                snap(prefs.getInt("frameHeight", 360), 150, screen.height));
        setLocation(
                snap(
                prefs.getInt("frameX", (screen.width - getWidth()) / 2),
                screen.x, screen.x + screen.width - getWidth()),
                snap(
                prefs.getInt("frameY", screen.y + (screen.height - getHeight()) / 3),
                screen.y, screen.y + screen.height - getHeight()));
    }

    private int snap(final int ideal, final int min, final int max) {
        final int TOLERANCE = 0;
        return ideal < min + TOLERANCE ? min : (ideal > max - TOLERANCE ? max : ideal);
    }

    /**
     * Populates MRU List.
     */
    private void populateMRUList() {
        String[] fileNames = prefs.get("MruList", "").split(File.pathSeparator);
        for (String fileName : fileNames) {
            if (!fileName.equals("")) {
                mruList.add(fileName);
            }
        }
        updateMRUMenu();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser = new javax.swing.JFileChooser();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonReload = new javax.swing.JButton();
        jButtonMerge = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jPanelStatus = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jButtonPrevPage = new javax.swing.JButton();
        jButtonNextPage = new javax.swing.JButton();
        jLabelPageNbr = new javax.swing.JLabel();
        jTabbedPaneBoxData = new javax.swing.JTabbedPane();
        jScrollPaneCoord = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPaneBoxData = new javax.swing.JScrollPane();
        jTextArea = new javax.swing.JTextArea();
        jScrollPaneImage = new javax.swing.JScrollPane();
        jScrollPaneImage.getVerticalScrollBar().setUnitIncrement(20);
        jScrollPaneImage.getHorizontalScrollBar().setUnitIncrement(20);
        jLabelImage = new JImageLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuRecentFiles = new javax.swing.JMenu();
        jSeparatorExit = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jMenuItemFont = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuLookAndFeel = new javax.swing.JMenu();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jSeparatorAbout = new javax.swing.JPopupMenu.Separator();
        jMenuItemAbout = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui"); // NOI18N
        jFileChooser.setDialogTitle(bundle.getString("jButtonOpen.ToolTipText")); // NOI18N

        currentDirectory = prefs.get("currentDirectory", null);
        outputDirectory = prefs.get("outputDirectory", null);
        jFileChooser.setCurrentDirectory(currentDirectory == null ? null : new File(currentDirectory));
        filterIndex = prefs.getInt("filterIndex", 0);
        FileFilter allImageFilter = new SimpleFilter("bmp;png;tif;tiff", bundle.getString("All_Image_Files"));
        FileFilter bmpFilter = new SimpleFilter("bmp", "Bitmap");
        FileFilter pngFilter = new SimpleFilter("png", "PNG");
        FileFilter tiffFilter = new SimpleFilter("tif;tiff", "TIFF");
        FileFilter textFilter = new SimpleFilter("box;txt", "Box Files");

        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.addChoosableFileFilter(allImageFilter);
        jFileChooser.addChoosableFileFilter(bmpFilter);
        jFileChooser.addChoosableFileFilter(pngFilter);
        jFileChooser.addChoosableFileFilter(tiffFilter);
        jFileChooser.addChoosableFileFilter(textFilter);
        fileFilters = jFileChooser.getChoosableFileFilters();
        if (filterIndex < fileFilters.length) {
            jFileChooser.setFileFilter(fileFilters[filterIndex]);
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jTessBoxEditor");

        jToolBar1.setRollover(true);

        jButtonOpen.setText(bundle.getString("jButtonOpen.Text")); // NOI18N
        jButtonOpen.setToolTipText(bundle.getString("jButtonOpen.ToolTipText")); // NOI18N
        jButtonOpen.setFocusable(false);
        jButtonOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonOpen);

        jButtonSave.setText(bundle.getString("jButtonSave.Text")); // NOI18N
        jButtonSave.setToolTipText(bundle.getString("jButtonSave.ToolTipText")); // NOI18N
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonSave);

        jButtonReload.setText("Reload");
        jButtonReload.setToolTipText("Reload Box File");
        jButtonReload.setFocusable(false);
        jButtonReload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonReload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReloadActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonReload);

        jButtonMerge.setText("Merge");
        jButtonMerge.setFocusable(false);
        jButtonMerge.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMerge.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMergeActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonMerge);

        jButtonDelete.setText("Delete");
        jButtonDelete.setFocusable(false);
        jButtonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDelete);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jPanelStatus.add(jLabelStatus);

        jButtonPrevPage.setText("Previous");
        jButtonPrevPage.setFocusable(false);
        jButtonPrevPage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPrevPage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPrevPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevPageActionPerformed(evt);
            }
        });
        jPanelStatus.add(jButtonPrevPage);

        jButtonNextPage.setText("Next");
        jButtonNextPage.setFocusable(false);
        jButtonNextPage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNextPage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNextPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextPageActionPerformed(evt);
            }
        });
        jPanelStatus.add(jButtonNextPage);

        jLabelPageNbr.setText("Page: ");
        jPanelStatus.add(jLabelPageNbr);

        getContentPane().add(jPanelStatus, java.awt.BorderLayout.SOUTH);

        jScrollPaneCoord.setPreferredSize(new java.awt.Dimension(200, 275));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Char", "X", "Y", "Width", "Height"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setFillsViewportHeight(true);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneCoord.setViewportView(jTable1);

        jTabbedPaneBoxData.addTab("Box Coordinates", jScrollPaneCoord);

        jTextArea.setColumns(20);
        jTextArea.setRows(5);
        jScrollPaneBoxData.setViewportView(jTextArea);

        jTabbedPaneBoxData.addTab("Box Data", jScrollPaneBoxData);

        getContentPane().add(jTabbedPaneBoxData, java.awt.BorderLayout.WEST);

        jScrollPaneImage.setViewportView(jLabelImage);

        getContentPane().add(jScrollPaneImage, java.awt.BorderLayout.CENTER);

        jMenuFile.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuFile.Mnemonic").charAt(0));
        jMenuFile.setText(bundle.getString("jMenuFile.Text")); // NOI18N

        jMenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpen.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemOpen.Mnemonic").charAt(0));
        jMenuItemOpen.setText(bundle.getString("jMenuItemOpen.Text")); // NOI18N
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemSave.Mnemonic").charAt(0));
        jMenuItemSave.setText(bundle.getString("jMenuItemSave.Text")); // NOI18N
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);
        jMenuFile.add(jSeparator1);

        jMenuRecentFiles.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuRecentFiles.Mnemonic").charAt(0));
        jMenuRecentFiles.setText(bundle.getString("jMenuRecentFiles.Text")); // NOI18N
        jMenuFile.add(jMenuRecentFiles);
        jMenuFile.add(jSeparatorExit);

        jMenuItemExit.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemExit.Mnemonic").charAt(0));
        jMenuItemExit.setText(bundle.getString("jMenuItemExit.Text")); // NOI18N
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuSettings.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuSettings.Mnemonic").charAt(0));
        jMenuSettings.setText("Settings");

        jMenuItemFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemFont.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemFont.Mnemonic").charAt(0));
        jMenuItemFont.setText(bundle.getString("jMenuItemFont.Text")); // NOI18N
        jMenuItemFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFontActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemFont);
        jMenuSettings.add(jSeparator3);

        jMenuLookAndFeel.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuLookAndFeel.Mnemonic").charAt(0));
        jMenuLookAndFeel.setText(bundle.getString("jMenuLookAndFeel.Text")); // NOI18N
        jMenuSettings.add(jMenuLookAndFeel);

        jMenuBar.add(jMenuSettings);

        jMenuHelp.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuHelp.Mnemonic").charAt(0));
        jMenuHelp.setText(bundle.getString("jMenuHelp.Text")); // NOI18N

        jMenuItemHelp.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemHelp.Mnemonic").charAt(0));
        jMenuItemHelp.setText(bundle.getString("jMenuItemHelp.Text")); // NOI18N
        jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemHelp);
        jMenuHelp.add(jSeparatorAbout);

        jMenuItemAbout.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemAbout.Mnemonic").charAt(0));
        jMenuItemAbout.setText(bundle.getString("jMenuItemAbout.Text")); // NOI18N
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar.add(jMenuHelp);

        setJMenuBar(jMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        if (jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentDirectory = jFileChooser.getCurrentDirectory().getPath();
            selectedFile = jFileChooser.getSelectedFile();
            openFile(jFileChooser.getSelectedFile());

            for (int i = 0; i < fileFilters.length; i++) {
                if (fileFilters[i] == jFileChooser.getFileFilter()) {
                    filterIndex = i;
                    break;
                }
            }
        }
    }//GEN-LAST:event_jMenuItemOpenActionPerformed
    public void openFile(final File selectedFile) {
        if (!selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, bundle.getString("File_not_exist"), APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

//        // if text file, load it into textarea
//        if (selectedFile.getName().endsWith(".txt") || selectedFile.getName().endsWith(".box")) {
//            if (!promptToSave()) {
//                return;
//            }
//            try {
//                BufferedReader in = new BufferedReader(new InputStreamReader(
//                        new FileInputStream(selectedFile), "UTF8"));
//                this.jTextArea.read(in, null);
//                in.close();
//                this.boxFile = selectedFile;
//                javax.swing.text.Document doc = this.jTextArea.getDocument();
//                if (doc.getText(0, 1).equals("\uFEFF")) {
//                    doc.remove(0, 1); // remove BOM
//                }
////                doc.addUndoableEditListener(rawListener);
//                updateMRUList(selectedFile.getPath());
////                updateSave(false);
//                this.jTextArea.requestFocusInWindow();
//            } catch (Exception e) {
//            }
//            return;
//        }

//        jLabelStatus.setText(bundle.getString("Loading_image..."));
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        SwingWorker loadWorker = new SwingWorker<File, Void>() {

            @Override
            protected File doInBackground() throws Exception {
                return selectedFile;
            }

            @Override
            protected void done() {

                try {
                    loadImage(get());
                    jLabelStatus.setText(bundle.getString("Loading_completed"));
                    updateMRUList(selectedFile.getPath());
                    // read box file
                    readBoxFile(selectedFile);
                    jLabelPageNbr.setText("    Page: " + String.valueOf(imageIndex + 1) + " of " + imageList.size());
                } catch (InterruptedException ignore) {
//                    ignore.printStackTrace();
                    jLabelStatus.setText("Loading canceled.");
                } catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        if (cause instanceof OutOfMemoryError) {
                            why = bundle.getString("OutOfMemoryError");
                        } else {
                            why = cause.getMessage();
                        }
                    } else {
                        why = e.getMessage();
                    }
//                    jLabelStatus.setText(null);
                    JOptionPane.showMessageDialog(Gui.this, why, APP_NAME, JOptionPane.ERROR_MESSAGE);
                } finally {
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                }
            }
        };

        loadWorker.execute();
    }

    void loadImage(File selectedFile) {
        try {
            imageList = ImageIOHelper.getImageList(selectedFile);
            imageIndex = 0;
            image = imageList.get(imageIndex);
            this.jLabelImage.setIcon(new ImageIcon(image));
            setButton();
            this.setTitle("jTessBoxEditor - " + selectedFile.getName());
        } catch (Exception e) {
        }
        if (imageList == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Cannotloadimage"), APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }
//
//        imageTotal = imageList.size();
//        imageIndex = 0;
//
//        displayImage();
//
//        this.setTitle(selectedFile.getName() + " - " + APP_NAME);
//
//        ((JImageLabel) jImageLabel).deselect();
//
        if (imageList.size() == 1) {
            this.jButtonNextPage.setEnabled(false);
            this.jButtonPrevPage.setEnabled(false);
        } else {
            this.jButtonNextPage.setEnabled(true);
            this.jButtonPrevPage.setEnabled(true);
        }
    }

    void readBoxFile(final File selectedFile) {
        int lastDot = selectedFile.getName().lastIndexOf(".");
        boxFile = new File(selectedFile.getParentFile(), selectedFile.getName().substring(0, lastDot) + ".box");

        if (boxFile.exists()) {
            if (!promptToSave()) {
                return;
            }

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(boxFile), "UTF8"));
                // load into textarea first
                this.jTextArea.read(in, null);
                in.close();

                // load into coordinate tab
                boxes.clear();
                String[] boxdata = this.jTextArea.getText().split("\\n");
                // Note that the coordinate system used in the box file has (0,0) at the bottom-left.
                for (String box : boxdata) {
                    String[] items = box.split("\\s+");

                    // skip invalid data
                    if (items.length < 5 || items.length > 6) {
                        continue;
                    }

                    int x = Integer.parseInt(items[1]);
                    int y = Integer.parseInt(items[2]);
                    int w = Integer.parseInt(items[3]) - x;
                    int h = Integer.parseInt(items[4]) - y;
                    y = image.getHeight() - y - h; // flip the y-coordinate

                    short page;
                    if (items.length == 6) {
                        page = Short.parseShort(items[5]); // Tess 3.0x format
                    } else {
                        page = 0; // Tess 2.0x format
                    }
                    this.boxes.add(new TessBox(items[0], new Rectangle(x, y, w, h), page));
                }

                model.setDataVector(this.boxes.getTableDataList().toArray(new String[0][5]), headers);

                ((JImageLabel) this.jLabelImage).setBoxes(this.boxes);
                ((JImageLabel) this.jLabelImage).setPage(imageIndex);
                this.boxFile = selectedFile;
                updateMRUList(selectedFile.getPath());
//                updateSave(false);

            } catch (Exception e) {
            }
            return;
        }
    }

    // in the your JTable
//   jump to last row
//rowCount =  table.getRowCount () - 1;
//showCell(rowcount, 0);
//
////   jump to first row
//showCell(0, 0);
    void showCell(int row, int column) {
        Rectangle rect = this.jTable1.getCellRect(row, column, true);
        this.jScrollPaneCoord.scrollRectToVisible(rect);
        this.jTable1.clearSelection();
        this.jTable1.setRowSelectionInterval(row, row);
        model.fireTableDataChanged(); // notify the model
    }

    /**
     *  Displays a dialog to save changes.
     *
     *@return    false if user canceled, true else
     */
    protected boolean promptToSave() {
        if (!boxChanged) {
            return true;
        }
        switch (JOptionPane.showConfirmDialog(this,
                bundle.getString("Do_you_want_to_save_the_changes_to_")
                + (boxFile == null ? bundle.getString("Untitled") : boxFile.getName()) + "?",
                APP_NAME, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
            case JOptionPane.YES_OPTION:
                return saveAction();
            case JOptionPane.NO_OPTION:
                return true;
            default:
                return false;
        }
    }

    boolean saveAction() {
        if (boxFile == null || !boxFile.exists()) {
            return saveFileDlg();
        } else {
            return saveBoxFile();
        }
    }

    boolean saveFileDlg() {
        JFileChooser saveChooser = new JFileChooser(outputDirectory);
        FileFilter textFilter = new SimpleFilter("box;txt", "Box Files");
        saveChooser.addChoosableFileFilter(textFilter);
        saveChooser.setDialogTitle(bundle.getString("Save_As"));
        if (boxFile != null) {
            saveChooser.setSelectedFile(boxFile);
        }

        if (saveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = saveChooser.getCurrentDirectory().getPath();
            File f = saveChooser.getSelectedFile();
            if (saveChooser.getFileFilter() == textFilter) {
                if (!f.getName().endsWith(".box")) {
                    f = new File(f.getPath() + ".box");
                }
                if (boxFile != null && boxFile.getPath().equals(f.getPath())) {
                    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
                            Gui.this,
                            boxFile.getName() + bundle.getString("file_already_exist"),
                            bundle.getString("Confirm_Save_As"), JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE)) {
                        return false;
                    }
                } else {
                    boxFile = f;
                }
            }
            return saveBoxFile();
        } else {
            return false;
        }
    }

    boolean saveBoxFile() {
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(boxFile), UTF8));
            out.write("");
            out.close();
            updateMRUList(boxFile.getPath());
//            updateSave(false);
        } catch (OutOfMemoryError oome) {
//            oome.printStackTrace();
            JOptionPane.showMessageDialog(this, oome.getMessage(), bundle.getString("OutOfMemoryError"), JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException fnfe) {
        } catch (Exception ex) {
        } finally {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                }
            });
        }

        return true;
    }

    /**
     * Update MRU List.
     *
     * @param fileName
     */
    private void updateMRUList(String fileName) {
        if (mruList.contains(fileName)) {
            mruList.remove(fileName);
        }
        mruList.add(0, fileName);

        if (mruList.size() > 10) {
            mruList.remove(10);
        }

        updateMRUMenu();
    }

    /**
     * Update MRU Submenu.
     */
    private void updateMRUMenu() {
        this.jMenuRecentFiles.removeAll();

        if (mruList.isEmpty()) {
            this.jMenuRecentFiles.add(bundle.getString("No_Recent_Files"));
        } else {
            Action mruAction = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JMenuItem item = (JMenuItem) e.getSource();
                    String fileName = item.getText();

                    if (fileName.equals(strClearRecentFiles)) {
                        mruList.clear();
                        jMenuRecentFiles.removeAll();
                        jMenuRecentFiles.add(bundle.getString("No_Recent_Files"));
                    } else {
                        openFile(new File(fileName));
                    }
                }
            };

            for (String fileName : mruList) {
                JMenuItem item = this.jMenuRecentFiles.add(fileName);
                item.addActionListener(mruAction);
            }
            this.jMenuRecentFiles.addSeparator();
            strClearRecentFiles = bundle.getString("Clear_Recent_Files");
            JMenuItem jMenuItemClear = this.jMenuRecentFiles.add(strClearRecentFiles);
            jMenuItemClear.setMnemonic(bundle.getString("jMenuItemClear.Mnemonic").charAt(0));
            jMenuItemClear.addActionListener(mruAction);
        }
    }

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        saveAction();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        quit();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    void quit() {
        if (!promptToSave()) {
            return;
        }

        if (currentDirectory != null) {
            prefs.put("currentDirectory", currentDirectory);
        }
        if (outputDirectory != null) {
            prefs.put("outputDirectory", outputDirectory);
        }

        prefs.putInt("windowState", getExtendedState());

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.mruList.size(); i++) {
            buf.append(this.mruList.get(i)).append(File.pathSeparatorChar);
        }
        prefs.put("MruList", buf.toString());

        if (getExtendedState() == NORMAL) {
            prefs.putInt("frameHeight", getHeight());
            prefs.putInt("frameWidth", getWidth());
            prefs.putInt("frameX", getX());
            prefs.putInt("frameY", getY());
        }

        prefs.putInt("filterIndex", filterIndex);

        System.exit(0);
    }

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        about();
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    void about() {
        try {
            Properties config = new Properties();
            config.loadFromXML(getClass().getResourceAsStream("config.xml"));
            String version = config.getProperty("Version");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date releaseDate = sdf.parse(config.getProperty("ReleaseDate"));

            JOptionPane.showMessageDialog(this, APP_NAME + ", " + version + " \u00a9 2011\n"
                    + "Box Editor for Tesseract OCR Data\n"
                    + DateFormat.getDateInstance(DateFormat.LONG).format(releaseDate)
                    + "\nhttp://vietocr.sourceforge.net", jMenuItemAbout.getText(), JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    private void jButtonPrevPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevPageActionPerformed
        if (imageIndex > 0) {
            --imageIndex;
            jLabelPageNbr.setText("    Image: " + String.valueOf(imageIndex + 1) + " of " + imageList.size());
            loadImage();
            showCell(0, 0);
        }
    }//GEN-LAST:event_jButtonPrevPageActionPerformed

    private void jButtonNextPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextPageActionPerformed
        if (imageIndex < imageList.size() - 1) {
            ++imageIndex;
            jLabelPageNbr.setText("    Image: " + String.valueOf(imageIndex + 1) + " of " + imageList.size());
            loadImage();
            showCell(100, 0);
        }
    }//GEN-LAST:event_jButtonNextPageActionPerformed

    void loadImage() {
        if (imageIndex >= 0 || imageIndex < imageList.size() - 1) {
            image = imageList.get(imageIndex);
            this.jLabelImage.setIcon(new ImageIcon(image));
        }
        setButton();
        ((JImageLabel) this.jLabelImage).setPage(imageIndex);
    }

    void setButton() {
        if (imageIndex == 0) {
            this.jButtonPrevPage.setEnabled(false);
        } else {
            this.jButtonPrevPage.setEnabled(true);
        }

        if (imageIndex == imageList.size() - 1) {
            this.jButtonNextPage.setEnabled(false);
        } else {
            this.jButtonNextPage.setEnabled(true);
        }
    }

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        jMenuItemOpenActionPerformed(evt);
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        this.jMenuItemSaveActionPerformed(evt);
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReloadActionPerformed
        if (selectedFile != null) {
            readBoxFile(selectedFile);
        }
    }//GEN-LAST:event_jButtonReloadActionPerformed

    private void jMenuItemFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFontActionPerformed
        openFontDialog();
    }//GEN-LAST:event_jMenuItemFontActionPerformed

    private void jButtonMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMergeActionPerformed
        mergeAction();
    }//GEN-LAST:event_jButtonMergeActionPerformed
    void mergeAction() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }
    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        deleteAction();
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
        final String readme = bundle.getString("readme");
        if (MAC_OS_X && new File(readme).exists()) {
            try {
                Runtime.getRuntime().exec(new String[]{"open", "-a", "Help Viewer", readme});
            } catch (IOException x) {
                x.printStackTrace();
            }
        } else {
            if (helptopicsFrame == null) {
                helptopicsFrame = new JFrame(jMenuItemHelp.getText());
                helptopicsFrame.getContentPane().setLayout(new BorderLayout());
                HtmlPane helpPane = new HtmlPane(readme);
                helptopicsFrame.getContentPane().add(helpPane, BorderLayout.CENTER);
                helptopicsFrame.getContentPane().add(helpPane.getStatusBar(), BorderLayout.SOUTH);
                helptopicsFrame.pack();
                helptopicsFrame.setLocation((screen.width - helptopicsFrame.getWidth()) / 2, 40);
            }
            helptopicsFrame.setVisible(true);
        }
    }//GEN-LAST:event_jMenuItemHelpActionPerformed
    void deleteAction() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    void openFontDialog() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonMerge;
    private javax.swing.JButton jButtonNextPage;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonPrevPage;
    private javax.swing.JButton jButtonReload;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JFileChooser jFileChooser;
    protected javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelPageNbr;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemFont;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSave;
    protected javax.swing.JMenu jMenuLookAndFeel;
    private javax.swing.JMenu jMenuRecentFiles;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JScrollPane jScrollPaneBoxData;
    private javax.swing.JScrollPane jScrollPaneCoord;
    private javax.swing.JScrollPane jScrollPaneImage;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparatorAbout;
    private javax.swing.JPopupMenu.Separator jSeparatorExit;
    private javax.swing.JTabbedPane jTabbedPaneBoxData;
    protected javax.swing.JTable jTable1;
    protected javax.swing.JTextArea jTextArea;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    private JFrame helptopicsFrame;
}
