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
}
