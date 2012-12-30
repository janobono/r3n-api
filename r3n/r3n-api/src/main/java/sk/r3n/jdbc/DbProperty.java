package sk.r3n.jdbc;

public enum DbProperty {

    DRIVER,
    HOST,
    PORT,
    NAME,
    USER,
    PASSWORD;

    public String code() {
        return name().toLowerCase();
    }

}
