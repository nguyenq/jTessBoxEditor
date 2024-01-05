package net.sourceforge.vietocr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * When Runtime.exec() won't.
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 */
public class StreamGobbler extends Thread {

    InputStream is;
    StringBuilder outputMessage = new StringBuilder();

    private final static Logger logger = Logger.getLogger(StreamGobbler.class.getName());

    public StreamGobbler(InputStream is) {
        this.is = is;
    }

    public String getMessage() {
        return outputMessage.toString();
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                outputMessage.append(line).append("\n");
            }
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }
}
