package sk.r3n.db.impl;

import sk.r3n.db.*;

public class DefaultSQLGenerator implements SQLGenerator, Condition, Operator {

    public String COLUMN_NAME_SEPARATOR;
    public String FIELDS_SEPARATOR;
    public String VALUE_SQL;
    public String EQUALS_SQL;
    public String EQUALS_MORE_SQL;
    public String EQUALS_LESS_SQL;
    public String EQUALS_NOT_SQL;
    public String MORE_SQL;
    public String LESS_SQL;
    public String LIKE_SQL;
    public String IS_NULL_SQL;
    public String IS_NOT_NULL_SQL;
    public String IN_SQL;
    public String NOT_IN_SQL;
    public String COUNT;
    public String ALL_COLUMNS;
    public String MAX;
    public String DELETE_SQL;
    public String INSERT_SQL;
    public String UPDATE_SQL;
    public String SELECT_SQL;
    public String FROM_SQL;
    public String WHERE_SQL;
    public String VALUES_SQL;
    public String SET_SQL;
    public String DISTINCT_SQL;
    public String ORDER_BY_SQL;
    public String ASC_SQL;
    public String DESC_SQL;
    public String OR_SQL;
    public String AND_SQL;
    public String LEFT_BRACE;
    public String RIGHT_BRACE;
    public String QUOTES;
    public String UNION;

    public DefaultSQLGenerator() {
        super();
        COLUMN_NAME_SEPARATOR = ".";
        FIELDS_SEPARATOR = ", ";
        VALUE_SQL = "?";
        EQUALS_SQL = " = ";
        EQUALS_MORE_SQL = " >= ";
        EQUALS_LESS_SQL = " <= ";
        EQUALS_NOT_SQL = " != ";
        MORE_SQL = " > ";
        LESS_SQL = " < ";
        LIKE_SQL = " like ";
        IS_NULL_SQL = " is null";
        IS_NOT_NULL_SQL = " is not null";
        IN_SQL = " in";
        NOT_IN_SQL = " not in";

        COUNT = "count";
        ALL_COLUMNS = "*";
        MAX = "max";

        DELETE_SQL = "delete ";
        INSERT_SQL = "insert into ";
        UPDATE_SQL = "update ";
        SELECT_SQL = "select ";
        FROM_SQL = " from ";

        WHERE_SQL = " where ";

        VALUES_SQL = " values ";
        SET_SQL = " set ";

        DISTINCT_SQL = "distinct ";
        ORDER_BY_SQL = " order by ";
        ASC_SQL = " asc";
        DESC_SQL = " desc";

        OR_SQL = " or ";
        AND_SQL = " and ";

        LEFT_BRACE = "(";
        RIGHT_BRACE = ")";

        QUOTES = "\"";

        UNION = " union ";
    }

    @Override
    public String putIntoQuotes(String string) {
        return QUOTES + string + QUOTES;
    }

    protected String toSQL(boolean tablePrefix, SQLColumn sqlColumn) {
        StringBuilder result = new StringBuilder();
        if (tablePrefix) {
            result.append(putIntoQuotes(sqlColumn.getTable()));
            result.append(COLUMN_NAME_SEPARATOR);
        }
        result.append(putIntoQuotes(sqlColumn.getName()));
        return result.toString();
    }

    @Override
    public String toSQL(boolean tablePrefix, SQLColumn column, boolean asc) {
        StringBuilder result = new StringBuilder();
        result.append(ORDER_BY_SQL);
        result.append(toSQL(tablePrefix, column));
        if (asc) {
            result.append(ASC_SQL);
        } else {
            result.append(DESC_SQL);
        }
        return result.toString();
    }

