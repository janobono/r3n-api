package sk.r3n.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Criteria;
import sk.r3n.sql.CriteriaManager;
import sk.r3n.sql.Criterion;
import sk.r3n.sql.ColumnSelect;
import sk.r3n.sql.Join;
import sk.r3n.sql.JoinCriterion;
import sk.r3n.sql.OrderCriterion;
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.TableSelect;

public abstract class SqlBuilder {

    private static final Log LOG = LogFactory.getLog(SqlBuilder.class);

    protected static final char SPACE = ' ';
    protected static final char LEFT_BRACE = '(';
    protected static final char RIGHT_BRACE = ')';
    protected static final char QUESTION_MARK = '?';
    protected static final char EQUALS = '=';
    protected static final char COMMA = ',';

    private File tmpDir;

    public File getTmpDir() {
        if (tmpDir == null) {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            LOG.debug("Default tmp dir will be used - " + tmpDir.getAbsolutePath());
        }
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    private List<SqlParam> params;

    public List<SqlParam> params() {
        if (params == null) {
            params = new ArrayList<SqlParam>();
        }
        return params;
    }

    public String toSql(Query query) {
        String result = null;
        switch (query.getQueryType()) {
            case SELECT:
                result = toSelect(query);
                break;
            case INSERT:
                result = toInsert(query);
                break;
            case UPDATE:
                result = toUpdate(query);
                break;
            case DELETE:
                result = toDelete(query);
                break;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(result);
        }
        return result;
    }

    public String toSql(CriteriaManager criteriaManager) {
        StringBuilder sql = new StringBuilder();
        Criteria lastCriteria = null;
        for (int i = 0; i < criteriaManager.getCriteriaList().size(); i++) {
            Criteria criteria = criteriaManager.getCriteriaList().get(i);
            if (criteria.isCriteria()) {
                if (lastCriteria != null) {
                    sql.append(SPACE);
                    sql.append(lastCriteria.getOperator());
                    sql.append(SPACE);
                }
                sql.append(toSql(criteria));
                lastCriteria = criteria;
            }
        }
        return sql.toString();
    }

    public String toSql(Criteria criteria) {
        StringBuilder sql = new StringBuilder();
        sql.append(LEFT_BRACE);
        Object lastObject = null;
        boolean criteriaSequence = false;
        for (Object object : criteria.getContent()) {
            if (object instanceof Criterion) {
                if (lastObject != null) {
                    if (object instanceof Criterion && criteriaSequence) {
                        sql.append(RIGHT_BRACE);
                        criteriaSequence = false;
                    }
                    sql.append(SPACE);
                    if (lastObject instanceof Criterion) {
                        sql.append(((Criterion) lastObject).getOperator());
                    } else {
                        sql.append(((Criteria) lastObject).getOperator());
                    }
                    sql.append(SPACE);
                }
                sql.append(toSql((Criterion) object));
                lastObject = object;
            } else {
                if (((Criteria) object).isCriteria()) {
                    if (lastObject != null) {
                        if (object instanceof Criterion && criteriaSequence) {
                            sql.append(RIGHT_BRACE);
                            criteriaSequence = false;
                        }
                        sql.append(SPACE);
                        if (lastObject instanceof Criterion) {
                            sql.append(((Criterion) lastObject).getOperator());
                        } else {
                            sql.append(((Criteria) lastObject).getOperator());
                        }
                        sql.append(SPACE);
                    }
                    if (!criteriaSequence) {
                        sql.append(LEFT_BRACE);
                        criteriaSequence = true;
                    }
                    sql.append(toSql((Criteria) object));
                    lastObject = object;
                }
            }
        }
        if (criteriaSequence) {
            sql.append(RIGHT_BRACE);
        }
        sql.append(RIGHT_BRACE);
        return sql.toString();
    }

    public String toSql(Criterion criterion) {
        StringBuilder sql = new StringBuilder();
        if (criterion.getCondition() == Condition.DIRECT) {
            sql.append(criterion.getValue());
        } else {
            if (criterion.getValue() instanceof Query) {
                sql.append(criterion.getColumn());
                sql.append(criterion.getCondition().condition());
                sql.append(LEFT_BRACE);
                sql.append(toSql((Query) criterion.getValue()));
                sql.append(RIGHT_BRACE);
            } else if (criterion.getValue() instanceof Column) {
                if (criterion.getRepresentation() == null) {
                    sql.append(criterion.getColumn());
                    sql.append(criterion.getCondition().condition());
                    sql.append(criterion.getValue());
                } else {
                    sql.append(MessageFormat.format(criterion.getRepresentation(),
                            criterion.getColumn(), criterion.getCondition().condition(),
                            criterion.getValue().toString()));
                }
            } else {
                if (criterion.getRepresentation() == null) {
                    sql.append(criterion.getColumn());
                    sql.append(criterion.getCondition().condition());
                    if (criterion.getValue() != null) {
                        if (criterion.getValue() instanceof List<?> || criterion.getValue() instanceof Object[]) {
                            Object[] array;
                            if (criterion.getValue() instanceof List<?>) {
                                array = ((List<?>) criterion.getValue()).toArray();
                            } else {
                                array = (Object[]) criterion.getValue();
                            }
                            sql.append(LEFT_BRACE);
                            for (int i = 0; i < array.length; i++) {
                                sql.append(QUESTION_MARK);
                                params().add(new SqlParam(criterion.getColumn().getDataType(), array[i]));
                                if (i < array.length - 1) {
                                    sql.append(COMMA);
                                }
                            }
                            sql.append(RIGHT_BRACE);
                        } else {
                            sql.append(QUESTION_MARK);
                            params().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
                        }
                    }
                } else {
                    sql.append(MessageFormat.format(criterion.getRepresentation(),
                            criterion.getColumn(), criterion.getCondition().condition()));
                    if (criterion.getValue() != null) {
                        params().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
                    }
                }
            }
        }
        return sql.toString();
    }

    protected String toSelect(Query query) {
        if (query.getPagination() && query.getCount()) {
            throw new IllegalArgumentException("Pagination and count can't be in one query!");
        }
        String sql;

        if (query.getTable() instanceof TableSelect) {
            TableSelect tableSelect = (TableSelect) query.getTable();
            if (tableSelect.getQuery().getQueryType() != Query.QueryType.SELECT
                    || tableSelect.getQuery().getPagination()
                    || tableSelect.getQuery().getCount()) {
                throw new IllegalArgumentException("Wrong sub query for select!");
            }
            if (query.getPagination()) {
                sql = toPaginatedSubSelect(query);
            } else {
                sql = toSubSelect(query);
            }
        } else {
            if (query.getPagination()) {
                sql = toPaginatedSelect(query);
            } else {
                sql = toStandardSelect(query);
            }
        }

        return sql;
    }

    protected String toSubSelect(Query query) {
        realias(query);

        StringBuilder sql = new StringBuilder();

        if (query.getCount()) {
            sql.append("SELECT COUNT(*) FROM ").append(LEFT_BRACE);
        }

        sql.append("SELECT ");

        if (query.getDistinct()) {
            sql.append("DISTINCT ");
        }

        Column[] columns = query.getColumns();
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            if (column instanceof ColumnSelect) {
                ColumnSelect innerSelect = (ColumnSelect) column;
                sql.append(LEFT_BRACE);
                sql.append(toSelect(innerSelect.getQuery()));
                sql.append(RIGHT_BRACE);
                sql.append(" AS ").append(column.getName());
            } else {
                sql.append(column);
            }
            if (i < columns.length - 1) {
                sql.append(COMMA);
            }
            sql.append(SPACE);
        }

        sql.append("FROM ").append(LEFT_BRACE);
        TableSelect tableSelect = (TableSelect) query.getTable();
        sql.append(toSelect(tableSelect));
        sql.append(RIGHT_BRACE);

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager()));
        }

        if (query.getGroupByColumns() != null) {
            sql.append(SPACE).append("GROUP BY ");
            columns = query.getGroupByColumns();
            for (int i = 0; i < columns.length; i++) {
                sql.append(columns[i]);
                if (i < columns.length - 1) {
                    sql.append(COMMA).append(SPACE);
                }
            }
        }

        if (!query.getOrderCriteria().isEmpty()) {
            sql.append(SPACE).append("ORDER BY ");
            for (int i = 0; i < query.getOrderCriteria().size(); i++) {
                sql.append(query.getOrderCriteria().get(i).getColumn()).append(SPACE).append(query.getOrderCriteria().get(i).getOrder());
                if (i < query.getOrderCriteria().size() - 1) {
                    sql.append(COMMA);
                }
                sql.append(SPACE);
            }
        }

        if (query.getCount()) {
            sql.append(RIGHT_BRACE);
        }

        return sql.toString();
    }

    protected void realias(Query query) {
        TableSelect tableSelect = (TableSelect) query.getTable();

        Map<Column, String> aliasMap = new HashMap<Column, String>();
        int lastAliasNum = 0;

        lastAliasNum = realias(aliasMap, lastAliasNum, tableSelect.getQuery().getColumns(), false);
        realias(aliasMap, lastAliasNum, query.getColumns(), true);

        if (query.getCriteriaManager().isCriteria()) {
            for (int i = 0; i < query.getCriteriaManager().getCriteriaList().size(); i++) {
                Criteria criteria = query.getCriteriaManager().getCriteriaList().get(i);
                if (criteria.isCriteria()) {
                    realias(aliasMap, criteria);
                }
            }
        }

        if (query.getGroupByColumns() != null) {
            Column[] columns = query.getGroupByColumns();
            for (Column column : columns) {
                if (!(column instanceof ColumnSelect)) {
                    column.setAlias(aliasMap.get(column));
                }
            }
        }

        if (!query.getOrderCriteria().isEmpty()) {
            for (OrderCriterion orderCriterion : query.getOrderCriteria()) {
                orderCriterion.getColumn().setAlias(aliasMap.get(orderCriterion.getColumn()));
            }
        }
    }

    protected int realias(Map<Column, String> aliasMap, int lastAliasNum, Column[] columns, boolean values) {
        for (Column column : columns) {
            if (column instanceof ColumnSelect) {
                ColumnSelect columnSelect = (ColumnSelect) column;
                if (columnSelect.getQuery().getCriteriaManager().isCriteria()) {
                    for (int i = 0; i < columnSelect.getQuery().getCriteriaManager().getCriteriaList().size(); i++) {
                        Criteria c = columnSelect.getQuery().getCriteriaManager().getCriteriaList().get(i);
                        if (c.isCriteria()) {
                            realiasValues(aliasMap, c);
                        }
                    }
                }
            } else {
                if (!aliasMap.containsKey(column)) {
                    aliasMap.put(column, "icol" + lastAliasNum++);
                }
                column.setAlias(aliasMap.get(column));
                column.setAlias(aliasMap.get(column));
            }
        }
        return lastAliasNum;
    }

    protected void realias(Map<Column, String> aliasMap, Criteria criteria) {
        for (Object object : criteria.getContent()) {
            if (object instanceof Criterion) {
                Criterion criterion = (Criterion) object;
                criterion.getColumn().setAlias(aliasMap.get(criterion.getColumn()));
                if (criterion.getValue() != null) {
                    if (criterion.getValue() instanceof Query) {
                        Query query = (Query) criterion.getValue();
                        if (query.getCriteriaManager().isCriteria()) {
                            for (int i = 0; i < query.getCriteriaManager().getCriteriaList().size(); i++) {
                                Criteria c = query.getCriteriaManager().getCriteriaList().get(i);
                                if (c.isCriteria()) {
                                    realiasValues(aliasMap, c);
                                }
                            }
                        }
                    } else if (criterion.getValue() instanceof Column) {
                        Column column = (Column) criterion.getValue();
                        column.setAlias(aliasMap.get(column));
                    }
                } else {
                    realias(aliasMap, (Criteria) object);
                }
            }
        }
    }

    protected void realiasValues(Map<Column, String> aliasMap, Criteria criteria) {
        for (Object object : criteria.getContent()) {
            if (object instanceof Criterion) {
                Criterion criterion = (Criterion) object;
                if (criterion.getValue() != null) {
                    if (criterion.getValue() instanceof Column) {
                        Column column = (Column) criterion.getValue();
                        column.setAlias(aliasMap.get(column));
                    }
                } else {
                    realiasValues(aliasMap, (Criteria) object);
                }
            }
        }
    }

    protected String toSelect(TableSelect tableSelect) {
        Query query = tableSelect.getQuery();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        if (query.getDistinct()) {
            sql.append("DISTINCT ");
        }

        Column[] columns = query.getColumns();
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            if (column instanceof ColumnSelect) {
                ColumnSelect innerSelect = (ColumnSelect) column;
                sql.append(LEFT_BRACE);
                sql.append(toSelect(innerSelect.getQuery()));
                sql.append(RIGHT_BRACE);
                sql.append(" AS ").append(column.getName());
            } else {
                sql.append(column.nameWithAlias()).append(" AS ").append(column);
            }
            if (i < columns.length - 1) {
                sql.append(COMMA);
            }
            sql.append(SPACE);
        }

        sql.append("FROM ").append(query.getTable()).append(SPACE);

        for (JoinCriterion joinCriterion : query.getJoinCriteria()) {
            sql.append(SPACE).append(joinCriterion.getJoin());
            if (joinCriterion.getJoin() == Join.FULL) {
                sql.append(" OUTER");
            }
            sql.append(" JOIN ").append(joinCriterion.getTable()).append(" ON ");
            sql.append(toSql(joinCriterion.getCriteriaManager()));
        }

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager()));
        }

        if (query.getGroupByColumns() != null) {
            sql.append(SPACE).append("GROUP BY ");
            columns = query.getGroupByColumns();
            for (int i = 0; i < columns.length; i++) {
                sql.append(columns[i]);
                if (i < columns.length - 1) {
                    sql.append(COMMA).append(SPACE);
                }
            }
        }

        if (!query.getOrderCriteria().isEmpty()) {
            sql.append(SPACE).append("ORDER BY ");
            for (int i = 0; i < query.getOrderCriteria().size(); i++) {
                sql.append(query.getOrderCriteria().get(i).getColumn()).append(SPACE).append(query.getOrderCriteria().get(i).getOrder());
                if (i < query.getOrderCriteria().size() - 1) {
                    sql.append(COMMA);
                }
                sql.append(SPACE);
            }
        }
        return sql.toString();
    }

    protected String toStandardSelect(Query query) {
        StringBuilder sql = new StringBuilder();

        if (query.getCount()) {
            sql.append("SELECT COUNT(*) FROM ").append(LEFT_BRACE);
        }

        sql.append("SELECT ");

        if (query.getDistinct()) {
            sql.append("DISTINCT ");
        }

        Column[] columns = query.getColumns();
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            if (column instanceof ColumnSelect) {
                ColumnSelect innerSelect = (ColumnSelect) column;
                sql.append(LEFT_BRACE);
                sql.append(toSelect(innerSelect.getQuery()));
                sql.append(RIGHT_BRACE);
                sql.append(" AS ").append(column.getName());
            } else {
                sql.append(column);
            }
            if (i < columns.length - 1) {
                sql.append(COMMA);
            }
            sql.append(SPACE);
        }

        sql.append("FROM ").append(query.getTable()).append(SPACE);

        for (JoinCriterion joinCriterion : query.getJoinCriteria()) {
            sql.append(SPACE).append(joinCriterion.getJoin());
            if (joinCriterion.getJoin() == Join.FULL) {
                sql.append(" OUTER");
            }
            sql.append(" JOIN ").append(joinCriterion.getTable()).append(" ON ");
            sql.append(toSql(joinCriterion.getCriteriaManager()));
        }

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager()));
        }

        if (query.getGroupByColumns() != null) {
            sql.append(SPACE).append("GROUP BY ");
            columns = query.getGroupByColumns();
            for (int i = 0; i < columns.length; i++) {
                sql.append(columns[i]);
                if (i < columns.length - 1) {
                    sql.append(COMMA).append(SPACE);
                }
            }
        }

        if (!query.getOrderCriteria().isEmpty()) {
            sql.append(SPACE).append("ORDER BY ");
            for (int i = 0; i < query.getOrderCriteria().size(); i++) {
                sql.append(query.getOrderCriteria().get(i).getColumn()).append(SPACE).append(query.getOrderCriteria().get(i).getOrder());
                if (i < query.getOrderCriteria().size() - 1) {
                    sql.append(COMMA);
                }
                sql.append(SPACE);
            }
        }

        if (query.getCount()) {
            sql.append(RIGHT_BRACE);
        }

        return sql.toString();
    }

    protected String toInsert(Query query) {
        StringBuilder sql = new StringBuilder();

        if (query.getReturning() != null) {
            sql.append(toInsertReturningValue(query));
        } else {
            sql.append("INSERT INTO ").append(query.getTable()).append(SPACE).append(LEFT_BRACE);
            Column[] columns = query.getColumns();
            for (int i = 0; i < columns.length; i++) {
                sql.append(columns[i]);
                if (i < columns.length - 1) {
                    sql.append(COMMA).append(SPACE);
                }
            }
            sql.append(RIGHT_BRACE).append(" VALUES ").append(LEFT_BRACE);
            Object[] values = query.getValues();
            for (int i = 0; i < columns.length; i++) {
                if (values[i] instanceof Sequence) {
                    sql.append(nextVal((Sequence) values[i]));
                } else if (values[i] instanceof Column) {
                    sql.append((Column) values[i]);
                } else {
                    sql.append(QUESTION_MARK);
                    params().add(new SqlParam(columns[i].getDataType(), values[i]));
                }
                if (i < columns.length - 1) {
                    sql.append(COMMA).append(SPACE);
                }
            }
            sql.append(RIGHT_BRACE);
        }

        return sql.toString();
    }

    protected String toUpdate(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(query.getTable()).append(" SET ");
        Column[] columns = query.getColumns();
        Object[] values = query.getValues();
        for (int i = 0; i < columns.length; i++) {
            if (values[i] instanceof Sequence) {
                sql.append(columns[i]).append(EQUALS).append(nextVal((Sequence) values[i]));
            } else if (values[i] instanceof Column) {
                sql.append(columns[i]).append(EQUALS).append((Column) values[i]);
            } else {
                sql.append(columns[i]).append(EQUALS).append(QUESTION_MARK);
                params().add(new SqlParam(columns[i].getDataType(), values[i]));
            }
            if (i < columns.length - 1) {
                sql.append(COMMA).append(SPACE);
            }
        }
        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager()));
        }
        return sql.toString();
    }

    protected String toDelete(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(query.getTable()).append(SPACE);
        if (query.getCriteriaManager().isCriteria()) {
            sql.append("WHERE ");
            sql.append(toSql(query.getCriteriaManager()));
        }
        return sql.toString();
    }

    public List<Object[]> executeQuery(Connection connection, Query query) throws SQLException {
        if (query.getQueryType() != Query.QueryType.SELECT) {
            throw new IllegalArgumentException("Wrong query type!");
        }

        params().clear();
        String sql = toSql(query);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
            LOG.debug(params());
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Object[]> result = new ArrayList<Object[]>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            SqlUtil.setParams(connection, preparedStatement, params().toArray(new SqlParam[params().size()]));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Object[] row;
                if (query.getCount()) {
                    row = new Object[]{resultSet.getInt(1)};
                } else {
                    row = getRow(resultSet, query.getColumns());
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ROW:" + Arrays.toString(row));
                }
                result.add(row);
            }
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("RESULT SIZE:" + result.size());
        }
        return result;
    }

    protected Object[] getRow(ResultSet resultSet, Column... columns) throws SQLException {
        Object[] result = new Object[columns.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = SqlUtil.getColumn(resultSet, i + 1, columns[i], getTmpDir());
        }

        return result;
    }

    public Object executeUpdate(Connection connection, Query query) throws SQLException {
        if (query.getQueryType() == Query.QueryType.SELECT) {
            throw new IllegalArgumentException("Wrong query type!");
        }

        Object result = null;
        params().clear();
        String sql = toSql(query);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
            LOG.debug(params());
        }

        switch (query.getQueryType()) {
            case INSERT:
                if (query.getReturning() != null) {
                    result = executeInsert(connection, sql, query.getColumns(), query.getReturning());
                } else {
                    executeUpdate(connection, sql);
                }
                break;
            default:
                executeUpdate(connection, sql);
                break;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("RESULT:" + result);
        }
        return result;
    }

    protected void executeUpdate(Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            SqlUtil.setParams(connection, preparedStatement, params().toArray(new SqlParam[params().size()]));
            preparedStatement.executeUpdate();
        } finally {
            SqlUtil.close(preparedStatement);
        }
    }

    public abstract String nextVal(Sequence sequence);

    public abstract long nextVal(Connection connection, Sequence sequence) throws SQLException;

    protected abstract String toPaginatedSelect(Query query);

    protected abstract String toPaginatedSubSelect(Query query);

    protected abstract String toInsertReturningValue(Query query);

    protected abstract Object executeInsert(Connection connection, String sql, Column[] columns, Column returning)
            throws SQLException;

}
