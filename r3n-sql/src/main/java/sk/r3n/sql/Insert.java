package sk.r3n.sql;

import java.io.Serializable;

public class Insert implements Serializable {

    protected Table table;

    protected Column[] columns;

    protected Object[] values;

    protected Column returning;

    public Insert INTO(Table table, Column... columns) {
        this.table = table;
        this.columns = columns;
        return this;
    }

    public Insert VALUES(Object... values) {
        this.values = values;
        return this;
    }

    public Insert RETURNING(Column returning) {
        this.returning = returning;
        return this;
    }

    public Table getTable() {
        return table;
    }

    public Column[] getColumns() {
        return columns;
    }

    public Object[] getValues() {
        return values;
    }

    public Column getReturning() {
        return returning;
    }

}
