package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Query implements Serializable {

    public enum QueryType {

        SELECT, INSERT, UPDATE, DELETE;
    }

    public class Select implements Serializable {

        protected Column[] columns;

        protected Table table;

        protected Query.Select[] subSelects;

        protected DataSetOperator dataSetOperator;

        protected List<JoinCriterion> joinCriteria;

        protected CriteriaManager cm;

        protected List<OrderCriterion> orderCriteria;

        protected Column[] groupByColumns;

        protected Criterion havingCriterion;

        protected boolean count;

        protected boolean distinct;

        protected int firstRow;

        protected int lastRow;

        public Select(Column... columns) {
            cm = new CriteriaManager();
            joinCriteria = new ArrayList<JoinCriterion>();
            orderCriteria = new ArrayList<OrderCriterion>();
            this.columns = columns;
            count = false;
            distinct = false;
            firstRow = -1;
            lastRow = -1;
        }

        public Query getParent() {
            return Query.this;
        }

        public Select firstRow(int firstRow) {
            if (firstRow < 0) {
                firstRow = 0;
            }
            this.firstRow = firstRow;
            return this;
        }

        public Select lastRow(int lastRow) {
            if (lastRow < 0) {
                lastRow = -1;
            }
            this.lastRow = lastRow;
            return this;
        }

        public Select interval(int start, int count) {
            firstRow = 0;
            lastRow = 0;

            if (start < 0 || count <= 0) {
                start = 0;
            }

            if (count >= 0) {
                count--;
            } else {
                count = -1;
            }

            firstRow = start;
            lastRow = start + count;

            this.count = false;
            return this;
        }

        public Select page(int page, int size) {
            return interval(page * size, size);
        }

        public Select COUNT() {
            count = true;

            firstRow = -1;
            lastRow = -1;
            return this;
        }

        public Select DISTINCT() {
            distinct = true;
            return this;
        }

        public Select FROM(Table table) {
            this.table = table;

            subSelects = null;
            return this;
        }

        public Select FROM(Query.Select... subSelects) {
            return FROM(DataSetOperator.UNION_ALL, subSelects);
        }

        public Select FROM(DataSetOperator dataSetOperator, Query.Select... subSelects) {
            this.subSelects = subSelects;
            this.dataSetOperator = dataSetOperator;

            return this;
        }

        public Select INNER_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.INNER, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            joinCriteria.add(joinCriterion);
            return this;
        }

        public Select LEFT_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.LEFT, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            joinCriteria.add(joinCriterion);
            return this;
        }

        public Select RIGHT_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.RIGHT, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            joinCriteria.add(joinCriterion);
            return this;
        }

        public Select FULL_JOIN(Table table, Column col1, Column col2) {
            JoinCriterion joinCriterion = new JoinCriterion(Join.FULL, table);
            joinCriterion.getCriteriaManager().addCriterion(col1, Condition.EQUALS, col2, null, Operator.AND);
            joinCriteria.add(joinCriterion);
            return this;
        }

        public Select WHERE(Column column, Condition condition, Object value, String representation) {
            cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Select WHERE(Column column, Condition condition, Object value) {
            cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Select AND(Column column, Condition condition, Object value, String representation) {
            cm.addCriterion(column, condition, value, representation, Operator.AND);
            return this;
        }

        public Select AND(Column column, Condition condition, Object value) {
            cm.addCriterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Select OR(Column column, Condition condition, Object value, String representation) {
            cm.addCriterion(column, condition, value, representation, Operator.OR);
            return this;
        }

        public Select OR(Column column, Condition condition, Object value) {
            cm.addCriterion(column, condition, value, null, Operator.OR);
            return this;
        }

        public Select AND_NEXT() {
            cm.next(Operator.AND);
            return this;
        }

        public Select OR_NEXT() {
            cm.next(Operator.OR);
            return this;
        }

        public Select AND_IN() {
            cm.in(Operator.AND);
            return this;
        }

        public Select OR_IN() {
            cm.in(Operator.OR);
            return this;
        }

        public Select OUT() {
            cm.out();
            return this;
        }

        public Select ORDER_BY(Column column, Order order) {
            OrderCriterion orderCriterion = new OrderCriterion();
            orderCriterion.setColumn(column);
            orderCriterion.setOrder(order);
            orderCriteria.add(orderCriterion);
            return this;
        }

        public Select GROUP_BY(Column... columns) {
            groupByColumns = columns;
            return this;
        }

        public Select HAVING(Column column, Condition condition, Object value) {
            havingCriterion = new Criterion(column, condition, value, null, Operator.AND);
            return this;
        }

        public Column[] getColumns() {
            return columns;
        }

        public Table getTable() {
            return table;
        }

        public Query.Select[] getSubSelects() {
            return subSelects;
        }

        public DataSetOperator getDataSetOperator() {
            return dataSetOperator;
        }

        public List<JoinCriterion> getJoinCriteria() {
            return joinCriteria;
        }

        public List<OrderCriterion> getOrderCriteria() {
            return orderCriteria;
        }

        public Column[] getGroupByColumns() {
            return groupByColumns;
        }

        public Criterion getHavingCriterion() {
            return havingCriterion;
        }

        public boolean getCount() {
            return count;
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

        public boolean getPagination() {
            return getFirstRow() != -1 && getLastRow() != -1;
        }

        public int getPageSize() {
            int result = 0;
            if (getLastRow() >= 0) {
                result = getLastRow() - getFirstRow();
                result++;
            }
            return result;
        }

        public CriteriaManager getCriteriaManager() {
            return cm;
        }

    }

    public class Insert implements Serializable {

        protected Table table;

        protected Column[] columns;

        protected Object[] values;

        protected Column returning;

        public Query getParent() {
            return Query.this;
        }

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

    public class Update implements Serializable {

        protected CriteriaManager cm;

        protected Table table;

        protected Column[] columns;

        protected Object[] values;

        public Query getParent() {
            return Query.this;
        }

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

    public class Delete implements Serializable {

        protected CriteriaManager cm;

        protected Table table;

        public Query getParent() {
            return Query.this;
        }

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

    private QueryType queryType;

    private Object queryObject;

    public QueryType getQueryType() {
        return queryType;
    }

    public Object getQueryObject() {
        return queryObject;
    }

    public Select SELECT(Column... columns) {
        queryType = QueryType.SELECT;
        Select select = new Select(columns);
        queryObject = select;
        return select;
    }

    public Insert INSERT() {
        queryType = QueryType.INSERT;
        Insert insert = new Insert();
        queryObject = insert;
        return insert;
    }

    public Update UPDATE(Table table) {
        queryType = QueryType.UPDATE;
        Update update = new Update(table);
        queryObject = update;
        return update;
    }

    public Delete DELETE() {
        queryType = QueryType.DELETE;
        Delete delete = new Delete();
        queryObject = delete;
        return delete;
    }

}
