package sk.r3n.app.impl;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.osgi.service.component.ComponentContext;
import sk.r3n.app.AppHelp;

public class AppHelpImpl implements AppHelp {

    private static final Logger LOGGER = Logger.getLogger(AppHelp.class.getCanonicalName());
    private String DIR;
    private Properties properties;

    protected void activate(ComponentContext context) {
        properties = new Properties();
        DIR = context.getBundleContext().getProperty(HELP_DIR);
        InputStream in = null;
        try {
            in = new FileInputStream(DIR
                    + context.getBundleContext().getProperty(HELP_MAP));
            properties.load(in);
        } catch (Exception e) {
            LOGGER.log(
                    Level.WARNING,
                    ResourceBundle.getBundle(
                    AppHelpImpl.class.getCanonicalName()).getString(
                    "LOAD"), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
        }
    }

    protected void deactivate(ComponentContext context) {
        OutputStream out = null;
        try {
            File file = new File(DIR
                    + context.getBundleContext().getProperty(HELP_MAP));
            if (file.canWrite()) {
                out = new FileOutputStream(file);
                properties.store(out, AppHelp.class.getCanonicalName());
            }
        } catch (Exception e) {
            LOGGER.log(
                    Level.WARNING,
                    ResourceBundle.getBundle(
                    AppHelpImpl.class.getCanonicalName()).getString(
                    "SAVE"), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void showHelp() {
        showPage("index.html");
    }

    @Override
    public void showHelp(String key) {
        if (!properties.containsKey(key)) {
            properties.put(key, "");
        }
        if (properties.getProperty(key).equals("")) {
            showHelp();
        } else {
            showPage(properties.getProperty(key));
        }
    }

    private void showPage(String name) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    URI uri = new File(DIR + name).toURI();
                    desktop.browse(uri);
                    return;
                } catch (Exception e) {
                    LOGGER.log(
                            Level.WARNING,
                            ResourceBundle.getBundle(
                            AppHelpImpl.class.getCanonicalName()).getString("SHOW_PAGE"), e);
                }
            }
        }
        LOGGER.log(
                Level.WARNING,
                "{0}[{1}{2}]",
                new Object[]{ResourceBundle.getBundle(AppHelpImpl.class.getCanonicalName()).getString("SHOW_PAGE"), DIR, name});
    }
}
