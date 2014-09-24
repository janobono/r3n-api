package sk.r3n.sw.util;

import java.util.ResourceBundle;

public enum MessageType {

    INFO,
    WARNING,
    ERROR,
    QUESTION;

    private final String BUNDLE = MessageType.class.getCanonicalName();

    public String value() {
        return ResourceBundle.getBundle(BUNDLE).getString(name());
    }
}
