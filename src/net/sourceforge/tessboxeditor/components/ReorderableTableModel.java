package net.sourceforge.tessboxeditor.components;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Implements Reorderable interface to enable row re-ordering.
 */
public class ReorderableTableModel extends DefaultTableModel implements Reorderable {
    
    public ReorderableTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reorder(int from, int to) {
        Object obj = getDataVector().remove(from);
        getDataVector().add(from > to ? to : to - 1, (Vector) obj);
        fireTableRowsDeleted(from, from);
        fireTableRowsInserted(to, to);
//        fireTableDataChanged();
    }
}
