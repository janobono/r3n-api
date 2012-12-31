package sk.r3n.db;

import sk.r3n.util.BundleResolver;
import sk.r3n.util.R3NException;

public enum DbManagerException {

    UNKNOWN(5),
    CANCELLED(10),
    CHECK_STRUCTURE_ERR(20),
    CREATE_DB_ERR(30),
    CREATE_USER_ERR(40);

    private static final String BUNDLE = DbManagerException.class.getCanonicalName();

    private final int code;

    DbManagerException(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public String message() {
        return BundleResolver.resolve(BUNDLE, name());
    }

    public void raise() throws R3NException {
        throw new R3NException(message(), code());
    }

    public void raise(Throwable throwable) throws R3NException {
        throw new R3NException(message(), code(), throwable);
    }

}
