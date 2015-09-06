package sk.r3n.jdbc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.sql.Column;
import sk.r3n.sql.ColumnFunction;
import sk.r3n.sql.ColumnSelect;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Criteria;
import sk.r3n.sql.CriteriaManager;
import sk.r3n.sql.Criterion;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Join;
import sk.r3n.sql.JoinCriterion;
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;
import sk.r3n.util.FileUtil;

public class H2SqlBuilder extends SqlBuilder {

    private class BaseSql {

        private String toSql(Query.Select select) {
            StringBuilder sql = new StringBuilder();

            if (select.getCount()) {
                sql.append("SELECT COUNT(*) FROM ").append(LEFT_BRACE);
            }

            if (select.getPagination()) {
                sql.append("SELECT * FROM ").append(LEFT_BRACE);
            }

            sql.append("SELECT ");

            if (select.getDistinct()) {
                sql.append("DISTINCT ");
            }

            Column[] columns = select.getColumns();
            for (int i = 0; i < columns.length; i++) {
                Column column = columns[i];
                sql.append(toSql(column));
                sql.append(" AS col").append(i);
                if (i < columns.length - 1) {
                    sql.append(COMMA);
                }
                sql.append(SPACE);
            }

            sql.append("FROM ").append(toSql(select.getTable())).append(SPACE);

            for (JoinCriterion joinCriterion : select.getJoinCriteria()) {
                sql.append(SPACE).append(joinCriterion.getJoin());
                if (joinCriterion.getJoin() == Join.FULL) {
                    sql.append(" OUTER");
                }
                sql.append(" JOIN ").append(toSql(joinCriterion.getTable())).append(" ON ");
                sql.append(toSql(joinCriterion.getCriteriaManager()));
            }

            if (select.getCriteriaManager().isCriteria()) {
                sql.append(SPACE).append("WHERE ");
                sql.append(toSql(select.getCriteriaManager()));
            }

            if (select.getGroupByColumns() != null) {
                sql.append(SPACE).append("GROUP BY ");
                columns = select.getGroupByColumns();
                for (int i = 0; i < columns.length; i++) {
                    sql.append(toSql(columns[i]));
                    if (i < columns.length - 1) {
                        sql.append(COMMA).append(SPACE);
                    }
                }
            }

            if (select.getHavingCriterion() != null) {
                sql.append(SPACE).append("HAVING ");
                sql.append(toSql(select.getHavingCriterion()));
            }

            if (!select.getOrderCriteria().isEmpty()) {
                sql.append(SPACE).append("ORDER BY ");
                for (int i = 0; i < select.getOrderCriteria().size(); i++) {
                    sql.append(toSql(select.getOrderCriteria().get(i).getColumn())).append(SPACE).append(select.getOrderCriteria().get(i).getOrder());
                    if (i < select.getOrderCriteria().size() - 1) {
                        sql.append(COMMA);
                    }
                    sql.append(SPACE);
                }
            }

            if (select.getCount()) {
                sql.append(RIGHT_BRACE);
            }

            if (select.getPagination()) {
                sql.append(RIGHT_BRACE).append(" LIMIT ? OFFSET ?");
                params().add(new SqlParam(DataType.INTEGER, select.getPageSize()));
                params().add(new SqlParam(DataType.INTEGER, select.getFirstRow()));
            }
            return sql.toString();
        }

        private String toSql(CriteriaManager criteriaManager) {
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

        private String toSql(Criteria criteria) {
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

        private String toSql(Criterion criterion) {
            StringBuilder sql = new StringBuilder();
            if (criterion.getCondition() == Condition.DIRECT) {
                sql.append(criterion.getValue());
            } else {
                if (criterion.getValue() instanceof Query.Select) {
                    sql.append(toSql(criterion.getColumn()));
                    sql.append(criterion.getCondition().condition());
                    sql.append(LEFT_BRACE);
                    sql.append(toSelect((Query.Select) criterion.getValue()));
                    sql.append(RIGHT_BRACE);
                } else if (criterion.getValue() instanceof Column) {
                    if (criterion.getRepresentation() == null) {
                        sql.append(toSql(criterion.getColumn()));
                        sql.append(criterion.getCondition().condition());
                        sql.append(toSql((Column) criterion.getValue()));
                    } else {
                        sql.append(MessageFormat.format(criterion.getRepresentation(),
                                toSql(criterion.getColumn()), criterion.getCondition().condition(),
                                toSql((Column) criterion.getValue())));
                    }
                } else {
                    if (criterion.getRepresentation() == null) {
                        sql.append(toSql(criterion.getColumn()));
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
                                toSql(criterion.getColumn()), criterion.getCondition().condition()));
                        if (criterion.getValue() != null) {
                            params().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
                        }
                    }
                }
            }
            return sql.toString();
        }

