package sk.r3n.db;

import java.util.ResourceBundle;

public enum DbStatus {

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

    public String value() {
        return ResourceBundle.getBundle(DbStatus.class.getCanonicalName()).getString(name());
    }
}
