package sk.r3n.jdbc;

public enum ConnectionParameter {

    DRIVER,
    HOST,
    PORT,
    NAME,
    USER,
    PASSWORD;

    public String key() {
        return name().toLowerCase();
    }

}
