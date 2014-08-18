package sk.r3n.sql;

import java.io.Serializable;

public class DELETE implements Serializable {

    private Table table;

    private final CriteriaManager cm;

    public DELETE() {
        super();
        cm = new CriteriaManager();
    }

    public DELETE FROM(Table table) {
        this.table = table;
        return this;
    }

    public DELETE WHERE(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public DELETE WHERE(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public DELETE AND(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public DELETE AND(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public DELETE OR(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public DELETE OR(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public DELETE AND_NEXT() {
        cm.next(Operator.AND);
        return this;
    }

    public DELETE OR_NEXT() {
        cm.next(Operator.OR);
        return this;
    }

    public DELETE AND_IN() {
        cm.in(Operator.AND);
        return this;
    }

    public DELETE OR_IN() {
        cm.in(Operator.OR);
        return this;
    }

    public DELETE OUT() {
        cm.out();
        return this;
    }

    public Table getTable() {
        return table;
    }

    public CriteriaManager getCriteriaManager() {
        return cm;
    }

}
