package sk.r3n.db;

import sk.r3n.ui.UIActionKey;
import sk.r3n.util.BundleResolver;

public enum DbManagerAction implements UIActionKey {

    TEST(DbManagerAction.class.getCanonicalName(), 10);
    
    private static final String BUNDLE = DbManagerAction.class.getCanonicalName();

    private final String group;

    private final int code;

    private DbManagerAction(String group, int code) {
        this.group = group;
        this.code = code;
    }

    @Override
    public String group() {
        return group;
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
