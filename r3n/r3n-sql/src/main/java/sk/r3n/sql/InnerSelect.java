package sk.r3n.sql;

/**
 *
 * @author jan
 */
public class InnerSelect extends Column {

    private Query query;

    public InnerSelect(Query query, String name, DataType dataType) {
        super(name, null, dataType);
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public String nameWithAlias() {
        return getName();
    }

}
