package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class BILL_ITEM implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.BILL_ITEM(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.BILL_ITEM(alias), DataType.LONG);
    }

    public static Column BILL_FK() {
        return new Column("bill_fk", TABLE.BILL_ITEM(), DataType.LONG);
    }

    public static Column BILL_FK(String alias) {
        return new Column("bill_fk", TABLE.BILL_ITEM(alias), DataType.LONG);
    }

    public static Column NOTE() {
        return new Column("note", TABLE.BILL_ITEM(), DataType.STRING);
    }

    public static Column NOTE(String alias) {
        return new Column("note", TABLE.BILL_ITEM(alias), DataType.STRING);
    }

    public static Column VALUE() {
        return new Column("value", TABLE.BILL_ITEM(), DataType.BIG_DECIMAL);
    }

    public static Column VALUE(String alias) {
        return new Column("value", TABLE.BILL_ITEM(alias), DataType.BIG_DECIMAL);
    }

    public static Column[] columns() {
        return new Column[]{ID(), BILL_FK(), NOTE(), VALUE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), BILL_FK(alias), NOTE(alias), VALUE(alias)};
    }
}
