package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class BILL implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.BILL(), DataType.LONG);
    }
    public static Column NOTE() {
        return new Column("note", TABLE.BILL(), DataType.STRING);
    }
    public static Column NOTE_SCDF() {
        return new Column("note_scdf", TABLE.BILL(), DataType.STRING);
    }
    public static Column BILL_DATE() {
        return new Column("bill_date", TABLE.BILL(), DataType.DATE);
    }
    public static Column BILL_TIME() {
        return new Column("bill_time", TABLE.BILL(), DataType.TIME);
    }

}
