package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class H_PROPERTIES implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.H_PROPERTIES(), DataType.STRING);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.H_PROPERTIES(alias), DataType.STRING);
    }

    public static Column VALUE() {
        return new Column("value", TABLE.H_PROPERTIES(), DataType.STRING);
    }

    public static Column VALUE(String alias) {
        return new Column("value", TABLE.H_PROPERTIES(alias), DataType.STRING);
    }

    public static Column[] columns() {
        return new Column[]{ID(), VALUE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), VALUE(alias)};
    }
}
