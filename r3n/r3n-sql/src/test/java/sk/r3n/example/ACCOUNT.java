package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class ACCOUNT implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.ACCOUNT(), DataType.LONG);
    }
    public static Column TYPE() {
        return new Column("type", TABLE.ACCOUNT(), DataType.SHORT);
    }
    public static Column NAME() {
        return new Column("name", TABLE.ACCOUNT(), DataType.STRING);
    }
    public static Column NAME_SCDF() {
        return new Column("name_scdf", TABLE.ACCOUNT(), DataType.STRING);
    }
    public static Column NOTE() {
        return new Column("note", TABLE.ACCOUNT(), DataType.STRING);
    }

}
