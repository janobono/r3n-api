package sk.r3n.db;

import java.util.ResourceBundle;
import sk.r3n.sw.util.UIActionKey;

public enum DbManagerAction implements UIActionKey {

    TEST(10);

    private static final String BUNDLE = DbManagerAction.class.getCanonicalName();

    private final int code;

    private DbManagerAction(int code) {
        this.code = code;
    }

    @Override
    public String group() {
        return DbManagerAction.class.getCanonicalName();
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String actionName() {
        return ResourceBundle.getBundle(BUNDLE).getString(name());
    }
}
