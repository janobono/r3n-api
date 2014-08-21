package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class H_PROPERTIES implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.H_PROPERTIES(), DataType.STRING);
    }
    public static Column VALUE() {
        return new Column("value", TABLE.H_PROPERTIES(), DataType.STRING);
    }

}
