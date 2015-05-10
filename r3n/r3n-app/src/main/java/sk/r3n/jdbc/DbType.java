package sk.r3n.jdbc;

import java.util.ResourceBundle;

public enum DbType {

    POSTGRE("org.postgresql.Driver"),
    SQL_SERVER("net.sourceforge.jtds.jdbc.Driver"),
    H2("org.h2.Driver");

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
