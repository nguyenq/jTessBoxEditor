package net.sourceforge.tessboxeditor;

import com.apple.eawt.*;
import com.apple.eawt.AppEvent.*;
import java.io.File;

/**
 *  Mac OS X functionality for JTessBoxEditor.
 *
 *@author     Quan Nguyen
 *@modified   April 2, 2011
 */
class MacOSXApplication {

    private final static int ZOOM_LIMIT = 60;
    // http://www.mactech.com/articles/develop/issue_17/Yu_final.html

    Application app = null;

    /**
     *  Constructor for the MacOSXApplication object.
     *
     *@param  gui  calling instance of GUI
     */
    public MacOSXApplication(final Gui gui) {
        app = Application.getApplication();

//        gui.setMaximizedBounds(new Rectangle(
//                Math.max(gui.getWidth(), gui.font.getSize() * ZOOM_LIMIT),
//                Integer.MAX_VALUE));
//        gui.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        gui.scrollPane.setBorder(null); // line up scrollbars with grow box
//        gui.m_toolBar.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0.5765F, 0.5765F, 0.5765F)),
//                vietOCR.jToolBar.getBorder()));
        
//        app.setDefaultMenuBar(vietOCR.getJMenuBar());
        app.setAboutHandler(new AboutHandler() {

            @Override
            public void handleAbout(AboutEvent ae) {
                gui.about();
            }
        });

        app.setOpenFileHandler(new OpenFilesHandler() {

            @Override
            public void openFiles(OpenFilesEvent ofe) {
                File droppedFile = ofe.getFiles().get(0);
                if (droppedFile.isFile() && gui.promptToSave()) {
                    gui.openFile(droppedFile);
                }
            }
        });

        app.setPreferencesHandler(new PreferencesHandler() {

            @Override
            public void handlePreferences(PreferencesEvent pe) {
//                gui.openOptionsDialog();
            }
        });

        app.setQuitHandler(new QuitHandler() {

            @Override
            public void handleQuitRequestWith(QuitEvent qe, QuitResponse qr) {
                gui.quit();
            }
        });
    }
}
