package sk.r3n.ui;

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
