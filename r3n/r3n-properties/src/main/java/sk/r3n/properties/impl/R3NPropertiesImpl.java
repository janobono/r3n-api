package sk.r3n.properties.impl;

import java.io.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.osgi.service.component.ComponentContext;
import sk.r3n.app.AppLoader;
import sk.r3n.properties.R3NProperties;
import sk.r3n.util.Encrypter;

public class R3NPropertiesImpl implements R3NProperties {

    private static final Logger LOGGER = Logger.getLogger(R3NProperties.class.getCanonicalName());
    private Properties properties;

    protected void activate(ComponentContext context) {
        properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(System.getProperty("user.home")
                    + File.separatorChar
                    + context.getBundleContext().getProperty(
                    AppLoader.APP_DATA_DIR) + File.separatorChar
                    + context.getBundleContext().getProperty(FILE_NAME));
            properties.load(in);
        } catch (Exception e) {
            LOGGER.log(
                    Level.WARNING,
                    ResourceBundle.getBundle(
                    R3NPropertiesImpl.class.getCanonicalName()).getString("LOAD"), e);
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
            File file = new File(System.getProperty("user.home")
                    + File.separatorChar
                    + context.getBundleContext().getProperty(
                    AppLoader.APP_DATA_DIR) + File.separatorChar
                    + context.getBundleContext().getProperty(FILE_NAME));
            if (file.canWrite()) {
                out = new FileOutputStream(file);
                properties.store(out, R3NProperties.class.getCanonicalName());
            }
        } catch (Exception e) {
            LOGGER.log(
                    Level.WARNING,
                    ResourceBundle.getBundle(
                    R3NPropertiesImpl.class.getCanonicalName()).getString("SAVE"), e);
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
    public String decrypt(String string) {
        if (string.equals("")) {
            return string;
        }
        try {
            Encrypter encrypter = new Encrypter();
            return new String(encrypter.decrypt(new HexBinaryAdapter().unmarshal(string)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encrypt(String string) {
        if (string.equals("")) {
            return string;
        }
        try {
            Encrypter encrypter = new Encrypter();
            return new HexBinaryAdapter().marshal(encrypter.encrypt(string.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String key, String defaultValue) {
        if (!properties.containsKey(key)) {
            properties.setProperty(key, defaultValue);
        }
        return properties.getProperty(key);
    }

    public void print() {
        try {
            System.out.println(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(String key, String value) {
        if (value != null) {
            properties.setProperty(key, value);
        } else {
            properties.remove(key);
        }
        LOGGER.log(
                Level.INFO,
                ResourceBundle.getBundle(
                R3NPropertiesImpl.class.getCanonicalName()).getString(
                "SET"), new Object[]{key, value});
    }
}
