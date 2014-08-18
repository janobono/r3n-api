package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SELECT implements Serializable {

    private boolean distinct = false;

    protected int firstRow = -1;

    protected int lastRow = -1;

    private Column[] columns;

    private Table table;

    private final List<JoinCriterion> joinCriteria;

    private final CriteriaManager cm;

    private final List<OrderCriterion> orderCriteria;

    public SELECT() {
        super();
        joinCriteria = new ArrayList<JoinCriterion>();
        cm = new CriteriaManager();
        orderCriteria = new ArrayList<OrderCriterion>();
    }

    public SELECT firstRow(int firstRow) {
        this.firstRow = firstRow;
        return this;
    }

    public SELECT lastRow(int lastRow) {
        this.lastRow = lastRow;
        return this;
    }

    public SELECT interval(int start, int count) {
        this.firstRow = 0;
        this.lastRow = 0;
        if (start < 0) {
            start = 0;
        }
        if (count < 0) {
            count = 0;
        }
        if (count != 0) {
            this.firstRow = start;
            this.lastRow = start + count;
        }
        return this;
    }

    public SELECT page(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        this.firstRow = page * size;
        this.lastRow = firstRow + size;
        return this;
    }

    public SELECT DISTINCT() {
        distinct = true;
        return this;
    }

    public SELECT COLUMNS(Column... columns) {
        this.columns = columns;
        return this;
    }

    public SELECT FROM(Table table) {
        this.table = table;
        return this;
    }

    public SELECT INNER_JOIN(Table table, Column col1, Column col2) {
        JoinCriterion joinCriterion = new JoinCriterion(Join.INNER, table);
        joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
        joinCriteria.add(joinCriterion);
        return this;
    }

    public SELECT LEFT_JOIN(Table table, Column col1, Column col2) {
        JoinCriterion joinCriterion = new JoinCriterion(Join.LEFT, table);
        joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
        joinCriteria.add(joinCriterion);
        return this;
    }

    public SELECT RIGHT_JOIN(Table table, Column col1, Column col2) {
        JoinCriterion joinCriterion = new JoinCriterion(Join.RIGHT, table);
        joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
        joinCriteria.add(joinCriterion);
        return this;
    }

    public SELECT FULL_JOIN(Table table, Column col1, Column col2) {
        JoinCriterion joinCriterion = new JoinCriterion(Join.FULL, table);
        joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
        joinCriteria.add(joinCriterion);
        return this;
    }

    public SELECT WHERE(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public SELECT WHERE(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public SELECT AND(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public SELECT AND(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public SELECT OR(Column column, Condition condition, Object value, String representation) {
        cm.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public SELECT OR(Column column, Condition condition, Object value) {
        cm.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public SELECT AND_NEXT() {
        cm.next(Operator.AND);
        return this;
    }

    public SELECT OR_NEXT() {
        cm.next(Operator.OR);
        return this;
    }

    public SELECT AND_IN() {
        cm.in(Operator.AND);
        return this;
    }

    public SELECT OR_IN() {
        cm.in(Operator.OR);
        return this;
    }

    public SELECT OUT() {
        cm.out();
        return this;
    }

    public SELECT ORDER_BY(Column column, Order order) {
        orderCriteria.add(new OrderCriterion(column, order));
        return this;
    }

    public boolean getDistinct() {
        return distinct;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public int getPageSize() {
        return getLastRow() - getFirstRow();
    }

    public Column[] getColumns() {
        return columns;
    }

    public Table getTable() {
        return table;
    }

    public List<JoinCriterion> getJoinCriteria() {
        return joinCriteria;
    }

    public CriteriaManager getCriteriaManager() {
        return cm;
    }

    public List<OrderCriterion> getOrderCriteria() {
        return orderCriteria;
    }

}
