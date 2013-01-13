package sk.r3n.db;

import sk.r3n.jdbc.DbProperty;

public enum DbManagerProperties {

    DRIVER(DbProperty.DRIVER.name()),
    HOST(DbProperty.HOST.name()),
    PORT(DbProperty.PORT.name()),
    NAME(DbProperty.NAME.name()),
    USER(DbProperty.USER.name()),
    PASSWORD(DbProperty.PASSWORD.name()),
    ADMIN_NAME("1"),
    ADMIN_USER("2"),
    ADMIN_PASSWORD("3");

    private final String appCode;

    private final String connCode;

    DbManagerProperties(String connCode) {
        this.appCode = "sk.r3n.db." + connCode;
        this.connCode = connCode;
    }

    public String appCode() {
        return appCode;
    }

    public String connCode() {
        return connCode;
    }

}
