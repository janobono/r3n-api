package sk.r3n.jdbc;

import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public enum DbStatus implements BundleEnum {

    OK(0),
    UNKNOWN(10),
    NOT_INIT(20),
    SERVER_ERR(30),
    DB_ERR(40),
    AUTH_ERR(50);

    public static DbStatus byCode(int code) {
        DbStatus result = null;
        for (DbStatus dbStatus : DbStatus.values()) {
            if (dbStatus.code() == code) {
                result = dbStatus;
                break;
            }
        }
        return result;
    }

    private final int code;

    DbStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    @Override
    public String value() {
        return BundleResolver.resolve(DbStatus.class.getCanonicalName(), name());
    }

    @Override
    public String value(Object[] parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
