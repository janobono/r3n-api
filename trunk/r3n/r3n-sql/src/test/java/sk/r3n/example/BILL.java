package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class BILL implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.BILL(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.BILL(alias), DataType.LONG);
    }

    public static Column NOTE() {
        return new Column("note", TABLE.BILL(), DataType.STRING);
    }

    public static Column NOTE(String alias) {
        return new Column("note", TABLE.BILL(alias), DataType.STRING);
    }

    public static Column NOTE_SCDF() {
        return new Column("note_scdf", TABLE.BILL(), DataType.STRING);
    }

    public static Column NOTE_SCDF(String alias) {
        return new Column("note_scdf", TABLE.BILL(alias), DataType.STRING);
    }

    public static Column BILL_DATE() {
        return new Column("bill_date", TABLE.BILL(), DataType.DATE);
    }

    public static Column BILL_DATE(String alias) {
        return new Column("bill_date", TABLE.BILL(alias), DataType.DATE);
    }

    public static Column BILL_TIME() {
        return new Column("bill_time", TABLE.BILL(), DataType.TIME);
    }

    public static Column BILL_TIME(String alias) {
        return new Column("bill_time", TABLE.BILL(alias), DataType.TIME);
    }

    public static Column[] columns() {
        return new Column[]{ID(), NOTE(), NOTE_SCDF(), BILL_DATE(), BILL_TIME()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), NOTE(alias), NOTE_SCDF(alias), BILL_DATE(alias), BILL_TIME(alias)};
    }
}
