/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.r3n.sql.Column;
import sk.r3n.sql.ColumnFunction;
import sk.r3n.sql.ColumnSelect;
import sk.r3n.sql.Condition;
import sk.r3n.sql.CriteriaContent;
import sk.r3n.sql.Criteria;
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

/**
 * Oracle sql builder implementation.
 */
public class OraSqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = Logger.getLogger(OraSqlBuilder.class.getCanonicalName());

    @Override
    public Sql nextVal(Sequence sequence) {
        Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence)).append(" ").FROM().append("dual");
        return sql;
    }

    private String sequenceSQL(Sequence sequence) {
        StringBuilder sw = new StringBuilder();
        sw.append(sequence.getName());
        sw.append(".");
        sw.append("nextval");
        return sw.toString();
    }

    @Override
    public long nextVal(Connection connection, Sequence sequence) throws SQLException {
        long result;

        Sql sql = nextVal(sequence);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(sql.toString());
        }

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql.toSql());
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

        select.getJoinCriteria().stream().map((joinCriterion) -> {
            sql.append(" ").append(joinCriterion.getJoin().name().replaceAll("_", " "));
            return joinCriterion;
        }).map((joinCriterion) -> {
            sql.append(" ").JOIN().append(tableSQL(false, joinCriterion.getTable())).append(" ").ON();
            return joinCriterion;
        }).forEachOrdered((joinCriterion) -> {
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
                    sql.append(MessageFormat.format(columnFunction.getName(), membersList.toArray(new Object[membersList.size()])));
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

    @Override
    public void execute(Connection connection, Sql sql) throws SQLException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(sql.toString());
        }

        if (sql.getParams().isEmpty()) {
            SqlUtil.execute(connection, sql.toSql());
        } else {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(sql.toSql());
                setParams(connection, preparedStatement, sql.getParams().toArray(new SqlParam[sql.getParams().size()]));
                preparedStatement.executeUpdate();
            } finally {
                SqlUtil.close(preparedStatement);
            }
        }
    }

    @Override
    public Object execute(Connection connection, Sql sql, DataType dataType) throws SQLException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(sql.toString());
        }

        Object result = null;
        CallableStatement callableStatement = null;
        try {
            int index = sql.getParams().size() + 1;

            callableStatement = connection.prepareCall(sql.toSql());
            setParams(connection, callableStatement, sql.getParams().toArray(new SqlParam[sql.getParams().size()]));

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

    @Override
    public List<Object[]> executeQuery(Connection connection, Sql sql, DataType... dataTypes) throws SQLException {
        List<Object[]> result = new ArrayList<>();

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(sql.toString());
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql.toSql());
            setParams(connection, preparedStatement, sql.getParams().toArray(new SqlParam[sql.getParams().size()]));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Object[] row = getRow(resultSet, dataTypes);
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "ROW:{0}", Arrays.toString(row));
                }
                result.add(row);
            }
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "RESULT SIZE:{0}", result.size());
        }
        return result;
    }

}
