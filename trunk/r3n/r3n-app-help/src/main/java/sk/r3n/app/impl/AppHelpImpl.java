package sk.r3n.app.impl;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.osgi.service.component.ComponentContext;
import sk.r3n.app.AppHelp;
import sk.r3n.app.AppProperty;

public class AppHelpImpl implements AppHelp {

    private enum HelpBundle {

        LOAD,
        SAVE,
        SHOW_PAGE_ERR,
        SHOW_PAGE_WARNING;

        public String value() {
            return ResourceBundle.getBundle(AppHelpImpl.class.getCanonicalName()).getString(name());
        }

        public String value(Object[] parameters) {
            return MessageFormat.format(value(), parameters);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(AppHelp.class.getCanonicalName());

    private String DIR;

    private Properties properties;

    protected void activate(ComponentContext context) {
        properties = new Properties();
        DIR = context.getBundleContext().getProperty(AppProperty.HELP_DIR.code());
        InputStream in = null;
        try {
            in = new FileInputStream(DIR + context.getBundleContext().getProperty(AppProperty.HELP_MAP.code()));
            properties.load(in);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, HelpBundle.LOAD.value(), e);
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
                    LOGGER.log(Level.WARNING, HelpBundle.SHOW_PAGE_ERR.value(), e);
                }
            }
        }
        LOGGER.log(Level.WARNING, HelpBundle.SHOW_PAGE_WARNING.value(new Object[]{DIR, name}));
    }
}
