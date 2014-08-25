package sk.r3n.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Criteria;
import sk.r3n.sql.CriteriaManager;
import sk.r3n.sql.Criterion;
import sk.r3n.sql.Join;
import sk.r3n.sql.JoinCriterion;
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;
import sk.r3n.util.FileUtil;

public abstract class SqlBuilder {

    private static final Log LOG = LogFactory.getLog(SqlBuilder.class);

    protected static final char NEW_LINE = '\n';
    protected static final char SPACE = ' ';
    protected static final char LEFT_BRACE = '(';
    protected static final char RIGHT_BRACE = ')';
    protected static final char QUESTION_MARK = '?';
    protected static final char EQUALS = '=';
    protected static final char COMMA = ',';

    private List<SqlParam> params;

    private File tmpDir;

    public List<SqlParam> params() {
        if (params == null) {
            params = new ArrayList<SqlParam>();
        }
        return params;
    }

    public File getTmpDir() {
        if (tmpDir == null) {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            LOG.warn("Default tmp dir will be used - " + tmpDir.getAbsolutePath());
        }
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
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
            if (object instanceof Criterion && criteriaSequence) {
                sql.append(RIGHT_BRACE);
                criteriaSequence = false;
            }
            if (lastObject != null) {
                sql.append(SPACE);
                if (lastObject instanceof Criterion) {
                    sql.append(((Criterion) lastObject).getOperator());
                } else {
                    sql.append(((Criteria) lastObject).getOperator());
                }
                sql.append(SPACE);
            }
            if (object instanceof Criterion) {
                sql.append(toSql((Criterion) object));
            } else {
                if (!criteriaSequence) {
                    sql.append(LEFT_BRACE);
                    criteriaSequence = true;
                }
                sql.append(toSql((Criteria) object));
            }
            lastObject = object;
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
                        sql.append(QUESTION_MARK);
                        params().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
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
        StringBuilder sql = new StringBuilder();

        if (query.getPagination()) {
            sql.append(toPaginatedSelect(query));
        } else {
            if (query.getCount()) {
                sql.append("SELECT COUNT(*) FROM ").append(LEFT_BRACE);
            }

            sql.append("SELECT ");

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

            sql.append(NEW_LINE).append("FROM ").append(query.getTable()).append(SPACE);

            for (JoinCriterion joinCriterion : query.getJoinCriteria()) {
                sql.append(SPACE).append(NEW_LINE).append(joinCriterion.getJoin());
                if (joinCriterion.getJoin() == Join.FULL) {
                    sql.append(" OUTER");
                }
                sql.append(" JOIN ").append(joinCriterion.getTable()).append(" ON ");
                sql.append(toSql(joinCriterion.getCriteriaManager()));
            }

            if (query.getCriteriaManager().isCriteria()) {
                sql.append(SPACE).append(NEW_LINE).append("WHERE ").append(NEW_LINE);
                sql.append(toSql(query.getCriteriaManager()));
            }

            if (!query.getOrderCriteria().isEmpty()) {
                sql.append(SPACE).append(NEW_LINE).append("ORDER BY ");
                for (int i = 0; i < query.getOrderCriteria().size(); i++) {
                    sql.append(query.getOrderCriteria().get(i).getColumn()).append(SPACE).append(query.getOrderCriteria().get(i).getOrder());
                    if (i < query.getOrderCriteria().size() - 1) {
                        sql.append(COMMA);
                    }
                    sql.append(SPACE);
                }
            }

            if (query.getGroupByColumns() != null) {
                sql.append(SPACE).append(NEW_LINE).append("GROUP BY ");
                columns = query.getGroupByColumns();
                for (int i = 0; i < columns.length; i++) {
                    sql.append(columns[i]);
                    if (i < columns.length - 1) {
                        sql.append(COMMA).append(SPACE);
                    }
                }
            }

            if (query.getCount()) {
                sql.append(RIGHT_BRACE);
            }
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
            } else {
                sql.append(columns[i]).append(EQUALS).append(QUESTION_MARK);
                params().add(new SqlParam(columns[i].getDataType(), values[i]));
            }
            if (i < columns.length - 1) {
                sql.append(COMMA).append(SPACE);
            }
        }
        if (query.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append(NEW_LINE).append("WHERE ").append(NEW_LINE);
            sql.append(toSql(query.getCriteriaManager()));
        }
        return sql.toString();
    }

    protected String toDelete(Query query) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(query.getTable()).append(SPACE);
        if (query.getCriteriaManager().isCriteria()) {
            sql.append(NEW_LINE).append("WHERE ").append(NEW_LINE);
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
            setParams(connection, preparedStatement);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (query.getCount()) {
                    result.add(new Object[]{resultSet.getInt(1)});
                } else {
                    result.add(getRow(resultSet, query.getColumns()));
                }
            }
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("RESULT:" + result);
        }
        return result;
    }

    protected Object[] getRow(ResultSet resultSet, Column... columns) throws SQLException {
        Object[] result = new Object[columns.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = getColumn(resultSet, i + 1, columns[i]);
        }

        return result;
    }

    protected Object getColumn(ResultSet resultSet, int index, Column column) throws SQLException {
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
                    result = new Date(date.getTime());
                    break;
                case TIME:
                    java.sql.Time time = resultSet.getTime(index);
                    result = new Date(time.getTime());
                    break;
                case TIME_STAMP:
                    java.sql.Timestamp timestamp = resultSet.getTimestamp(index);
                    result = new Date(timestamp.getTime());
                    break;
                case BLOB:
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", getTmpDir());
                        Blob blob = resultSet.getBlob(index);
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

    protected void setParams(Connection connection, PreparedStatement preparedStatement) throws SQLException {
        int i = 1;
        for (SqlParam param : params()) {
            if (param != null) {
                switch (param.getDataType()) {
                    case BLOB:
                        Blob blob = connection.createBlob();
                        FileUtil.fileToStream((File) param.getValue(), blob.setBinaryStream(1));
                        preparedStatement.setBlob(i++, blob);
                        break;
                    case DATE:
                        preparedStatement.setDate(i++, new java.sql.Date(((java.util.Date) param.getValue()).getTime()));
                        break;
                    case TIME:
                        preparedStatement.setTime(i++, new java.sql.Time(((java.util.Date) param.getValue()).getTime()));
                        break;
                    case TIME_STAMP:
                        preparedStatement.setTimestamp(i++, new java.sql.Timestamp(((java.util.Date) param.getValue()).getTime()));
                        break;
                    default:
                        preparedStatement.setObject(i++, param.getValue());
                        break;
                }
            } else {
                preparedStatement.setNull(i++, Types.NULL);
            }
        }
    }

    protected void executeUpdate(Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            setParams(connection, preparedStatement);
            preparedStatement.executeUpdate();
        } finally {
            SqlUtil.close(preparedStatement);
        }
    }

    public abstract String nextVal(Sequence sequence);

    public abstract long nextVal(Connection connection, Sequence sequence) throws SQLException;

    protected abstract String toPaginatedSelect(Query query);

    protected abstract String toInsertReturningValue(Query query);

    protected abstract Object executeInsert(Connection connection, String sql, Column[] columns, Column returning)
            throws SQLException;

}
