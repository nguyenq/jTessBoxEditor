/*
 *  Copyright 1999-2002 Matthew Robinson and Pavel Vorobiev.
 *  All Rights Reserved.
 *
 *  ===================================================
 *  This program contains code from the book "Swing"
 *  2nd Edition by Matthew Robinson and Pavel Vorobiev
 *  http://www.spindoczine.com/sbe
 *  ===================================================
 */
package net.sourceforge.vietpad.components;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  Font Dialog
 *
 *@author     Quan Nguyen
 *@version    1.3, April 12, 2011
 *@see        <a href="http://vietpad.sourceforge.net">VietPad</a>
 */
public class FontDialog extends JDialog {

    final static boolean MAC_OS_X = System.getProperty("os.name").startsWith("Mac");
    private OpenList m_lstFontName;
    private OpenList m_lstFontStyle;
    private OpenList m_lstFontSize;
    private JLabel m_preview;
    private final ResourceBundle bundle;
    private Font curFont;
    private boolean m_succeeded = false;
    private JComboBox combo;
    protected final File baseDir;
    
    private final static Logger logger = Logger.getLogger(FontDialog.class.getName());
    
    /**
     *  Constructor for the FontDialog object.
     *
     *@param  owner  the Frame from which the dialog is displayed
     */
    public FontDialog(JFrame owner) {
        super(owner, true);
        this.baseDir = getBaseDir(FontDialog.this);
        setLocale(owner.getLocale());
        bundle = ResourceBundle.getBundle("net.sourceforge.vietpad.components.FontDialog");
        this.setTitle(bundle.getString("this.Title"));
        setResizable(false);
        initComponents();

        // Handle Escape key to hide the dialog
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction =
                new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    /**
     *  Initializes the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        final JPanel pp = new JPanel();
        pp.setBorder(new EmptyBorder(5, 10, 11, 9));
        pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));
        JPanel p = new JPanel();
//      JPanel p = new JPanel(new GridLayout(1, 3, 10, 2));
        // Caused the fontname list to collapse in Solaris (Bug ID: 4682565)
        p.setBorder(BorderFactory.createEmptyBorder(0, -2, 0, -2));

        final Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        final Collection<String> fontFamilies = new TreeSet<String>();
        for (Font allFont : allFonts) {
            fontFamilies.add(allFont.getFamily());
        }

        m_lstFontName = new OpenList(fontFamilies.toArray(), bundle.getString("Name"));
        p.add(m_lstFontName);

        m_lstFontStyle = new OpenList(
                new String[]{"Regular", "Bold", "Italic", "Bold Italic"},
                bundle.getString("Style"),
                11);
        p.add(m_lstFontStyle);

        m_lstFontSize = new OpenList(
                new String[]{"9", "10", "11", "12", "13", "14", "18", "24", "36", "48", "64", "72", "96"},
                bundle.getString("Size"),
                5);
        p.add(m_lstFontSize);

        pp.add(p);

        m_lstFontName.setPreferredSizeList(m_lstFontStyle.getPreferredSizeList());
        // work around Bug ID: 4682565

        ListSelectionListener lsel =
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        SwingUtilities.invokeLater(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        updatePreview();
                                    }
                                });
                    }
                };
        m_lstFontName.addListSelectionListener(lsel);
        m_lstFontStyle.addListSelectionListener(lsel);
        m_lstFontSize.addListSelectionListener(lsel);

        m_lstFontSize.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        updatePreview();
                                    }
                                });
                    }
                });

        p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder(new EtchedBorder(), bundle.getString("Preview")));
        m_preview = new JLabel(
                "The quick brown fox jumps over the lazy dog.",
                JLabel.CENTER);
        m_preview.setBackground(Color.white);
        m_preview.setForeground(Color.black);
        m_preview.setOpaque(true);
        m_preview.setBorder(new LineBorder(Color.black));
        m_preview.setPreferredSize(new Dimension(120, 70));
        p.add(m_preview);
        pp.add(p);
        pp.add(Box.createVerticalStrut(9));

        p = new JPanel(null);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

        final Properties prop = new Properties();

        try {
            File xmlFile = new File(baseDir, "data/pangram.xml");
            prop.loadFromXML(new FileInputStream(xmlFile));
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, ioe.getMessage(), "Font Dialog", JOptionPane.ERROR_MESSAGE);
        } catch (Exception exc) {
        }
        Object[] langNames = prop.stringPropertyNames().toArray();
        Arrays.sort(langNames);
        combo = new JComboBox(langNames);
        combo.setSelectedItem("English");
        combo.setToolTipText("Language");
        combo.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_preview.setText(prop.getProperty(combo.getSelectedItem().toString()));
            }
        });
        p.add(combo);
        p.add(Box.createHorizontalGlue());

        JButton btOK = new JButton(bundle.getString("btOK.Text"));
        btOK.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        m_succeeded = true;
                        dispose();
                    }
                });

        JButton btCancel = new JButton(bundle.getString("btCancel.Text"));
        btCancel.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });

        if (MAC_OS_X) {
            p.add(btCancel);
            p.add(Box.createHorizontalStrut(7));
            p.add(btOK);
        } else {
            p.add(btOK);
            p.add(Box.createHorizontalStrut(5));
            p.add(btCancel);
            p.add(Box.createHorizontalStrut(2));
        }
        pp.add(p);

        getContentPane().add(pp);
        rootPane.setDefaultButton(btOK);
        pack();
        setLocationRelativeTo(getOwner());
    }

    /**
     *  Sets the attributes attribute of the FontDialog object.
     *
     *@param  font  The new attributes value
     */
    public void setAttributes(Font font) {
        m_lstFontName.setSelected(font.getName());
        m_lstFontSize.setSelectedInt(font.getSize());

        String styleName;
        switch (font.getStyle()) {
            case Font.BOLD | Font.ITALIC:
                styleName = "Bold Italic";
                break;
            case Font.BOLD:
                styleName = "Bold";
                break;
            case Font.ITALIC:
                styleName = "Italic";
                break;
            default:
                styleName = "Regular";
                break;
        }
        m_lstFontStyle.setSelected(styleName);
        updatePreview();
    }

    /**
     *  Whether font changes succeed.
     *
     *@return    Description of the Return Value
     */
    public boolean succeeded() {
        return m_succeeded;
    }

    /**
     *  Gets the font attribute of the FontDialog object.
     *
     *@return    The font value
     */
    @Override
    public Font getFont() {
        return curFont;
    }

    /**
     *  Updates Font Preview.
     */
    protected void updatePreview() {
        String name = m_lstFontName.getSelected();
        int size = m_lstFontSize.getSelectedInt();
        if (size <= 0) {
            return;
        }
        int style = m_lstFontStyle.getSelectedIndex();
        Font fn = new Font(name, style, size);
        m_preview.setFont(fn);
        m_preview.repaint();
        curFont = fn;
    }

    /**
     * Sets the preview text.
     * 
     * @param text
     */
    public void setPreviewText(String text) {
        m_preview.setText(text);
    }

    /**
     * Sets the selected language.
     *
     * @param lang
     */
    public void setSelectedLanguage(String lang) {
        combo.setSelectedItem(lang);
    }
    
    /**
     *
     * @param aType
     * @return the directory of the running jar
     */
    public static File getBaseDir(Object aType) {
        URL dir = aType.getClass().getResource("/" + aType.getClass().getName().replaceAll("\\.", "/") + ".class");
        File dbDir = new File(System.getProperty("user.dir"));

        try {
            if (dir.toString().startsWith("jar:")) {
                dir = new URL(dir.toString().replaceFirst("^jar:", "").replaceFirst("/[^/]+.jar!.*$", ""));
                dbDir = new File(dir.toURI());
            }
        } catch (MalformedURLException | URISyntaxException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return dbDir;
    }
}