        private String toSql(Table table) {
            StringBuilder result = new StringBuilder();
            result.append(table.getName()).append(SPACE).append(table.getAlias());
            return result.toString();
        }

        private String toSql(Column column) {
            StringBuilder result = new StringBuilder();

            if (column instanceof ColumnFunction) {
                ColumnFunction columnFunction = (ColumnFunction) column;
                if (columnFunction.getMembers() != null) {
                    List<Object> membersList = new ArrayList<Object>();
                    for (Column member : columnFunction.getMembers()) {
                        membersList.add(toSql(member));
                    }
                    result.append(MessageFormat.format(columnFunction.getName(), membersList.toArray(new Object[membersList.size()])));
                } else {
                    result.append(columnFunction.getName());
                }
            } else if (column instanceof ColumnSelect) {
                ColumnSelect columnSelect = (ColumnSelect) column;
                result.append(LEFT_BRACE);
                result.append(toSelect(columnSelect.getSelect()));
                result.append(RIGHT_BRACE);
            } else {
                result.append(column.getTable().getAlias()).append(DOT).append(column.getName());
            }
            return result.toString();
        }
    }

    private class UnionSql {

        private final Map<Column, Integer> indexMap = new HashMap<Column, Integer>();

        private String toSql(Query.Select select) {
            StringBuilder sql = new StringBuilder();

            if (select.getCount()) {
                sql.append("SELECT COUNT(*) FROM ").append(LEFT_BRACE);
            }

            if (select.getPagination()) {
                sql.append("SELECT * FROM ").append(LEFT_BRACE);
            }

            Query.Select[] subSelects = select.getSubSelects();
            for (Query.Select subSelect : subSelects) {
                int index = 0;
                for (Column column : subSelect.getColumns()) {
                    indexMap.put(column, index);
                    index++;
                }
            }

            sql.append("SELECT ");

            if (select.getDistinct()) {
                sql.append("DISTINCT ");
            }

            Column[] columns = select.getColumns();
            for (int i = 0; i < columns.length; i++) {
                if (indexMap.get(columns[i]) == null) {
                    createIndex(columns[i]);
                }
                sql.append("col").append(indexMap.get(columns[i]));
                if (i < columns.length - 1) {
                    sql.append(COMMA);
                }
                sql.append(SPACE);
            }

            sql.append("FROM ").append(LEFT_BRACE);

            for (int i = 0; i < subSelects.length; i++) {
                sql.append(LEFT_BRACE).append(toSelect(subSelects[i])).append(RIGHT_BRACE);
                if (i < subSelects.length - 1) {
                    sql.append(SPACE).append(select.getDataSetOperator().name().replaceAll("_", " ")).append(SPACE);
                }
            }

            sql.append(RIGHT_BRACE);

            if (select.getCriteriaManager().isCriteria()) {
                sql.append(SPACE).append("WHERE ");
                sql.append(toSql(select.getCriteriaManager()));
            }

            if (select.getGroupByColumns() != null) {
                sql.append(SPACE).append("GROUP BY ");
                columns = select.getGroupByColumns();
                for (int i = 0; i < columns.length; i++) {
                    sql.append(toSql(columns[i]));
                    if (i < columns.length - 1) {
                        sql.append(COMMA).append(SPACE);
                    }
                }
            }

            if (select.getHavingCriterion() != null) {
                sql.append(SPACE).append("HAVING ");
                sql.append(toSql(select.getHavingCriterion()));
            }

            if (!select.getOrderCriteria().isEmpty()) {
                sql.append(SPACE).append("ORDER BY ");
                for (int i = 0; i < select.getOrderCriteria().size(); i++) {
                    sql.append(toSql(select.getOrderCriteria().get(i).getColumn())).append(SPACE).append(select.getOrderCriteria().get(i).getOrder());
                    if (i < select.getOrderCriteria().size() - 1) {
                        sql.append(COMMA);
                    }
                    sql.append(SPACE);
                }
            }

            if (select.getCount()) {
                sql.append(RIGHT_BRACE);
            }

            if (select.getPagination()) {
                sql.append(RIGHT_BRACE).append(" LIMIT ? OFFSET ?");
                params().add(new SqlParam(DataType.INTEGER, select.getPageSize()));
                params().add(new SqlParam(DataType.INTEGER, select.getFirstRow()));
            }
            return sql.toString();
        }

