package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class ACCOUNT_ACTIVITY implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.ACCOUNT_ACTIVITY(), DataType.LONG);
    }
    public static Column ACCOUNT_FK() {
        return new Column("account_fk", TABLE.ACCOUNT_ACTIVITY(), DataType.LONG);
    }
    public static Column TYPE() {
        return new Column("type", TABLE.ACCOUNT_ACTIVITY(), DataType.SHORT);
    }
    public static Column SUB_TYPE() {
        return new Column("sub_type", TABLE.ACCOUNT_ACTIVITY(), DataType.SHORT);
    }
    public static Column ACTIVITY_DATE() {
        return new Column("activity_date", TABLE.ACCOUNT_ACTIVITY(), DataType.DATE);
    }
    public static Column ACTIVITY_TIME() {
        return new Column("activity_time", TABLE.ACCOUNT_ACTIVITY(), DataType.TIME);
    }
    public static Column VALUE() {
        return new Column("value", TABLE.ACCOUNT_ACTIVITY(), DataType.BIG_DECIMAL);
    }
    public static Column NOTE() {
        return new Column("note", TABLE.ACCOUNT_ACTIVITY(), DataType.STRING);
    }

}
