package sk.r3n.db;

import java.util.ResourceBundle;

public enum DbType {

    POSTGRE(org.postgresql.Driver.class.getCanonicalName()),
    H2(org.h2.Driver.class.getCanonicalName());

    private final String driver;

    public static DbType get(String driver) {
        DbType result = null;
        for (DbType type : DbType.values()) {
            if (type.driver().equals(driver)) {
                result = type;
                break;
            }
        }
        return result;
    }

    DbType(String driver) {
        this.driver = driver;
    }

    public String driver() {
        return driver;
    }

    public String value() {
        return ResourceBundle.getBundle(DbType.class.getCanonicalName()).getString(name());
    }
}
