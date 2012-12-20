package sk.r3n.app.impl;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.osgi.service.component.ComponentContext;
import sk.r3n.app.AppHelp;
import sk.r3n.app.AppProperty;
import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public class AppHelpImpl implements AppHelp {

    private enum HelpBundle implements BundleEnum {

        LOAD,
        SAVE,
        SHOW_PAGE_ERR,
        SHOW_PAGE_WARNING;

        @Override
        public String value() {
            return BundleResolver.resolve(AppHelpImpl.class.getCanonicalName(), name());
        }

        @Override
        public String value(Object[] parameters) {
            return BundleResolver.resolve(AppHelpImpl.class.getCanonicalName(), name(), parameters);
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
        OutputStream out = null;
        try {
            File file = new File(DIR + context.getBundleContext().getProperty(AppProperty.HELP_MAP.code()));
            if (file.canWrite()) {
                out = new FileOutputStream(file);
                properties.store(out, AppHelp.class.getCanonicalName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, HelpBundle.SAVE.value(), e);
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
                    LOGGER.log(Level.WARNING, HelpBundle.SHOW_PAGE_ERR.value(), e);
                }
            }
        }
        LOGGER.log(Level.WARNING, HelpBundle.SHOW_PAGE_WARNING.value(new Object[]{DIR, name}));
    }

}
