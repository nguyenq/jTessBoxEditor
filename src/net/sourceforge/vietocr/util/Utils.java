/**
 * Copyright @ 2009 Quan Nguyen
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
package net.sourceforge.vietocr.util;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private static final String EOL = "\n";
    private final static Logger logger = Logger.getLogger(Utils.class.getName());

    /**
     * Gets the directory of the executing jar.
     *
     * @param aType
     * @return the directory of the running jar
     */
    public static File getBaseDir(Object aType) {
        URL dir = aType.getClass().getResource("/" + aType.getClass().getName().replaceAll("\\.", "/") + ".class");
        File dbDir = new File(System.getProperty("user.dir"));

        try {
            if (dir.toString().startsWith("jar:")) {
                dir = new URL(dir.toString().replaceFirst("^jar:", "").replaceFirst("/[^/]+.jar!.*$", ""));
                dbDir = new File(dir.toURI());
            }
        } catch (MalformedURLException | URISyntaxException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return dbDir;
    }

    /**
     * Gets filename without extension.
     * http://stackoverflow.com/questions/924394/how-to-get-file-name-without-the-extension
     *
     * @param str
     * @return
     */
    public static String stripExtension(String str) {
        // Handle null case specially.
        if (str == null) {
            return null;
        }

        // Get position of last '.'.
        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) {
            return str;
        }

        // Otherwise return the string, up to the dot.
        return str.substring(0, pos);
    }

    /**
     * Outputs Unicode codepoint representation.
     *
     * @param source
     * @return
     */
    public static String toHex(String source) {
        StringBuilder sb = new StringBuilder();
        for (char ch : source.toCharArray()) {
            sb.append("U+").append(padLeft(ch, 4));
        }
        return sb.toString();
    }

    /**
     * Helper method to add padding for Unicode codepoint.
     *
     * @param source
     * @param n
     * @return
     */
    static String padLeft(int source, int n) {
        return String.format("%1$0" + n + "X", source);
    }

    /**
     * Capitalizes first letter.
     *
     * @param line
     * @return
     */
    public static String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1).toLowerCase();
    }

    /**
     * Strings join.
     * http://stackoverflow.com/questions/187676/java-equivalents-of-c-sharp-string-format-and-string-join
     *
     * @param s
     * @param delimiter
     * @return
     */
    public static String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }

    /**
     * Reads a text file.
     *
     * @param textFile
     * @return
     * @throws Exception
     */
    public static String readTextFile(File textFile) throws Exception {
        return new String(Files.readAllBytes(textFile.toPath()), "UTF8"); // Java 7 API
    }
}
