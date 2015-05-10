package sk.r3n.db;

import java.util.ResourceBundle;
import sk.r3n.app.AppException;

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
        return ResourceBundle.getBundle(BUNDLE).getString(name());
    }

    public void raise() throws AppException {
        throw new AppException(message(), code());
    }

    public void raise(Throwable throwable) throws AppException {
        throw new AppException(message(), code(), throwable);
    }
}
