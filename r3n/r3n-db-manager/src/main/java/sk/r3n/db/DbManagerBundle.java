package sk.r3n.db;

import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public enum DbManagerBundle implements BundleEnum {

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
    SET_PROP_QUESTION;

    @Override
    public String value() {
        return BundleResolver.resolve(DbManagerBundle.class.getCanonicalName(), name());
    }

    @Override
    public String value(Object[] parameters) {
        return BundleResolver.resolve(DbManagerBundle.class.getCanonicalName(), name(), parameters);
    }

}
