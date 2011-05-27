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
import javax.swing.JOptionPane;
import net.sourceforge.tessboxeditor.GuiWithTools;

public class JTessBoxEditor {

    public static void main(String[] args) {
        String jreVersion = System.getProperty("java.version");
        if (jreVersion.compareTo("1.6") < 0) {
            JOptionPane.showMessageDialog(null,
                    "Could not launch the application because\n"
                    + "it requires Java 6 (1.6) or later.\n"
                    + "The current Java version is " + jreVersion + ".",
                    "Insufficient Java Version", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        GuiWithTools.main(args);
    }
}
