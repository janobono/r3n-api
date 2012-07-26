package sk.r3n.db;

public interface Condition {

    public short UNKNOWN = 0;
    public short EQUALS = 10;
    public short EQUALS_MORE = 11;
    public short EQUALS_LESS = 12;
    public short EQUALS_NOT = 13;
    public short MORE = 20;
    public short LESS = 30;
    public short LIKE = 40;
    public short IS_NULL = 50;
    public short IS_NOT_NULL = 51;
    public short IN = 60;
    public short NOT_IN = 61;
}
