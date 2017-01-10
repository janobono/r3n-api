package sk.r3n.jdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryResult implements Serializable {

    protected String query;

    protected List<SqlParam> params;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<SqlParam> getParams() {
        if (params == null) {
            params = new ArrayList<>();
        }
        return params;
    }

    public void setParams(List<SqlParam> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "QueryResult{" + "query=" + query + ", params=" + params + '}';
    }

}
