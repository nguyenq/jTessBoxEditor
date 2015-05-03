package net.sourceforge.vietpad.utilities;

import java.awt.Toolkit;
import javax.swing.text.*;

/**
 *  Limited Length Document
 *
 *@author     Quan Nguyen
 *@version    1.0.5, 19 April 2003
 *@see        "http://vietpad.sourceforge.net"
 */
public class LimitedLengthDocument extends PlainDocument {
    private final int max;

    public LimitedLengthDocument(int max) {
        this.max = max;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (getLength() + str.length() <= max) {
            super.insertString(offs, str, a);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

}
