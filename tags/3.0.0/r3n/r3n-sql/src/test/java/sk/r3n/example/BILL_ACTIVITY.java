package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class BILL_ACTIVITY implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.BILL_ACTIVITY(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.BILL_ACTIVITY(alias), DataType.LONG);
    }

    public static Column BILL_FK() {
        return new Column("bill_fk", TABLE.BILL_ACTIVITY(), DataType.LONG);
    }

    public static Column BILL_FK(String alias) {
        return new Column("bill_fk", TABLE.BILL_ACTIVITY(alias), DataType.LONG);
    }

    public static Column ACCOUNT_ACTIVITY_FK() {
        return new Column("account_activity_fk", TABLE.BILL_ACTIVITY(), DataType.LONG);
    }

    public static Column ACCOUNT_ACTIVITY_FK(String alias) {
        return new Column("account_activity_fk", TABLE.BILL_ACTIVITY(alias), DataType.LONG);
    }

    public static Column[] columns() {
        return new Column[]{ID(), BILL_FK(), ACCOUNT_ACTIVITY_FK()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), BILL_FK(alias), ACCOUNT_ACTIVITY_FK(alias)};
    }
}