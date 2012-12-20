package sk.r3n.app.impl;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.osgi.service.component.ComponentContext;
import sk.r3n.app.AppProperties;
import sk.r3n.app.AppProperty;
import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;
import sk.r3n.util.Encrypter;

public class AppPropertiesImpl implements AppProperties {

    private enum PropertiesBundle implements BundleEnum {

        LOAD,
        SAVE,
        SET;

        @Override
        public String value() {
            return BundleResolver.resolve(AppPropertiesImpl.class.getCanonicalName(), name());
        }

        @Override
        public String value(Object[] parameters) {
            return BundleResolver.resolve(AppPropertiesImpl.class.getCanonicalName(), name(), parameters);
        }

    }
    private static final Logger LOGGER = Logger.getLogger(AppProperties.class.getCanonicalName());

    private Properties properties;

    private ComponentContext context;

    protected void activate(ComponentContext context) {
        this.context = context;
        properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(System.getProperty("user.home")
                    + File.separatorChar + get(AppProperty.APP_DATA_DIR)
                    + File.separatorChar + get(AppProperty.PROPERTIES_FILE));
            properties.load(in);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, PropertiesBundle.LOAD.value(), e);
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
                    + File.separatorChar + get(AppProperty.APP_DATA_DIR)
                    + File.separatorChar + get(AppProperty.PROPERTIES_FILE));
            if (file.canWrite()) {
                out = new FileOutputStream(file);
                properties.store(out, AppProperties.class.getCanonicalName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, PropertiesBundle.SAVE.value(), e);
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
    public String get(AppProperty appProperty) {
        return context.getBundleContext().getProperty(appProperty.code());
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
        LOGGER.log(Level.INFO, PropertiesBundle.SET.value(new Object[]{key, value}));
    }

}
