package sk.r3n.db;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public enum DbManagerBundle {

    TITLE,
    BASE,
    DRIVER,
    HOST,
    PORT,
    ADMIN,
    NAME,
    USER,
    PASSWORD,
    MESSAGE_TITLE,
    UNSUPPORTED,
    NOT_RUN_QUESTION,
    AGAIN_QUESTION;

    public String value() {
        return ResourceBundle.getBundle(DbManagerBundle.class.getCanonicalName()).getString(name());
    }

    public String value(Object[] parameters) {
        return MessageFormat.format(value(), parameters);
    }
}
