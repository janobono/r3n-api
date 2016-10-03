package sk.r3n.example.postgres;

import java.io.Serializable;
import sk.r3n.sql.Table;

public class TABLE implements Serializable {

    public static Table T_JOIN() {
        return new Table("T_JOIN", "T1");
    }

    public static Table T_JOIN(String alias) {
        return new Table("T_JOIN", alias);
    }

    public static Table T_BASE_TYPES() {
        return new Table("T_BASE_TYPES", "T2");
    }

    public static Table T_BASE_TYPES(String alias) {
        return new Table("T_BASE_TYPES", alias);
    }

}
