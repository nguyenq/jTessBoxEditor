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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.*;
import net.sourceforge.tessboxeditor.components.*;
import net.sourceforge.tessboxeditor.datamodel.*;
import net.sourceforge.vietocr.util.Utils;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.vietpad.components.*;
import net.sourceforge.vietpad.utilities.TextUtilities;

public class Gui extends javax.swing.JFrame {

    public static final String APP_NAME = "jTessBoxEditor";
    public static final String TO_BE_IMPLEMENTED = "To be implemented in subclass";
    public static final String WORDSTR = "WordStr";
    final String[] headers = {"Char", "X", "Y", "Width", "Height"};
    static final boolean MAC_OS_X = System.getProperty("os.name").startsWith("Mac");
    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    static final String EOL = System.getProperty("line.separator");
    protected ResourceBundle bundle;
    static final Preferences prefs = Preferences.userRoot().node("/net/sourceforge/tessboxeditor");
    private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    private int filterIndex;
    private FileFilter[] fileFilters;
    private File boxFile;
    private String currentDirectory, outputDirectory;
    private boolean boxChanged = true;
    protected boolean tableSelectAction;
    protected List<TessBoxCollection> boxPages;
    protected TessBoxCollection boxes; // boxes of current page
    private TessBox removedBox;
    protected short imageIndex;
    protected List<BufferedImage> imageList;
    protected final File baseDir;
    DefaultTableModel tableModel;
    private boolean isTess2_0Format;
    private boolean isWordStrFormat;
    protected RowHeaderList rowHeader;
    protected Font font;

    protected static int iconMargin = 3;
    protected static boolean invertControls = false;
    protected static int scaleFactor = 4;
    protected static int iconPosX = 0;
    protected static int iconPosY = 0;
    protected static int iconWidth = 0;
    protected static int iconHeight = 0;
    protected static int imageWidth = 0;
    protected static int imageHeight = 0;
    protected static int movementMultiplier = 1;
    protected static int fixedMinColWidth = 43;
    protected static int fixedMaxColWidth = 50;

    private final static Logger logger = Logger.getLogger(Gui.class.getName());

