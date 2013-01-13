package sk.r3n.db;

import sk.r3n.ui.UIActionKey;
import sk.r3n.util.BundleResolver;

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
        return BundleResolver.resolve(BUNDLE, name());
    }

}
