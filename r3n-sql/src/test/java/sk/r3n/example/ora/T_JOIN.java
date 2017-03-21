package sk.r3n.example.ora;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class T_JOIN implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.T_JOIN(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.T_JOIN(alias), DataType.LONG);
    }

    public static Column T_BASE_TYPES_FK() {
        return new Column("t_base_types_fk", TABLE.T_JOIN(), DataType.LONG);
    }

    public static Column T_BASE_TYPES_FK(String alias) {
        return new Column("t_base_types_fk", TABLE.T_JOIN(alias), DataType.LONG);
    }

    public static Column T_JOIN_STRING() {
        return new Column("t_join_string", TABLE.T_JOIN(), DataType.STRING);
    }

    public static Column T_JOIN_STRING(String alias) {
        return new Column("t_join_string", TABLE.T_JOIN(alias), DataType.STRING);
    }

    public static Column[] columns() {
        return new Column[]{ID(), T_BASE_TYPES_FK(), T_JOIN_STRING()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), T_BASE_TYPES_FK(alias), T_JOIN_STRING(alias)};
    }
}