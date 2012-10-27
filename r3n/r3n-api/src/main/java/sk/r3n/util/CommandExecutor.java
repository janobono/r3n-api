package sk.r3n.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandExecutor {

    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class.getCanonicalName());

    public int execute(String command) {
        String s;
        String txt = "";
        int result = 0;
        try {
            // spustenie prikazu
            // logger.finest(command);
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));
            // nacitanie vystupov z prikazu
            while ((s = stdInput.readLine()) != null) {
                txt += s;
                result = 1;
            }
            if (result == 1) {
                LOGGER.log(Level.CONFIG, "Command standard out {0}", txt);
            }
            // chybove vystupy
            txt = "";
            while ((s = stdError.readLine()) != null) {
                LOGGER.info(s);
                result = 2;
            }
            if (result == 2) {
                LOGGER.log(Level.CONFIG, "Command err out {0}", txt);
            }
        } catch (Exception e) {
            LOGGER.log(Level.CONFIG, "Command err out {0}", e.getMessage());
            result = 3;
        }
        return result;
    }

}
