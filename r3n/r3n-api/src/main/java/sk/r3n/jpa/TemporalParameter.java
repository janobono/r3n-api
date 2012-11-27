package sk.r3n.jpa;

import java.util.Calendar;
import javax.persistence.TemporalType;

class TemporalParameter {
    private final Calendar value;
    private final TemporalType temporalType;
    
    public TemporalParameter(Calendar value, TemporalType temporalType) {
        this.value = value;
        this.temporalType = temporalType;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public Calendar getValue() {
        return value;
    }
}
