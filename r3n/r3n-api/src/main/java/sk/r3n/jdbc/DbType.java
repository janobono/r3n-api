package sk.r3n.jdbc;

public enum DbType {

    POSTGRE(org.postgresql.Driver.class.getCanonicalName()),
    SQL_SERVER(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName()),
    H2(org.h2.Driver.class.getCanonicalName());

    private final String driver;

    DbType(String driver) {
        this.driver = driver;
    }

    public String driver() {
        return driver;
    }

}
