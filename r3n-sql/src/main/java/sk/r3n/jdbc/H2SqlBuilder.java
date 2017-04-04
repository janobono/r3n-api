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
import sk.r3n.sql.Query;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;
import sk.r3n.util.FileUtil;

/**
 * H2 sql builder implementation.
 */
public class H2SqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = Logger.getLogger(H2SqlBuilder.class.getCanonicalName());

    @Override
    public Sql nextVal(Sequence sequence) {
        Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence));
        return sql;
    }

    private String sequenceSQL(Sequence sequence) {
        StringBuilder sw = new StringBuilder();
        sw.append("nextval('").append(sequence.getName()).append("')");
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
    public Sql select(Query.Select select) {
        Sql sql = new Sql();

        if (select.getSubSelects() != null && select.getSubSelects().length > 0) {
            Map<String, Integer> indexMap = new HashMap<>();

            if (select.getCount()) {
                sql.SELECT().append("count(*) ").FROM().append("(");
            }

            if (select.getPagination()) {
                sql.SELECT().append("* ").FROM().append("(");
            }

            for (Query.Select subSelect : select.getSubSelects()) {
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

            sql.FROM().append("(");

            for (int i = 0; i < select.getSubSelects().length; i++) {
                Query.Select subSelect = select.getSubSelects()[i];
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
                    sql.append(columnSQL(false, orderCriterion.getColumn(), indexMap)).append(" ").append(orderCriterion.getOrder().name());
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

    private Sql selectSQL(Query.Select select) {
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
            if (criterion.getValue() instanceof Query.Select) {
                sql.append(columnSQL(onlyName, criterion.getColumn(), indexMap)).append(" ").append(criterion.getCondition().condition());
                sql.append(" (").append(selectSQL((Query.Select) criterion.getValue())).append(")");
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
    public List<Object[]> select(Connection connection, Query.Select select) throws SQLException {
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
    public Sql insert(Query.Insert insert) {
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

        return sql;
    }

    @Override
    public Object insert(Connection connection, Query.Insert insert) throws SQLException {
        Object result = null;

        execute(connection, insert(insert));

        if (insert.getReturning() != null) {
            result = select(connection, Query
                    .SELECT(new ColumnFunction("i1", "max({0})", insert.getReturning().getDataType(), insert.getReturning()))
                    .FROM(insert.getTable())).get(0)[0];
        }

        return result;
    }

    @Override
    public Sql update(Query.Update update) {
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
    public void update(Connection connection, Query.Update update) throws SQLException {
        execute(connection, update(update));
    }

    @Override
    public Sql delete(Query.Delete delete) {
        Sql sql = new Sql();

        sql.DELETE().FROM().append(tableSQL(true, delete.getTable()));
        if (delete.getCriteriaManager().isCriteria()) {
            sql.append(" ").WHERE().append(criteriaManagerSQL(true, delete.getCriteriaManager(), null));
        }

        return sql;
    }

    @Override
    public void delete(Connection connection, Query.Delete delete) throws SQLException {
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

    @Override
    public void execute(Connection connection, Sql sql) throws SQLException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(sql.toString());
        }

        if (sql.getParams().isEmpty()) {
            SqlUtil.execute(connection, sql.toSql());
        } else {
            List<InputStream> streams = new ArrayList<>();

            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(sql.toSql());
                setParams(preparedStatement, sql.getParams().toArray(new SqlParam[sql.getParams().size()]), streams);
                preparedStatement.executeUpdate();
            } finally {
                SqlUtil.close(preparedStatement);

                streams.forEach((stream) -> {
                    FileUtil.close(stream);
                });
            }
        }
    }

    @Override
    public Object execute(Connection connection, Sql sql, DataType dataType) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Object[]> executeQuery(Connection connection, Sql sql, DataType... dataTypes) throws SQLException {
        List<Object[]> result = new ArrayList<>();

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest(sql.toString());
        }

        List<InputStream> streams = new ArrayList<>();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql.toSql());
            setParams(preparedStatement, sql.getParams().toArray(new SqlParam[sql.getParams().size()]), streams);
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

            streams.forEach((stream) -> {
                FileUtil.close(stream);
            });
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "RESULT SIZE:{0}", result.size());
        }
        return result;
    }

}
