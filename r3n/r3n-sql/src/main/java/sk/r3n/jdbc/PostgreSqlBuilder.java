package sk.r3n.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static sk.r3n.jdbc.SqlBuilder.LEFT_BRACE;
import sk.r3n.sql.Column;
import sk.r3n.sql.ColumnSelect;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Criteria;
import sk.r3n.sql.CriteriaManager;
import sk.r3n.sql.Criterion;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Join;
import sk.r3n.sql.JoinCriterion;
import sk.r3n.sql.OrderCriterion;
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.TableSelect;
import sk.r3n.util.FileUtil;

public class PostgreSqlBuilder extends SqlBuilder {

    private static final Log LOG = LogFactory.getLog(PostgreSqlBuilder.class);

    private List<InputStream> streams;

    private int sub;

    @Override
    public String nextVal(Sequence sequence) {
        StringBuilder sb = new StringBuilder();
        sb.append("nextval('").append(sequence.getName()).append("')");
        return sb.toString();
    }

    @Override
    public long nextVal(Connection connection, Sequence sequence) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(nextVal(sequence));

        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }

        long result;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql.toString());
            resultSet.next();
            result = resultSet.getLong(1);
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("RESULT:" + Long.toString(result));
        }
        return result;
    }

    @Override
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

        streams = new ArrayList<InputStream>();
        try {
            switch (query.getQueryType()) {
                case INSERT:
                    if (query.getReturning() != null) {
                        result = executeUpdate(connection, sql, query.getReturning());
                    } else {
                        executeUpdate(connection, sql);
                    }
                    break;
                default:
                    executeUpdate(connection, sql);
                    break;
            }
        } finally {
            for (InputStream stream : streams) {
                FileUtil.close(stream);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("RESULT:" + result);
        }
        return result;
    }

    @Override
    protected String toSelect(Query query) {
        String sql;
        sub = 0;

        if (query.getTable() instanceof TableSelect) {
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

    @Override
    protected String toInsert(Query query) {
        StringBuilder sql = new StringBuilder();

        if (query.getReturning() != null) {
            sql.append(toInsertReturningValue(query));
        } else {
            sql.append("INSERT INTO ").append(query.getTable().getName()).append(SPACE).append(LEFT_BRACE);
            Column[] columns = query.getColumns();
            for (int i = 0; i < columns.length; i++) {
                sql.append(columns[i].getName());
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
                    sql.append(((Column) values[i]).getName());
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

    @Override
    protected String toUpdate(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(query.getTable().getName()).append(" SET ");
        Column[] columns = query.getColumns();
        Object[] values = query.getValues();
        for (int i = 0; i < columns.length; i++) {
            if (values[i] instanceof Sequence) {
                sql.append(columns[i].getName()).append(EQUALS).append(nextVal((Sequence) values[i]));
            } else if (values[i] instanceof Column) {
                sql.append(columns[i].getName()).append(EQUALS).append(((Column) values[i]).getName());
            } else {
                sql.append(columns[i].getName()).append(EQUALS).append(QUESTION_MARK);
                params().add(new SqlParam(columns[i].getDataType(), values[i]));
            }
            if (i < columns.length - 1) {
                sql.append(COMMA).append(SPACE);
            }
        }
        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager(), true));
        }
        return sql.toString();
    }

    @Override
    protected String toDelete(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(query.getTable().getName()).append(SPACE);
        if (query.getCriteriaManager().isCriteria()) {
            sql.append("WHERE ");
            sql.append(toSql(query.getCriteriaManager(), true));
        }
        return sql.toString();
    }

    private Object executeUpdate(Connection connection, String sql, Column returning) throws SQLException {
        Object result;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            setParams(connection, preparedStatement, params().toArray(new SqlParam[params().size()]));
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = getColumn(resultSet, 1, returning, getTmpDir());
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);
        }
        return result;
    }

    private String toPaginatedSubSelect(Query query) {
        realias(query);

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
        sql.append(" AS sub").append(sub++);

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager(), false));
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

        sql.append(" OFFSET ? LIMIT ?");
        params().add(new SqlParam(DataType.INTEGER, query.getFirstRow()));
        params().add(new SqlParam(DataType.INTEGER, query.getPageSize()));

        return sql.toString();
    }

    private String toSubSelect(Query query) {
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
        sql.append(" AS sub").append(sub++);

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager(), false));
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

    private String toPaginatedSelect(Query query) {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT * FROM (SELECT ");

        if (query.getDistinct()) {
            sql.append("DISTINCT ");
        }

        Column[] columns = query.getColumns();
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(COMMA);
            }
            sql.append(SPACE);
        }

        sql.append("FROM ").append(query.getTable()).append(SPACE);

        for (JoinCriterion joinCriterion : query.getJoinCriteria()) {
            sql.append(joinCriterion.getJoin());
            if (joinCriterion.getJoin() == Join.FULL) {
                sql.append(" OUTER");
            }
            sql.append(" JOIN ").append(joinCriterion.getTable()).append(" ON ");
            sql.append(toSql(joinCriterion.getCriteriaManager(), false));
        }

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager(), false));
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

        sql.append(RIGHT_BRACE);
        sql.append(" AS sub").append(sub++);
        sql.append(" LIMIT ? OFFSET ?");
        params().add(new SqlParam(DataType.INTEGER, query.getPageSize()));
        params().add(new SqlParam(DataType.INTEGER, query.getFirstRow()));

        return sql.toString();
    }

    private String toStandardSelect(Query query) {
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
            sql.append(toSql(joinCriterion.getCriteriaManager(), false));
        }

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager(), false));
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
            sql.append(" AS sub").append(sub++);
        }

        return sql.toString();
    }

    private String toSql(CriteriaManager criteriaManager, boolean onlyName) {
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
                sql.append(toSql(criteria, onlyName));
                lastCriteria = criteria;
            }
        }
        return sql.toString();
    }

    private String toSql(Criteria criteria, boolean onlyName) {
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
                sql.append(toSql((Criterion) object, onlyName));
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
                    sql.append(toSql((Criteria) object, onlyName));
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

    private String toSql(Criterion criterion, boolean onlyName) {
        StringBuilder sql = new StringBuilder();
        if (criterion.getCondition() == Condition.DIRECT) {
            sql.append(criterion.getValue());
        } else {
            if (criterion.getValue() instanceof Query) {
                if (onlyName) {
                    sql.append(criterion.getColumn().getName());
                } else {
                    sql.append(criterion.getColumn());
                }
                sql.append(criterion.getCondition().condition());
                sql.append(LEFT_BRACE);
                sql.append(toSql((Query) criterion.getValue()));
                sql.append(RIGHT_BRACE);
            } else if (criterion.getValue() instanceof Column) {
                if (criterion.getRepresentation() == null) {
                    if (onlyName) {
                        sql.append(criterion.getColumn().getName());
                    } else {
                        sql.append(criterion.getColumn());
                    }
                    sql.append(criterion.getCondition().condition());
                    if (onlyName) {
                        sql.append(((Column) criterion.getValue()).getName());
                    } else {
                        sql.append(criterion.getValue());
                    }
                } else {
                    if (onlyName) {
                        sql.append(MessageFormat.format(criterion.getRepresentation(),
                                criterion.getColumn(), criterion.getCondition().condition(),
                                ((Column) criterion.getValue()).getName()));
                    } else {
                        sql.append(MessageFormat.format(criterion.getRepresentation(),
                                criterion.getColumn(), criterion.getCondition().condition(),
                                criterion.getValue().toString()));
                    }
                }
            } else {
                if (criterion.getRepresentation() == null) {
                    if (onlyName) {
                        sql.append(criterion.getColumn().getName());
                    } else {
                        sql.append(criterion.getColumn());
                    }
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
                    if (onlyName) {
                        sql.append(MessageFormat.format(criterion.getRepresentation(),
                                criterion.getColumn().getName(), criterion.getCondition().condition()));
                    } else {
                        sql.append(MessageFormat.format(criterion.getRepresentation(),
                                criterion.getColumn(), criterion.getCondition().condition()));
                    }
                    if (criterion.getValue() != null) {
                        params().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
                    }
                }
            }
        }
        return sql.toString();
    }

    private String toSelect(TableSelect tableSelect) {
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
            sql.append(toSql(joinCriterion.getCriteriaManager(), false));
        }

        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(toSql(query.getCriteriaManager(), false));
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

    private void realias(Query query) {
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

    private int realias(Map<Column, String> aliasMap, int lastAliasNum, Column[] columns, boolean values) {
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

    private void realias(Map<Column, String> aliasMap, Criteria criteria) {
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

    private void realiasValues(Map<Column, String> aliasMap, Criteria criteria) {
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

    private String toInsertReturningValue(Query query) {
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO ").append(query.getTable().getName()).append(SPACE).append(LEFT_BRACE);
        Column[] columns = query.getColumns();
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i].getName());
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
                sql.append(((Column) values[i]).getName());
            } else {
                sql.append(QUESTION_MARK);
                params().add(new SqlParam(columns[i].getDataType(), values[i]));
            }
            if (i < columns.length - 1) {
                sql.append(COMMA).append(SPACE);
            }
        }
        sql.append(RIGHT_BRACE).append(" RETURNING ").append(query.getReturning().getName());
        return sql.toString();
    }

    @Override
    protected Object getColumn(ResultSet resultSet, int index, Column column, File dir) throws SQLException {
        Object result = null;

        if (resultSet.getObject(index) != null) {
            switch (column.getDataType()) {
                case BLOB:
                    InputStream is = null;
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", dir);
                        is = resultSet.getBinaryStream(index);
                        FileUtil.streamToFile(is, file);
                        result = file;
                    } catch (IOException e) {
                        if (file != null) {
                            file.delete();
                        }
                        throw new SQLException(e);
                    } finally {
                        FileUtil.close(is);
                    }
                    break;
                default:
                    result = super.getColumn(resultSet, index, column, dir);
                    break;
            }
        }
        return result;
    }

    @Override
    protected void setParam(Connection connection, PreparedStatement preparedStatement, int index, SqlParam param) throws SQLException {
        if (param.getValue() != null) {
            switch (param.getDataType()) {
                case BLOB:
                    try {
                        File file = (File) param.getValue();
                        InputStream is = new FileInputStream(file);
                        streams.add(is);
                        preparedStatement.setBinaryStream(index, is, (int) file.length());
                    } catch (IOException e) {
                        throw new SQLException(e);
                    }
                    break;
                default:
                    super.setParam(connection, preparedStatement, index, param);
                    break;
            }
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }
}
