package sk.r3n.app.impl;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.app.AppCore;
import sk.r3n.app.AppHelp;
import sk.r3n.app.AppProperties;
import sk.r3n.app.AppProperty;

public class AppHelpImpl implements AppHelp {

    private enum HelpBundle {

        LOAD,
        SAVE,
        SHOW_PAGE_ERR;

        public String value() {
            return ResourceBundle.getBundle(AppHelpImpl.class.getCanonicalName()).getString(name());
        }

        public String value(Object[] parameters) {
            return MessageFormat.format(value(), parameters);
        }
    }

    private static final Log LOG = LogFactory.getLog(AppHelp.class.getCanonicalName());

    private String DIR;

    private Properties properties;

    public void start() {
        properties = new Properties();
        if (AppCore.getModule(AppProperties.class.getCanonicalName()) != null) {
            DIR = ((AppProperties) AppCore.getModule(AppProperties.class.getCanonicalName())).get(AppProperty.HELP_DIR);
        } else {
            DIR = AppCore.getProperty(AppProperty.HELP_DIR.code());
        }
        InputStream in = null;
        try {
            String mapFile;
            if (AppCore.getModule(AppProperties.class.getCanonicalName()) != null) {
                mapFile = ((AppProperties) AppCore.getModule(AppProperties.class.getCanonicalName())).get(AppProperty.HELP_MAP);
            } else {
                mapFile = AppCore.getProperty(AppProperty.HELP_MAP.code());
            }
            in = new FileInputStream(DIR + mapFile);
            properties.load(in);
        } catch (IOException e) {
            LOG.error(HelpBundle.LOAD.value(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public void stop() {
    }

    @Override
    public void showHelp() {
        showPage("index.html");
    }

    @Override
    public void showHelp(String key) {
        if (properties.getProperty(key, "").equals("")) {
            showHelp();
        } else {
            showPage(properties.getProperty(key));
        }
    }

    protected void showPage(String name) {
        try {
            URL url = new File(DIR + name).toURI().toURL();
            new ProcessBuilder("x-www-browser", url.toString()).start();
        } catch (Exception e) {
            LOG.error(HelpBundle.SHOW_PAGE_ERR.value(), e);
        }
    }

}
