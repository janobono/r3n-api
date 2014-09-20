package sk.r3n.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static sk.r3n.jdbc.SqlBuilder.COMMA;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Join;
import sk.r3n.sql.JoinCriterion;
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;

public class H2SqlBuilder extends SqlBuilder {

    private static final Log LOG = LogFactory.getLog(H2SqlBuilder.class);

    @Override
    public String nextVal(Sequence sequence) {
        StringBuilder sb = new StringBuilder();
        sb.append("NEXTVAL('").append(sequence.getName()).append("')");
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
    protected String toPaginatedSelect(Query query) {
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

        sql.append(NEW_LINE).append("FROM ").append(query.getTable()).append(SPACE);

        for (JoinCriterion joinCriterion : query.getJoinCriteria()) {
            sql.append(NEW_LINE).append(joinCriterion.getJoin());
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

        sql.append(") LIMIT ? OFFSET ?");
        params().add(new SqlParam(DataType.INTEGER, query.getPageSize()));
        params().add(new SqlParam(DataType.INTEGER, query.getFirstRow()));

        return sql.toString();
    }

    @Override
    protected String toInsert(Query query) {
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
            } else {
                sql.append(QUESTION_MARK);
                params().add(new SqlParam(columns[i].getDataType(), values[i]));
            }
            if (i < columns.length - 1) {
                sql.append(COMMA).append(SPACE);
            }
        }
        sql.append(RIGHT_BRACE);

        return sql.toString();
    }

    @Override
    protected String toInsertReturningValue(Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Object executeInsert(Connection connection, String sql, Column[] columns, Column returning) throws SQLException {
        Object result;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParams(connection, preparedStatement);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            result = getColumn(resultSet, 1, returning);
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);
        }
        return result;
    }

}
