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

        protected Column[] columns;

        protected Table table;

        protected List<JoinCriterion> joinCriteria;

        protected CriteriaManager cm;

        protected List<OrderCriterion> orderCriteria;

        protected Column[] groupByColumns;

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

        public Select firstRow(int firstRow) {
            this.firstRow = firstRow;
            return this;
        }

        public Select lastRow(int lastRow) {
            this.lastRow = lastRow;
            return this;
        }

        public Select interval(int start, int count) {
            firstRow = 0;
            lastRow = 0;
            if (start < 0) {
                start = 0;
            }
            count = count - 1;
            if (count < 0) {
                count = 0;
            }
            if (count != 0) {
                firstRow = start;
                lastRow = start + count;
            }
            return this;
        }

        public Select page(int page, int size) {
            firstRow = 0;
            lastRow = 0;
            if (page < 0) {
                page = 0;
            }
            size = size - 1;
            if (size < 0) {
                size = 0;
            }
            firstRow = page * size;
            lastRow = firstRow + size;
            return this;
        }

        public Select COUNT() {
            count = true;
            return this;
        }

        public Select DISTINCT() {
            distinct = true;
            return this;
        }

        public Select FROM(Table table) {
            this.table = table;
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

    }

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
    }

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

    }

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

    }

    private QueryType queryType;

    private Object queryObject;

    public QueryType getQueryType() {
        return queryType;
    }

    public CriteriaManager getCriteriaManager() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).cm;
                case UPDATE:
                    return ((Update) queryObject).cm;
                case DELETE:
                    return ((Delete) queryObject).cm;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
    }

    public Table getTable() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).table;
                case INSERT:
                    return ((Insert) queryObject).table;
                case UPDATE:
                    return ((Update) queryObject).table;
                case DELETE:
                    return ((Delete) queryObject).table;
            }
        }
        return null;
    }

    public Column[] getColumns() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).columns;
                case INSERT:
                    return ((Insert) queryObject).columns;
                case UPDATE:
                    return ((Update) queryObject).columns;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
    }

    public Object[] getValues() {
        if (queryType != null) {
            switch (queryType) {
                case INSERT:
                    return ((Insert) queryObject).values;
                case UPDATE:
                    return ((Update) queryObject).values;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
    }

    public Column[] getGroupByColumns() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).groupByColumns;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
    }

    public boolean getCount() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).count;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return false;
    }

    public boolean getDistinct() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).distinct;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return false;
    }

    public boolean getPagination() {
        return getFirstRow() != -1 && getLastRow() != -1;
    }

    public int getFirstRow() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).firstRow;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return -1;
    }

    public int getLastRow() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).lastRow;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return -1;
    }

    public int getPageSize() {
        int result = 0;
        if (getLastRow() > 0) {
            result = getLastRow() - getFirstRow();
            result++;
        }
        return result;
    }

    public List<JoinCriterion> getJoinCriteria() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).joinCriteria;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
    }

    public List<OrderCriterion> getOrderCriteria() {
        if (queryType != null) {
            switch (queryType) {
                case SELECT:
                    return ((Select) queryObject).orderCriteria;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
    }

    public Column getReturning() {
        if (queryType != null) {
            switch (queryType) {
                case INSERT:
                    return ((Insert) queryObject).returning;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
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
