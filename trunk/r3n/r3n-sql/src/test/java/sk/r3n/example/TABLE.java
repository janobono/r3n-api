package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;

public class TABLE implements Serializable {

    public static Table PERSON() {
        return new Table("PERSON", "T1");
    }

    public static Table PERSON(String alias) {
        return new Table("PERSON", alias);
    }

    public static Table ADDRESS() {
        return new Table("ADDRESS", "T2");
    }

    public static Table ADDRESS(String alias) {
        return new Table("ADDRESS", alias);
    }

}
