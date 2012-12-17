package sk.r3n.sw.util;

import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public enum MessageType implements BundleEnum {

    INFO,
    WARNING,
    ERROR,
    QUESTION;

    private final String BUNDLE = MessageType.class.getCanonicalName();

    @Override
    public String value() {
        return BundleResolver.resolve(BUNDLE, name());
    }

    @Override
    public String value(Object[] parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
