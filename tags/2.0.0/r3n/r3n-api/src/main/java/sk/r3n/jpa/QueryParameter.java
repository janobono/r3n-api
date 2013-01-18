package sk.r3n.jpa;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.TemporalType;

public class QueryParameter {

    private Map parameters = null;

    private QueryParameter(String name, Object value) {
        this.parameters = new HashMap();
        this.parameters.put(name, value);
    }

    public static QueryParameter with(String name, Object value) {
        return new QueryParameter(name, value);
    }

    public QueryParameter and(String name, Object value) {
        this.parameters.put(name, value);
        return this;
    }

    public QueryParameter and(String name, Calendar value, TemporalType temporalType) {
        this.parameters.put(name, new TemporalParameter(value, temporalType));
        return this;
    }

    public Map parameters() {
        return this.parameters;
    }

}
