/*
 * Copyright 2016 janobono. All rights reserved.
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
import sk.r3n.sql.impl.ColumnBase;
import sk.r3n.sql.impl.ColumnFunction;
import sk.r3n.sql.impl.ColumnSelect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * PostgreSQL sql builder implementation.
 */
public class PostgreSqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlBuilder.class);

    @Override
    public Sql nextVal(Sequence sequence) {
        Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence));
        return sql;
    }

    private String sequenceSQL(Sequence sequence) {
        return "nextval('" + sequence.name() + "')";
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
                sql.SELECT().append("* ").FROM().append("(");
            }
            for (Select subSelect : select.getSubSelects()) {
                int index = 0;
                for (Column column : subSelect.getColumns()) {
                    indexMap.put(column.columnId(), index);
                    index++;
                }
            }
            sql.SELECT();
            if (select.getDistinct()) {
                sql.DISTINCT();
            }
            for (int i = 0; i < select.getColumns().length; i++) {
                Column column = select.getColumns()[i];
                indexMap.put(column.columnId(), i);
                sql.append("col").append(Integer.toString(indexMap.get(column.columnId())));
                if (i < select.getColumns().length - 1) {
                    sql.append(",");
                }
                sql.append(" ");
            }
            sql.FROM().append("(");
            for (int i = 0; i < select.getSubSelects().length; i++) {
                Select subSelect = select.getSubSelects()[i];
                sql.append(selectSQL(subSelect));
                if (i < select.getSubSelects().length - 1) {
                    sql.append(" ").append(select.getDataSetOperator().name().replaceAll("_", " ")).append(" ");
                }
            }
            sql.append(") as union_result ");
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
                    sql.append(columnSQL(false, orderCriterion.column(), indexMap)).append(" ").append(orderCriterion.order().name());
                    if (i < select.getOrderCriteria().size() - 1) {
                        sql.append(",");
                    }
                    sql.append(" ");
                }
            }
            if (select.getCount()) {
                sql.append(") as count_result");
            }
            if (select.getPagination()) {
                sql.addParam(DataType.INTEGER, select.getPageSize());
                sql.append(") as paginated_result limit ?");

                sql.addParam(DataType.INTEGER, select.getFirstRow());
                sql.append(" offset ?");
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
            sql.SELECT().append("* ").FROM().append("(");
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
                sql.append(columnSQL(false, orderCriterion.column(), null)).append(" ").append(orderCriterion.order().name());
                if (i < select.getOrderCriteria().size() - 1) {
                    sql.append(",");
                }
                sql.append(" ");
            }
        }
        if (select.getCount()) {
            sql.append(") as count_result");
        }
        if (select.getPagination()) {
            sql.addParam(DataType.INTEGER, select.getPageSize());
            sql.append(") as paginated_result limit ?");
            sql.addParam(DataType.INTEGER, select.getFirstRow());
            sql.append(" offset ?");
        }
        return sql;
    }

    private String tableSQL(boolean onlyName, Table table) {
        StringBuilder sw = new StringBuilder();
        sw.append(table.name());
        if (!onlyName) {
            sw.append(" ").append(table.alias());
        }
        return sw.toString();
    }

    private Sql columnSQL(boolean onlyName, Column column, Map<String, Integer> indexMap) {
        Sql sql = new Sql();
        if (indexMap != null) {
            sql.append("col").append(Integer.toString(indexMap.get(column.columnId())));
        } else {
            if (column instanceof ColumnFunction columnFunction) {
                if (columnFunction.members() != null) {
                    List<String> membersList = new ArrayList<>();
                    for (Column member : columnFunction.members()) {
                        Sql memberSql = columnSQL(onlyName, member, indexMap);
                        membersList.add(memberSql.toSql());
                        memberSql.getParams().forEach((param) -> sql.addParam(param.dataType(), param.value()));
                    }
                    sql.append(MessageFormat.format(columnFunction.function(), membersList.toArray(new Object[0])));
                } else {
                    sql.append(columnFunction.function());
                }
            } else if (column instanceof ColumnSelect columnSelect) {
                sql.append("(").append(selectSQL(columnSelect.select())).append(")");
            } else {
                ColumnBase columnBase = (ColumnBase) column;
                if (!onlyName && columnBase.table() != null) {
                    sql.append(columnBase.table().alias()).append(".");
                }
                sql.append(columnBase.name());
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
                sql.addParam(column.dataType(), value);
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
            } else if (criterion.getValue() instanceof Column valueColumn) {
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
                                sql.addParam(criterion.getColumn().dataType(), val);
                                sql.append("?");
                                if (index < valueArray.length - 1) {
                                    sql.append(", ");
                                }
                            }
                            sql.append(")");
                        } else {
                            sql.addParam(criterion.getColumn().dataType(), criterion.getValue());
                            sql.append("?");
                        }
                    }
                } else {
                    if (criterion.getValue() != null) {
                        sql.addParam(criterion.getColumn().dataType(), criterion.getValue());
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
                dataTypes[i] = select.getColumns()[i].dataType();
            }
        }
        return executeQuery(connection, select(select), dataTypes);
    }

    @Override
    public Sql insert(Insert insert) {
        Sql sql = new Sql();
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
            sql.append(" returning ").append(columnSQL(true, insert.getReturning(), null));
        }
        return sql;
    }

    @Override
    public Object insert(Connection connection, Insert insert) throws SQLException {
        Object result = null;
        if (insert.getReturning() == null) {
            execute(connection, insert(insert));
        } else {
            result = execute(connection, insert(insert), insert.getReturning().dataType());
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
                case BOOLEAN -> result = resultSet.getBoolean(index);
                case STRING -> result = resultSet.getString(index);
                case SHORT -> result = resultSet.getShort(index);
                case INTEGER -> result = resultSet.getInt(index);
                case LONG -> result = resultSet.getLong(index);
                case BIG_DECIMAL -> result = resultSet.getBigDecimal(index);
                case DATE -> result = resultSet.getDate(index).toLocalDate();
                case TIME -> result = resultSet.getTime(index).toLocalTime();
                case TIME_STAMP -> result = resultSet.getTimestamp(index).toLocalDateTime();
                case BLOB -> {
                    InputStream is = null;
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", dir);
                        is = resultSet.getBinaryStream(index);
                        streamToFile(is, file);
                        result = file;
                    } catch (IOException e) {
                        delete(file);
                        throw new SQLException(e);
                    } finally {
                        close(is);
                    }
                }
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
        if (param.value() != null) {
            switch (param.dataType()) {
                case BLOB:
                    try {
                        File file = (File) param.value();
                        InputStream is = new FileInputStream(file);
                        streams.add(is);
                        preparedStatement.setBinaryStream(index, is, (int) file.length());
                    } catch (IOException e) {
                        throw new SQLException(e);
                    }
                    break;
                case DATE:
                    preparedStatement.setDate(index, Date.valueOf(((LocalDate) param.value())));
                    break;
                case TIME:
                    preparedStatement.setTime(index, Time.valueOf(((LocalTime) param.value())));
                    break;
                case TIME_STAMP:
                    preparedStatement.setTimestamp(index, Timestamp.valueOf(((LocalDateTime) param.value())));
                    break;
                default:
                    preparedStatement.setObject(index, param.value());
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
            List<InputStream> streams = new ArrayList<>();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
                setParams(preparedStatement, sql.getParams().toArray(new SqlParam[0]), streams);
                preparedStatement.executeUpdate();
            } finally {
                streams.forEach(this::close);
            }
        }
    }

    @Override
    public Object execute(Connection connection, Sql sql, DataType dataType) throws SQLException {
        LOGGER.debug(sql.toString());
        List<InputStream> streams = new ArrayList<>();
        Object result;
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
            setParams(preparedStatement, sql.getParams().toArray(new SqlParam[0]), streams);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = getColumn(resultSet, 1, dataType, getTmpDir());
        } finally {
            SqlUtil.close(resultSet);
            streams.forEach(this::close);
        }
        return result;
    }

    @Override
    public List<Object[]> executeQuery(Connection connection, Sql sql, DataType... dataTypes) throws SQLException {
        LOGGER.debug(sql.toString());
        List<Object[]> result = new ArrayList<>();
        List<InputStream> streams = new ArrayList<>();
        ResultSet resultSet = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
            setParams(preparedStatement, sql.getParams().toArray(new SqlParam[0]), streams);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Object[] row = getRow(resultSet, dataTypes);
                LOGGER.debug("ROW:{}", row);
                result.add(row);
            }
        } finally {
            SqlUtil.close(resultSet);
            streams.forEach(this::close);
        }
        LOGGER.debug("RESULT SIZE:{}", result.size());
        return result;
    }
}