        private void createIndex(Column column) {
            int max = 0;
            for (Integer val : indexMap.values()) {
                if (val > max) {
                    max = val;
                }
            }
            indexMap.put(column, max + 1);
        }

        private String toSql(CriteriaManager criteriaManager) {
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

        private String toSql(Criteria criteria) {
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

        private String toSql(Criterion criterion) {
            StringBuilder sql = new StringBuilder();
            if (criterion.getCondition() == Condition.DIRECT) {
                sql.append(criterion.getValue());
            } else {
                if (criterion.getValue() instanceof Query.Select) {
                    sql.append(toSql(criterion.getColumn()));
                    sql.append(criterion.getCondition().condition());
                    sql.append(LEFT_BRACE);
                    sql.append(toSql((Query.Select) criterion.getValue()));
                    sql.append(RIGHT_BRACE);
                } else if (criterion.getValue() instanceof Column) {
                    if (criterion.getRepresentation() == null) {
                        sql.append(toSql(criterion.getColumn()));
                        sql.append(criterion.getCondition().condition());
                        sql.append(toSql((Column) criterion.getValue()));
                    } else {
                        sql.append(MessageFormat.format(criterion.getRepresentation(),
                                toSql(criterion.getColumn()), criterion.getCondition().condition(),
                                toSql((Column) criterion.getValue())));
                    }
                } else {
                    if (criterion.getRepresentation() == null) {
                        sql.append(toSql(criterion.getColumn()));
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
                                toSql(criterion.getColumn()), criterion.getCondition().condition()));
                        if (criterion.getValue() != null) {
                            params().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
                        }
                    }
                }
            }
            return sql.toString();
        }

        private String toSql(Column column) {
            return "col" + indexMap.get(column);
        }

    }

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
    protected String toSelect(Query.Select select) {
        if (select.getSubSelects() == null) {
            return new BaseSql().toSql(select);
        }
        return new UnionSql().toSql(select);
    }

    @Override
    protected String toInsert(Query.Insert insert) {
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO ").append(insert.getTable().getName()).append(SPACE).append(LEFT_BRACE);
        Column[] columns = insert.getColumns();
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i].getName());
            if (i < columns.length - 1) {
                sql.append(COMMA).append(SPACE);
            }
        }
        sql.append(RIGHT_BRACE).append(" VALUES ").append(LEFT_BRACE);
        Object[] values = insert.getValues();
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

        return sql.toString();
    }

    @Override
    protected String toUpdate(Query.Update update) {
        StringBuilder sql = new StringBuilder();
        BaseSql baseSql = new BaseSql();

        sql.append("UPDATE ").append(baseSql.toSql(update.getTable())).append(" SET ");
        Column[] columns = update.getColumns();
        Object[] values = update.getValues();
        for (int i = 0; i < columns.length; i++) {
            sql.append(baseSql.toSql(columns[i])).append(EQUALS);
            if (values[i] instanceof Sequence) {
                sql.append(nextVal((Sequence) values[i]));
            } else if (values[i] instanceof Column) {
                sql.append(baseSql.toSql((Column) values[i]));
            } else {
                sql.append(QUESTION_MARK);
                params().add(new SqlParam(columns[i].getDataType(), values[i]));
            }
            if (i < columns.length - 1) {
                sql.append(COMMA).append(SPACE);
            }
        }
        if (update.getCriteriaManager().isCriteria()) {
            sql.append(SPACE).append("WHERE ");
            sql.append(baseSql.toSql(update.getCriteriaManager()));
        }
        return sql.toString();
    }

    @Override
    protected String toDelete(Query.Delete delete) {
        StringBuilder sql = new StringBuilder();
        BaseSql baseSql = new BaseSql();

        sql.append("DELETE FROM ").append(baseSql.toSql(delete.getTable()));
        if (delete.getCriteriaManager().isCriteria()) {
            sql.append(" WHERE ");
            sql.append(baseSql.toSql(delete.getCriteriaManager()));
        }
        return sql.toString();
    }

    @Override
    public Object executeUpdate(Connection connection, Query query) throws SQLException {
        if (query.getQueryType() == Query.QueryType.SELECT) {
            throw new IllegalArgumentException("Wrong query type!");
        }

        params().clear();
        String sql = toSql(query);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
            LOG.debug(params());
        }

        executeUpdate(connection, sql);

        return null;
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

}
