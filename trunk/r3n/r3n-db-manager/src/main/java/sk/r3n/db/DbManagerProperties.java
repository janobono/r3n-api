package sk.r3n.db;

import sk.r3n.jdbc.DbProperty;

public enum DbManagerProperties {

    DRIVER(DbProperty.DRIVER.code()),
    HOST(DbProperty.HOST.code()),
    PORT(DbProperty.PORT.code()),
    NAME(DbProperty.NAME.code()),
    USER(DbProperty.USER.code()),
    PASSWORD(DbProperty.PASSWORD.code()),
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
