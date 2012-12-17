package sk.r3n.jdbc;

import java.util.EnumSet;
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
        EnumSet<DbStatus> allStatuses = EnumSet.allOf(DbStatus.class);
        for (DbStatus dbStatus : allStatuses) {
            if (dbStatus.code() == code) {
                return dbStatus;
            }
        }
        return null;
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
