package net.sourceforge.tessboxeditor.components;

/**
 * To be implemented by the target TableModel to allow for re-ordering.
 */
public interface Reorderable {
   public void reorder(int fromIndex, int toIndex);
}
