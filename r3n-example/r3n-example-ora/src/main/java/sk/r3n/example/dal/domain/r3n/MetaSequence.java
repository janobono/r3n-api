package sk.r3n.example.dal.domain.r3n;

import sk.r3n.sql.Sequence;

public enum MetaSequence {

    SQ_HOTEL("sq_hotel");

    private final String sequenceName;

    MetaSequence(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Sequence sequence() {
        return new Sequence(sequenceName);
    }
}
