package sk.r3n.sql;

public class TableSelect extends Table {

    private Query query;

    public TableSelect(Query query, String name) {
        super(name, null);
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
