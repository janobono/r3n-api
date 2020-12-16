/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.r3n.sql.*;
import sk.r3n.sql.Query.Delete;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Query.Update;
import sk.r3n.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Oracle sql builder implementation.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class OraSqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(OraSqlBuilder.class);

    @Override
    public Sql nextVal(Sequence sequence) {
        Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence)).append(" ").FROM().append("dual");
        return sql;
    }

    private String sequenceSQL(Sequence sequence) {
        return sequence.getName() + "." + "nextval";
    }

    @Override
    public long nextVal(Connection connection, Sequence sequence) throws SQLException {
        Sql sql = nextVal(sequence);
        LOGGER.debug(sql.toString());
        long result;
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql.toSql())
        ) {
            resultSet.next();
            result = resultSet.getLong(1);
        }
        LOGGER.debug("RESULT:{}", result);
        return result;
    }

    @Override
    public Sql select(Select select) {
        Sql sql = new Sql();
        if (select.getSubSelects() != null && select.getSubSelects().length > 0) {
            Map<String, Integer> indexMap = new HashMap<>();
            if (select.getCount()) {
                sql.SELECT().append("count(*) ").FROM().append("(");
            }
            if (select.getPagination()) {
                sql.SELECT();
                for (int index = 0; index < select.getColumns().length; index++) {
                    sql.append("col").append(Integer.toString(index));
                    if (index < select.getColumns().length - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(" ").FROM().append("( ").SELECT();
                for (int index = 0; index < select.getColumns().length; index++) {
                    sql.append("col").append(Integer.toString(index)).append(", ");
                }
                sql.append("rownum rnm ").FROM().append("(");
            }
            for (Select subSelect : select.getSubSelects()) {
                int index = 0;
                for (Column column : subSelect.getColumns()) {
                    indexMap.put(column.getColumnId(), index);
                    index++;
                }
            }
            sql.SELECT();
            if (select.getDistinct()) {
                sql.DISTINCT();
            }
            for (int i = 0; i < select.getColumns().length; i++) {
                Column column = select.getColumns()[i];
                indexMap.put(column.getColumnId(), i);
                sql.append("col").append(Integer.toString(indexMap.get(column.getColumnId())));
                if (i < select.getColumns().length - 1) {
                    sql.append(",");
                }
                sql.append(" ");
            }
            sql.append(" ").FROM().append("(");
            for (int i = 0; i < select.getSubSelects().length; i++) {
                Select subSelect = select.getSubSelects()[i];
                sql.append(selectSQL(subSelect));
                if (i < select.getSubSelects().length - 1) {
                    sql.append(" ").append(select.getDataSetOperator().name().replaceAll("_", " ")).append(" ");
                }
            }
            sql.append(")");
            if (select.getCriteriaManager().isCriteria()) {
                sql.append(" ").WHERE().append(criteriaManagerSQL(false, select.getCriteriaManager(), indexMap));
            }
            if (select.getGroupByColumns() != null && select.getGroupByColumns().length > 0) {
                sql.append(" ").GROUP_BY();
                for (int i = 0; i < select.getGroupByColumns().length; i++) {
                    Column column = select.getGroupByColumns()[i];
                    sql.append(columnSQL(false, column, indexMap));
                    if (i < select.getGroupByColumns().length - 1) {
                        sql.append(",");
                    }
                    sql.append(" ");
                }
            }
            if (select.getHavingCriterion() != null) {
                sql.append(" ").HAVING().append(criterionSQL(false, select.getHavingCriterion(), indexMap));
            }
            if (!select.getOrderCriteria().isEmpty()) {
                sql.append(" ").ORDER_BY();
                for (int i = 0; i < select.getOrderCriteria().size(); i++) {
                    OrderCriterion orderCriterion = select.getOrderCriteria().get(i);
                    sql.append(columnSQL(false, orderCriterion.getColumn(), indexMap)).append(" ").append(orderCriterion.getOrder().name());
                    if (i < select.getOrderCriteria().size() - 1) {
                        sql.append(",");
                    }
                    sql.append(" ");
                }
            }
            if (select.getCount()) {
                sql.append(")");
            }
            if (select.getPagination()) {
                sql.addParam(DataType.INTEGER, select.getLastRow() + 1);
                sql.append(") ").WHERE().append("rownum <= ?");

                sql.addParam(DataType.INTEGER, select.getFirstRow() + 1);
                sql.append(") ").WHERE().append("rnm >= ?");
            }
        } else {
            sql.append(selectSQL(select));
        }
        return sql;
    }

    private Sql selectSQL(Select select) {
        Sql sql = new Sql();
        if (select.getCount()) {
            sql.SELECT().append("count(*) ").FROM().append("(");
        }
        if (select.getPagination()) {
            sql.SELECT();
            for (int index = 0; index < select.getColumns().length; index++) {
                sql.append("col").append(Integer.toString(index));
                if (index < select.getColumns().length - 1) {
                    sql.append(", ");
                }
            }
            sql.append(" ").FROM().append("(").SELECT();
            for (int index = 0; index < select.getColumns().length; index++) {
                sql.append("col").append(Integer.toString(index)).append(", ");
            }
            sql.append("rownum rnm ").FROM().append("(");
        }
        sql.SELECT();
        if (select.getDistinct()) {
            sql.DISTINCT();
        }
        for (int index = 0; index < select.getColumns().length; index++) {
            Column column = select.getColumns()[index];
            sql.append(columnSQL(false, column, null)).append(" as col").append(Integer.toString(index));
            if (index < select.getColumns().length - 1) {
                sql.append(",");
            }
            sql.append(" ");
        }
        sql.FROM().append(tableSQL(false, select.getTable())).append(" ");
        select.getJoinCriteria().stream().peek(
                (joinCriterion) -> sql.append(" ").append(joinCriterion.getJoin().name().replaceAll("_", " "))
        ).peek(
                (joinCriterion) -> sql.append(" ").JOIN().append(tableSQL(false, joinCriterion.getTable())).append(" ").ON()
        ).forEachOrdered((joinCriterion) -> {
            sql.append(criteriaManagerSQL(false, joinCriterion.getCriteriaManager(), null));
        });
        if (select.getCriteriaManager().isCriteria()) {
            sql.append(" ").WHERE().append(criteriaManagerSQL(false, select.getCriteriaManager(), null));
        }
        if (select.getGroupByColumns() != null && select.getGroupByColumns().length > 0) {
            sql.append(" ").GROUP_BY();
            for (int i = 0; i < select.getGroupByColumns().length; i++) {
                Column column = select.getGroupByColumns()[i];
                sql.append(columnSQL(false, column, null));
                if (i < select.getGroupByColumns().length - 1) {
                    sql.append(",");
                }
                sql.append(" ");
            }
        }
        if (select.getHavingCriterion() != null) {
            sql.append(" ").HAVING().append(criterionSQL(false, select.getHavingCriterion(), null));
        }
        if (!select.getOrderCriteria().isEmpty()) {
            sql.append(" ").ORDER_BY();
            for (int i = 0; i < select.getOrderCriteria().size(); i++) {
                OrderCriterion orderCriterion = select.getOrderCriteria().get(i);
                sql.append(columnSQL(false, orderCriterion.getColumn(), null)).append(" ").append(orderCriterion.getOrder().name());
                if (i < select.getOrderCriteria().size() - 1) {
                    sql.append(",");
                }
                sql.append(" ");
            }
        }
        if (select.getCount()) {
            sql.append(")");
        }
        if (select.getPagination()) {
            sql.addParam(DataType.INTEGER, select.getLastRow() + 1);
            sql.append(") ").WHERE().append("rownum <= ?");
            sql.addParam(DataType.INTEGER, select.getFirstRow() + 1);
            sql.append(") ").WHERE().append("rnm >= ?");
        }
        return sql;
    }

    private String tableSQL(boolean onlyName, Table table) {
        StringBuilder sw = new StringBuilder();
        sw.append(table.getName());
        if (!onlyName) {
            sw.append(" ").append(table.getAlias());
        }
        return sw.toString();
    }

    private Sql columnSQL(boolean onlyName, Column column, Map<String, Integer> indexMap) {
        Sql sql = new Sql();
        if (indexMap != null) {
            sql.append("col").append(Integer.toString(indexMap.get(column.getColumnId())));
        } else {
            if (column instanceof ColumnFunction) {
                ColumnFunction columnFunction = (ColumnFunction) column;
                if (columnFunction.getMembers() != null) {
                    List<String> membersList = new ArrayList<>();
                    for (Column member : columnFunction.getMembers()) {
                        Sql memberSql = columnSQL(onlyName, member, indexMap);
                        membersList.add(memberSql.toSql());
                        memberSql.getParams().forEach((param) -> {
                            sql.addParam(param.getDataType(), param.getValue());
                        });
                    }
                    sql.append(MessageFormat.format(columnFunction.getName(), membersList.toArray(new Object[0])));
                } else {
                    sql.append(columnFunction.getName());
                }
            } else if (column instanceof ColumnSelect) {
                ColumnSelect columnSelect = (ColumnSelect) column;
                sql.append("(").append(selectSQL(columnSelect.getSelect())).append(")");
            } else {
                if (!onlyName && column.getTable() != null) {
                    sql.append(column.getTable().getAlias()).append(".");
                }
                sql.append(column.getName());
            }
        }
        return sql;
    }

    private Sql valueSQL(boolean onlyName, Column column, Object value, Map<String, Integer> indexMap) {
        Sql sql = new Sql();
        if (value instanceof Sequence) {
            sql.append(sequenceSQL((Sequence) value));
        } else if (value instanceof Column) {
            sql.append(columnSQL(onlyName, (Column) value, indexMap));
        } else {
            if (value != null) {
                sql.addParam(column.getDataType(), value);
                sql.append("?");
            } else {
                sql.append("null");
            }
        }
        return sql;
    }

    private Sql criteriaManagerSQL(boolean onlyName, CriteriaManager criteriaManager, Map<String, Integer> indexMap) {
        Sql sql = new Sql();
        Criteria lastCriteria = null;
        for (Criteria criteria : criteriaManager.getCriteriaList()) {
            if (criteria.isCriteria()) {
                if (lastCriteria != null) {
                    sql.append(" ").append(lastCriteria.getOperator().name()).append(" ");
                }
                sql.append(criteriaSQL(onlyName, criteria, indexMap));
                lastCriteria = criteria;
            }
        }
        return sql;
    }

    private Sql criteriaSQL(boolean onlyName, Criteria criteria, Map<String, Integer> indexMap) {
        Sql sql = new Sql();
        CriteriaContent lastCriteriaContent = null;
        boolean criteriaSequence = false;
        sql.append("(");
        for (CriteriaContent criteriaContent : criteria.getContent()) {
            if (criteriaContent instanceof Criterion) {
                if (lastCriteriaContent != null) {
                    if (criteriaSequence) {
                        sql.append(")");
                        criteriaSequence = false;
                    }
                    sql.append(" ").append(lastCriteriaContent.getOperator().name()).append(" ");
                }
                sql.append(criterionSQL(onlyName, (Criterion) criteriaContent, indexMap));
                lastCriteriaContent = criteriaContent;
            } else {
                Criteria subCriteria = (Criteria) criteriaContent;
                if (subCriteria.isCriteria()) {
                    if (lastCriteriaContent != null) {
                        sql.append(" ").append(lastCriteriaContent.getOperator().name()).append(" ");
                    }
                    if (!criteriaSequence) {
                        sql.append("(");
                        criteriaSequence = true;
                    }
                    sql.append(criteriaSQL(onlyName, subCriteria, indexMap));
                    lastCriteriaContent = subCriteria;
                }
            }
        }
        if (criteriaSequence) {
            sql.append(")");
        }
        sql.append(")");
        return sql;
    }

    private Sql criterionSQL(boolean onlyName, Criterion criterion, Map<String, Integer> indexMap) {
        Sql sql = new Sql();
        if (criterion.getCondition() == Condition.DIRECT) {
            sql.append((String) criterion.getValue());
        } else {
            if (criterion.getValue() instanceof Select) {
                sql.append(columnSQL(onlyName, criterion.getColumn(), indexMap)).append(" ").append(criterion.getCondition().condition());
                sql.append(" (").append(selectSQL((Select) criterion.getValue())).append(")");
            } else if (criterion.getValue() instanceof Column) {
                Column valueColumn = (Column) criterion.getValue();
                if (criterion.getRepresentation() == null) {
                    sql.append(columnSQL(onlyName, criterion.getColumn(), indexMap)).append(" ").append(criterion.getCondition().condition()).append(" ");
                    sql.append(columnSQL(onlyName, valueColumn, indexMap));
                } else {
                    sql.append(MessageFormat.format(criterion.getRepresentation(),
                            columnSQL(onlyName, criterion.getColumn(), indexMap), criterion.getCondition().condition(), columnSQL(onlyName, valueColumn, indexMap)));
                }
            } else {
                if (criterion.getRepresentation() == null) {
                    sql.append(columnSQL(onlyName, criterion.getColumn(), indexMap)).append(" ").append(criterion.getCondition().condition()).append(" ");
                    if (criterion.getValue() != null) {
                        if (criterion.getValue() instanceof List<?> || criterion.getValue() instanceof Object[]) {
                            Object[] valueArray;
                            if (criterion.getValue() instanceof List<?>) {
                                valueArray = ((List<?>) criterion.getValue()).toArray();
                            } else {
                                valueArray = (Object[]) criterion.getValue();
                            }
                            sql.append("(");
                            for (int index = 0; index < valueArray.length; index++) {
                                Object val = valueArray[index];
                                sql.addParam(criterion.getColumn().getDataType(), val);
                                sql.append("?");
                                if (index < valueArray.length - 1) {
                                    sql.append(", ");
                                }
                            }
                            sql.append(")");
                        } else {
                            sql.addParam(criterion.getColumn().getDataType(), criterion.getValue());
                            sql.append("?");
                        }
                    }
                } else {
                    if (criterion.getValue() != null) {
                        sql.addParam(criterion.getColumn().getDataType(), criterion.getValue());
                        sql.append(MessageFormat.format(criterion.getRepresentation(), columnSQL(onlyName, criterion.getColumn(), indexMap), criterion.getCondition().condition(), "?"));
                    } else {
                        sql.append(MessageFormat.format(criterion.getRepresentation(), columnSQL(onlyName, criterion.getColumn(), indexMap), criterion.getCondition().condition()));
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public List<Object[]> select(Connection connection, Select select) throws SQLException {
        DataType[] dataTypes;
        if (select.getCount()) {
            dataTypes = new DataType[]{DataType.INTEGER};
        } else {
            dataTypes = new DataType[select.getColumns().length];
            for (int i = 0; i < dataTypes.length; i++) {
                dataTypes[i] = select.getColumns()[i].getDataType();
            }
        }
        return executeQuery(connection, select(select), dataTypes);
    }

    @Override
    public Sql insert(Insert insert) {
        Sql sql = new Sql();
        if (insert.getReturning() != null) {
            sql.append("begin ");
        }
        sql.INSERT().INTO().append(tableSQL(true, insert.getTable())).append(" (");
        for (int index = 0; index < insert.getColumns().length; index++) {
            Column column = insert.getColumns()[index];

            sql.append(columnSQL(true, column, null));
            if (index < insert.getColumns().length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") ").VALUES().append("(");
        for (int index = 0; index < insert.getColumns().length; index++) {
            Column column = insert.getColumns()[index];
            Object value = insert.getValues()[index];
            sql.append(valueSQL(true, column, value, null));
            if (index < insert.getColumns().length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        if (insert.getReturning() != null) {
            sql.append(" returning ").append(columnSQL(true, insert.getReturning(), null)).append(" into ?;end;");
        }
        return sql;
    }

    @Override
    public Object insert(Connection connection, Insert insert) throws SQLException {
        Object result = null;
        if (insert.getReturning() == null) {
            execute(connection, insert(insert));
        } else {
            result = execute(connection, insert(insert), insert.getReturning().getDataType());
        }
        return result;
    }

    @Override
    public Sql update(Update update) {
        Sql sql = new Sql();
        sql.UPDATE().append(tableSQL(true, update.getTable())).append(" ").SET();
        for (int index = 0; index < update.getColumns().length; index++) {
            Column column = update.getColumns()[index];
            Object value = update.getValues()[index];
            sql.append(columnSQL(true, column, null)).append(" = ").append(valueSQL(true, column, value, null));
            if (index < update.getColumns().length - 1) {
                sql.append(", ");
            }
        }
        if (update.getCriteriaManager().isCriteria()) {
            sql.append(" ").WHERE().append(criteriaManagerSQL(true, update.getCriteriaManager(), null));
        }
        return sql;
    }

    @Override
    public void update(Connection connection, Update update) throws SQLException {
        execute(connection, update(update));
    }

    @Override
    public Sql delete(Delete delete) {
        Sql sql = new Sql();
        sql.DELETE().FROM().append(tableSQL(true, delete.getTable()));
        if (delete.getCriteriaManager().isCriteria()) {
            sql.append(" ").WHERE().append(criteriaManagerSQL(true, delete.getCriteriaManager(), null));
        }
        return sql;
    }

    @Override
    public void delete(Connection connection, Delete delete) throws SQLException {
        execute(connection, delete(delete));
    }

    private Object[] getRow(ResultSet resultSet, DataType... dataTypes) throws SQLException {
        Object[] result = new Object[dataTypes.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getColumn(resultSet, i + 1, dataTypes[i], getTmpDir());
        }
        return result;
    }

    private Object getColumn(ResultSet resultSet, int index, DataType dataType, File dir) throws SQLException {
        Object result = null;
        if (resultSet.getObject(index) != null) {
            switch (dataType) {
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
                    result = resultSet.getDate(index).toLocalDate();
                    break;
                case TIME:
                    result = resultSet.getTime(index).toLocalTime();
                    break;
                case TIME_STAMP:
                    result = resultSet.getTimestamp(index).toLocalDateTime();
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
                        FileUtil.delete(file);
                        throw new SQLException(e);
                    }
                    break;
            }
        }
        return result;
    }

    private void setParams(Connection connection, PreparedStatement preparedStatement, SqlParam[] params) throws SQLException {
        int i = 1;
        for (SqlParam param : params) {
            setParam(connection, preparedStatement, i++, param);
        }
    }

    private void setParam(Connection connection, PreparedStatement preparedStatement, int index, SqlParam param) throws SQLException {
        if (param.getValue() != null) {
            switch (param.getDataType()) {
                case BLOB:
                    Blob blob = connection.createBlob();
                    FileUtil.fileToStream((File) param.getValue(), blob.setBinaryStream(1));
                    preparedStatement.setBlob(index, blob);
                    break;
                case DATE:
                    preparedStatement.setDate(index, Date.valueOf(((LocalDate) param.getValue())));
                    break;
                case TIME:
                    preparedStatement.setTime(index, Time.valueOf(((LocalTime) param.getValue())));
                    break;
                case TIME_STAMP:
                    preparedStatement.setTimestamp(index, Timestamp.valueOf(((LocalDateTime) param.getValue())));
                    break;
                default:
                    preparedStatement.setObject(index, param.getValue());
                    break;
            }
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    @Override
    public void execute(Connection connection, Sql sql) throws SQLException {
        LOGGER.debug(sql.toString());
        if (sql.getParams().isEmpty()) {
            SqlUtil.execute(connection, sql.toSql());
        } else {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
                setParams(connection, preparedStatement, sql.getParams().toArray(new SqlParam[0]));
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public Object execute(Connection connection, Sql sql, DataType dataType) throws SQLException {
        LOGGER.debug(sql.toString());
        Object result = null;
        try (CallableStatement callableStatement = connection.prepareCall(sql.toSql())) {
            int index = sql.getParams().size() + 1;
            setParams(connection, callableStatement, sql.getParams().toArray(new SqlParam[0]));

            switch (dataType) {
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

            switch (dataType) {
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
                    result = callableStatement.getDate(index).toLocalDate();
                    break;
                case TIME:
                    result = callableStatement.getTime(index).toLocalTime();
                    break;
                case TIME_STAMP:
                    result = callableStatement.getTimestamp(index).toLocalDateTime();
                    break;
                case BLOB:
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", getTmpDir());
                        Blob blob = callableStatement.getBlob(index);
                        FileUtil.streamToFile(blob.getBinaryStream(1, blob.length()), file);
                        result = file;
                    } catch (IOException e) {
                        FileUtil.delete(file);
                        throw new SQLException(e);
                    }
                    break;
            }
        }
        return result;
    }

    @Override
    public List<Object[]> executeQuery(Connection connection, Sql sql, DataType... dataTypes) throws SQLException {
        LOGGER.debug(sql.toString());
        List<Object[]> result = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
            setParams(connection, preparedStatement, sql.getParams().toArray(new SqlParam[0]));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Object[] row = getRow(resultSet, dataTypes);
                LOGGER.debug("ROW:{}", row);
                result.add(row);
            }
        } finally {
            SqlUtil.close(resultSet);
        }
        LOGGER.debug("RESULT SIZE:{}", result.size());
        return result;
    }
}
