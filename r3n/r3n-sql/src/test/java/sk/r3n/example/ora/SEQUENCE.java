package sk.r3n.example.ora;

import java.io.Serializable;
import sk.r3n.sql.Sequence;

public class SEQUENCE implements Serializable {

    public static Sequence TEST_SEQUENCE() {
        return new Sequence("TEST_SEQUENCE");
    }

}
