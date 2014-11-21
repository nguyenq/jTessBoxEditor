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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sourceforge.vietpad.utilities.LimitedLengthDocument;

/**
 *  Open List
 *
 *@author     Quan Nguyen
 *@version    1.0.5, 19 April 2003
 *@see        <a href="http://vietpad.sourceforge.net">VietPad</a>
 */
public class OpenList extends JPanel implements ListSelectionListener, ActionListener {

    protected JLabel m_title;
    protected JTextField m_text;
    protected JList m_list;
    protected JScrollPane m_scroll;

    /**
     * Constructor for the OpenList object.
     * @param data List items
     * @param title Title
     * @param limit Max of characters in entry box
     */
    public OpenList(String[] data, String title, int limit) {
        this(data, title);
        m_text.setDocument(new LimitedLengthDocument(limit));
        m_text.setColumns(limit);
    }

    /**
     * Constructor for the OpenList object.
     * @param data List items
     * @param title Title
     */
    @SuppressWarnings("unchecked")
    public OpenList(Object[] data, String title) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        m_title = new JLabel(title);
        m_title.setLabelFor(m_list);
        add(m_title);
        m_text = new JTextField();
        m_text.setAlignmentX(LEFT_ALIGNMENT);
        m_text.addActionListener(this);
        add(m_text);
        m_list = new JList(data);
        m_list.setVisibleRowCount(4);
        m_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_list.addListSelectionListener(this);
        m_list.setFont(m_text.getFont());
        m_scroll = new JScrollPane(m_list);
        m_scroll.setAlignmentX(LEFT_ALIGNMENT);
        add(m_scroll);
    }

    public void setSelected(String sel) {
        m_list.setSelectedValue(sel, true);
        m_text.setText(sel);
    }

    public String getSelected() {
        return m_text.getText();
    }

    public int getSelectedIndex() {
        return m_list.getSelectedIndex();
    }

    public void setSelectedInt(int value) {
        setSelected(Integer.toString(value));
    }

    public int getSelectedInt() {
        try {
            return Integer.parseInt(getSelected());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        Object obj = m_list.getSelectedValue();
        if (obj != null) {
            m_text.setText(obj.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ListModel model = m_list.getModel();
        String key = m_text.getText().toLowerCase();
        for (int k = 0; k < model.getSize(); k++) {
            String data = (String) model.getElementAt(k);
            if (data.toLowerCase().startsWith(key)) {
                m_list.setSelectedValue(data, true);
                key = null;
                break;
            }
        }
        if (key != null) {
            m_list.clearSelection();
        }
    }

    public void addListSelectionListener(ListSelectionListener lst) {
        m_list.addListSelectionListener(lst);
    }

    public void addActionListener(ActionListener lst) {
        m_text.addActionListener(lst);
    }

    /**
     *  Gets the preferredSizeList attribute of the OpenList object,
     *  added to work around Bug ID: 4682565
     *
     *@return    The preferredSizeList value
     */
    public Dimension getPreferredSizeList() {
        return m_scroll.getPreferredSize();
    }

    /**
     *  Sets the preferredSizeList attribute of the OpenList object
     *  added to work around Bug ID: 4682565
     *
     *@param  d  The new preferredSizeList value
     */
    public void setPreferredSizeList(Dimension d) {
        m_scroll.setPreferredSize(new Dimension(m_scroll.getPreferredSize().width, d.height));
    }
}
