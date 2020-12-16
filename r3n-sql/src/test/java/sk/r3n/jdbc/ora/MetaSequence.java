package sk.r3n.jdbc.ora;

import sk.r3n.sql.Sequence;

public enum MetaSequence {

    TEST_SEQUENCE("test_sequence");

    private final String sequenceName;

    MetaSequence(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Sequence sequence() {
        return new Sequence(sequenceName);
    }
}