    protected String toSQL(boolean tablePrefix, SQLColumn[] columns) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < columns.length - 1; i++) {
            result.append(toSQL(tablePrefix, columns[i]));
            result.append(FIELDS_SEPARATOR);
        }
        result.append(toSQL(tablePrefix, columns[columns.length - 1]));
        return result.toString();
    }

    protected String toSQL(boolean tablePrefix, SQLColumn[] columns,
            String suffix) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < columns.length - 1; i++) {
            result.append(toSQL(tablePrefix, columns[i]));
            result.append(suffix);
            result.append(FIELDS_SEPARATOR);
        }
        result.append(toSQL(tablePrefix, columns[columns.length - 1]));
        result.append(suffix);
        return result.toString();
    }

    @Override
    public String toSQL(boolean tablePrefix, SQLCondition condition) {
        StringBuilder result = new StringBuilder();
        result.append(toSQL(tablePrefix, condition.getColumn()));
        switch (condition.getCondition()) {
            case IS_NULL:
                result.append(IS_NULL_SQL);
                break;
            case IS_NOT_NULL:
                result.append(IS_NOT_NULL_SQL);
                break;
            case IN:
                result.append(IN_SQL);
                result.append(LEFT_BRACE);
                result.append(condition.getExpression());
                result.append(RIGHT_BRACE);
                break;
            case NOT_IN:
                result.append(NOT_IN_SQL);
                result.append(LEFT_BRACE);
                result.append(condition.getExpression());
                result.append(RIGHT_BRACE);
                break;
            default:
                result.append(toSQLCondition(condition.getCondition()));
                result.append(VALUE_SQL);
                break;
        }
        return result.toString();
    }

    @Override
    public String toSQL(boolean tablePrefix, SQLCondition[] conditions) {
        StringBuilder result = new StringBuilder();
        if (conditions != null && conditions.length > 0) {
            result.append(LEFT_BRACE);
            for (int i = 0; i < conditions.length; i++) {
                result.append(toSQL(tablePrefix, conditions[i]));
                if (i < conditions.length - 1) {
                    result.append(toSQLOperator(conditions[i].getOperator()));
                } else {
                    result.append(RIGHT_BRACE);
                }
            }
        }
        return result.toString();
    }

    @Override
    public String toSQL(boolean tablePrefix, SQLCondition[][] conditions) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < conditions.length; i++) {
            String cond = toSQL(tablePrefix, conditions[i]);
            if (cond.length() > 0) {
                result.append(cond);
                if (i < conditions.length - 1) {
                    result.append(toSQLOperator(conditions[i][conditions[i].length - 1].getOperator()));
                }
            }
        }
        return result.toString();
    }

    protected String toSQLCondition(short condition) {
        switch (condition) {
            case EQUALS:
                return EQUALS_SQL;
            case EQUALS_LESS:
                return EQUALS_LESS_SQL;
            case EQUALS_MORE:
                return EQUALS_MORE_SQL;
            case EQUALS_NOT:
                return EQUALS_NOT_SQL;
            case MORE:
                return MORE_SQL;
            case LESS:
                return LESS_SQL;
            case LIKE:
                return LIKE_SQL;
            case IS_NULL:
                return IS_NULL_SQL;
            case IS_NOT_NULL:
                return IS_NOT_NULL_SQL;
            case IN:
                return IN_SQL;
            case NOT_IN:
                return NOT_IN_SQL;
        }
        throw new RuntimeException("Unknown condition type!");
    }

    protected String toSQLOperator(short operator) {
        switch (operator) {
            case OR:
                return OR_SQL;
            case AND:
                return AND_SQL;
        }
        throw new RuntimeException("Unknown operator type!");
    }

    protected String toSQLValues(int size) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            result.append(VALUE_SQL);
            result.append(FIELDS_SEPARATOR);
        }
        result.append(VALUE_SQL);
        return result.toString();
    }

    protected boolean contains(SQLColumn[] columns, SQLColumn orderBy) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(orderBy)) {
                return true;
            }
        }
        return false;
    }

    protected boolean contains(SQLColumn[][] columns, SQLColumn orderBy) {
        for (int i = 0; i < columns.length; i++) {
            if (contains(columns[i], orderBy)) {
                return true;
            }
        }
        return false;
    }

    // DELETE---------------------------------------------------------------------------
    @Override
    public String delete(String table) {
        StringBuilder result = new StringBuilder();
        result.append(DELETE_SQL);
        result.append(FROM_SQL);
        result.append(putIntoQuotes(table));
        return result.toString();
    }

    @Override
    public String delete(String table, SQLCondition condition) {
        StringBuilder result = new StringBuilder();
        result.append(delete(table));
        if (condition != null) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, condition));
        }
        return result.toString();
    }

    @Override
    public String delete(String table, SQLCondition[] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(delete(table));
        if (conditions != null && conditions.length > 0) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, conditions));
        }
        return result.toString();
    }

    @Override
    public String delete(String table, SQLCondition[][] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(delete(table));
        if (conditions != null) {
            String cond = toSQL(false, conditions);
            if (cond.length() > 0) {
                result.append(WHERE_SQL);
                result.append(cond);
            }
        }
        return result.toString();
    }

    // INSERT---------------------------------------------------------------------------
    @Override
    public String insert(String table, SQLColumn[] columns) {
        StringBuilder result = new StringBuilder();
        result.append(INSERT_SQL);
        result.append(putIntoQuotes(table));
        result.append(" ");
        result.append(LEFT_BRACE);
        result.append(toSQL(false, columns));
        result.append(RIGHT_BRACE);
        result.append(VALUES_SQL);
        result.append(LEFT_BRACE);
        result.append(toSQLValues(columns.length));
        result.append(RIGHT_BRACE);
        return result.toString();
    }

    // SELECT---------------------------------------------------------------------------
    protected String select(SQLColumn[] columns, String table) {
        StringBuilder result = new StringBuilder();
        result.append(SELECT_SQL);
        result.append(toSQL(false, columns));
        result.append(FROM_SQL);
        result.append(putIntoQuotes(table));
        return result.toString();
    }

    @Override
    public String select(SQLColumn[] columns, String table,
            SQLCondition condition) {
        StringBuilder result = new StringBuilder();
        result.append(select(columns, table));
        if (condition != null) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, condition));
        }
        return result.toString();
    }

    @Override
    public String select(SQLColumn[] columns, String table,
            SQLCondition[] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(select(columns, table));
        if (conditions != null && conditions.length > 0) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, conditions));
        }
        return result.toString();
    }

    protected String select(SQLColumn[] columns, String table, SQLColumn orderBy) {
        StringBuilder result = new StringBuilder();
        result.append(SELECT_SQL);
        result.append(toSQL(false, columns));
        if (orderBy != null && !contains(columns, orderBy)) {
            result.append(FIELDS_SEPARATOR);
            result.append(toSQL(false, orderBy));
        }
        result.append(FROM_SQL);
        result.append(putIntoQuotes(table));
        return result.toString();
    }

    @Override
    public String select(SQLColumn[] columns, String table, SQLColumn orderBy,
            boolean asc) {
        StringBuilder result = new StringBuilder();
        result.append(select(columns, table, orderBy));
        if (orderBy != null) {
            result.append(toSQL(false, orderBy, asc));
        }
        return result.toString();
    }

    @Override
    public String select(SQLColumn[] columns, String table,
            SQLCondition condition, SQLColumn orderBy, boolean asc) {
        StringBuilder result = new StringBuilder();
        result.append(select(columns, table, orderBy));
        if (condition != null) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, condition));
        }
        if (orderBy != null) {
            result.append(toSQL(false, orderBy, asc));
        }
        return result.toString();
    }

    @Override
    public String select(SQLColumn[] columns, String table,
            SQLCondition[] conditions, SQLColumn orderBy, boolean asc) {
        StringBuilder result = new StringBuilder();
        result.append(select(columns, table, orderBy));
        if (conditions != null && conditions.length > 0) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, conditions));
        }
        if (orderBy != null) {
            result.append(toSQL(false, orderBy, asc));
        }
        return result.toString();
    }

    protected String select(boolean distinct, SQLColumn[] columns,
            String table, SQLColumn orderBy) {
        StringBuilder result = new StringBuilder();
        result.append(SELECT_SQL);
        if (distinct) {
            result.append(DISTINCT_SQL);
        }
        result.append(toSQL(false, columns));
        if (orderBy != null && !contains(columns, orderBy)) {
            result.append(FIELDS_SEPARATOR);
            result.append(toSQL(false, orderBy));
        }
        result.append(FROM_SQL);
        result.append(putIntoQuotes(table));
        return result.toString();
    }

    @Override
    public String select(boolean distinct, SQLColumn[] columns, String table,
            SQLColumn orderBy, boolean asc) {
        StringBuilder result = new StringBuilder();
        result.append(select(distinct, columns, table, orderBy));
        if (orderBy != null) {
            result.append(toSQL(false, orderBy, asc));
        }
        return result.toString();
    }

    @Override
    public String select(boolean distinct, SQLColumn[] columns, String table,
            SQLCondition[] conditions, SQLColumn orderBy, boolean asc) {
        StringBuilder result = new StringBuilder();
        result.append(select(distinct, columns, table, orderBy));
        if (conditions != null && conditions.length > 0) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, conditions));
        }
        if (orderBy != null) {
            result.append(toSQL(false, orderBy, asc));
        }
        return result.toString();
    }

    @Override
    public String select(boolean distinct, SQLColumn[] columns, String table,
            SQLCondition[][] conditions, SQLColumn orderBy, boolean asc) {
        StringBuilder result = new StringBuilder();
        result.append(select(distinct, columns, table, orderBy));
        if (conditions != null) {
            String cond = toSQL(false, conditions);
            if (cond.length() > 0) {
                result.append(WHERE_SQL);
                result.append(cond);
            }
        }
        if (orderBy != null) {
            result.append(toSQL(false, orderBy, asc));
        }
        return result.toString();
    }

    protected String select(boolean distinct, SQLColumn[][] columns,
            String[] tables, SQLColumn orderBy) {
        StringBuilder result = new StringBuilder();
        result.append(SELECT_SQL);
        if (distinct) {
            result.append(DISTINCT_SQL);
        }
        for (int i = 0; i < columns.length; i++) {
            result.append(toSQL(true, columns[i]));
            if (i < columns.length - 1) {
                result.append(FIELDS_SEPARATOR);
            }
        }
        if (orderBy != null && !contains(columns, orderBy)) {
            result.append(FIELDS_SEPARATOR);
            result.append(toSQL(true, orderBy));
        }
        result.append(FROM_SQL);
        for (int i = 0; i < tables.length; i++) {
            result.append(putIntoQuotes(tables[i]));
            if (i < tables.length - 1) {
                result.append(FIELDS_SEPARATOR);
            }
        }
        return result.toString();
    }

    @Override
    public String selectCount(String table) {
        StringBuilder result = new StringBuilder();
        result.append(SELECT_SQL);
        result.append(COUNT);
        result.append(LEFT_BRACE);
        result.append(ALL_COLUMNS);
        result.append(RIGHT_BRACE);
        result.append(FROM_SQL);
        result.append(putIntoQuotes(table));
        return result.toString();
    }

    @Override
    public String selectCount(String table, SQLCondition condition) {
        StringBuilder result = new StringBuilder();
        result.append(selectCount(table));
        if (condition != null) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, condition));
        }
        return result.toString();
    }

    @Override
    public String selectCount(String table, SQLCondition[] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(selectCount(table));
        if (conditions != null && conditions.length > 0) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, conditions));
        }
        return result.toString();
    }

    @Override
    public String selectCount(String table, SQLCondition[][] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(selectCount(table));
        if (conditions != null) {
            String cond = toSQL(false, conditions);
            if (cond.length() > 0) {
                result.append(WHERE_SQL);
                result.append(cond);
            }
        }
        return result.toString();
    }

    @Override
    public String selectMax(SQLColumn column, String table) {
        StringBuilder result = new StringBuilder();
        result.append(SELECT_SQL);
        result.append(MAX);
        result.append(LEFT_BRACE);
        result.append(toSQL(false, column));
        result.append(RIGHT_BRACE);
        result.append(FROM_SQL);
        result.append(putIntoQuotes(table));
        return result.toString();
    }

    @Override
    public String selectMax(SQLColumn column, String table,
            SQLCondition condition) {
        StringBuilder result = new StringBuilder();
        result.append(selectMax(column, table));
        if (condition != null) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, condition));
        }
        return result.toString();
    }

    @Override
    public String selectMax(SQLColumn column, String table,
            SQLCondition[] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(selectMax(column, table));
        if (conditions != null && conditions.length > 0) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, conditions));
        }
        return result.toString();
    }

    @Override
    public String selectMax(SQLColumn column, String table,
            SQLCondition[][] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(selectMax(column, table));
        if (conditions != null) {
            String cond = toSQL(false, conditions);
            if (cond.length() > 0) {
                result.append(WHERE_SQL);
                result.append(cond);
            }
        }
        return result.toString();
    }

    // UPDATE---------------------------------------------------------------------------
    @Override
    public String update(String table, SQLColumn[] columns) {
        StringBuilder result = new StringBuilder();
        result.append(UPDATE_SQL);
        result.append(putIntoQuotes(table));
        result.append(SET_SQL);
        result.append(toSQL(false, columns, EQUALS_SQL + VALUE_SQL));
        return result.toString();
    }

    @Override
    public String update(String table, SQLColumn[] columns,
            SQLCondition condition) {
        StringBuilder result = new StringBuilder();
        result.append(update(table, columns));
        if (condition != null) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, condition));
        }
        return result.toString();
    }

    @Override
    public String update(String table, SQLColumn[] columns,
            SQLCondition[] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(update(table, columns));
        if (conditions != null && conditions.length > 0) {
            result.append(WHERE_SQL);
            result.append(toSQL(false, conditions));
        }
        return result.toString();
    }

    @Override
    public String update(String table, SQLColumn[] columns,
            SQLCondition[][] conditions) {
        StringBuilder result = new StringBuilder();
        result.append(update(table, columns));
        if (conditions != null) {
            String cond = toSQL(false, conditions);
            if (cond.length() > 0) {
                result.append(WHERE_SQL);
                result.append(cond);
            }
        }
        return result.toString();
    }

    @Override
    public String union(String[] selects, Integer orderBy) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < selects.length; i++) {
            result.append(selects[i]);
            if (i < selects.length - 1) {
                result.append(UNION);
            }
        }
        if (orderBy != null) {
            result.append(ORDER_BY_SQL);
            result.append(orderBy);
        }
        return result.toString();
    }
}
