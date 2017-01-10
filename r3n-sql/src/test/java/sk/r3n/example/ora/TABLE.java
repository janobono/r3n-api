package sk.r3n.example.ora;

import java.io.Serializable;
import sk.r3n.sql.Table;

public class TABLE implements Serializable {

    public static Table T_BASE_TYPES() {
        return new Table("t_base_types", "t1");
    }

    public static Table T_BASE_TYPES(String alias) {
        return new Table("t_base_types", alias);
    }

    public static Table T_JOIN() {
        return new Table("t_join", "t2");
    }

    public static Table T_JOIN(String alias) {
        return new Table("t_join", alias);
    }

}
