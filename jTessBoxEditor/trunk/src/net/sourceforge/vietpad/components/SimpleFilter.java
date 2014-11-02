/**
 *  Copyright 1999-2002 Matthew Robinson and Pavel Vorobiev.
 *  All Rights Reserved.
 *
 *  ===================================================
 *  This program contains code from the book "Swing"
 *  2nd Edition by Matthew Robinson and Pavel Vorobiev
 *  http://www.spindoczine.com/sbe
 *  ===================================================
 *
 */
package net.sourceforge.vietpad.components;

import java.io.File;

/**
 *  Simple File Filter
 *
 *  Modified to accept multiple filters.
 * 
 *@author     Quan Nguyen
 *@version    1.2, June 21, 2009
 */
public class SimpleFilter extends javax.swing.filechooser.FileFilter {

    private final String m_description;
    private final String m_extension;
    private final String[] extensions;

    public SimpleFilter(String extension, String description) {
        m_description = description;
        m_extension = extension.toLowerCase();
        extensions = m_extension.split(";");
    }

    @Override
    public String getDescription() {
        return m_description;
    }

    public String getExtension() {
        return m_extension;
    }

    @Override
    public boolean accept(File f) {
        if (f == null) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        }
        if (m_extension.equals("*")) {
            return true;
        }

        String lowerCaseFileName = f.getName().toLowerCase();

        for (String ext : extensions) {
            if (lowerCaseFileName.endsWith("." + ext)) {
                return true;
            }
        }

        return false;
    }
}
