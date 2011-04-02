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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JOptionPane;
import net.sourceforge.vietpad.components.FontDialog;

public class GuiWithSettings extends Gui {

    @Override
    void openFontDialog() {
        FontDialog dlg = new FontDialog(this);
        dlg.setAttributes(font);

        Properties prop = new Properties();

        try {
            File xmlFile = new File(baseDir, "data/pangram.xml");
            prop.loadFromXML(new FileInputStream(xmlFile));
            dlg.setPreviewText(prop.getProperty(langCode));
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, ioe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        dlg.setVisible(true);
        if (dlg.succeeded()) {
            jTextArea.setFont(font = dlg.getFont());
            jTextArea.validate();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GuiWithSettings().setVisible(true);
            }
        });
    }
}
