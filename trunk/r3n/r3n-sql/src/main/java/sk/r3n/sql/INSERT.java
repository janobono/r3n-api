package sk.r3n.sql;

import java.io.Serializable;

public class INSERT implements Serializable {

    private Table table;

    private Column[] columns;

    private Object[] values;

    private Column returning;

    public INSERT() {
        super();
    }

    public INSERT INTO(Table table) {
        this.table = table;
        return this;
    }

    public INSERT COLUMNS(Column... columns) {
        this.columns = columns;
        return this;
    }

    public INSERT VALUES(Object... values) {
        this.values = values;
        return this;
    }

    public INSERT RETURNING(Column returning) {
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
