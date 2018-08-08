package sk.r3n.sw;

import java.util.ResourceBundle;

public enum MessageType {

    INFO, WARNING, ERROR, QUESTION;

    public String value() {
        return ResourceBundle.getBundle(MessageType.class.getCanonicalName()).getString(name());
    }
}
