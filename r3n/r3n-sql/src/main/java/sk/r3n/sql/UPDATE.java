package sk.r3n.sql;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UPDATE implements Serializable {

    private Table table;

    private final Map<Column, Object> columnMap;

    private final CriteriaManager cm;

    public UPDATE() {
        super();
        columnMap = new HashMap<Column, Object>();
        cm = new CriteriaManager();
    }

    public UPDATE UPDATE(Table table) {
        this.table = table;
        return this;
    }

    public UPDATE SET(Column column, Object value) {
        columnMap.put(column, value);
        return this;
    }

    public UPDATE WHERE(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public UPDATE WHERE(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public UPDATE AND(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public UPDATE AND(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public UPDATE OR(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public UPDATE OR(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public UPDATE AND_NEXT() {
        cm.next(Operator.AND);
        return this;
    }

    public UPDATE OR_NEXT() {
        cm.next(Operator.OR);
        return this;
    }

    public UPDATE AND_IN() {
        cm.in(Operator.AND);
        return this;
    }

    public UPDATE OR_IN() {
        cm.in(Operator.OR);
        return this;
    }

    public UPDATE OUT() {
        cm.out();
        return this;
    }

    public Table getTable() {
        return table;
    }

    public Map<Column, Object> getColumnMap() {
        return columnMap;
    }

    public CriteriaManager getCriteriaManager() {
        return cm;
    }

}
