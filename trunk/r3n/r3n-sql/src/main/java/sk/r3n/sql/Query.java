package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Query implements Serializable {

    public enum QueryType {

        SELECT, INSERT, UPDATE, DELETE;
    }

    public final class Select implements Serializable {

        public Select(Column... columns) {
            Query.this.columns = columns;
        }

        public Select firstRow(int firstRow) {
            Query.this.firstRow = firstRow;
            return this;
        }

        public Select lastRow(int lastRow) {
            Query.this.lastRow = lastRow;
            return this;
        }

        public Select interval(int start, int count) {
            Query.this.firstRow = 0;
            Query.this.lastRow = 0;
            if (start < 0) {
                start = 0;
            }
            if (count < 0) {
                count = 0;
            }
            if (count != 0) {
                Query.this.firstRow = start;
                Query.this.lastRow = start + count;
            }
            return this;
        }

        public Select page(int page, int size) {
            if (page < 0) {
                page = 0;
            }
            if (size < 0) {
                size = 0;
            }
            Query.this.firstRow = page * size;
            Query.this.lastRow = Query.this.firstRow + size;
            return this;
        }

        public Select COUNT() {
            Query.this.count = true;
            return this;
        }

        public Select DISTINCT() {
            Query.this.distinct = true;
            return this;
        }

        public Select FROM(Table table) {
            Query.this.table = table;
            return this;
        }

        public Select INNER_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.INNER, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            Query.this.joinCriteria.add(joinCriterion);
            return this;
        }

        public Select LEFT_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.LEFT, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            Query.this.joinCriteria.add(joinCriterion);
            return this;
        }

        public Select RIGHT_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.RIGHT, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            Query.this.joinCriteria.add(joinCriterion);
            return this;
        }

        public Select FULL_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.FULL, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            Query.this.joinCriteria.add(joinCriterion);
            return this;
        }

        public Select WHERE(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Select WHERE(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Select AND(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Select AND(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Select OR(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Select OR(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Select AND_NEXT() {
            Query.this.cm.next(Operator.AND);
            return this;
        }

        public Select OR_NEXT() {
            Query.this.cm.next(Operator.OR);
            return this;
        }

        public Select AND_IN() {
            Query.this.cm.in(Operator.AND);
            return this;
        }

        public Select OR_IN() {
            Query.this.cm.in(Operator.OR);
            return this;
        }

        public Select OUT() {
            Query.this.cm.out();
            return this;
        }

        public Select ORDER_BY(Order order, Column... columns) {
            Query.this.order = order;
            Query.this.orderColumns = columns;
            return this;
        }

        public Select GROUP_BY(Column... columns) {
            Query.this.groupByColumns = columns;
            return this;
        }

    }

    public class Insert implements Serializable {

        public Insert INTO(Table table, Column... columns) {
            Query.this.table = table;
            Query.this.columns = columns;
            return this;
        }

        public Insert VALUES(Object... values) {
            Query.this.values = values;
            return this;
        }

        public Insert RETURNING(Column returning) {
            Query.this.returning = returning;
            return this;
        }
    }

    public class Update implements Serializable {

        public Update(Table table) {
            Query.this.table = table;
        }

        public Update SET(Column column, Object value) {
            if (Query.this.columns == null) {
                Query.this.columns = new Column[]{};
            }
            Column[] columns = Arrays.copyOf(Query.this.columns, Query.this.columns.length + 1);
            columns[columns.length - 1] = column;

            if (Query.this.values == null) {
                Query.this.values = new Object[]{};
            }
            Object[] values = Arrays.copyOf(Query.this.values, Query.this.values.length + 1);
            values[values.length - 1] = value;

            Query.this.columns = columns;
            Query.this.values = values;
            return this;
        }

        public Update WHERE(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Update WHERE(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Update AND(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Update AND(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Update OR(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Update OR(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Update AND_NEXT() {
            Query.this.cm.next(Operator.AND);
            return this;
        }

        public Update OR_NEXT() {
            Query.this.cm.next(Operator.OR);
            return this;
        }

        public Update AND_IN() {
            Query.this.cm.in(Operator.AND);
            return this;
        }

        public Update OR_IN() {
            Query.this.cm.in(Operator.OR);
            return this;
        }

        public Update OUT() {
            Query.this.cm.out();
            return this;
        }

    }

    public class Delete implements Serializable {

        public Delete FROM(Table from) {
            Query.this.table = from;
            return this;
        }

        public Delete WHERE(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Delete WHERE(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Delete AND(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Delete AND(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Delete OR(Column column, Condition condition, Object value, String representation) {
            Query.this.cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Delete OR(Column column, Condition condition, Object value) {
            Query.this.cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Delete AND_NEXT() {
            Query.this.cm.next(Operator.AND);
            return this;
        }

        public Delete OR_NEXT() {
            Query.this.cm.next(Operator.OR);
            return this;
        }

        public Delete AND_IN() {
            Query.this.cm.in(Operator.AND);
            return this;
        }

        public Delete OR_IN() {
            Query.this.cm.in(Operator.OR);
            return this;
        }

        public Delete OUT() {
            Query.this.cm.out();
            return this;
        }

    }

    private QueryType queryType;

    private CriteriaManager cm;

    private Table table;

    private Column[] columns;

    private Object[] values;

    private Column[] orderColumns;

    private Order order;

    private Column[] groupByColumns;

    private boolean count = false;

    private boolean distinct = false;

    private int firstRow = -1;

    private int lastRow = -1;

    private List<JoinCriterion> joinCriteria;

    private Column returning;

    public QueryType getQueryType() {
        return queryType;
    }

    public CriteriaManager getCriteriaManager() {
        return cm;
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

    public Column[] getGroupByColumns() {
        return groupByColumns;
    }

    public boolean getCount() {
        return count;
    }

    public boolean getDistinct() {
        return distinct;
    }

    public boolean getPagination() {
        return getFirstRow() != -1 && getLastRow() != -1;
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

    public List<JoinCriterion> getJoinCriteria() {
        return joinCriteria;
    }

    public Column[] getOrderColumns() {
        return orderColumns;
    }

    public Order getOrder() {
        return order;
    }

    public Column getReturning() {
        return returning;
    }

    public Select SELECT(Column... columns) {
        queryType = QueryType.SELECT;
        cm = new CriteriaManager();
        joinCriteria = new ArrayList<JoinCriterion>();
        return new Select(columns);
    }

    public Insert INSERT() {
        queryType = QueryType.INSERT;
        cm = new CriteriaManager();
        joinCriteria = new ArrayList<JoinCriterion>();
        return new Insert();
    }

    public Update UPDATE(Table table) {
        queryType = QueryType.UPDATE;
        cm = new CriteriaManager();
        joinCriteria = new ArrayList<JoinCriterion>();
        return new Update(table);
    }

    public Delete DELETE() {
        queryType = QueryType.DELETE;
        cm = new CriteriaManager();
        joinCriteria = new ArrayList<JoinCriterion>();
        return new Delete();
    }

}
