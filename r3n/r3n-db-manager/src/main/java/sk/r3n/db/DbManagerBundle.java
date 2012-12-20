package sk.r3n.db;

import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public enum DbManagerBundle implements BundleEnum {

    ;

    @Override
    public String value() {
        return BundleResolver.resolve(DbManagerBundle.class.getCanonicalName(), name());
    }

    @Override
    public String value(Object[] parameters) {
        return BundleResolver.resolve(DbManagerBundle.class.getCanonicalName(), name(), parameters);
    }

}
