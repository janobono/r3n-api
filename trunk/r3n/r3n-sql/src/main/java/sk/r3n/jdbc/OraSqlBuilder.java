package sk.r3n.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
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
import sk.r3n.util.FileUtil;

public class OraSqlBuilder extends SqlBuilder {

    private static final Log LOG = LogFactory.getLog(OraSqlBuilder.class);

    @Override
    public String nextVal(Sequence sequence) {
        StringBuilder sb = new StringBuilder();
        sb.append(sequence.getName());
        sb.append(".");
        sb.append("NEXTVAL");
        return sb.toString();
    }

    @Override
    public long nextVal(Connection connection, Sequence sequence) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(nextVal(sequence)).append(" FROM DUAL");

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
    protected String toPaginatedSelect(Query query) {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT * FROM ").append(LEFT_BRACE).append("SELECT ");

        Column[] columns = query.getColumns();
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            if (column instanceof ColumnSelect) {
                sql.append(column.getName()).append(", ");
            } else {
                sql.append("col").append(i).append(", ");
            }
        }
        sql.append("ROWNUM rnm");

        sql.append(" FROM ").append(LEFT_BRACE).append("SELECT ");
        if (query.getDistinct()) {
            sql.append("DISTINCT ");
        }
        for (int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            if (column instanceof ColumnSelect) {
                ColumnSelect innerSelect = (ColumnSelect) column;
                sql.append(LEFT_BRACE);
                sql.append(toSelect(innerSelect.getQuery()));
                sql.append(RIGHT_BRACE);
                sql.append(" AS ").append(column.getName());
            } else {
                sql.append(column.nameWithAlias()).append(" AS col").append(i);
            }
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(" FROM ").append(query.getTable()).append(SPACE);

        for (JoinCriterion joinCriterion : query.getJoinCriteria()) {
            sql.append(joinCriterion.getJoin());
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

        sql.append(RIGHT_BRACE).append(" WHERE ROWNUM <= ? ").append(RIGHT_BRACE).append(" WHERE rnm >= ?");
        params().add(new SqlParam(DataType.INTEGER, query.getLastRow() + 1));
        params().add(new SqlParam(DataType.INTEGER, query.getFirstRow() + 1));

        return sql.toString();
    }

    @Override
    protected String toPaginatedSubSelect(Query query) {
        realias(query);

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT * FROM ").append(LEFT_BRACE).append("SELECT ");

        Column[] columns = query.getColumns();
        for (Column column : columns) {
            sql.append(column).append(", ");
        }
        sql.append("ROWNUM rnm");

        sql.append(" FROM ").append(LEFT_BRACE).append("SELECT ");
        if (query.getDistinct()) {
            sql.append("DISTINCT ");
        }
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
                sql.append(", ");
            }
        }

        sql.append(" FROM ").append(LEFT_BRACE);
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

        sql.append(RIGHT_BRACE).append(" WHERE ROWNUM <= ? ").append(RIGHT_BRACE).append(" WHERE rnm >= ?");
        params().add(new SqlParam(DataType.INTEGER, query.getLastRow() + 1));
        params().add(new SqlParam(DataType.INTEGER, query.getFirstRow() + 1));

        return sql.toString();
    }

    @Override
    protected String toInsertReturningValue(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("BEGIN INSERT INTO ").append(query.getTable()).append(SPACE).append(LEFT_BRACE);
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
        sql.append(RIGHT_BRACE).append(" RETURNING ").append(query.getReturning()).append(" INTO ?; END;");
        return sql.toString();
    }

    @Override
    protected Object executeInsert(Connection connection, String sql, Column[] columns, Column returning)
            throws SQLException {
        Object result = null;
        CallableStatement callableStatement = null;
        try {
            int index = params().size() + 1;

            callableStatement = connection.prepareCall(sql);
            SqlUtil.setParams(connection, callableStatement, params().toArray(new SqlParam[params().size()]));

            switch (returning.getDataType()) {
                case BOOLEAN:
                    callableStatement.registerOutParameter(index, Types.BOOLEAN);
                    break;
                case STRING:
                    callableStatement.registerOutParameter(index, Types.VARCHAR);
                    break;
                case SHORT:
                    callableStatement.registerOutParameter(index, Types.SMALLINT);
                    break;
                case INTEGER:
                    callableStatement.registerOutParameter(index, Types.INTEGER);
                    break;
                case LONG:
                    callableStatement.registerOutParameter(index, Types.BIGINT);
                    break;
                case BIG_DECIMAL:
                    callableStatement.registerOutParameter(index, Types.NUMERIC);
                    break;
                case DATE:
                    callableStatement.registerOutParameter(index, Types.DATE);
                    break;
                case TIME:
                    callableStatement.registerOutParameter(index, Types.TIME);
                    break;
                case TIME_STAMP:
                    callableStatement.registerOutParameter(index, Types.TIMESTAMP);
                    break;
                case BLOB:
                    callableStatement.registerOutParameter(index, Types.BLOB);
                    break;
            }

            callableStatement.execute();

            switch (returning.getDataType()) {
                case BOOLEAN:
                    result = callableStatement.getBoolean(index);
                    break;
                case STRING:
                    result = callableStatement.getString(index);
                    break;
                case SHORT:
                    result = callableStatement.getShort(index);
                    break;
                case INTEGER:
                    result = callableStatement.getInt(index);
                    break;
                case LONG:
                    result = callableStatement.getLong(index);
                    break;
                case BIG_DECIMAL:
                    result = callableStatement.getBigDecimal(index);
                    break;
                case DATE:
                    java.sql.Date date = callableStatement.getDate(index);
                    result = new Date(date.getTime());
                    break;
                case TIME:
                    java.sql.Time time = callableStatement.getTime(index);
                    result = new Date(time.getTime());
                    break;
                case TIME_STAMP:
                    java.sql.Timestamp timestamp = callableStatement.getTimestamp(index);
                    result = new Date(timestamp.getTime());
                    break;
                case BLOB:
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", getTmpDir());
                        Blob blob = callableStatement.getBlob(index);
                        FileUtil.streamToFile(blob.getBinaryStream(1, blob.length()), file);
                        result = file;
                    } catch (IOException e) {
                        if (file != null) {
                            file.delete();
                        }
                        throw new SQLException(e);
                    }
                    break;
            }
        } finally {
            SqlUtil.close(callableStatement);
        }
        return result;
    }

}
