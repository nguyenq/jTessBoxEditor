/**
 * Copyright @ 2011 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.tessboxeditor;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.SwingUtilities;
import net.sourceforge.vietpad.components.FontDialog;

public class GuiWithFont extends GuiWithSpinner {

    private Font font;

    public GuiWithFont() {
        font = new Font(
                prefs.get("fontName", MAC_OS_X ? "Lucida Grande" : "Tahoma"),
                prefs.getInt("fontStyle", Font.PLAIN),
                prefs.getInt("fontSize", 12));
        changeFont(font);
    }

    @Override
    void jMenuItemFontActionPerformed(java.awt.event.ActionEvent evt) {
        FontDialog dlg = new FontDialog(this);
        dlg.setAttributes(font);
        dlg.setVisible(true);

        if (dlg.succeeded()) {
            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            getGlassPane().setVisible(true);
            
            try {
                font = dlg.getFont();
                changeFont(font);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(
                        new Runnable() {

                            @Override
                            public void run() {
                                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                                getGlassPane().setVisible(false);
                            }
                        });
            }
        }
    }

    private void changeFont(Font font) {
        this.jTextArea.setFont(font);
        this.jTextArea.validate();
        this.jTextFieldChar.setFont(font);
        this.jTextFieldChar.validate();
        this.jLabelChar.setFont(font.deriveFont(14.0f));
        jTable.setFont(font);
        FontMetrics metrics = jTable.getFontMetrics(font);
        jTable.setRowHeight(metrics.getHeight()); // set row height to match font
        jPanelCoord.revalidate();
        jLabelImage.setFont(font);
    }

    @Override
    void quit() {
        prefs.put("fontName", font.getName());
        prefs.putInt("fontSize", font.getSize());
        prefs.putInt("fontStyle", font.getStyle());

        super.quit();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GuiWithFont().setVisible(true);
            }
        });
    }
}
