package sk.r3n.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.sql.Column;
import sk.r3n.sql.ColumnSelect;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Join;
import sk.r3n.sql.JoinCriterion;
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.TableSelect;

public class PostgreSqlBuilder extends SqlBuilder {

    private static final Log LOG = LogFactory.getLog(PostgreSqlBuilder.class);

//    @Override
//    public String nextVal(Sequence sequence) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("nextval('").append(sequence.getName()).append("')");
//        return sb.toString();
//    }
//
//    @Override
//    public long nextVal(Connection connection, Sequence sequence) throws SQLException {
//        Statement statement = null;
//        ResultSet resultSet = null;
//
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT ").append(nextVal(sequence));
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug(sql);
//        }
//
//        long result;
//        try {
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(sql.toString());
//            resultSet.next();
//            result = resultSet.getLong(1);
//        } finally {
//            SqlUtil.close(resultSet);
//            SqlUtil.close(statement);
//        }
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("RESULT:" + Long.toString(result));
//        }
//        return result;
//    }
//
//    @Override
//    public Object executeUpdate(Connection connection, Query query) throws SQLException {
//        if (query.getQueryType() == Query.QueryType.SELECT) {
//            throw new IllegalArgumentException("Wrong query type!");
//        }
//
//        Object result = null;
//        params().clear();
//        String sql = toSql(query);
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug(sql);
//            LOG.debug(params());
//        }
//
//        switch (query.getQueryType()) {
//            case INSERT:
//                if (query.getReturning() != null) {
//                    result = executeUpdate(connection, sql, query.getReturning());
//                } else {
//                    executeUpdate(connection, sql);
//                }
//                break;
//            default:
//                executeUpdate(connection, sql);
//                break;
//        }
//
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("RESULT:" + result);
//        }
//        return result;
//    }
//
//    @Override
//    protected String toSelect(Query query) {
//        String sql;
//
//        if (query.getTable() instanceof TableSelect) {
//            if (query.getPagination()) {
//                sql = toPaginatedSubSelect(query);
//            } else {
//                sql = toSubSelect(query);
//            }
//        } else {
//            if (query.getPagination()) {
//                sql = toPaginatedSelect(query);
//            } else {
//                sql = toStandardSelect(query);
//            }
//        }
//
//        return sql;
//    }
//
//    @Override
//    protected String toInsert(Query query) {
//        StringBuilder sql = new StringBuilder();
//
//        if (query.getReturning() != null) {
//            sql.append(toInsertReturningValue(query));
//        } else {
//            sql.append("INSERT INTO ").append(query.getTable()).append(SPACE).append(LEFT_BRACE);
//            Column[] columns = query.getColumns();
//            for (int i = 0; i < columns.length; i++) {
//                sql.append(columns[i]);
//                if (i < columns.length - 1) {
//                    sql.append(COMMA).append(SPACE);
//                }
//            }
//            sql.append(RIGHT_BRACE).append(" VALUES ").append(LEFT_BRACE);
//            Object[] values = query.getValues();
//            for (int i = 0; i < columns.length; i++) {
//                if (values[i] instanceof Sequence) {
//                    sql.append(nextVal((Sequence) values[i]));
//                } else if (values[i] instanceof Column) {
//                    sql.append((Column) values[i]);
//                } else {
//                    sql.append(QUESTION_MARK);
//                    params().add(new SqlParam(columns[i].getDataType(), values[i]));
//                }
//                if (i < columns.length - 1) {
//                    sql.append(COMMA).append(SPACE);
//                }
//            }
//            sql.append(RIGHT_BRACE);
//        }
//
//        return sql.toString();
//    }
//
//    @Override
//    protected String toUpdate(Query query) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("UPDATE ").append(query.getTable().getName()).append(" SET ");
//        Column[] columns = query.getColumns();
//        Object[] values = query.getValues();
//        for (int i = 0; i < columns.length; i++) {
//            if (values[i] instanceof Sequence) {
//                sql.append(columns[i].getName()).append(EQUALS).append(nextVal((Sequence) values[i]));
//            } else if (values[i] instanceof Column) {
//                sql.append(columns[i].getName()).append(EQUALS).append(((Column) values[i]).getName());
//            } else {
//                sql.append(columns[i].getName()).append(EQUALS).append(QUESTION_MARK);
//                params().add(new SqlParam(columns[i].getDataType(), values[i]));
//            }
//            if (i < columns.length - 1) {
//                sql.append(COMMA).append(SPACE);
//            }
//        }
//        if (query.getCriteriaManager().isCriteria()) {
//            sql.append(SPACE).append("WHERE ");
//            sql.append(toSql(query.getCriteriaManager()));
//        }
//        return sql.toString();
//    }
//
//    @Override
//    protected String toDelete(Query query) {
//        StringBuilder sql = new StringBuilder();
//        sql.append("DELETE FROM ").append(query.getTable().getName()).append(SPACE);
//        if (query.getCriteriaManager().isCriteria()) {
//            sql.append("WHERE ");
//            sql.append(toSql(query.getCriteriaManager()));
//        }
//        return sql.toString();
//    }
//
//    private Object executeUpdate(Connection connection, String sql, Column returning) throws SQLException {
//        Object result;
//        PreparedStatement preparedStatement = null;
//        ResultSet resultSet = null;
//
//        try {
//            preparedStatement = connection.prepareStatement(sql);
//            setParams(connection, preparedStatement, params().toArray(new SqlParam[params().size()]));
//            resultSet = preparedStatement.executeQuery();
//            resultSet.next();
//            result = getColumn(resultSet, 1, returning, getTmpDir());
//        } finally {
//            SqlUtil.close(resultSet);
//            SqlUtil.close(preparedStatement);
//        }
//        return result;
//    }
//
//    private String toPaginatedSelect(Query query) {
//        StringBuilder sql = new StringBuilder();
//
//        sql.append("SELECT ");
//
//        if (query.getDistinct()) {
//            sql.append("DISTINCT ");
//        }
//
//        Column[] columns = query.getColumns();
//        for (int i = 0; i < columns.length; i++) {
//            sql.append(columns[i]);
//            if (i < columns.length - 1) {
//                sql.append(COMMA);
//            }
//            sql.append(SPACE);
//        }
//
//        sql.append("FROM ").append(query.getTable()).append(SPACE);
//
//        for (JoinCriterion joinCriterion : query.getJoinCriteria()) {
//            sql.append(joinCriterion.getJoin());
//            if (joinCriterion.getJoin() == Join.FULL) {
//                sql.append(" OUTER");
//            }
//            sql.append(" JOIN ").append(joinCriterion.getTable()).append(" ON ");
//            sql.append(toSql(joinCriterion.getCriteriaManager()));
//        }
//
//        if (query.getCriteriaManager().isCriteria()) {
//            sql.append(SPACE).append("WHERE ");
//            sql.append(toSql(query.getCriteriaManager()));
//        }
//
//        if (query.getGroupByColumns() != null) {
//            sql.append(SPACE).append("GROUP BY ");
//            columns = query.getGroupByColumns();
//            for (int i = 0; i < columns.length; i++) {
//                sql.append(columns[i]);
//                if (i < columns.length - 1) {
//                    sql.append(COMMA).append(SPACE);
//                }
//            }
//        }
//
//        if (!query.getOrderCriteria().isEmpty()) {
//            sql.append(SPACE).append("ORDER BY ");
//            for (int i = 0; i < query.getOrderCriteria().size(); i++) {
//                sql.append(query.getOrderCriteria().get(i).getColumn()).append(SPACE).append(query.getOrderCriteria().get(i).getOrder());
//                if (i < query.getOrderCriteria().size() - 1) {
//                    sql.append(COMMA);
//                }
//                sql.append(SPACE);
//            }
//        }
//
//        sql.append(" OFFSET ? LIMIT ?");
//        params().add(new SqlParam(DataType.INTEGER, query.getFirstRow()));
//        params().add(new SqlParam(DataType.INTEGER, query.getPageSize()));
//
//        return sql.toString();
//    }
//
//    private String toPaginatedSubSelect(Query query) {
//        realias(query);
//
//        StringBuilder sql = new StringBuilder();
//
//        sql.append("SELECT ");
//
//        if (query.getDistinct()) {
//            sql.append("DISTINCT ");
//        }
//
//        Column[] columns = query.getColumns();
//        for (int i = 0; i < columns.length; i++) {
//            Column column = columns[i];
//            if (column instanceof ColumnSelect) {
//                ColumnSelect innerSelect = (ColumnSelect) column;
//                sql.append(LEFT_BRACE);
//                sql.append(toSelect(innerSelect.getQuery()));
//                sql.append(RIGHT_BRACE);
//                sql.append(" AS ").append(column.getName());
//            } else {
//                sql.append(column);
//            }
//            if (i < columns.length - 1) {
//                sql.append(COMMA);
//            }
//            sql.append(SPACE);
//        }
//
//        sql.append("FROM ").append(LEFT_BRACE);
//        TableSelect tableSelect = (TableSelect) query.getTable();
//        sql.append(toSelect(tableSelect));
//        sql.append(RIGHT_BRACE);
//
//        if (query.getCriteriaManager().isCriteria()) {
//            sql.append(SPACE).append("WHERE ");
//            sql.append(toSql(query.getCriteriaManager()));
//        }
//
//        if (query.getGroupByColumns() != null) {
//            sql.append(SPACE).append("GROUP BY ");
//            columns = query.getGroupByColumns();
//            for (int i = 0; i < columns.length; i++) {
//                sql.append(columns[i]);
//                if (i < columns.length - 1) {
//                    sql.append(COMMA).append(SPACE);
//                }
//            }
//        }
//
//        if (!query.getOrderCriteria().isEmpty()) {
//            sql.append(SPACE).append("ORDER BY ");
//            for (int i = 0; i < query.getOrderCriteria().size(); i++) {
//                sql.append(query.getOrderCriteria().get(i).getColumn()).append(SPACE).append(query.getOrderCriteria().get(i).getOrder());
//                if (i < query.getOrderCriteria().size() - 1) {
//                    sql.append(COMMA);
//                }
//                sql.append(SPACE);
//            }
//        }
//
//        sql.append(" OFFSET ? LIMIT ?");
//        params().add(new SqlParam(DataType.INTEGER, query.getFirstRow()));
//        params().add(new SqlParam(DataType.INTEGER, query.getPageSize()));
//
//        return sql.toString();
//    }
//
//    private String toInsertReturningValue(Query query) {
//        StringBuilder sql = new StringBuilder();
//
//        sql.append("INSERT INTO ").append(query.getTable().getName()).append(SPACE).append(LEFT_BRACE);
//        Column[] columns = query.getColumns();
//        for (int i = 0; i < columns.length; i++) {
//            sql.append(columns[i].getName());
//            if (i < columns.length - 1) {
//                sql.append(COMMA).append(SPACE);
//            }
//        }
//        sql.append(RIGHT_BRACE).append(" VALUES ").append(LEFT_BRACE);
//        Object[] values = query.getValues();
//        for (int i = 0; i < columns.length; i++) {
//            if (values[i] instanceof Sequence) {
//                sql.append(nextVal((Sequence) values[i]));
//            } else if (values[i] instanceof Column) {
//                sql.append((Column) values[i]);
//            } else {
//                sql.append(QUESTION_MARK);
//                params().add(new SqlParam(columns[i].getDataType(), values[i]));
//            }
//            if (i < columns.length - 1) {
//                sql.append(COMMA).append(SPACE);
//            }
//        }
//        sql.append(RIGHT_BRACE).append(" RETURNING ").append(query.getReturning().getName());
//        return sql.toString();
//    }

    @Override
    public String nextVal(Sequence sequence) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long nextVal(Connection connection, Sequence sequence) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object executeUpdate(Connection connection, Query query) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String toSelect(Query query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String toInsert(Query query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String toUpdate(Query query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String toDelete(Query query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
