package net.sourceforge.tessboxeditor;

import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 * File Drop Target Listener
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 * @version 1.2, 05 November 2013
 * @see "http://vietpad.sourceforge.net"
 */
public class FileDropTargetListener extends DropTargetAdapter {

    private final Window holder;
    private final Component comp;
    private File droppedFile;
    
    private final static Logger logger = Logger.getLogger(FileDropTargetListener.class.getName());

    /**
     * Constructor for the FileDropTargetListener object
     *
     *
     * @param holder instance of Window
     * @param comp component that has a file dropped on
     */
    public FileDropTargetListener(Window holder, Component comp) {
        this.holder = holder;
        this.comp = comp;
    }

    /**
     * Gives visual feedback
     *
     * @param dtde the DropTargetDragEvent
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        if (droppedFile == null) {
            DataFlavor[] flavors = dtde.getCurrentDataFlavors();
            for (DataFlavor flavor : flavors) {
                if (flavor.isFlavorJavaFileListType()) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                    return;
                }
            }
        }
        dtde.rejectDrag();
    }

    /**
     * Handles dropped files
     *
     * @param dtde the DropTargetDropEvent
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        Transferable transferable = dtde.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        final boolean LINUX = System.getProperty("os.name").equals("Linux");
        for (DataFlavor flavor : flavors) {
            try {
                if (flavor.equals(DataFlavor.javaFileListFlavor) || (LINUX && flavor.getPrimaryType().equals("text") && flavor.getSubType().equals("uri-list"))) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    // Missing DataFlavor.javaFileListFlavor on Linux (Bug ID: 4899516)
                    if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                        java.util.List fileList = (java.util.List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        droppedFile = (File) fileList.get(0);
                    } else {
                        // This workaround is for File DnD on Linux
                        String string
                                = transferable.getTransferData(DataFlavor.stringFlavor).toString().replaceAll("\r\n?", "\n");
                        URI uri = new URI(string.substring(0, string.indexOf('\n')));
                        droppedFile = new File(uri);
                    }
                    // Note: On Windows, Java 1.4.2 can't recognize a Unicode file name
                    // (Bug ID 4896217). Fixed in Java 1.5.
                    // Processes one dropped file at a time in a separate thread
                    new Thread() {

                        @Override
                        public void run() {
                            if (holder instanceof Gui) {
                                if (comp instanceof JTextArea) {
                                    ((GuiWithGenerator) holder).openTextFile(droppedFile);
                                } else {
                                    ((Gui) holder).openFile(droppedFile);
                                }
                            }
                            droppedFile = null;
                        }
                    }.start();
                    dtde.dropComplete(true);
                    return;
                }
            } catch (UnsupportedFlavorException | IOException | URISyntaxException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                dtde.rejectDrop();
            }
        }
        dtde.dropComplete(false);
    }
}
