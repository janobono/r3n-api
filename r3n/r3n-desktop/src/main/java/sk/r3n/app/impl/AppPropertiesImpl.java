package sk.r3n.app.impl;

import java.io.*;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.app.AppCore;
import sk.r3n.app.AppProperties;
import sk.r3n.app.AppProperty;
import sk.r3n.util.Encrypter;

public class AppPropertiesImpl implements AppProperties {

    private enum PropertiesBundle {

        LOAD,
        SAVE,
        SET;

        public String value() {
            return ResourceBundle.getBundle(AppPropertiesImpl.class.getCanonicalName()).getString(name());
        }

        public String value(Object[] parameters) {
            return MessageFormat.format(value(), parameters);
        }
    }

    private static final Log LOG = LogFactory.getLog(AppProperties.class.getCanonicalName());

    private Properties properties;

    public void start() {
        properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(System.getProperty("user.home")
                    + File.separatorChar + get(AppProperty.APP_DATA_DIR)
                    + File.separatorChar + get(AppProperty.PROPERTIES_FILE));
            properties.load(in);
        } catch (IOException e) {
            LOG.warn(PropertiesBundle.LOAD.value(), e);
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
        OutputStream out = null;
        try {
            File file = new File(System.getProperty("user.home")
                    + File.separatorChar + get(AppProperty.APP_DATA_DIR)
                    + File.separatorChar + get(AppProperty.PROPERTIES_FILE));
            out = new FileOutputStream(file);
            properties.store(out, AppProperties.class.getCanonicalName());
        } catch (IOException e) {
            LOG.warn(PropertiesBundle.SAVE.value(), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
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
            String result = new HexBinaryAdapter().marshal(encrypter.encrypt(string.getBytes()));
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(AppProperty appProperty) {
        return AppCore.getProperty(appProperty.code());
    }

    @Override
    public String get(String key, String defaultValue) {
        if (!properties.containsKey(key)) {
            properties.setProperty(key, defaultValue);
        }
        return properties.getProperty(key);
    }

    public void print() {
        System.out.println(properties);
    }

    @Override
    public void set(String key, String value) {
        if (value != null) {
            properties.setProperty(key, value);
        } else {
            properties.remove(key);
        }
        LOG.warn(PropertiesBundle.SET.value(new Object[]{key, value}));
    }
}
