package at.ac.fhcampuswien.alcatraz.client.util;

import at.ac.fhcampuswien.alcatraz.client.ClientGui;
import org.jboss.logging.Logger;

import javax.swing.*;

public class AlcatrazClientLogger {

    private static final Logger log = Logger.getLogger(AlcatrazClientLogger.class);

    public static void logInfo(String msg){
        log.info(msg);
    }

    public static void logInfo(JTextArea jTextArea, String msg){
        jTextArea.append(msg + "\n");
        log.info(msg);
    }

    public static void logError(ClientGui clientGui, String message) {
        JOptionPane.showMessageDialog(clientGui, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
