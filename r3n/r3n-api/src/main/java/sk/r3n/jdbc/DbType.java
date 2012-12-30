package sk.r3n.jdbc;

import java.util.EnumSet;
import sk.r3n.util.BundleResolver;

public enum DbType {

    POSTGRE(org.postgresql.Driver.class.getCanonicalName()),
    SQL_SERVER(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName()),
    H2(org.h2.Driver.class.getCanonicalName());

    private final String driver;

    public static DbType get(String driver) {
        DbType result = null;
        for (DbType type : EnumSet.allOf(DbType.class)) {
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
        return BundleResolver.resolve(DbType.class.getCanonicalName(), name());
    }

}
