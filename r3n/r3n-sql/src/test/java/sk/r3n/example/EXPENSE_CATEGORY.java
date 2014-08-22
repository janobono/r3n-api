package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class EXPENSE_CATEGORY implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.EXPENSE_CATEGORY(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.EXPENSE_CATEGORY(alias), DataType.LONG);
    }

    public static Column NAME() {
        return new Column("name", TABLE.EXPENSE_CATEGORY(), DataType.STRING);
    }

    public static Column NAME(String alias) {
        return new Column("name", TABLE.EXPENSE_CATEGORY(alias), DataType.STRING);
    }

    public static Column NAME_SCDF() {
        return new Column("name_scdf", TABLE.EXPENSE_CATEGORY(), DataType.STRING);
    }

    public static Column NAME_SCDF(String alias) {
        return new Column("name_scdf", TABLE.EXPENSE_CATEGORY(alias), DataType.STRING);
    }

    public static Column NOTE() {
        return new Column("note", TABLE.EXPENSE_CATEGORY(), DataType.STRING);
    }

    public static Column NOTE(String alias) {
        return new Column("note", TABLE.EXPENSE_CATEGORY(alias), DataType.STRING);
    }

    public static Column[] columns() {
        return new Column[]{ID(), NAME(), NAME_SCDF(), NOTE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), NAME(alias), NAME_SCDF(alias), NOTE(alias)};
    }
}
