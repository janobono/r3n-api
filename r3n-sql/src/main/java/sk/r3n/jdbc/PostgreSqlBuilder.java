/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.r3n.sql.Column;
import sk.r3n.sql.ColumnFunction;
import sk.r3n.sql.ColumnSelect;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Criteria;
import sk.r3n.sql.CriteriaContent;
import sk.r3n.sql.CriteriaManager;
import sk.r3n.sql.Criterion;
import sk.r3n.sql.DataType;
import sk.r3n.sql.OrderCriterion;
import sk.r3n.sql.Query.Delete;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Query.Update;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;
import sk.r3n.util.FileUtil;

public class PostgreSqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = Logger.getLogger(PostgreSqlBuilder.class.getCanonicalName());

    @Override
    public QueryResult nextVal(Sequence sequence) {
        QueryResult result = new QueryResult();

        StringBuilder sw = new StringBuilder();
        sw.append("select ").append(sequenceSQL(sequence));

        result.setQuery(sw.toString());
        return result;
    }

    private String sequenceSQL(Sequence sequence) {
        StringBuilder sw = new StringBuilder();
        sw.append("nextval('").append(sequence.getName()).append("')");
        return sw.toString();
    }

    @Override
    public long nextVal(Connection connection, Sequence sequence) throws SQLException {
        long result;

        QueryResult queryResult = nextVal(sequence);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(queryResult.toString());
        }

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryResult.getQuery());
            resultSet.next();
            result = resultSet.getLong(1);
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "RESULT:{0}", Long.toString(result));
        }
        return result;
    }

    @Override
    public QueryResult select(Select select) {
        QueryResult result = new QueryResult();
        StringBuilder sw = new StringBuilder();

        if (select.getSubSelects() != null && select.getSubSelects().length > 0) {
            Map<String, Integer> indexMap = new HashMap<>();

            if (select.getCount()) {
                sw.append("select count(*) from (");
            }

            if (select.getPagination()) {
                sw.append("select * from (");
            }

            for (Select subSelect : select.getSubSelects()) {
                int index = 0;
                for (Column column : subSelect.getColumns()) {
                    indexMap.put(column.getColumnId(), index);
                    index++;
                }
            }

            sw.append("select ");

            if (select.getDistinct()) {
                sw.append("distinct ");
            }

            for (int i = 0; i < select.getColumns().length; i++) {
                Column column = select.getColumns()[i];
                indexMap.put(column.getColumnId(), i);
                sw.append("col").append(indexMap.get(column.getColumnId()));
                if (i < select.getColumns().length - 1) {
                    sw.append(",");
                }
                sw.append(" ");
            }

            for (int i = 0; i < select.getColumns().length; i++) {
                Column column = select.getColumns()[i];
                indexMap.put(column.getColumnId(), i);
                sw.append("col").append(indexMap.get(column.getColumnId()));
                if (i < select.getColumns().length - 1) {
                    sw.append(",");
                }
                sw.append(" ");
            }

            sw.append(" from (");

            for (int i = 0; i < select.getSubSelects().length; i++) {
                Select subSelect = select.getSubSelects()[i];
                sw.append(selectSQL(subSelect, result));
                if (i < select.getSubSelects().length - 1) {
                    sw.append(" ").append(select.getDataSetOperator().name().replaceAll("_", " ")).append(" ");
                }
            }

            sw.append(") as union_result ");

            if (select.getCriteriaManager().isCriteria()) {
                sw.append(" where ").append(criteriaManagerSQL(false, select.getCriteriaManager(), result, indexMap));
            }

            if (select.getGroupByColumns() != null && select.getGroupByColumns().length > 0) {
                sw.append(" group by ");
                for (int i = 0; i < select.getGroupByColumns().length; i++) {
                    Column column = select.getGroupByColumns()[i];
                    sw.append(columnSQL(false, column, result, indexMap));
                    if (i < select.getGroupByColumns().length - 1) {
                        sw.append(",");
                    }
                    sw.append(" ");
                }
            }

            if (select.getHavingCriterion() != null) {
                sw.append(" having ").append(criterionSQL(false, select.getHavingCriterion(), result, indexMap));
            }

            if (!select.getOrderCriteria().isEmpty()) {
                sw.append(" order by ");
                for (int i = 0; i < select.getOrderCriteria().size(); i++) {
                    OrderCriterion orderCriterion = select.getOrderCriteria().get(i);
                    sw.append(columnSQL(false, orderCriterion.getColumn(), result, indexMap)).append(" ").append(orderCriterion.getOrder().name());
                    if (i < select.getOrderCriteria().size() - 1) {
                        sw.append(",");
                    }
                    sw.append(" ");
                }
            }

            if (select.getCount()) {
                sw.append(") as count_result");
            }

            if (select.getPagination()) {
                result.getParams().add(new SqlParam(DataType.INTEGER, select.getPageSize()));
                sw.append(") as paginated_result limit ?");

                result.getParams().add(new SqlParam(DataType.INTEGER, select.getFirstRow()));
                sw.append(" offset ?");
            }

        } else {
            sw.append(selectSQL(select, result));
        }

        result.setQuery(sw.toString());
        return result;
    }

    private String selectSQL(Select select, QueryResult qr) {
        StringBuilder sw = new StringBuilder();

        if (select.getCount()) {
            sw.append("select count(*) from (");
        }

        if (select.getPagination()) {
            sw.append("select * from (");
        }

        sw.append("select ");

        if (select.getDistinct()) {
            sw.append("distinct ");
        }

        for (int index = 0; index < select.getColumns().length; index++) {
            Column column = select.getColumns()[index];
            sw.append(columnSQL(false, column, qr, null)).append(" as col").append(index);
            if (index < select.getColumns().length - 1) {
                sw.append(",");
            }
            sw.append(" ");
        }

        sw.append("from ").append(tableSQL(false, select.getTable())).append(" ");

        select.getJoinCriteria().stream().map((joinCriterion) -> {
            sw.append(" ").append(joinCriterion.getJoin().name().replaceAll("_", " "));
            return joinCriterion;
        }).map((joinCriterion) -> {
            sw.append(" join ").append(tableSQL(false, joinCriterion.getTable())).append(" on ");
            return joinCriterion;
        }).forEachOrdered((joinCriterion) -> {
            sw.append(criteriaManagerSQL(false, joinCriterion.getCriteriaManager(), qr, null));
        });

        if (select.getCriteriaManager().isCriteria()) {
            sw.append(" where ").append(criteriaManagerSQL(false, select.getCriteriaManager(), qr, null));
        }

        if (select.getGroupByColumns() != null && select.getGroupByColumns().length > 0) {
            sw.append(" group by ");
            for (int i = 0; i < select.getGroupByColumns().length; i++) {
                Column column = select.getGroupByColumns()[i];
                sw.append(columnSQL(false, column, qr, null));
                if (i < select.getGroupByColumns().length - 1) {
                    sw.append(",");
                }
                sw.append(" ");
            }
        }

        if (select.getHavingCriterion() != null) {
            sw.append(" having ").append(criterionSQL(false, select.getHavingCriterion(), qr, null));
        }

        if (!select.getOrderCriteria().isEmpty()) {
            sw.append(" order by ");
            for (int i = 0; i < select.getOrderCriteria().size(); i++) {
                OrderCriterion orderCriterion = select.getOrderCriteria().get(i);
                sw.append(columnSQL(false, orderCriterion.getColumn(), qr, null)).append(" ").append(orderCriterion.getOrder().name());
                if (i < select.getOrderCriteria().size() - 1) {
                    sw.append(",");
                }
                sw.append(" ");
            }
        }

        if (select.getCount()) {
            sw.append(") as count_result");
        }

        if (select.getPagination()) {
            qr.getParams().add(new SqlParam(DataType.INTEGER, select.getPageSize()));
            sw.append(") as paginated_result limit ?");

            qr.getParams().add(new SqlParam(DataType.INTEGER, select.getFirstRow()));
            sw.append(" offset ?");
        }

        return sw.toString();
    }

    private String tableSQL(boolean onlyName, Table table) {
        StringBuilder sw = new StringBuilder();

        sw.append(table.getName());
        if (!onlyName) {
            sw.append(" ").append(table.getAlias());
        }

        return sw.toString();
    }

    private String columnSQL(boolean onlyName, Column column, QueryResult qr, Map<String, Integer> indexMap) {
        StringBuilder sw = new StringBuilder();

        if (indexMap != null) {
            sw.append("col").append(indexMap.get(column.getColumnId()));
        } else {
            if (column instanceof ColumnFunction) {
                ColumnFunction columnFunction = (ColumnFunction) column;
                if (columnFunction.getMembers() != null) {
                    List<Object> membersList = new ArrayList<>();
                    for (Column member : columnFunction.getMembers()) {
                        membersList.add(columnSQL(onlyName, member, qr, indexMap));
                    }
                    sw.append(MessageFormat.format(columnFunction.getName(), membersList.toArray(new Object[membersList.size()])));
                } else {
                    sw.append(columnFunction.getName());
                }
            } else if (column instanceof ColumnSelect) {
                ColumnSelect columnSelect = (ColumnSelect) column;
                sw.append("(").append(selectSQL(columnSelect.getSelect(), qr)).append(")");

            } else {
                if (!onlyName && column.getTable() != null) {
                    sw.append(column.getTable().getAlias()).append(".");
                }
                sw.append(column.getName());
            }
        }

        return sw.toString();
    }

    private String valueSQL(boolean onlyName, Column column, Object value, QueryResult qr, Map<String, Integer> indexMap) {
        StringBuilder sw = new StringBuilder();

        if (value instanceof Sequence) {
            sw.append(sequenceSQL((Sequence) value));
        } else if (value instanceof Column) {
            sw.append(columnSQL(onlyName, (Column) value, qr, indexMap));
        } else {
            if (value != null) {
                qr.getParams().add(new SqlParam(column.getDataType(), value));
                sw.append("?");
            } else {
                sw.append("null");
            }
        }

        return sw.toString();
    }

    private String criteriaManagerSQL(boolean onlyName, CriteriaManager criteriaManager, QueryResult qr, Map<String, Integer> indexMap) {
        StringBuilder sw = new StringBuilder();

        Criteria lastCriteria = null;
        for (Criteria criteria : criteriaManager.getCriteriaList()) {
            if (criteria.isCriteria()) {
                if (lastCriteria != null) {
                    sw.append(" ").append(lastCriteria.getOperator().name()).append(" ");
                }
                sw.append(criteriaSQL(onlyName, criteria, qr, indexMap));
                lastCriteria = criteria;
            }
        }

        return sw.toString();
    }

    private String criteriaSQL(boolean onlyName, Criteria criteria, QueryResult qr, Map<String, Integer> indexMap) {
        StringBuilder sw = new StringBuilder();

        CriteriaContent lastCriteriaContent = null;
        boolean criteriaSequence = false;

        sw.append("(");
        for (CriteriaContent criteriaContent : criteria.getContent()) {
            if (criteriaContent instanceof Criterion) {
                if (lastCriteriaContent != null) {
                    if (criteriaSequence) {
                        sw.append(")");
                        criteriaSequence = false;
                    }
                    sw.append(" ").append(lastCriteriaContent.getOperator().name()).append(" ");
                }
                sw.append(criterionSQL(onlyName, (Criterion) criteriaContent, qr, indexMap));
                lastCriteriaContent = criteriaContent;
            } else {
                Criteria subCriteria = (Criteria) criteriaContent;
                if (subCriteria.isCriteria()) {
                    if (lastCriteriaContent != null) {
                        sw.append(" ").append(lastCriteriaContent.getOperator().name()).append(" ");
                    }
                    if (!criteriaSequence) {
                        sw.append("(");
                        criteriaSequence = true;
                    }
                    sw.append(criteriaSQL(onlyName, subCriteria, qr, indexMap));
                    lastCriteriaContent = subCriteria;
                }
            }
        }
        if (criteriaSequence) {
            sw.append(")");
        }
        sw.append(")");

        return sw.toString();
    }

    private String criterionSQL(boolean onlyName, Criterion criterion, QueryResult qr, Map<String, Integer> indexMap) {
        StringBuilder sw = new StringBuilder();

        if (criterion.getCondition() == Condition.DIRECT) {
            sw.append(criterion.getValue());
        } else {
            if (criterion.getValue() instanceof Select) {
                sw.append(columnSQL(onlyName, criterion.getColumn(), qr, indexMap)).append(" ").append(criterion.getCondition().condition());
                sw.append(" (").append(selectSQL((Select) criterion.getValue(), qr)).append(")");
            } else if (criterion.getValue() instanceof Column) {
                Column valueColumn = (Column) criterion.getValue();
                if (criterion.getRepresentation() == null) {
                    sw.append(columnSQL(onlyName, criterion.getColumn(), qr, indexMap)).append(" ").append(criterion.getCondition().condition()).append(" ");
                    sw.append(columnSQL(onlyName, valueColumn, qr, indexMap));
                } else {
                    sw.append(MessageFormat.format(criterion.getRepresentation(),
                            columnSQL(onlyName, criterion.getColumn(), qr, indexMap), criterion.getCondition().condition(), columnSQL(onlyName, valueColumn, qr, indexMap)));
                }
            } else {
                if (criterion.getRepresentation() == null) {
                    sw.append(columnSQL(onlyName, criterion.getColumn(), qr, indexMap)).append(" ").append(criterion.getCondition().condition()).append(" ");
                    if (criterion.getValue() != null) {
                        if (criterion.getValue() instanceof List<?> || criterion.getValue() instanceof Object[]) {
                            Object[] valueArray;
                            if (criterion.getValue() instanceof List<?>) {
                                valueArray = ((List<?>) criterion.getValue()).toArray();
                            } else {
                                valueArray = (Object[]) criterion.getValue();
                            }
                            sw.append("(");
                            for (int index = 0; index < valueArray.length; index++) {
                                Object val = valueArray[index];
                                qr.getParams().add(new SqlParam(criterion.getColumn().getDataType(), val));
                                sw.append("?");
                                if (index < valueArray.length - 1) {
                                    sw.append(", ");
                                }
                            }
                            sw.append(")");
                        } else {
                            qr.getParams().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
                            sw.append("?");
                        }
                    }
                } else {
                    if (criterion.getValue() != null) {
                        qr.getParams().add(new SqlParam(criterion.getColumn().getDataType(), criterion.getValue()));
                        sw.append(MessageFormat.format(criterion.getRepresentation(), columnSQL(onlyName, criterion.getColumn(), qr, indexMap), criterion.getCondition().condition(), "?"));
                    } else {
                        sw.append(MessageFormat.format(criterion.getRepresentation(), columnSQL(onlyName, criterion.getColumn(), qr, indexMap), criterion.getCondition().condition()));
                    }
                }
            }
        }
        return sw.toString();
    }

    @Override
    public List<Object[]> select(Connection connection, Select select) throws SQLException {
        List<Object[]> result = new ArrayList<>();

        QueryResult queryResult = select(select);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(queryResult.getQuery());
            LOGGER.finest(Arrays.toString(queryResult.getParams().toArray()));
        }

        List<InputStream> streams = new ArrayList<>();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(queryResult.getQuery());
            setParams(preparedStatement, queryResult.getParams().toArray(new SqlParam[queryResult.getParams().size()]), streams);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Object[] row;
                if (select.getCount()) {
                    row = new Object[]{resultSet.getInt(1)};
                } else {
                    row = getRow(resultSet, select.getColumns());
                }
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "ROW:{0}", Arrays.toString(row));
                }
                result.add(row);
            }
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);

            streams.forEach((stream) -> {
                FileUtil.close(stream);
            });
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "RESULT SIZE:{0}", result.size());
        }
        return result;
    }

    @Override
    public QueryResult insert(Insert insert) {
        QueryResult result = new QueryResult();
        StringBuilder sw = new StringBuilder();

        sw.append("insert into ").append(tableSQL(true, insert.getTable())).append(" (");

        for (int index = 0; index < insert.getColumns().length; index++) {
            Column column = insert.getColumns()[index];

            sw.append(columnSQL(true, column, result, null));
            if (index < insert.getColumns().length - 1) {
                sw.append(", ");
            }
        }

        sw.append(") values (");

        for (int index = 0; index < insert.getColumns().length; index++) {
            Column column = insert.getColumns()[index];
            Object value = insert.getValues()[index];
            sw.append(valueSQL(true, column, value, result, null));
            if (index < insert.getColumns().length - 1) {
                sw.append(", ");
            }
        }
        sw.append(")");

        if (insert.getReturning() != null) {
            sw.append(" returning ").append(columnSQL(true, insert.getReturning(), result, null));
        }

        result.setQuery(sw.toString());
        return result;
    }

    @Override
    public Object insert(Connection connection, Insert insert) throws SQLException {
        Object result = null;

        QueryResult queryResult = insert(insert);

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(queryResult.getQuery());
            LOGGER.finest(Arrays.toString(queryResult.getParams().toArray()));
        }

        if (insert.getReturning() == null) {
            execute(connection, queryResult.getQuery(), queryResult.getParams());
        } else {
            result = insert(connection, queryResult, insert.getReturning());
        }

        return result;
    }

    private Object insert(Connection connection, QueryResult queryResult, Column returning) throws SQLException {
        Object result;

        List<InputStream> streams = new ArrayList<>();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(queryResult.getQuery());
            setParams(preparedStatement, queryResult.getParams().toArray(new SqlParam[queryResult.getParams().size()]), streams);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = getColumn(resultSet, 1, returning, getTmpDir());
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);

            streams.forEach((stream) -> {
                FileUtil.close(stream);
            });
        }
        return result;
    }

    @Override
    public QueryResult update(Update update) {
        QueryResult result = new QueryResult();
        StringBuilder sw = new StringBuilder();

        sw.append("update ").append(tableSQL(true, update.getTable())).append(" set ");
        for (int index = 0; index < update.getColumns().length; index++) {
            Column column = update.getColumns()[index];
            Object value = update.getValues()[index];

            sw.append(columnSQL(true, column, result, null)).append(" = ").append(valueSQL(true, column, value, result, null));
            if (index < update.getColumns().length - 1) {
                sw.append(", ");
            }
        }
        if (update.getCriteriaManager().isCriteria()) {
            sw.append(" where ").append(criteriaManagerSQL(true, update.getCriteriaManager(), result, null));
        }

        result.setQuery(sw.toString());
        return result;
    }

    @Override
    public void update(Connection connection, Update update) throws SQLException {
        QueryResult queryResult = update(update);
        execute(connection, queryResult.getQuery(), queryResult.getParams());
    }

    @Override
    public QueryResult delete(Delete delete) {
        QueryResult result = new QueryResult();
        StringBuilder sw = new StringBuilder();

        sw.append("delete from ").append(tableSQL(true, delete.getTable()));
        if (delete.getCriteriaManager().isCriteria()) {
            sw.append(" where ").append(criteriaManagerSQL(true, delete.getCriteriaManager(), result, null));
        }

        result.setQuery(sw.toString());
        return result;
    }

    @Override
    public void delete(Connection connection, Delete delete) throws SQLException {
        QueryResult queryResult = delete(delete);
        execute(connection, queryResult.getQuery(), queryResult.getParams());
    }

    private Object[] getRow(ResultSet resultSet, Column... columns) throws SQLException {
        Object[] result = new Object[columns.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = getColumn(resultSet, i + 1, columns[i], getTmpDir());
        }

        return result;
    }

    private Object getColumn(ResultSet resultSet, int index, Column column, File dir) throws SQLException {
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
            }
        }
        return result;
    }

    private void setParams(PreparedStatement preparedStatement, SqlParam[] params, List<InputStream> streams) throws SQLException {
        int i = 1;
        for (SqlParam param : params) {
            setParam(preparedStatement, i++, param, streams);
        }
    }

    private void setParam(PreparedStatement preparedStatement, int index, SqlParam param, List<InputStream> streams) throws SQLException {
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

    protected void execute(Connection connection, String sql, List<SqlParam> params) throws SQLException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(sql);
            LOGGER.finest(Arrays.toString(params.toArray()));
        }

        if (params.isEmpty()) {
            SqlUtil.execute(connection, sql);
        } else {
            List<InputStream> streams = new ArrayList<>();

            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(sql);
                setParams(preparedStatement, params.toArray(new SqlParam[params.size()]), streams);
                preparedStatement.executeUpdate();
            } finally {
                SqlUtil.close(preparedStatement);

                streams.forEach((stream) -> {
                    FileUtil.close(stream);
                });
            }
        }
    }
}
