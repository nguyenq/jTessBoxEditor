/**
 * Copyright @ 2013 Quan Nguyen
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

import javax.swing.JDialog;

public class GuiWithTrainer extends GuiWithTools {
    JDialog trainerDialog;
    
    @Override
    void jMenuItemTrainActionPerformed(java.awt.event.ActionEvent evt) {
        trainerDialog = new TrainDialog(this, true);
        trainerDialog.setVisible(true);
    }
    
      /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new GuiWithTrainer().setVisible(true);
            }
        });
    }
}
