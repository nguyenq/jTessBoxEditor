/* Sun Micro System
 *
 * @(#)HtmlPane.java    1.0  4/24/2001
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. THE AUTHOR SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF THE AUTHOR HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 */

package net.sourceforge.vietpad.components;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * @author: Quan Nguyen
 * @version: 1.4, 19 May 2004
 * @see: http://vietpad.sourceforge.net
 * @note: Modified to open external html links with default browser
 */
public class HtmlPane extends JScrollPane implements HyperlinkListener {
    private JEditorPane html;
    private JTextField statusBar;
    private final ClassLoader cLoader;

    public HtmlPane(String resource) {
        cLoader = getClass().getClassLoader();
        setPreferredSize(new Dimension(600, 360));      
        try {
            URL url = cLoader.getResource(resource);
            html = new JEditorPane(url);
            html.setEditable(false);
            html.setMargin(new Insets(5,10,5,5));
            html.addHyperlinkListener(this);
            JViewport vp = getViewport();
            vp.add(html);
            statusBar = new JTextField();
            statusBar.setEditable(false);
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }   
    }

    /**
     * Notification of a change relative to a hyperlink.
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            statusBar.setText(e.getURL().toString());
        } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            statusBar.setText(null);
        } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            linkActivated(e.getURL());
        }
    }
    
    /**
     * Follows the reference in an
     * link.  The given url is the requested reference.
     * By default this calls <a href="#setPage">setPage</a>,
     * and if an exception is thrown the original previous
     * document is restored and a beep sounded.  If an 
     * attempt was made to follow a link, but it represented
     * a malformed url, this method will be called with a
     * null argument.
     *
     * @param url the URL to follow
     */
    protected void linkActivated(URL url) {
        try {
            if (url.toString().startsWith("jar:")) {
                html.setPage(url);
            } else {
                Desktop.getDesktop().browse(url.toURI());
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error message: " + e.getMessage()); 
            Cursor cursor = html.getCursor();
            Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
            html.setCursor(waitCursor);
            SwingUtilities.invokeLater(new PageLoader(url, cursor));
        }
    }
    
    /**
     * Get the Status Bar.
     * 
     * @return 
     */
    public JTextField getStatusBar() {
        return statusBar;
    }
    
    /**
     * temporary class that loads synchronously (although
     * later than the request so that a cursor change
     * can be done).
     */
    class PageLoader implements Runnable {
        URL url;
        Cursor cursor;
        
        PageLoader(URL u, Cursor c) {
            url = u;
            cursor = c;
        }

        @Override
        public void run() {
            if (url == null) {
                // restore the original cursor
                html.setCursor(cursor);

                // PENDING(prinz) remove this hack when 
                // automatic validation is activated.
                Container parent = html.getParent();
                parent.repaint();
            } else {
                Document doc = html.getDocument();
                try {
                    html.setPage(url);
                } catch (IOException ioe) {
                    html.setDocument(doc);
                    getToolkit().beep();
                } finally {
                    // schedule the cursor to revert after
                    // the paint has happended.
                    url = null;
                    SwingUtilities.invokeLater(this);
                }
            }
        }
    }
}
