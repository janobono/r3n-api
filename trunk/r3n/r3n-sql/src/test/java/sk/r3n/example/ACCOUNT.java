package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class ACCOUNT implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.ACCOUNT(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.ACCOUNT(alias), DataType.LONG);
    }

    public static Column TYPE() {
        return new Column("type", TABLE.ACCOUNT(), DataType.SHORT);
    }

    public static Column TYPE(String alias) {
        return new Column("type", TABLE.ACCOUNT(alias), DataType.SHORT);
    }

    public static Column NAME() {
        return new Column("name", TABLE.ACCOUNT(), DataType.STRING);
    }

    public static Column NAME(String alias) {
        return new Column("name", TABLE.ACCOUNT(alias), DataType.STRING);
    }

    public static Column NAME_SCDF() {
        return new Column("name_scdf", TABLE.ACCOUNT(), DataType.STRING);
    }

    public static Column NAME_SCDF(String alias) {
        return new Column("name_scdf", TABLE.ACCOUNT(alias), DataType.STRING);
    }

    public static Column NOTE() {
        return new Column("note", TABLE.ACCOUNT(), DataType.STRING);
    }

    public static Column NOTE(String alias) {
        return new Column("note", TABLE.ACCOUNT(alias), DataType.STRING);
    }

    public static Column[] columns() {
        return new Column[]{ID(), TYPE(), NAME(), NAME_SCDF(), NOTE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), TYPE(alias), NAME(alias), NAME_SCDF(alias), NOTE(alias)};
    }
}
