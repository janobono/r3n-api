package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class ACCOUNT_ACTIVITY implements Serializable {

    public static Column ID() {
        return new Column("ID", TABLE.ACCOUNT_ACTIVITY(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("ID", TABLE.ACCOUNT_ACTIVITY(alias), DataType.LONG);
    }

    public static Column ACCOUNT_FK() {
        return new Column("ACCOUNT_FK", TABLE.ACCOUNT_ACTIVITY(), DataType.LONG);
    }

    public static Column ACCOUNT_FK(String alias) {
        return new Column("ACCOUNT_FK", TABLE.ACCOUNT_ACTIVITY(alias), DataType.LONG);
    }

    public static Column TYPE() {
        return new Column("TYPE", TABLE.ACCOUNT_ACTIVITY(), DataType.SHORT);
    }

    public static Column TYPE(String alias) {
        return new Column("TYPE", TABLE.ACCOUNT_ACTIVITY(alias), DataType.SHORT);
    }

    public static Column SUB_TYPE() {
        return new Column("SUB_TYPE", TABLE.ACCOUNT_ACTIVITY(), DataType.SHORT);
    }

    public static Column SUB_TYPE(String alias) {
        return new Column("SUB_TYPE", TABLE.ACCOUNT_ACTIVITY(alias), DataType.SHORT);
    }

    public static Column ACTIVITY_DATE() {
        return new Column("ACTIVITY_DATE", TABLE.ACCOUNT_ACTIVITY(), DataType.DATE);
    }

    public static Column ACTIVITY_DATE(String alias) {
        return new Column("ACTIVITY_DATE", TABLE.ACCOUNT_ACTIVITY(alias), DataType.DATE);
    }

    public static Column ACTIVITY_TIME() {
        return new Column("ACTIVITY_TIME", TABLE.ACCOUNT_ACTIVITY(), DataType.TIME);
    }

    public static Column ACTIVITY_TIME(String alias) {
        return new Column("ACTIVITY_TIME", TABLE.ACCOUNT_ACTIVITY(alias), DataType.TIME);
    }

    public static Column VALUE() {
        return new Column("VALUE", TABLE.ACCOUNT_ACTIVITY(), DataType.BIG_DECIMAL);
    }

    public static Column VALUE(String alias) {
        return new Column("VALUE", TABLE.ACCOUNT_ACTIVITY(alias), DataType.BIG_DECIMAL);
    }

    public static Column NOTE() {
        return new Column("NOTE", TABLE.ACCOUNT_ACTIVITY(), DataType.STRING);
    }

    public static Column NOTE(String alias) {
        return new Column("NOTE", TABLE.ACCOUNT_ACTIVITY(alias), DataType.STRING);
    }

    public static Column[] columns() {
        return new Column[]{ID(), ACCOUNT_FK(), TYPE(), SUB_TYPE(), ACTIVITY_DATE(), ACTIVITY_TIME(), VALUE(), NOTE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), ACCOUNT_FK(alias), TYPE(alias), SUB_TYPE(alias), ACTIVITY_DATE(alias), ACTIVITY_TIME(alias), VALUE(alias), NOTE(alias)};
    }
}
