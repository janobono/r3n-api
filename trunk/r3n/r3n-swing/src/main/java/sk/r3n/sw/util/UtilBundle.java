package sk.r3n.sw.util;

import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public enum UtilBundle implements BundleEnum {

    OPEN,
    SAVE;

    private static final String BUNDLE = UtilBundle.class.getCanonicalName();

    @Override
    public String value() {
        return BundleResolver.resolve(BUNDLE, name());
    }

    @Override
    public String value(Object[] parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
