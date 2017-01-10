package sk.r3n.sql;

import java.io.Serializable;

public class Delete implements Serializable {

    protected CriteriaManager cm;

    protected Table table;

    public Delete FROM(Table from) {
        cm = new CriteriaManager();
        table = from;
        return this;
    }

    public Delete WHERE(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public Delete WHERE(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public Delete AND(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public Delete AND(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public Delete OR(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public Delete OR(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public Delete AND_NEXT() {
        cm.next(Operator.AND);
        return this;
    }

    public Delete OR_NEXT() {
        cm.next(Operator.OR);
        return this;
    }

    public Delete AND_IN() {
        cm.in(Operator.AND);
        return this;
    }

    public Delete OR_IN() {
        cm.in(Operator.OR);
        return this;
    }

    public Delete OUT() {
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