    /**
     * Creates new form JTessBoxEditor.
     */
    public Gui() {
        this.baseDir = Utils.getBaseDir(Gui.this);
        try {
            UIManager.setLookAndFeel(prefs.get("lookAndFeel", UIManager.getSystemLookAndFeelClassName()));
        } catch (Exception e) {
            // keep default LAF
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        bundle = ResourceBundle.getBundle("net.sourceforge.tessboxeditor.Gui"); // NOI18N
        initComponents();

        boxPages = new ArrayList<TessBoxCollection>();

        // DnD support
        new DropTarget(this.jSplitPaneEditor, new FileDropTargetListener(Gui.this, this.jSplitPaneEditor));

        this.addWindowListener(
                new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                updateSave(false);
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

        KeyEventDispatcher dispatcher = new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_F3) {
                        jButtonFind.doClick();
                    }
                }
                return false;
            }
        };
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
    }

    private int snap(final int ideal, final int min, final int max) {
        final int TOLERANCE = 0;
        return ideal < min + TOLERANCE ? min : (ideal > max - TOLERANCE ? max : ideal);
    }

    /**
     * Populates MRU List.
     */
    protected void populateMRUList() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserInputImage = new javax.swing.JFileChooser();
        jDialogValidationResult = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaValidationResult = new javax.swing.JTextArea();
        jPanelCommand = new javax.swing.JPanel();
        jButtonCloseDialog = new javax.swing.JButton();
        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jPanelTrainer = new javax.swing.JPanel();
        jToolBarTrainer = new javax.swing.JToolBar();
        jPanelMain = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldTessDir = new javax.swing.JTextField();
        jButtonBrowseTess = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldDataDir = new javax.swing.JTextField();
        jButtonBrowseData = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldLang = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldBootstrapLang = new javax.swing.JTextField();
        jCheckBoxRTL = new javax.swing.JCheckBox();
        jComboBoxOps = new javax.swing.JComboBox();
        jButtonTrain = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jButtonCancel.setEnabled(false);
        jButtonValidate = new javax.swing.JButton();
        jButtonSaveLog = new javax.swing.JButton();
        jButtonClearLog = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaOutput = new javax.swing.JTextArea();
        jPanelStatus1 = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 17), new java.awt.Dimension(0, 17), new java.awt.Dimension(32767, 17));
        jProgressBar1 = new javax.swing.JProgressBar();
        jProgressBar1.setVisible(false);
        jLabelTime = new javax.swing.JLabel();
        jPanelEditor = new javax.swing.JPanel();
        jToolBarEditor = new javax.swing.JToolBar();
        jPanel4 = new javax.swing.JPanel();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonReload = new javax.swing.JButton();
        jButtonMerge = new javax.swing.JButton();
        jButtonSplit = new javax.swing.JButton();
        jButtonInsert = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jPanelSpinner = new javax.swing.JPanel();
        jLabelCharacter = new javax.swing.JLabel();
        jTextFieldCharacter = new javax.swing.JTextField();
        jButtonConvert = new javax.swing.JButton();
        jLabelX = new javax.swing.JLabel();
        jSpinnerX = new javax.swing.JSpinner();
        jLabelY = new javax.swing.JLabel();
        jSpinnerY = new javax.swing.JSpinner();
        jLabelW = new javax.swing.JLabel();
        jSpinnerW = new javax.swing.JSpinner();
        jLabelH = new javax.swing.JLabel();
        jSpinnerH = new javax.swing.JSpinner();
        jSplitPaneEditor = new javax.swing.JSplitPane();
        jTabbedPaneBoxData = new javax.swing.JTabbedPane();
        jPanelCoord = new javax.swing.JPanel();
        jScrollPaneCoord = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable() {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (font != null && column == 0) {
                    c.setFont(font);
                }
                return c;
            }
        };
        TableCellRenderer tcr = this.jTable.getDefaultRenderer(Integer.class);
        DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) tcr;
        dtcr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable.setDragEnabled(true);
        jTable.setDropMode(DropMode.INSERT_ROWS);
        jTable.setTransferHandler(new TableRowTransferHandler(jTable)); ;
        jPanelFind = new javax.swing.JPanel();
        jTextFieldFind = new javax.swing.JTextField();
        jButtonFind = new javax.swing.JButton();
        jScrollPaneBoxData = new javax.swing.JScrollPane();
        jTextAreaBoxData = new javax.swing.JTextArea();
        jPanelBoxView = new javax.swing.JPanel();
        jPanelNorthContainer = new javax.swing.JPanel();
        jPanelChar = new javax.swing.JPanel();
        jLabelCodepoint = new javax.swing.JLabel();
        jLabelCodepoint.setFont(jLabelCodepoint.getFont().deriveFont(14.0f));
        jTextFieldChar = new javax.swing.JTextField();
        jTextFieldChar.setFont(jTextFieldChar.getFont().deriveFont(14.0f));
        jTextFieldCodepointValue = new javax.swing.JTextField();
        jTextFieldCodepointValue.setFont(jTextFieldCodepointValue.getFont().deriveFont(14.0f));
        jPanelControls = new javax.swing.JPanel();
        jLabelSpinnerMargin = new javax.swing.JLabel();
        jSpinnerMargin = new javax.swing.JSpinner();
        jLabelSpinnerScale = new javax.swing.JLabel();
        jSpinnerScale = new javax.swing.JSpinner();
        jLabelSubimage = new SubImageView();
        jPanelButtons = new javax.swing.JPanel();
        jButtonPrev = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jScrollPaneImage = new javax.swing.JScrollPane();
        jScrollPaneImage.getVerticalScrollBar().setUnitIncrement(20);
        jScrollPaneImage.getHorizontalScrollBar().setUnitIncrement(20);
        jLabelImage = new JImageLabel();
        jPanelStatus = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelPageNbr = new javax.swing.JLabel();
        jButtonPrevPage = new javax.swing.JButton();
        jButtonNextPage = new javax.swing.JButton();
        jPanelTIFFBox = new javax.swing.JPanel();
        jToolBarGenerator = new javax.swing.JToolBar();
        jPanel3 = new javax.swing.JPanel();
        jCheckBoxText2Image = new javax.swing.JCheckBox();
        jButtonInput = new javax.swing.JButton();
        jLabelOutput = new javax.swing.JLabel();
        jTextFieldOuputDir = new javax.swing.JTextField();
        jButtonBrowseOutputDir = new javax.swing.JButton();
        jTextFieldPrefix = new javax.swing.JTextField();
        jTextFieldPrefix.setText(prefs.get("trainLanguage", "eng"));
        jTextFieldFileName = new javax.swing.JTextField();
        jButtonFont = new javax.swing.JButton();
        jPanelFontFolder = new javax.swing.JPanel();
        jPanelFontFolder.setVisible(false);
        FlowLayout layout1 = (FlowLayout) jPanelFontFolder.getLayout();
        layout1.setVgap(0);
        jTextFieldFontFolder = new javax.swing.JTextField();
        jButtonBrowseFontFolder = new javax.swing.JButton();
        jLabelExposure = new javax.swing.JLabel();
        jSpinnerExposure = new javax.swing.JSpinner();
        jPanelFontAttrib = new javax.swing.JPanel();
        FlowLayout layout = (FlowLayout) jPanelFontAttrib.getLayout();
        layout.setVgap(0);
        jCheckBoxAntiAliasing = new javax.swing.JCheckBox();
        jLabelNoise = new javax.swing.JLabel();
        jSpinnerNoise = new javax.swing.JSpinner();
        jLabelTracking = new javax.swing.JLabel();
        jSpinnerTracking = new javax.swing.JSpinner();
        jLabelLeading = new javax.swing.JLabel();
        jSpinnerLeading = new javax.swing.JSpinner();
        jLabelW1 = new javax.swing.JLabel();
        jSpinnerW1 = new javax.swing.JSpinner();
        jLabelH1 = new javax.swing.JLabel();
        jSpinnerH1 = new javax.swing.JSpinner();
        jButtonGenerate = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jScrollPaneText = new javax.swing.JScrollPane();
        jTextAreaInput = new javax.swing.JTextArea();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparatorRecentFiles = new javax.swing.JPopupMenu.Separator();
        jMenuRecentFiles = new javax.swing.JMenu();
        jSeparatorExit = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        this.jMenuEdit.setVisible(false);
        jMenuItemMerge = new javax.swing.JMenuItem();
        jMenuItemSplit = new javax.swing.JMenuItem();
        jMenuItemInsert = new javax.swing.JMenuItem();
        jMenuItemDelete = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemMarkEOL = new javax.swing.JMenuItem();
        jMenuItemMarkEOLBulk = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemRemoveEmptyBoxes = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jMenuItemFont = new javax.swing.JMenuItem();
        jSeparatorLAF = new javax.swing.JPopupMenu.Separator();
        jMenuLookAndFeel = new javax.swing.JMenu();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemMergeTiff = new javax.swing.JMenuItem();
        jMenuItemSplitTiff = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jSeparatorAbout = new javax.swing.JPopupMenu.Separator();
        jMenuItemAbout = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui"); // NOI18N
        jFileChooserInputImage.setDialogTitle(bundle.getString("jButtonOpen.ToolTipText")); // NOI18N

        currentDirectory = prefs.get("currentDirectory", null);
        outputDirectory = currentDirectory;
        jFileChooserInputImage.setCurrentDirectory(currentDirectory == null ? null : new File(currentDirectory));
        filterIndex = prefs.getInt("filterIndex", 0);
        FileFilter allImageFilter = new SimpleFilter("bmp;jpg;jpeg;png;tif;tiff", bundle.getString("All_Image_Files"));
        FileFilter pngFilter = new SimpleFilter("png", "PNG");
        FileFilter tiffFilter = new SimpleFilter("tif;tiff", "TIFF");
        //FileFilter textFilter = new SimpleFilter("box;txt", "Box Files");

        jFileChooserInputImage.setAcceptAllFileFilterUsed(false);
        jFileChooserInputImage.addChoosableFileFilter(allImageFilter);
        jFileChooserInputImage.addChoosableFileFilter(pngFilter);
        jFileChooserInputImage.addChoosableFileFilter(tiffFilter);
        //jFileChooser.addChoosableFileFilter(textFilter);
        fileFilters = jFileChooserInputImage.getChoosableFileFilters();
        if (filterIndex < fileFilters.length) {
            jFileChooserInputImage.setFileFilter(fileFilters[filterIndex]);
        }

        jDialogValidationResult.setTitle("Validation Result");
        jDialogValidationResult.setMinimumSize(new java.awt.Dimension(600, 450));

        jTextAreaValidationResult.setEditable(false);
        jTextAreaValidationResult.setColumns(20);
        jTextAreaValidationResult.setRows(5);
        jTextAreaValidationResult.setMargin(new java.awt.Insets(5, 5, 2, 2));
        jScrollPane2.setViewportView(jTextAreaValidationResult);

        jDialogValidationResult.getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanelCommand.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 5, 1));

        jButtonCloseDialog.setText("Close");
        jButtonCloseDialog.setToolTipText("Close Dialog");
        jButtonCloseDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseDialogActionPerformed(evt);
            }
        });
        jPanelCommand.add(jButtonCloseDialog);

        jDialogValidationResult.getContentPane().add(jPanelCommand, java.awt.BorderLayout.SOUTH);

        jDialogValidationResult.setLocationRelativeTo(this);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jTessBoxEditor");

        jTabbedPaneMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(-2, 0, 0, 0));
        jTabbedPaneMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneMainStateChanged(evt);
            }
        });

        jPanelTrainer.setLayout(new java.awt.BorderLayout());

        jToolBarTrainer.setRollover(true);

        jPanelMain.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel4.setText("Tesseract Executables");
        jPanelMain.add(jLabel4);

        jTextFieldTessDir.setToolTipText("Location of Tesseract Executables");
        jTextFieldTessDir.setEnabled(false);
        jTextFieldTessDir.setPreferredSize(new java.awt.Dimension(180, 24));
        jPanelMain.add(jTextFieldTessDir);

        jButtonBrowseTess.setText("...");
        jButtonBrowseTess.setToolTipText("Browse");
        jButtonBrowseTess.setMaximumSize(new java.awt.Dimension(30, 23));
        jButtonBrowseTess.setMinimumSize(new java.awt.Dimension(30, 23));
        jButtonBrowseTess.setPreferredSize(new java.awt.Dimension(24, 23));
        jButtonBrowseTess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseTessActionPerformed(evt);
            }
        });
        jPanelMain.add(jButtonBrowseTess);

        jLabel3.setText("Training Data");
        jPanelMain.add(jLabel3);

        jTextFieldDataDir.setToolTipText("Location of Source Training Data");
        jTextFieldDataDir.setEnabled(false);
        jTextFieldDataDir.setPreferredSize(new java.awt.Dimension(180, 24));
        jPanelMain.add(jTextFieldDataDir);

        jButtonBrowseData.setText("...");
        jButtonBrowseData.setToolTipText("Browse");
        jButtonBrowseData.setMaximumSize(new java.awt.Dimension(30, 23));
        jButtonBrowseData.setMinimumSize(new java.awt.Dimension(30, 23));
        jButtonBrowseData.setPreferredSize(new java.awt.Dimension(24, 23));
        jButtonBrowseData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseDataActionPerformed(evt);
            }
        });
        jPanelMain.add(jButtonBrowseData);

        jLabel1.setText("Language");
        jPanelMain.add(jLabel1);

        jTextFieldLang.setMinimumSize(new java.awt.Dimension(34, 19));
        jTextFieldLang.setPreferredSize(new java.awt.Dimension(30, 24));
        jPanelMain.add(jTextFieldLang);

        jLabel2.setText("Bootstrap Language");
        jPanelMain.add(jLabel2);

        jTextFieldBootstrapLang.setMinimumSize(new java.awt.Dimension(34, 19));
        jTextFieldBootstrapLang.setPreferredSize(new java.awt.Dimension(30, 24));
        jPanelMain.add(jTextFieldBootstrapLang);

        jCheckBoxRTL.setText("RTL");
        jCheckBoxRTL.setToolTipText("Right-To-Left Text Direction");
        jPanelMain.add(jCheckBoxRTL);

        jComboBoxOps.setModel(new DefaultComboBoxModel(TrainingMode.values()));
        jComboBoxOps.setToolTipText("Training Mode");
        jPanelMain.add(jComboBoxOps);

        jButtonTrain.setText("Run");
        jButtonTrain.setToolTipText("Start Training");
        jButtonTrain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTrainActionPerformed(evt);
            }
        });
        jPanelMain.add(jButtonTrain);

        jButtonCancel.setText("Cancel");
        jButtonCancel.setToolTipText("Cancel Training");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanelMain.add(jButtonCancel);

        jButtonValidate.setText("Validate");
        jButtonValidate.setToolTipText("Validate Generated Traineddata");
        jButtonValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonValidateActionPerformed(evt);
            }
        });
        jPanelMain.add(jButtonValidate);

        jButtonSaveLog.setText("Save");
        jButtonSaveLog.setToolTipText("Save Log");
        jButtonSaveLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveLogActionPerformed(evt);
            }
        });
        jPanelMain.add(jButtonSaveLog);

        jButtonClearLog.setText("Clear");
        jButtonClearLog.setToolTipText("Clear Textaera");
        jButtonClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearLogActionPerformed(evt);
            }
        });
        jPanelMain.add(jButtonClearLog);

        jToolBarTrainer.add(jPanelMain);

        jPanelTrainer.add(jToolBarTrainer, java.awt.BorderLayout.PAGE_START);

        jTextAreaOutput.setEditable(false);
        jTextAreaOutput.setColumns(20);
        jTextAreaOutput.setRows(5);
        jTextAreaOutput.setMargin(new java.awt.Insets(5, 5, 2, 2));
        jScrollPane1.setViewportView(jTextAreaOutput);

        jPanelTrainer.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanelStatus1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanelStatus1.add(filler2);

        jProgressBar1.setStringPainted(true);
        jPanelStatus1.add(jProgressBar1);
        jPanelStatus1.add(jLabelTime);

        jPanelTrainer.add(jPanelStatus1, java.awt.BorderLayout.SOUTH);

        jTabbedPaneMain.addTab("Trainer", jPanelTrainer);

        jPanelEditor.setLayout(new java.awt.BorderLayout());

        jToolBarEditor.setRollover(true);

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 5));

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
        jPanel4.add(jButtonOpen);

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
        jPanel4.add(jButtonSave);

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
        jPanel4.add(jButtonReload);
        jPanel4.add(Box.createHorizontalStrut(100));

        jButtonMerge.setText(bundle.getString("jButtonMerge.Text")); // NOI18N
        jButtonMerge.setToolTipText(bundle.getString("jButtonMerge.ToolTipText")); // NOI18N
        jButtonMerge.setFocusable(false);
        jButtonMerge.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMerge.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMergeActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonMerge);

        jButtonSplit.setText(bundle.getString("jButtonSplit.Text")); // NOI18N
        jButtonSplit.setToolTipText(bundle.getString("jButtonSplit.ToolTipText")); // NOI18N
        jButtonSplit.setFocusable(false);
        jButtonSplit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSplit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSplitActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonSplit);

        jButtonInsert.setText(bundle.getString("jButtonInsert.Text")); // NOI18N
        jButtonInsert.setToolTipText(bundle.getString("jButtonInsert.ToolTipText")); // NOI18N
        jButtonInsert.setFocusable(false);
        jButtonInsert.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonInsert.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInsertActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonInsert);

        jButtonDelete.setText(bundle.getString("jButtonDelete.Text")); // NOI18N
        jButtonDelete.setToolTipText(bundle.getString("jButtonDelete.ToolTipText")); // NOI18N
        jButtonDelete.setFocusable(false);
        jButtonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonDelete);

        jToolBarEditor.add(jPanel4);

        jLabelCharacter.setLabelFor(jTextFieldCharacter);
        jLabelCharacter.setText("Character");
        jPanelSpinner.add(jLabelCharacter);

        jTextFieldCharacter.setColumns(8);
        jTextFieldCharacter.setEnabled(false);
        jTextFieldCharacter.setMargin(new java.awt.Insets(0, 2, 0, 2));
        jTextFieldCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCharacterActionPerformed(evt);
            }
        });
        jTextFieldCharacter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldCharacterKeyReleased(evt);
            }
        });
        jPanelSpinner.add(jTextFieldCharacter);

        jButtonConvert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/tessboxeditor/icons/tools.png"))); // NOI18N
        jButtonConvert.setToolTipText("<html>Convert NCR and Escape<br/>Sequence to Unicode</html>");
        jButtonConvert.setPreferredSize(new java.awt.Dimension(20, 20));
        jButtonConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConvertActionPerformed(evt);
            }
        });
        jPanelSpinner.add(jButtonConvert);
        jPanelSpinner.add(Box.createHorizontalStrut(10));

        jLabelX.setLabelFor(jSpinnerX);
        jLabelX.setText("X");
        jPanelSpinner.add(jLabelX);

        jSpinnerX.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerX, "#"));
        jSpinnerX.setEnabled(false);
        jSpinnerX.setPreferredSize(new java.awt.Dimension(63, 22));
        jSpinnerX.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerXStateChanged(evt);
            }
        });
        jPanelSpinner.add(jSpinnerX);

        jLabelY.setLabelFor(jSpinnerY);
        jLabelY.setText("Y");
        jPanelSpinner.add(jLabelY);

        jSpinnerY.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerY, "#"));
        jSpinnerY.setEnabled(false);
        jSpinnerY.setPreferredSize(new java.awt.Dimension(63, 22));
        jSpinnerY.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerYStateChanged(evt);
            }
        });
        jPanelSpinner.add(jSpinnerY);

        jLabelW.setLabelFor(jSpinnerW);
        jLabelW.setText("W");
        jPanelSpinner.add(jLabelW);

        jSpinnerW.setModel(new javax.swing.SpinnerNumberModel());
        jSpinnerW.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerW, "#"));
        jSpinnerW.setEnabled(false);
        jSpinnerW.setPreferredSize(new java.awt.Dimension(48, 22));
        jSpinnerW.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerWStateChanged(evt);
            }
        });
        jPanelSpinner.add(jSpinnerW);

        jLabelH.setLabelFor(jSpinnerH);
        jLabelH.setText("H");
        jPanelSpinner.add(jLabelH);

        jSpinnerH.setModel(new javax.swing.SpinnerNumberModel());
        jSpinnerH.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerH, "#"));
        jSpinnerH.setEnabled(false);
        jSpinnerH.setPreferredSize(new java.awt.Dimension(48, 22));
        jSpinnerH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerHStateChanged(evt);
            }
        });
        jPanelSpinner.add(jSpinnerH);

        jToolBarEditor.add(jPanelSpinner);
        jToolBarEditor.add(Box.createHorizontalGlue());

        jPanelEditor.add(jToolBarEditor, java.awt.BorderLayout.PAGE_START);

        jSplitPaneEditor.setDividerSize(2);

        jPanelCoord.setLayout(new java.awt.BorderLayout());

        jScrollPaneCoord.setPreferredSize(new java.awt.Dimension(200, 275));

        jTable.setModel(new ReorderableTableModel(
            new Object [][] {

            },
            new String [] {
                "Char", "X", "Y", "Width", "Height"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable.setFillsViewportHeight(true);
        jTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPaneCoord.setViewportView(jTable);
        tableModel = (DefaultTableModel) this.jTable.getModel();
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                switch (e.getType()) {
                    case TableModelEvent.INSERT:
                    if (removedBox != null) {
                        boxes.add(row, removedBox);
                        removedBox = null;
                    }
                    updateSave(true);
                    break;
                    case TableModelEvent.UPDATE:
                    // update only if change to column 0 (Char)
                    if (row != -1 && column == 0) {
                        TableModel model = (TableModel)e.getSource();
                        Object data = model.getValueAt(row, column);
                        String value = (String) data;
                        TessBox box = boxes.toList().get(row);
                        box.setChrs(value);
                        jTextFieldCharacter.setText(value);
                        jTextFieldChar.setText(value);
                        jTextFieldCodepointValue.setText(net.sourceforge.vietocr.util.Utils.toHex(value));
                        updateSave(true);
                    }
                    break;
                    case TableModelEvent.DELETE:
                    removedBox = boxes.remove(row);
                    updateSave(true);
                    break;
                }
            }
        });
        ListSelectionModel cellSelectionModel = jTable.getSelectionModel();
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = jTable.getSelectedRow();
                    if (selectedIndex != -1) {
                        if (!((JImageLabel) jLabelImage).isBoxClickAction()) { // not from image block click
                            boxes.deselectAll();
                        }
                        List<TessBox> boxesOfCurPage = boxes.toList(); // boxes of current page
                        for (int index : jTable.getSelectedRows()) {
                            TessBox box = boxesOfCurPage.get(index);
                            // select box
                            box.setSelected(true);
                            jLabelImage.scrollRectToVisible(box.getRect());
                        }
                        jLabelImage.repaint();

                        if (jTable.getSelectedRows().length == 1) {
                            enableReadout(true);
                            // update Character field
                            jTextFieldCharacter.setText((String) tableModel.getValueAt(selectedIndex, 0));
                            jTextFieldChar.setText(jTextFieldCharacter.getText());
                            jTextFieldCodepointValue.setText(net.sourceforge.vietocr.util.Utils.toHex(jTextFieldCharacter.getText()));
                            // update subimage label
                            Icon icon = jLabelImage.getIcon();
                            TessBox curBox = boxesOfCurPage.get(selectedIndex);
                            Rectangle rect = curBox.getRect();
                            try {
                                Image subImage = getSubimage((BufferedImage) ((ImageIcon) icon).getImage(), rect);
                                ImageIconScalable subIcon = new ImageIconScalable(subImage);
                                subIcon.setScaledFactor(scaleFactor);
                                jLabelSubimage.setIcon(subIcon);
                            } catch (Exception exc) {
                                //ignore
                            }
                            // mark this as table action event to prevent cyclic firing of events by spinners
                            tableSelectAction = true;
                            // update spinners
                            jSpinnerX.setValue(rect.x);
                            jSpinnerY.setValue(rect.y);
                            jSpinnerH.setValue(rect.height);
                            jSpinnerW.setValue(rect.width);
                            tableSelectAction = false;
                        } else {
                            enableReadout(false);
                            resetReadout();
                        }
                    } else {
                        boxes.deselectAll();
                        jLabelImage.repaint();
                        enableReadout(false);
                        tableSelectAction = true;
                        resetReadout();
                        tableSelectAction = false;
                    }
                }
            }
        });

        ((JLabel) jTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JImageLabel) this.jLabelImage).setTable(jTable);
        jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control C"), "none");
        jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control X"), "none");
        jTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control V"), "none");
        rowHeader = new RowHeaderList(this.jTable);
        this.jScrollPaneCoord.setRowHeaderView(rowHeader);
        this.jTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        this.jTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.jTable.setDefaultEditor(String.class, new MyTableCellEditor());

        jPanelCoord.add(jScrollPaneCoord, java.awt.BorderLayout.CENTER);

        jTextFieldFind.setPreferredSize(new java.awt.Dimension(200, 20));
        jTextFieldFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFindActionPerformed(evt);
            }
        });
        jPanelFind.add(jTextFieldFind);

        jButtonFind.setText(bundle.getString("jButtonFind.Text")); // NOI18N
        jButtonFind.setToolTipText(bundle.getString("jButtonFind.ToolTipText")); // NOI18N
        jButtonFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindActionPerformed(evt);
            }
        });
        jPanelFind.add(jButtonFind);

        jPanelCoord.add(jPanelFind, java.awt.BorderLayout.SOUTH);

        jTabbedPaneBoxData.addTab("Box Coordinates", jPanelCoord);

        jTextAreaBoxData.setEditable(false);
        jTextAreaBoxData.setColumns(20);
        jTextAreaBoxData.setRows(5);
        jTextAreaBoxData.setMargin(new java.awt.Insets(8, 8, 2, 2));
        jScrollPaneBoxData.setViewportView(jTextAreaBoxData);

        jTabbedPaneBoxData.addTab("Box Data", jScrollPaneBoxData);

        jPanelBoxView.setBackground(java.awt.Color.lightGray);
        jPanelBoxView.setLayout(new java.awt.BorderLayout());

        jPanelNorthContainer.setLayout(new javax.swing.BoxLayout(jPanelNorthContainer, javax.swing.BoxLayout.Y_AXIS));

        jPanelChar.setBackground(java.awt.Color.lightGray);
        jPanelChar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelCodepoint.setText("Char/Codepoint:");
        jPanelChar.add(jLabelCodepoint);

        jTextFieldChar.setEditable(false);
        jPanelChar.add(jTextFieldChar);

        jTextFieldCodepointValue.setEditable(false);
        jPanelChar.add(jTextFieldCodepointValue);

        jPanelNorthContainer.add(jPanelChar);

        jPanelControls.setBackground(java.awt.Color.lightGray);
        jPanelControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelSpinnerMargin.setLabelFor(jSpinnerMargin);
        jLabelSpinnerMargin.setText("Margins");
        jPanelControls.add(jLabelSpinnerMargin);

        jSpinnerMargin.setModel(new javax.swing.SpinnerNumberModel(3, 0, 20, 1));
        jSpinnerMargin.setFocusable(false);
        ((DefaultEditor) jSpinnerMargin.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) jSpinnerMargin.getEditor()).getTextField().setFocusable(false);
        jSpinnerMargin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerMarginStateChanged(evt);
            }
        });
        jPanelControls.add(jSpinnerMargin);

        jLabelSpinnerScale.setLabelFor(jSpinnerScale);
        jLabelSpinnerScale.setText("Scale");
        jPanelControls.add(Box.createHorizontalStrut(10));
        jPanelControls.add(jLabelSpinnerScale);

        jSpinnerScale.setModel(new javax.swing.SpinnerNumberModel(4, 1, 10, 1));
        jSpinnerScale.setFocusable(false);
        ((DefaultEditor) jSpinnerScale.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) jSpinnerScale.getEditor()).getTextField().setFocusable(false);
        jSpinnerScale.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerScaleStateChanged(evt);
            }
        });
        jPanelControls.add(jSpinnerScale);

        jPanelNorthContainer.add(jPanelControls);

        jPanelBoxView.add(jPanelNorthContainer, java.awt.BorderLayout.NORTH);

        jLabelSubimage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanelBoxView.add(jLabelSubimage, java.awt.BorderLayout.CENTER);

        jPanelButtons.setBackground(new java.awt.Color(192, 192, 192));

        jButtonPrev.setText("Prev");
        jButtonPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonPrev);

        jButtonNext.setText("Next");
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanelButtons.add(jButtonNext);

        jPanelBoxView.add(jPanelButtons, java.awt.BorderLayout.SOUTH);

        jTabbedPaneBoxData.addTab("Box View", jPanelBoxView);

        jSplitPaneEditor.setLeftComponent(jTabbedPaneBoxData);

        jLabelImage.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jScrollPaneImage.setViewportView(jLabelImage);

        jSplitPaneEditor.setRightComponent(jScrollPaneImage);

        jPanelEditor.add(jSplitPaneEditor, java.awt.BorderLayout.CENTER);

        jPanelStatus.add(jLabelStatus);
        jPanelStatus.add(jLabelPageNbr);
        this.jPanelStatus.add(Box.createHorizontalStrut(10));

        jButtonPrevPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/tessboxeditor/icons/PrevPage.gif"))); // NOI18N
        jButtonPrevPage.setToolTipText(bundle.getString("jButtonPrevPage.ToolTipText")); // NOI18N
        jButtonPrevPage.setFocusable(false);
        jButtonPrevPage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPrevPage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPrevPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevPageActionPerformed(evt);
            }
        });
        jPanelStatus.add(jButtonPrevPage);

        jButtonNextPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/tessboxeditor/icons/NextPage.gif"))); // NOI18N
        jButtonNextPage.setToolTipText(bundle.getString("jButtonNextPage.ToolTipText")); // NOI18N
        jButtonNextPage.setFocusable(false);
        jButtonNextPage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNextPage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNextPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextPageActionPerformed(evt);
            }
        });
        jPanelStatus.add(jButtonNextPage);

        jPanelEditor.add(jPanelStatus, java.awt.BorderLayout.SOUTH);

        jTabbedPaneMain.addTab("Box Editor", jPanelEditor);

        jPanelTIFFBox.setLayout(new java.awt.BorderLayout());

        jToolBarGenerator.setRollover(true);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jCheckBoxText2Image.setText("text2image");
        jCheckBoxText2Image.setToolTipText("Use text2image Command");
        jCheckBoxText2Image.setContentAreaFilled(false);
        jCheckBoxText2Image.setSelected(prefs.getBoolean("Text2Image", false));
        jCheckBoxText2ImageActionPerformed(null);
        jCheckBoxText2Image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxText2ImageActionPerformed(evt);
            }
        });
        jPanel3.add(jCheckBoxText2Image);

        jButtonInput.setText("Input");
        jButtonInput.setToolTipText("Load Text File");
        jButtonInput.setFocusable(false);
        jButtonInput.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonInput.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInputActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonInput);

        jLabelOutput.setLabelFor(jTextFieldOuputDir);
        jLabelOutput.setText("Output");
        jPanel3.add(jLabelOutput);

        jTextFieldOuputDir.setToolTipText("Ouput Directory");
        jTextFieldOuputDir.setEnabled(false);
        jTextFieldOuputDir.setPreferredSize(new java.awt.Dimension(180, 24));
        jPanel3.add(jTextFieldOuputDir);

        jButtonBrowseOutputDir.setText("...");
        jButtonBrowseOutputDir.setToolTipText("Browse");
        jButtonBrowseOutputDir.setPreferredSize(new java.awt.Dimension(24, 23));
        jButtonBrowseOutputDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseOutputDirActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonBrowseOutputDir);

        jTextFieldPrefix.setToolTipText("Prefix (Language Code)");
        jTextFieldPrefix.setPreferredSize(new java.awt.Dimension(38, 24));
        jPanel3.add(jTextFieldPrefix);

        jTextFieldFileName.setToolTipText("Filename");
        jTextFieldFileName.setPreferredSize(new java.awt.Dimension(140, 24));
        jPanel3.add(jTextFieldFileName);

        jButtonFont.setText("Font");
        jButtonFont.setToolTipText("Select Font");
        jButtonFont.setFocusable(false);
        jButtonFont.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFont.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFontActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonFont);

        jTextFieldFontFolder.setToolTipText("Font Folder");
        jTextFieldFontFolder.setEnabled(false);
        jTextFieldFontFolder.setPreferredSize(new java.awt.Dimension(120, 24));
        jPanelFontFolder.add(jTextFieldFontFolder);

        jButtonBrowseFontFolder.setText("...");
        jButtonBrowseFontFolder.setToolTipText("Browse");
        jButtonBrowseFontFolder.setPreferredSize(new java.awt.Dimension(24, 23));
        jButtonBrowseFontFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseFontFolderActionPerformed(evt);
            }
        });
        jPanelFontFolder.add(jButtonBrowseFontFolder);

        jLabelExposure.setLabelFor(jSpinnerExposure);
        jLabelExposure.setText("Exposure");
        jPanelFontFolder.add(jLabelExposure);

        jSpinnerExposure.setModel(new javax.swing.SpinnerNumberModel(0, -3, 3, 1));
        jSpinnerExposure.setToolTipText("Exposure Level");
        jSpinnerExposure.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerExposureStateChanged(evt);
            }
        });
        jPanelFontFolder.add(jSpinnerExposure);

        jPanel3.add(jPanelFontFolder);

        jCheckBoxAntiAliasing.setText("Anti-Aliasing");
        jPanelFontAttrib.add(jCheckBoxAntiAliasing);

        jLabelNoise.setLabelFor(jSpinnerNoise);
        jLabelNoise.setText("Noise");
        jPanelFontAttrib.add(jLabelNoise);

        jSpinnerNoise.setModel(new javax.swing.SpinnerNumberModel(0, 0, 5, 1));
        jSpinnerNoise.setToolTipText("Add Noise to Image");
        jSpinnerNoise.setName("Noise"); // NOI18N
        jSpinnerNoise.setPreferredSize(new java.awt.Dimension(47, 22));
        jPanelFontAttrib.add(jSpinnerNoise);

        jPanel3.add(jPanelFontAttrib);

        jLabelTracking.setLabelFor(jSpinnerTracking);
        jLabelTracking.setText("Tracking");
        jPanel3.add(jLabelTracking);

        jSpinnerTracking.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(-0.04f), Float.valueOf(0.1f), Float.valueOf(0.01f)));
        jSpinnerTracking.setToolTipText("Letter Spacing");
        jSpinnerTracking.setPreferredSize(new java.awt.Dimension(64, 22));
        jSpinnerTracking.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerTrackingStateChanged(evt);
            }
        });
        jPanel3.add(jSpinnerTracking);

        jLabelLeading.setLabelFor(jSpinnerLeading);
        jLabelLeading.setText("Leading");
        jPanel3.add(jLabelLeading);

        jSpinnerLeading.setModel(new javax.swing.SpinnerNumberModel(12, 6, 18, 1));
        jSpinnerLeading.setToolTipText("Line Spacing");
        jPanel3.add(jSpinnerLeading);

        jLabelW1.setLabelFor(jSpinnerW1);
        jLabelW1.setText("W");
        jPanel3.add(jLabelW1);

        jSpinnerW1.setModel(new javax.swing.SpinnerNumberModel(2550, 600, 2550, 10));
        jSpinnerW1.setToolTipText("Image Width");
        jSpinnerW1.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerW1, "#"));
        jSpinnerW1.setPreferredSize(new java.awt.Dimension(63, 22));
        jPanel3.add(jSpinnerW1);

        jLabelH1.setLabelFor(jSpinnerH1);
        jLabelH1.setText("H");
        jPanel3.add(jLabelH1);

        jSpinnerH1.setModel(new javax.swing.SpinnerNumberModel(3300, 400, 3300, 10));
        jSpinnerH1.setToolTipText("Image Height");
        jSpinnerH1.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinnerH1, "#"));
        jSpinnerH1.setPreferredSize(new java.awt.Dimension(63, 22));
        jPanel3.add(jSpinnerH1);

        jButtonGenerate.setText("Generate");
        jButtonGenerate.setToolTipText("Generate TIFF/Box");
        jButtonGenerate.setFocusable(false);
        jButtonGenerate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonGenerate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonGenerate);

        jButtonClear.setText("Clear");
        jButtonClear.setToolTipText("Clear Textarea");
        jButtonClear.setFocusable(false);
        jButtonClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonClear);

        jToolBarGenerator.add(jPanel3);

        jPanelTIFFBox.add(jToolBarGenerator, java.awt.BorderLayout.PAGE_START);

        jTextAreaInput.setColumns(20);
        jTextAreaInput.setLineWrap(true);
        jTextAreaInput.setRows(5);
        jTextAreaInput.setWrapStyleWord(true);
        jTextAreaInput.setMargin(new java.awt.Insets(5, 5, 2, 2));
        jScrollPaneText.setViewportView(jTextAreaInput);

        jPanelTIFFBox.add(jScrollPaneText, java.awt.BorderLayout.CENTER);

        jTabbedPaneMain.addTab("TIFF/Box Generator", jPanelTIFFBox);

        getContentPane().add(jTabbedPaneMain, java.awt.BorderLayout.CENTER);

        jMenuFile.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuFile.Mnemonic").charAt(0));
        jMenuFile.setText(bundle.getString("jMenuFile.Text")); // NOI18N

        jMenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemOpen.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemOpen.Mnemonic").charAt(0));
        jMenuItemOpen.setText(bundle.getString("jMenuItemOpen.Text")); // NOI18N
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSave.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemSave.Mnemonic").charAt(0));
        jMenuItemSave.setText(bundle.getString("jMenuItemSave.Text")); // NOI18N
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSaveAs.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemSaveAs.Mnemonic").charAt(0));
        jMenuItemSaveAs.setText(bundle.getString("jMenuItemSaveAs.Text")); // NOI18N
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparatorRecentFiles);

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

        jMenuEdit.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuEdit.Mnemonic").charAt(0));
        jMenuEdit.setText(bundle.getString("jMenuEdit.Text")); // NOI18N

        jMenuItemMerge.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemMerge.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemMerge.Mnemonic").charAt(0));
        jMenuItemMerge.setText(bundle.getString("jMenuItemMerge.Text")); // NOI18N
        jMenuItemMerge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMergeActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemMerge);

        jMenuItemSplit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSplit.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemSplit.Mnemonic").charAt(0));
        jMenuItemSplit.setText(bundle.getString("jMenuItemSplit.Text")); // NOI18N
        jMenuItemSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSplitActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemSplit);

        jMenuItemInsert.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemInsert.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemInsert.Mnemonic").charAt(0));
        jMenuItemInsert.setText(bundle.getString("jMenuItemInsert.Text")); // NOI18N
        jMenuItemInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInsertActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemInsert);

        jMenuItemDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMenuItemDelete.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemDelete.Mnemonic").charAt(0));
        jMenuItemDelete.setText(bundle.getString("jMenuItemDelete.Text")); // NOI18N
        jMenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeleteActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemDelete);
        jMenuEdit.add(jSeparator1);

        jMenuItemMarkEOL.setText("Mark EOL");
        jMenuItemMarkEOL.setToolTipText("Mark EOL with Tab character");
        jMenuItemMarkEOL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMarkEOLActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemMarkEOL);

        jMenuItemMarkEOLBulk.setText("Mark EOL Bulk...");
        jMenuItemMarkEOLBulk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMarkEOLBulkActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemMarkEOLBulk);
        jMenuEdit.add(jSeparator2);

        jMenuItemRemoveEmptyBoxes.setText("Remove Empty Boxes");
        jMenuItemRemoveEmptyBoxes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoveEmptyBoxesActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemRemoveEmptyBoxes);

        jMenuBar.add(jMenuEdit);

        jMenuSettings.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuSettings.Mnemonic").charAt(0));
        jMenuSettings.setText("Settings");

        jMenuItemFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemFont.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemFont.Mnemonic").charAt(0));
        jMenuItemFont.setText(bundle.getString("jMenuItemFont.Text")); // NOI18N
        jMenuItemFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFontActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemFont);
        jMenuSettings.add(jSeparatorLAF);

        jMenuLookAndFeel.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuLookAndFeel.Mnemonic").charAt(0));
        jMenuLookAndFeel.setText(bundle.getString("jMenuLookAndFeel.Text")); // NOI18N
        jMenuSettings.add(jMenuLookAndFeel);

        jMenuBar.add(jMenuSettings);

        jMenuTools.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuTools.Mnemonic").charAt(0));
        jMenuTools.setText(bundle.getString("jMenuTools.Text")); // NOI18N

        jMenuItemMergeTiff.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemMergeTiff.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemMergeTiff.Mnemonic").charAt(0));
        jMenuItemMergeTiff.setText(bundle.getString("jMenuItemMergeTiff.Text")); // NOI18N
        jMenuItemMergeTiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMergeTiffActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemMergeTiff);

        jMenuItemSplitTiff.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItemSplitTiff.setMnemonic(java.util.ResourceBundle.getBundle("net/sourceforge/tessboxeditor/Gui").getString("jMenuItemSplitTiff.Mnemonic").charAt(0));
        jMenuItemSplitTiff.setText(bundle.getString("jMenuItemSplitTiff.Text")); // NOI18N
        jMenuItemSplitTiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSplitTiffActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemSplitTiff);

        jMenuBar.add(jMenuTools);

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

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

            private void inc(JSpinner s) {
                if (s == jSpinnerX || s == jSpinnerY) {
                    if (!invertControls) {
                        s.setValue(Math.max(0, ((Integer) s.getValue()) - movementMultiplier));
                    } else {
                        s.setValue(((Integer) s.getValue()) + movementMultiplier);
                    }
                } else {
                    s.setValue(((Integer) s.getValue()) + movementMultiplier);
                }
            }

            private void inc(JSpinner s, int max) {
                if ((s == jSpinnerX || s == jSpinnerY) && !invertControls) {
                    s.setValue(Math.max(0, ((Integer) s.getValue()) - movementMultiplier));
                } else {
                    s.setValue(((Integer) s.getValue()) + movementMultiplier);
                }
            }

            private void dec(JSpinner s) {
                if ((s == jSpinnerX || s == jSpinnerY) && !invertControls) {
                    s.setValue(((Integer) s.getValue()) + movementMultiplier);
                } else {
                    s.setValue(Math.max(0, ((Integer) s.getValue()) - movementMultiplier));
                }
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getID() != KeyEvent.KEY_TYPED) {
                    return false;
                }

                Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                if ( (focusOwner instanceof JSpinner)
                    || (focusOwner instanceof JTextField)) {
                    return false;
                }

                if (jLabelCharacter.hasFocus()) {
                    return false;
                }

                if (!jPanelBoxView.isShowing()) {
                    return false;
                }

                if (e.isShiftDown()) {
                    movementMultiplier = 10;
                }
                else {
                    movementMultiplier = 1;

                }

                char c = Character.toLowerCase(e.getKeyChar());

                if (c == 'w') {
                    inc(jSpinnerY);
                } else if (c == 's') {
                    dec(jSpinnerY);
                } else if (c == 'd') {
                    dec(jSpinnerX);
                } else if (c == 'a') {
                    inc(jSpinnerX);
                } else if (c == 'q') {
                    dec(jSpinnerW);
                } else if (c == 'e') {
                    inc(jSpinnerW);
                } else if (c == 'r') {
                    dec(jSpinnerH);
                } else if (c == 'f') {
                    inc(jSpinnerH);
                } else if (c == ',') {
                    jButtonPrev.doClick();
                } else if (c == '.') {
                    jButtonNext.doClick();
                } else if (c == 'x') {
                    jTextFieldCharacter.requestFocus();
                } else {
                    return false;
                }

                return true;
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        if (jFileChooserInputImage.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentDirectory = jFileChooserInputImage.getCurrentDirectory().getPath();
            openFile(jFileChooserInputImage.getSelectedFile());

            for (int i = 0; i < fileFilters.length; i++) {
                if (fileFilters[i] == jFileChooserInputImage.getFileFilter()) {
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
        if (!promptToSave()) {
            return;
        }

//        jLabelStatus.setText(bundle.getString("Loading_image..."));
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        SwingWorker loadWorker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                readImageFile(selectedFile);
                updateMRUList(selectedFile.getPath());
                int lastDot = selectedFile.getName().lastIndexOf(".");
                boxFile = new File(selectedFile.getParentFile(), selectedFile.getName().substring(0, lastDot) + ".box");
                loadBoxes(boxFile);
                return null;
            }

            @Override
            protected void done() {
//                jLabelStatus.setText(bundle.getString("Loading_completed"));
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
            }
        };

        loadWorker.execute();
    }

    void readImageFile(File selectedFile) {
        try {
            imageList = ImageIOHelper.getImageList(selectedFile);
            if (imageList == null) {
                JOptionPane.showMessageDialog(this, bundle.getString("Cannotloadimage"), APP_NAME, JOptionPane.ERROR_MESSAGE);
                return;
            }
            imageIndex = 0;
            loadImage();
            this.jScrollPaneImage.getViewport().setViewPosition(new Point(0, 0));
            this.setTitle(APP_NAME + " - " + selectedFile.getName());
        } catch (OutOfMemoryError oome) {
            JOptionPane.showMessageDialog(this, oome.getMessage(), "Out-Of-Memory Exception", JOptionPane.ERROR_MESSAGE);
        } catch (IOException | HeadlessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            if (e.getMessage() != null) {
                JOptionPane.showMessageDialog(this, e.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void loadBoxes(File boxFile) {
        if (boxFile.exists()) {
            try {
                boxPages.clear();

                String str = Utils.readTextFile(boxFile);
                // load into textarea
                this.jTextAreaBoxData.setText(str);
                boxPages = parseBoxString(str, imageList);
                loadTable();
                setColumnWidth();
                updateSave(false);
            } catch (OutOfMemoryError oome) {
                logger.log(Level.SEVERE, oome.getMessage(), oome);
                JOptionPane.showMessageDialog(this, oome.getMessage(), "Out-Of-Memory Exception", JOptionPane.ERROR_MESSAGE);
            } catch (IOException | NumberFormatException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                if (e.getMessage() != null) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // clear table and box display
            tableModel.setDataVector((Object[][]) null, (Object[]) null);
            ((JImageLabel) this.jLabelImage).setBoxes(null);
            jTextAreaBoxData.setText(null);
        }
    }

    List<TessBoxCollection> parseBoxString(String boxStr, List<BufferedImage> imageList) throws IOException {
        List<TessBoxCollection> allBoxPages = new ArrayList<TessBoxCollection>();
        isWordStrFormat = false;
        String[] boxdata = boxStr.split("\\R"); // or "\\r?\\n"
        if (boxdata.length > 0) {
            // if only 5 fields, it's Tess 2.0x format
            isTess2_0Format = boxdata[0].split("\\s+").length == 5;
        }

        int startBoxIndex = 0;

        for (int curPage = 0; curPage < imageList.size(); curPage++) {
            TessBoxCollection boxCol = new TessBoxCollection();
            // Note that the coordinate system used in the box file has (0,0) at the bottom-left.
            // On computer graphics device, (0,0) is defined as top-left.
            int pageHeight = imageList.get(curPage).getHeight();
            for (int i = startBoxIndex; i < boxdata.length; i++) {
                String[] items = boxdata[i].split("(?<!^) +");

                // skip invalid data
                if (items.length < 5 || (items.length > 6 && !items[0].equals(WORDSTR))) {
                    continue;
                }

                String chrs = items[0];
                int x = Integer.parseInt(items[1]);
                int y = Integer.parseInt(items[2]);
                int w = Integer.parseInt(items[3]) - x;
                int h = Integer.parseInt(items[4]) - y;
                y = pageHeight - y - h; // flip the y-coordinate

                short page;
                if (items.length == 6 || (chrs.equals(WORDSTR) && items.length >= 7)) {
                    page = Short.parseShort(items[5]); // Tess 3.0x format
                } else {
                    page = 0; // Tess 2.0x format
                }

                if (chrs.equals(WORDSTR) && items.length >= 7 && items[6].startsWith("#")) {
                    chrs = boxdata[i].substring(boxdata[i].indexOf("#") + 1);
                    isWordStrFormat = true;
                }

                if (page > curPage) {
                    startBoxIndex = i; // mark begin of next page
                    break;
                }
                boxCol.add(new TessBox(chrs, new Rectangle(x, y, w, h), page));
            }
            allBoxPages.add(boxCol); // add the last page
        }

        return allBoxPages;
    }

    void loadTable() {
        if (!this.boxPages.isEmpty()) {
            boxes = this.boxPages.get(imageIndex);
            tableModel.setDataVector(boxes.getTableDataList().toArray(new String[0][5]), headers);
            ((JImageLabel) this.jLabelImage).setBoxes(boxes);
        }
    }

    /**
     * Displays a dialog to discard changes.
     *
     * @return false if user canceled or discard, true else
     */
    protected boolean promptToDiscardChanges() {
        if (!boxChanged) {
            return false;
        }
        switch (JOptionPane.showConfirmDialog(this,
                bundle.getString("Do_you_want_to_discard_the_changes_to_")
                + boxFile.getName() + "?",
                APP_NAME, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
            case JOptionPane.YES_OPTION:
                return true;
            default:
                return false;
        }
    }

    /**
     * Displays a dialog to save changes.
     *
     * @return false if user canceled, true else
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
            return saveBoxFile(boxFile);
        }
    }

    boolean saveFileDlg() {
        JFileChooser saveChooser = new JFileChooserWithConfirm(outputDirectory);
        FileFilter boxFilter = new SimpleFilter("box", "Box Files");
        saveChooser.addChoosableFileFilter(boxFilter);
        saveChooser.setFileFilter(boxFilter);
        saveChooser.setAcceptAllFileFilterUsed(false);
        saveChooser.setDialogTitle(bundle.getString("Save_As"));
        if (boxFile != null) {
            saveChooser.setSelectedFile(boxFile);
        }

        if (saveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = saveChooser.getCurrentDirectory().getPath();
            boxFile = saveChooser.getSelectedFile();
            return saveBoxFile(boxFile);
        } else {
            return false;
        }
    }

    boolean saveBoxFile(File file) {
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        try {
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                String str = formatOutputString(imageList, boxPages);
                out.write(str);
                this.jTextAreaBoxData.setText(str);
                this.jTextAreaBoxData.setCaretPosition(0);
            }
//            updateMRUList(boxFile.getPath());
            updateSave(false);
        } catch (OutOfMemoryError oome) {
            logger.log(Level.SEVERE, oome.getMessage(), oome);
            JOptionPane.showMessageDialog(this, oome.getMessage(), bundle.getString("OutOfMemoryError"), JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException fnfe) {
            logger.log(Level.SEVERE, fnfe.getMessage(), fnfe);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
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

    String formatOutputString(List<BufferedImage> imgList, List<TessBoxCollection> bxPages) {
        StringBuilder sb = new StringBuilder();
        for (short pageIndex = 0; pageIndex < imgList.size(); pageIndex++) {
            int pageHeight = ((BufferedImage) imgList.get(pageIndex)).getHeight(); // each page (in an image) can have different height
            for (TessBox box : bxPages.get(pageIndex).toList()) {
                Rectangle rect = box.getRect();
                if (isWordStrFormat && !box.getChrs().equals("\t")) {
                    sb.append(String.format("%s %d %d %d %d %d #%s", WORDSTR, rect.x, pageHeight - rect.y - rect.height, rect.x + rect.width, pageHeight - rect.y, pageIndex, box.getChrs())).append(EOL);
                } else {
                    sb.append(String.format("%s %d %d %d %d %d", box.getChrs(), rect.x, pageHeight - rect.y - rect.height, rect.x + rect.width, pageHeight - rect.y, pageIndex)).append(EOL);
                }
            }
        }
        if (isTess2_0Format) {
            return sb.toString().replace(" 0" + EOL, EOL); // strip the ending zeroes
        }
        return sb.toString();
    }

    /**
     * Update MRU List.
     *
     * @param fileName
     */
    protected void updateMRUList(String fileName) {
        // to be implemented in subclass
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

        prefs.putInt("windowState", getExtendedState());

        if (getExtendedState() == NORMAL) {
            prefs.putInt("frameHeight", getHeight());
            prefs.putInt("frameWidth", getWidth());
            prefs.putInt("frameX", getX());
            prefs.putInt("frameY", getY());
        }

        prefs.putInt("filterIndex", filterIndex);
        prefs.putBoolean("Text2Image", jCheckBoxText2Image.isSelected());

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

            JOptionPane.showMessageDialog(this, APP_NAME + " " + version + " \u00a9 2011\n"
                    + "Tesseract Box Editor & Trainer\n"
                    + DateFormat.getDateInstance(DateFormat.LONG).format(releaseDate)
                    + "\nhttp://vietocr.sourceforge.net", jMenuItemAbout.getText(), JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ParseException | HeadlessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
    private void jButtonPrevPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevPageActionPerformed
        if (imageList != null && imageIndex > 0) {
            --imageIndex;
            loadImage();
            loadTable();
        }
    }//GEN-LAST:event_jButtonPrevPageActionPerformed

    private void jButtonNextPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextPageActionPerformed
        if (imageList != null && imageIndex < imageList.size() - 1) {
            ++imageIndex;
            loadImage();
            loadTable();
        }
    }//GEN-LAST:event_jButtonNextPageActionPerformed

    void loadImage() {
        this.jLabelImage.setIcon(new ImageIcon(imageList.get(imageIndex)));
        if (boxes != null) {
            boxes.deselectAll();
        }
        this.jLabelImage.repaint();
        this.jLabelPageNbr.setText(String.format("Page: %d of %d", imageIndex + 1, imageList.size()));
        setButton();
        tableSelectAction = true;
        resetReadout();
        tableSelectAction = false;
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

    void resetReadout() {
        jTextFieldCharacter.setText(null);
        jTextFieldChar.setText(null);
        jTextFieldCodepointValue.setText(null);
        jSpinnerH.setValue(0);
        jSpinnerW.setValue(0);
        jSpinnerX.setValue(0);
        jSpinnerY.setValue(0);
        jLabelSubimage.setIcon(null);
    }

    void enableReadout(boolean enabled) {
        jTextFieldCharacter.setEnabled(enabled);
        jSpinnerX.setEnabled(enabled);
        jSpinnerY.setEnabled(enabled);
        jSpinnerH.setEnabled(enabled);
        jSpinnerW.setEnabled(enabled);
    }

    /**
     * Updates the Save action.
     *
     * @param modified whether file has been modified
     */
    void updateSave(boolean modified) {
        if (boxChanged != modified) {
            boxChanged = modified;
            this.jButtonSave.setEnabled(modified);
            this.jMenuItemSave.setEnabled(modified);
            rootPane.putClientProperty("windowModified", modified);
            // see http://developer.apple.com/qa/qa2001/qa1146.html
        }
    }

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        jMenuItemOpenActionPerformed(evt);
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        jMenuItemSaveActionPerformed(evt);
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReloadActionPerformed
        if (!promptToDiscardChanges()) {
            return;
        }

        if (boxFile != null) {
            jButtonReload.setEnabled(false);
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getGlassPane().setVisible(true);

            SwingWorker loadWorker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    loadBoxes(boxFile);
                    return null;
                }

                @Override
                protected void done() {
                    jButtonReload.setEnabled(true);
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                }
            };

            loadWorker.execute();
        }
    }//GEN-LAST:event_jButtonReloadActionPerformed

    void jMenuItemFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFontActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemFontActionPerformed

    private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
        final String readme = bundle.getString("readme");

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
    }//GEN-LAST:event_jMenuItemHelpActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        saveFileDlg();
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed
    private void jSpinnerXStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerXStateChanged
        stateChanged(evt);
    }//GEN-LAST:event_jSpinnerXStateChanged
    private void jSpinnerYStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerYStateChanged
        stateChanged(evt);
    }//GEN-LAST:event_jSpinnerYStateChanged
    private void jSpinnerWStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerWStateChanged
        stateChanged(evt);
    }//GEN-LAST:event_jSpinnerWStateChanged
    private void jSpinnerHStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerHStateChanged
        stateChanged(evt);
    }//GEN-LAST:event_jSpinnerHStateChanged
    void stateChanged(javax.swing.event.ChangeEvent evt) {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }
    private void jTextFieldCharacterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCharacterActionPerformed
        if (boxes == null) {
            return;
        }
        List<TessBox> selected = this.boxes.getSelectedBoxes();
        if (selected.size() <= 0) {
            return;
        } else if (selected.size() > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one box to apply the change.");
            return;
        }

        TessBox box = selected.get(0);
        int index = this.boxes.toList().indexOf(box);
        // Convert NCR or escape sequence to Unicode.
        this.jTextFieldCharacter.setText(TextUtilities.convertNCR(this.jTextFieldCharacter.getText()));
        String str = this.jTextFieldCharacter.getText();
        if (!box.getChrs().equals(str)) {
            box.setChrs(str);
            tableModel.setValueAt(box.getChrs(), index, 0);
            jTextFieldChar.setText(str);
            jTextFieldCodepointValue.setText(Utils.toHex(str));
            updateSave(true);
        }
    }//GEN-LAST:event_jTextFieldCharacterActionPerformed
    private void jButtonConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConvertActionPerformed
        String curChar = this.jTextFieldCharacter.getText();
        if (curChar.trim().length() == 0) {
            return;
        }
        // Convert NCR or escape sequence to Unicode.
        this.jTextFieldCharacter.setText(TextUtilities.convertNCR(this.jTextFieldCharacter.getText()));
        // Commit the change, if no conversion.
        if (curChar.equals(this.jTextFieldCharacter.getText())) {
            jTextFieldCharacterActionPerformed(evt);
        }
    }//GEN-LAST:event_jButtonConvertActionPerformed
    void jMenuItemMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMergeActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemMergeActionPerformed
    void jMenuItemSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSplitActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemSplitActionPerformed
    void jMenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeleteActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemDeleteActionPerformed
    void jMenuItemInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInsertActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemInsertActionPerformed
    private void jButtonMergeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMergeActionPerformed
        jMenuItemMergeActionPerformed(evt);
    }//GEN-LAST:event_jButtonMergeActionPerformed
    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        jMenuItemDeleteActionPerformed(evt);
    }//GEN-LAST:event_jButtonDeleteActionPerformed
    private void jButtonSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSplitActionPerformed
        jMenuItemSplitActionPerformed(evt);
    }//GEN-LAST:event_jButtonSplitActionPerformed
    private void jButtonInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInsertActionPerformed
        jMenuItemInsertActionPerformed(evt);
    }//GEN-LAST:event_jButtonInsertActionPerformed
    private void jButtonFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindActionPerformed
        if (imageList == null) {
            return;
        }
        int pageHeight = imageList.get(imageIndex).getHeight();
        String[] items = this.jTextFieldFind.getText().split("\\s+");
        try {
            TessBox findBox;

            if (items.length == 1) {
                String chrs = items[0];
                if (chrs.length() == 0) {
                    throw new Exception("Empty search values.");
                }
                // Convert NCR or escape sequence to Unicode.
                chrs = TextUtilities.convertNCR(chrs);

                findBox = new TessBox(chrs, new Rectangle(), imageIndex);
                findBox = boxes.selectByChars(findBox);
            } else {
                int x = Integer.parseInt(items[0]);
                int y = Integer.parseInt(items[1]);
                int w = Integer.parseInt(items[2]) - x;
                int h = Integer.parseInt(items[3]) - y;
                y = pageHeight - y - h; // flip the y-coordinate
                findBox = new TessBox("", new Rectangle(x, y, w, h), imageIndex);
                findBox = boxes.select(findBox);
            }

            if (findBox != null) {
                int index = boxes.toList().indexOf(findBox);
                this.jTable.setRowSelectionInterval(index, index);
                Rectangle rect = this.jTable.getCellRect(index, 0, true);
                this.jTable.scrollRectToVisible(rect);
            } else {
                this.jTable.clearSelection();
                String msg = String.format("No box with the specified %s was found.", items.length == 1 ? "character(s)" : "coordinates");
                JOptionPane.showMessageDialog(this, msg);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter box character(s) or coordinates (x1 y1 x2 y2).");
        }
    }//GEN-LAST:event_jButtonFindActionPerformed

    private void jTextFieldFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFindActionPerformed
        jButtonFindActionPerformed(evt);
    }//GEN-LAST:event_jTextFieldFindActionPerformed

    void jMenuItemMergeTiffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMergeTiffActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemMergeTiffActionPerformed

	void jMenuItemSplitTiffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSplitTiffActionPerformed
            JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemSplitTiffActionPerformed

    private void jButtonPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevActionPerformed
        int index = this.jTable.getSelectedRow();
        if (index > 0) {
            boxes.deselectAll();
            this.jTable.clearSelection();
            --index;
            java.util.List<TessBox> boxesOfCurPage = boxes.toList(); // boxes of current page
            TessBox selected = boxesOfCurPage.get(index);
            selected.setSelected(true);
            this.jTable.addRowSelectionInterval(index, index);
            Rectangle rect = this.jTable.getCellRect(index, 0, true);
            this.jTable.scrollRectToVisible(rect);
        }
    }//GEN-LAST:event_jButtonPrevActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        if (boxes == null) {
            return;
        }
        java.util.List<TessBox> boxesOfCurPage = boxes.toList(); // boxes of current page
        int index = this.jTable.getSelectedRow();
        if (index < boxesOfCurPage.size() - 1) {
            boxes.deselectAll();
            this.jTable.clearSelection();
            ++index;
            TessBox selected = boxesOfCurPage.get(index);
            selected.setSelected(true);
            this.jTable.addRowSelectionInterval(index, index);
            Rectangle rect = this.jTable.getCellRect(index, 0, true);
            this.jTable.scrollRectToVisible(rect);
        }
    }//GEN-LAST:event_jButtonNextActionPerformed

    void jButtonInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInputActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonInputActionPerformed

    void jButtonFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFontActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonFontActionPerformed

    void jSpinnerTrackingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerTrackingStateChanged
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jSpinnerTrackingStateChanged

    void jButtonGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonGenerateActionPerformed

    void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonClearActionPerformed

    void jButtonBrowseTessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseTessActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonBrowseTessActionPerformed

    void jButtonBrowseDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseDataActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonBrowseDataActionPerformed

    void jButtonTrainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTrainActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonTrainActionPerformed

    void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jTabbedPaneMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneMainStateChanged
        JTabbedPane pane = (JTabbedPane) evt.getSource();
        boolean boxEditorActive = pane.getSelectedIndex() == 1;
        this.jMenuEdit.setVisible(boxEditorActive);
        this.jMenuItemFont.setVisible(boxEditorActive || pane.getSelectedIndex() == 0);
        this.jSeparatorLAF.setVisible(this.jMenuItemFont.isVisible());
        this.jMenuItemOpen.setVisible(boxEditorActive);
        this.jMenuItemSave.setVisible(boxEditorActive);
        this.jMenuItemSaveAs.setVisible(boxEditorActive);
        this.jMenuRecentFiles.setVisible(boxEditorActive);
        this.jSeparatorRecentFiles.setVisible(boxEditorActive);
        this.jSeparatorExit.setVisible(boxEditorActive);
    }//GEN-LAST:event_jTabbedPaneMainStateChanged

    void jButtonClearLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearLogActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonClearLogActionPerformed

    void jButtonSaveLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveLogActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonSaveLogActionPerformed

    void jButtonValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonValidateActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonValidateActionPerformed

    void jButtonCloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseDialogActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonCloseDialogActionPerformed

    private void jSpinnerMarginStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerMarginStateChanged
        iconMargin = (Integer) jSpinnerMargin.getValue();
        int index = jTable.getSelectedRow();
        jTable.clearSelection();
        jTable.setRowSelectionInterval(index, index);
        jLabelSubimage.requestFocus();
    }//GEN-LAST:event_jSpinnerMarginStateChanged

    private void jSpinnerScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerScaleStateChanged
        scaleFactor = (Integer) jSpinnerScale.getValue();
        int index = jTable.getSelectedRow();
        jTable.clearSelection();
        jTable.setRowSelectionInterval(index, index);
        jLabelSubimage.requestFocus();
    }//GEN-LAST:event_jSpinnerScaleStateChanged

    private void jTextFieldCharacterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCharacterKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE || evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jLabelSubimage.requestFocus();
        }
    }//GEN-LAST:event_jTextFieldCharacterKeyReleased

    void jButtonBrowseOutputDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseOutputDirActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonBrowseOutputDirActionPerformed

    private void jCheckBoxText2ImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxText2ImageActionPerformed
        this.jPanelFontAttrib.setVisible(!this.jCheckBoxText2Image.isSelected());
        this.jPanelFontFolder.setVisible(this.jCheckBoxText2Image.isSelected());
        this.jTextAreaInput.setEnabled(!this.jCheckBoxText2Image.isSelected());
    }//GEN-LAST:event_jCheckBoxText2ImageActionPerformed

    void jButtonBrowseFontFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseFontFolderActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonBrowseFontFolderActionPerformed

    private void jSpinnerExposureStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerExposureStateChanged
        String imageFilename = jTextFieldFileName.getText().trim();
        int exposureLevel = (int) this.jSpinnerExposure.getValue();
        jTextFieldFileName.setText(imageFilename.replaceFirst("exp.*?\\.tif$", "exp" + exposureLevel + ".tif"));
    }//GEN-LAST:event_jSpinnerExposureStateChanged

    void jMenuItemMarkEOLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkEOLActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemMarkEOLActionPerformed

    void jMenuItemMarkEOLBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkEOLBulkActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemMarkEOLBulkActionPerformed

    void jMenuItemRemoveEmptyBoxesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoveEmptyBoxesActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemRemoveEmptyBoxesActionPerformed

    /**
     * Gets a subimage for display in boxview.
     *
     * @param image
     * @param rect
     * @return
     */
    BufferedImage getSubimage(BufferedImage image, Rectangle rect) {
        Gui.iconPosX = rect.x;
        Gui.iconPosY = rect.y;

        Gui.imageWidth = image.getWidth();
        Gui.imageHeight = image.getHeight();

        Gui.iconHeight = rect.height;
        Gui.iconWidth = rect.width;

        int height = Gui.iconHeight + Gui.iconMargin * 2;
        int width = Gui.iconWidth + Gui.iconMargin * 2;

        while (width + Gui.iconPosX > image.getWidth() + 1) {
            width -= 1;
        }

        while (height + Gui.iconPosY > image.getHeight() + 1) {
            height -= 1;
        }

        BufferedImage subImage = image.getSubimage(Math.max(0, Math.min(Gui.imageWidth - 1, Gui.iconPosX - Gui.iconMargin)),
                Math.max(0, Math.min(Gui.imageHeight - 1, Gui.iconPosY - Gui.iconMargin)),
                width,
                height
        );

        return subImage;
    }

    void setColumnWidth() {
        TableColumnModel columnModel = jTable.getColumnModel();
        float factor;
        if (this.jTable.getFont().getSize() >= 36) {
            factor = 2.3f;
        } else if (this.jTable.getFont().getSize() >= 24) {
            factor = 1.6f;
        } else {
            factor = this.jTable.getFont().getSize() / 12f;
        }

        int minColWidth = (int) (fixedMinColWidth * factor);
        columnModel.getColumn(0).setMinWidth(minColWidth);
        columnModel.getColumn(1).setMinWidth(minColWidth);
        columnModel.getColumn(2).setMinWidth(minColWidth);
        columnModel.getColumn(3).setMinWidth(minColWidth);
        columnModel.getColumn(4).setMinWidth(minColWidth);

        int maxColWidth = (int) (fixedMaxColWidth * factor);
        columnModel.getColumn(1).setMaxWidth(maxColWidth);
        columnModel.getColumn(2).setMaxWidth(maxColWidth);
        columnModel.getColumn(3).setMaxWidth(maxColWidth);
        columnModel.getColumn(4).setMaxWidth(maxColWidth);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler2;
    protected javax.swing.JButton jButtonBrowseData;
    private javax.swing.JButton jButtonBrowseFontFolder;
    private javax.swing.JButton jButtonBrowseOutputDir;
    protected javax.swing.JButton jButtonBrowseTess;
    protected javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonClear;
    protected javax.swing.JButton jButtonClearLog;
    private javax.swing.JButton jButtonCloseDialog;
    private javax.swing.JButton jButtonConvert;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonFind;
    protected javax.swing.JButton jButtonFont;
    protected javax.swing.JButton jButtonGenerate;
    private javax.swing.JButton jButtonInput;
    private javax.swing.JButton jButtonInsert;
    private javax.swing.JButton jButtonMerge;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonNextPage;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonPrev;
    private javax.swing.JButton jButtonPrevPage;
    private javax.swing.JButton jButtonReload;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSaveLog;
    private javax.swing.JButton jButtonSplit;
    protected javax.swing.JButton jButtonTrain;
    protected javax.swing.JButton jButtonValidate;
    protected javax.swing.JCheckBox jCheckBoxAntiAliasing;
    protected javax.swing.JCheckBox jCheckBoxRTL;
    protected javax.swing.JCheckBox jCheckBoxText2Image;
    protected javax.swing.JComboBox jComboBoxOps;
    protected javax.swing.JDialog jDialogValidationResult;
    protected javax.swing.JFileChooser jFileChooserInputImage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelCharacter;
    private javax.swing.JLabel jLabelCodepoint;
    private javax.swing.JLabel jLabelExposure;
    private javax.swing.JLabel jLabelH;
    private javax.swing.JLabel jLabelH1;
    protected javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelLeading;
    private javax.swing.JLabel jLabelNoise;
    private javax.swing.JLabel jLabelOutput;
    private javax.swing.JLabel jLabelPageNbr;
    private javax.swing.JLabel jLabelSpinnerMargin;
    private javax.swing.JLabel jLabelSpinnerScale;
    protected javax.swing.JLabel jLabelStatus;
    protected javax.swing.JLabel jLabelSubimage;
    protected javax.swing.JLabel jLabelTime;
    private javax.swing.JLabel jLabelTracking;
    private javax.swing.JLabel jLabelW;
    private javax.swing.JLabel jLabelW1;
    private javax.swing.JLabel jLabelX;
    private javax.swing.JLabel jLabelY;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemDelete;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemFont;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemInsert;
    private javax.swing.JMenuItem jMenuItemMarkEOL;
    protected javax.swing.JMenuItem jMenuItemMarkEOLBulk;
    private javax.swing.JMenuItem jMenuItemMerge;
    private javax.swing.JMenuItem jMenuItemMergeTiff;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemRemoveEmptyBoxes;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemSplit;
    private javax.swing.JMenuItem jMenuItemSplitTiff;
    protected javax.swing.JMenu jMenuLookAndFeel;
    protected javax.swing.JMenu jMenuRecentFiles;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelBoxView;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelChar;
    private javax.swing.JPanel jPanelCommand;
    private javax.swing.JPanel jPanelControls;
    protected javax.swing.JPanel jPanelCoord;
    private javax.swing.JPanel jPanelEditor;
    private javax.swing.JPanel jPanelFind;
    private javax.swing.JPanel jPanelFontAttrib;
    private javax.swing.JPanel jPanelFontFolder;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelNorthContainer;
    private javax.swing.JPanel jPanelSpinner;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanelStatus1;
    private javax.swing.JPanel jPanelTIFFBox;
    private javax.swing.JPanel jPanelTrainer;
    protected javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneBoxData;
    private javax.swing.JScrollPane jScrollPaneCoord;
    private javax.swing.JScrollPane jScrollPaneImage;
    private javax.swing.JScrollPane jScrollPaneText;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparatorAbout;
    private javax.swing.JPopupMenu.Separator jSeparatorExit;
    private javax.swing.JPopupMenu.Separator jSeparatorLAF;
    private javax.swing.JPopupMenu.Separator jSeparatorRecentFiles;
    protected javax.swing.JSpinner jSpinnerExposure;
    protected javax.swing.JSpinner jSpinnerH;
    protected javax.swing.JSpinner jSpinnerH1;
    protected javax.swing.JSpinner jSpinnerLeading;
    private javax.swing.JSpinner jSpinnerMargin;
    protected javax.swing.JSpinner jSpinnerNoise;
    private javax.swing.JSpinner jSpinnerScale;
    protected javax.swing.JSpinner jSpinnerTracking;
    protected javax.swing.JSpinner jSpinnerW;
    protected javax.swing.JSpinner jSpinnerW1;
    protected javax.swing.JSpinner jSpinnerX;
    protected javax.swing.JSpinner jSpinnerY;
    private javax.swing.JSplitPane jSplitPaneEditor;
    private javax.swing.JTabbedPane jTabbedPaneBoxData;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    protected javax.swing.JTable jTable;
    protected javax.swing.JTextArea jTextAreaBoxData;
    protected javax.swing.JTextArea jTextAreaInput;
    protected javax.swing.JTextArea jTextAreaOutput;
    protected javax.swing.JTextArea jTextAreaValidationResult;
    protected javax.swing.JTextField jTextFieldBootstrapLang;
    protected javax.swing.JTextField jTextFieldChar;
    protected javax.swing.JTextField jTextFieldCharacter;
    private javax.swing.JTextField jTextFieldCodepointValue;
    protected javax.swing.JTextField jTextFieldDataDir;
    protected javax.swing.JTextField jTextFieldFileName;
    protected javax.swing.JTextField jTextFieldFind;
    protected javax.swing.JTextField jTextFieldFontFolder;
    protected javax.swing.JTextField jTextFieldLang;
    protected javax.swing.JTextField jTextFieldOuputDir;
    protected javax.swing.JTextField jTextFieldPrefix;
    protected javax.swing.JTextField jTextFieldTessDir;
    private javax.swing.JToolBar jToolBarEditor;
    private javax.swing.JToolBar jToolBarGenerator;
    private javax.swing.JToolBar jToolBarTrainer;
    // End of variables declaration//GEN-END:variables
    private JFrame helptopicsFrame;
}
