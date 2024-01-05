/**
 * Copyright @ 2018 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.vietpad.components;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * JFileChooser save with confirm dialog to overwrite an existing file.
 */
public class JFileChooserWithConfirm extends JFileChooser {

    private String localizedMessage = "%1$s already exists.\nDo you want to replace it?";
        
    public JFileChooserWithConfirm() {
        super();
    }
        
    public JFileChooserWithConfirm(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    public JFileChooserWithConfirm(File currentDirectory) {
        super(currentDirectory);
    }

    @Override
    public void approveSelection() {
        File selectedFile = getSelectedFile();
        FileFilter ff = this.getFileFilter();
        String fileFilters = ".*";
        String ext = "";

        if (ff instanceof SimpleFilter) {
            String exts = ((SimpleFilter) ff).getExtension();
            ext = "." + exts.split(";")[0]; // use first of multiple extensions
            fileFilters = "(" + exts.replace(';', '|') + ")";
        }

        if (!(selectedFile.getName().matches(".*\\." + fileFilters + "$"))) { // skip if extensions match
            selectedFile = new File(selectedFile.getParent(), selectedFile.getName() + ext); // append extension
            setSelectedFile(selectedFile);
        }

        if (selectedFile.exists() && getDialogType() == JFileChooser.SAVE_DIALOG) {
            int dialogResult = JOptionPane.showConfirmDialog(this, String.format(getLocalizedMessage(), selectedFile.getName()), "Confirm " + this.getDialogTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            switch (dialogResult) {
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.CANCEL_OPTION:
                    cancelSelection();
                    return;
                default:
                    return;
            }
        }
        super.approveSelection();
    }

    /**
     * @return the localizedMessage
     */
    public String getLocalizedMessage() {
        return localizedMessage;
    }

    /**
     * @param localizedMessage the localizedMessage to set
     */
    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }

}
