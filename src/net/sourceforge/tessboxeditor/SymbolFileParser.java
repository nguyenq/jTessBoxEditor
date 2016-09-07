/**
 * Copyright @ 2016 Quan Nguyen
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
package net.sourceforge.tessboxeditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.vietocr.util.Utils;
import net.sourceforge.vietpad.utilities.TextUtilities;

/**
 * Parses combining symbols for complex language scripts.
 */
public class SymbolFileParser {

    private final File baseDir = Utils.getBaseDir(SymbolFileParser.this);
    private String combiningPattern;
    private String combiningSymbols;
    private String appendingSymbols;
    private String prependingSymbols;

    private final static Logger logger = Logger.getLogger(SymbolFileParser.class.getName());

    public SymbolFileParser() {
        combiningSymbols = readCombiningSymbols();
        setCombiningSymbols();
    }

    /**
     * Reads in combining symbols from <code>data/combiningsymbols.txt</code>.
     *
     * @return
     */
    private String readCombiningSymbols() {
        String str = null;
        try {
            File symbolFile = new File(baseDir, "data/combiningsymbols.txt");
            if (!symbolFile.exists()) {
                return null;
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    new FileInputStream(symbolFile), "UTF8"))) {
                while ((str = in.readLine()) != null) {
                    // strip BOM character
                    if (str.length() > 0 && str.charAt(0) == '\ufeff') {
                        str = str.substring(1);
                    }
                    // skip empty line or line starts with #
                    if (str.trim().length() > 0 && !str.trim().startsWith("#")) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        if (str != null) {
            str = str.replaceAll("[ \\[\\]]", ""); // strip regex special characters
            str = TextUtilities.convertNCR(str); // convert escaped sequences to Unicode
        }

        return str;
    }

    /**
     * Sets combining symbols.
     *
     * @param combiningSymbols
     */
    private void setCombiningSymbols() {
        if (combiningSymbols == null) {
            return;
        }
        String[] str = combiningSymbols.split(";");
        if (str.length > 0) {
            this.appendingSymbols = str[0];
            combiningPattern = ".[" + appendingSymbols + "]*";
        }
        if (str.length > 1) {
            this.prependingSymbols = str[1];
            combiningPattern = "[" + prependingSymbols + "]*" + combiningPattern;
        }

        combiningPattern = "(?s)" + combiningPattern;
    }

    /**
     * @return the combiningSymbols
     */
    public String getCombiningPattern() {
        return combiningPattern;
    }

    /**
     * @return the combiningSymbols
     */
    public String getCombiningSymbols() {
        return combiningSymbols;
    }

    /**
     * @return the appendingSymbols
     */
    public String getAppendingSymbols() {
        return appendingSymbols;
    }

    /**
     * @return the prependingSymbols
     */
    public String getPrependingSymbols() {
        return prependingSymbols;
    }
}
