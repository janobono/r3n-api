package sk.r3n.example.postgres;

import java.io.Serializable;
import sk.r3n.sql.Sequence;

public class SEQUENCE implements Serializable {

    public static Sequence TEST_SEQUENCE() {
        return new Sequence("test_sequence");
    }

}