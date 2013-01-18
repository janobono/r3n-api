package sk.r3n.db.test;

import java.util.Properties;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import sk.r3n.app.AppProperties;
import sk.r3n.app.AppProperty;
import sk.r3n.util.Encrypter;

public class TestAppProperties implements AppProperties {

    private Properties properties;

    public TestAppProperties() {
        super();
        properties = new Properties();
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
        return properties.getProperty(appProperty.code());
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
    }

}
