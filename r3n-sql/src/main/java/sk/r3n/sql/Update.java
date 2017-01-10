package sk.r3n.sql;

import java.io.Serializable;
import java.util.Arrays;

public class Update implements Serializable {

    protected CriteriaManager cm;

    protected Table table;

    protected Column[] columns;

    protected Object[] values;

    public Update(Table table) {
        cm = new CriteriaManager();
        this.table = table;
    }

    public Update SET(Column column, Object value) {
        if (this.columns == null) {
            this.columns = new Column[]{};
        }
        Column[] newColumns = Arrays.copyOf(this.columns, this.columns.length + 1);
        newColumns[newColumns.length - 1] = column;

        if (this.values == null) {
            this.values = new Object[]{};
        }
        Object[] newValues = Arrays.copyOf(this.values, this.values.length + 1);
        newValues[newValues.length - 1] = value;

        this.columns = newColumns;
        this.values = newValues;
        return this;
    }

    public Update SET(Column[] columns, Object[] values) {
        for (int i = 0; i < columns.length; i++) {
            SET(columns[i], values[i]);
        }
        return this;
    }

    public Update WHERE(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public Update WHERE(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public Update AND(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public Update AND(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public Update OR(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public Update OR(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public Update AND_NEXT() {
        cm.next(Operator.AND);
        return this;
    }

    public Update OR_NEXT() {
        cm.next(Operator.OR);
        return this;
    }

    public Update AND_IN() {
        cm.in(Operator.AND);
        return this;
    }

    public Update OR_IN() {
        cm.in(Operator.OR);
        return this;
    }

    public Update OUT() {
        cm.out();
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

    public CriteriaManager getCriteriaManager() {
        return cm;
    }
}
