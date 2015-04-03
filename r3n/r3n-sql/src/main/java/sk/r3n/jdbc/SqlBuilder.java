package sk.r3n.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.sql.Column;
import sk.r3n.sql.ColumnSelect;
import sk.r3n.sql.Criteria;
import sk.r3n.sql.Criterion;
import sk.r3n.sql.OrderCriterion;
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.TableSelect;
import sk.r3n.util.FileUtil;

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

    public abstract String nextVal(Sequence sequence);

    public abstract long nextVal(Connection connection, Sequence sequence) throws SQLException;

    public String toSql(Query query) {
        String result = null;
        switch (query.getQueryType()) {
            case SELECT:
                if (query.getPagination() && query.getCount()) {
                    throw new IllegalArgumentException("Pagination and count can't be in one query!");
                }
                if (query.getTable() instanceof TableSelect) {
                    TableSelect tableSelect = (TableSelect) query.getTable();
                    if (tableSelect.getQuery().getQueryType() != Query.QueryType.SELECT
                            || tableSelect.getQuery().getPagination()
                            || tableSelect.getQuery().getCount()) {
                        throw new IllegalArgumentException("Wrong sub query for select!");
                    }
                }
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
            setParams(connection, preparedStatement, params().toArray(new SqlParam[params().size()]));
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

    public abstract Object executeUpdate(Connection connection, Query query) throws SQLException;

    protected abstract String toSelect(Query query);

    protected abstract String toInsert(Query query);

    protected abstract String toUpdate(Query query);

    protected abstract String toDelete(Query query);

    protected Object[] getRow(ResultSet resultSet, Column... columns) throws SQLException {
        Object[] result = new Object[columns.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = getColumn(resultSet, i + 1, columns[i], getTmpDir());
        }

        return result;
    }

    protected Object getColumn(ResultSet resultSet, int index, Column column, File dir) throws SQLException {
        Object result = null;

        if (resultSet.getObject(index) != null) {
            switch (column.getDataType()) {
                case BOOLEAN:
                    result = resultSet.getBoolean(index);
                    break;
                case STRING:
                    result = resultSet.getString(index);
                    break;
                case SHORT:
                    result = resultSet.getShort(index);
                    break;
                case INTEGER:
                    result = resultSet.getInt(index);
                    break;
                case LONG:
                    result = resultSet.getLong(index);
                    break;
                case BIG_DECIMAL:
                    result = resultSet.getBigDecimal(index);
                    break;
                case DATE:
                    java.sql.Date date = resultSet.getDate(index);
                    result = new java.util.Date(date.getTime());
                    break;
                case TIME:
                    java.sql.Time time = resultSet.getTime(index);
                    result = new java.util.Date(time.getTime());
                    break;
                case TIME_STAMP:
                    java.sql.Timestamp timestamp = resultSet.getTimestamp(index);
                    result = new java.util.Date(timestamp.getTime());
                    break;
                case BLOB:
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", dir);
                        Blob blob = resultSet.getBlob(index);
                        if (blob.length() > 0) {
                            FileUtil.streamToFile(blob.getBinaryStream(1, blob.length()), file);
                        }
                        result = file;
                    } catch (IOException e) {
                        if (file != null) {
                            file.delete();
                        }
                        throw new SQLException(e);
                    }
                    break;
            }
        }
        return result;
    }

    protected void setParams(Connection connection, PreparedStatement preparedStatement, SqlParam[] params) throws SQLException {
        int i = 1;
        for (SqlParam param : params) {
            setParam(connection, preparedStatement, i++, param);
        }
    }

    protected void setParam(Connection connection, PreparedStatement preparedStatement, int index, SqlParam param) throws SQLException {
        if (param.getValue() != null) {
            switch (param.getDataType()) {
                case BLOB:
                    Blob blob = connection.createBlob();
                    FileUtil.fileToStream((File) param.getValue(), blob.setBinaryStream(1));
                    preparedStatement.setBlob(index, blob);
                    break;
                case DATE:
                    preparedStatement.setDate(index, new java.sql.Date(((java.util.Date) param.getValue()).getTime()));
                    break;
                case TIME:
                    preparedStatement.setTime(index, new java.sql.Time(((java.util.Date) param.getValue()).getTime()));
                    break;
                case TIME_STAMP:
                    preparedStatement.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) param.getValue()).getTime()));
                    break;
                default:
                    preparedStatement.setObject(index, param.getValue());
                    break;
            }
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    protected void executeUpdate(Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            setParams(connection, preparedStatement, params().toArray(new SqlParam[params().size()]));
            preparedStatement.executeUpdate();
        } finally {
            SqlUtil.close(preparedStatement);
        }
    }
 
}
