package net.sourceforge.vietpad.utilities;

import java.util.regex.*;

/**
 *  Text utilities
 *
 *@author     Quan Nguyen
 *@author     Gero Herrmann
 *@version    1.1, 24 February 2010
 */
public class TextUtilities {

    /**
     * Changes letter case.
     * @param text
     * @param typeOfCase
     * @return
     */
    public static String changeCase(String text, String typeOfCase) {
        String result;

        if (typeOfCase.equals("UPPERCASE")) {
            result = text.toUpperCase();
        } else if (typeOfCase.equals("lowercase")) {
            result = text.toLowerCase();
        } else if (typeOfCase.equals("Title_Case")) {
            StringBuilder strB = new StringBuilder(text.toLowerCase());
            Pattern pattern = Pattern.compile("(?<!\\p{InCombiningDiacriticalMarks}|\\p{L})\\p{L}");
            // word boundary
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                int index = matcher.start();
                strB.setCharAt(index, Character.toTitleCase(strB.charAt(index)));
            }
            result = strB.toString();
        } else if (typeOfCase.equals("Sentence_case")) {
            StringBuilder strB = new StringBuilder(text.toUpperCase().equals(text) ? text.toLowerCase() : text);
            Matcher matcher = Pattern.compile("\\p{L}(\\p{L}+)").matcher(text);
            while (matcher.find()) {
                if (!(matcher.group(0).toUpperCase().equals(matcher.group(0))
                        || matcher.group(1).toLowerCase().equals(matcher.group(1)))) {
                    for (int i = matcher.start(); i < matcher.end(); i++) {
                        strB.setCharAt(i, Character.toLowerCase(strB.charAt(i)));
                    }
                }
            }
            final String QUOTE = "\"'`,<>\u00AB\u00BB\u2018-\u203A";
            matcher = Pattern.compile("(?:[.?!\u203C-\u2049][])}"
                    + QUOTE + "]*|^|\n|:\\s+["
                    + QUOTE + "])[-=_*\u2010-\u2015\\s]*["
                    + QUOTE + "\\[({]*\\p{L}").matcher(text);
            // begin of a sentence
            while (matcher.find()) {
                int i = matcher.end() - 1;
                strB.setCharAt(i, Character.toUpperCase(strB.charAt(i)));
            }
            result = strB.toString();
        } else {
            result = text;
        }

        return result;
    }

    /**
     * Removes line breaks.
     * @param text
     * @return
     */
    public static String removeLineBreaks(String text) {
        return text.replaceAll("(?<=\n|^)[\t ]+|[\t ]+(?=$|\n)", "").replaceAll("(?<=.)\n(?=.)", " ");
    }

    /**
     *  Converts Unicode escape sequences to Unicode.
     * @param str input string
     * @return converted string
     */
    public static String convertNCR(String str) {
        final String[] NCRs = {"\\u", "U+"};
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < NCRs.length; i++) {
            int radix;
            int foundIndex;
            int startIndex = 0;
            final int STR_LENGTH = str.length();
            final String NCR = NCRs[i];
            final int NCR_LENGTH = NCR.length();

            if (NCR.equals("&#") || NCR.equals("#")) {
                radix = 10;
            } else {
                radix = 16;
            }

            while (startIndex < STR_LENGTH) {
                foundIndex = str.indexOf(NCR, startIndex);

                if (foundIndex == -1) {
                    result.append(str.substring(startIndex));
                    break;
                }

                result.append(str.substring(startIndex, foundIndex));
                if (NCR.equals("\\u") || NCR.equals("U+")) {
                    startIndex = foundIndex + 6;
                    if (startIndex > str.length()) startIndex = -1; // for invalid Unicode escape sequences
                } else {
                    startIndex = str.indexOf(";", foundIndex);
                }

                if (startIndex == -1) {
                    result.append(str.substring(foundIndex));
                    break;
                }

                String tok = str.substring(foundIndex + NCR_LENGTH, startIndex);

                try {
                    result.append((char) Integer.parseInt(tok, radix));
                } catch (NumberFormatException nfe) {
                    try {
                        if (NCR.equals("\\u") || NCR.equals("U+")) {
                            result.append(NCR).append(tok);
                        } else {
                            result.append(NCR).append(tok).append(str.charAt(startIndex));
                        }
                    } catch (StringIndexOutOfBoundsException sioobe) {
                        result.append(NCR).append(tok);
                    }
                }

                if (!NCR.equals("\\u") && !NCR.equals("U+")) {
                    startIndex++;
                }
            }

            str = result.toString();
            result.setLength(0);
        }
        return str;
    }
    
        
    /**
     * Gets filename without extension. 
     * http://stackoverflow.com/questions/924394/how-to-get-file-name-without-the-extension
     * @param str
     * @return 
     */
    public static String stripExtension(String str) {
        // Handle null case specially.
        if (str == null) return null;

        // Get position of last '.'.
        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.
        return str.substring(0, pos);
    }
}
