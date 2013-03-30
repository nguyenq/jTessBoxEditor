/**
 * Copyright © 2013 Java Rich Client
 * How to display line numbers in JTable
 * http://www.javarichclient.com/display-line-numbers-jtable/
 */
package net.sourceforge.tessboxeditor.components;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class LineNumberTableRowHeader extends JComponent {

    private final JTable table;
    private final JScrollPane scrollPane;

    public LineNumberTableRowHeader(JScrollPane jScrollPane, JTable table) {
        this.scrollPane = jScrollPane;
        this.table = table;
        this.table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent tme) {
                LineNumberTableRowHeader.this.repaint();
            }
        });

        this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                LineNumberTableRowHeader.this.repaint();
            }
        });

        this.scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent ae) {
                LineNumberTableRowHeader.this.repaint();
            }
        });

        setPreferredSize(new Dimension(50, 100));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Point viewPosition = scrollPane.getViewport().getViewPosition();
        Dimension viewSize = scrollPane.getViewport().getViewSize();

        g.setFont(table.getFont()); // use table font
        FontMetrics fm = g.getFontMetrics();

        if (getHeight() < viewSize.height) {
            Dimension size = getPreferredSize();
            size.height = viewSize.height;
            size.width = fm.stringWidth("9999") + 2; // set cell width equal 4-digit length + margin
            setSize(size);
            setPreferredSize(size);
        }

        int cellWidth = getWidth();
        g.setColor(getBackground());
        g.fillRect(0, 0, cellWidth, getHeight());

        for (int r = 0; r < table.getRowCount(); r++) {
            Rectangle cellRect = table.getCellRect(r, 0, false);

            boolean rowSelected = table.isRowSelected(r);

            if (rowSelected) {
                g.setColor(table.getSelectionBackground());
                g.fillRect(0, cellRect.y, cellWidth, cellRect.height);
            }

            if ((cellRect.y + cellRect.height) - viewPosition.y >= 0 && cellRect.y < viewPosition.y + viewSize.height) {
                g.setColor(table.getGridColor());
                g.drawLine(0, cellRect.y + cellRect.height, cellWidth, cellRect.y + cellRect.height);
                g.setColor(rowSelected ? table.getSelectionForeground() : getForeground());
                String s = Integer.toString(r + 1);
                g.drawString(s, (cellWidth - fm.stringWidth(s)) / 2, cellRect.y + cellRect.height - fm.getDescent()); // center
            }
        }

        if (table.getRowCount() > 0 && table.getShowVerticalLines()) {
            g.setColor(table.getGridColor());
            g.drawRect(0, 0, cellWidth - 1, getHeight() - 1);
        }
    }
}