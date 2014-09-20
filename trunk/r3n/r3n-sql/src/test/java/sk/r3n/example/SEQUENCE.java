package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Sequence;

public class SEQUENCE implements Serializable {

    public static Sequence H_SEQUENCE() {
        return new Sequence("H_SEQUENCE");
    }

}
