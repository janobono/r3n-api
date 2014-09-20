package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class BILL_ITEM implements Serializable {

    public static Column ID() {
        return new Column("ID", TABLE.BILL_ITEM(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("ID", TABLE.BILL_ITEM(alias), DataType.LONG);
    }

    public static Column BILL_FK() {
        return new Column("BILL_FK", TABLE.BILL_ITEM(), DataType.LONG);
    }

    public static Column BILL_FK(String alias) {
        return new Column("BILL_FK", TABLE.BILL_ITEM(alias), DataType.LONG);
    }

    public static Column NOTE() {
        return new Column("NOTE", TABLE.BILL_ITEM(), DataType.STRING);
    }

    public static Column NOTE(String alias) {
        return new Column("NOTE", TABLE.BILL_ITEM(alias), DataType.STRING);
    }

    public static Column VALUE() {
        return new Column("VALUE", TABLE.BILL_ITEM(), DataType.BIG_DECIMAL);
    }

    public static Column VALUE(String alias) {
        return new Column("VALUE", TABLE.BILL_ITEM(alias), DataType.BIG_DECIMAL);
    }

    public static Column[] columns() {
        return new Column[]{ID(), BILL_FK(), NOTE(), VALUE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), BILL_FK(alias), NOTE(alias), VALUE(alias)};
    }
}
