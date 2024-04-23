/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Sequence;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Oracle sql builder implementation.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class OraSqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(OraSqlBuilder.class);

    public OraSqlBuilder() {
    }

    public OraSqlBuilder(final Boolean blobFile) {
        super(blobFile);
    }

    @Override
    public Sql nextVal(final Sequence sequence) {
        final Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence)).append(" ").FROM().append("dual");
        return sql;
    }

    @Override
    protected String sequenceSQL(final Sequence sequence) {
        return sequence.name() + "." + "nextval";
    }

    @Override
    protected void selectSubSelectsStart(final Select select, final Sql sql) {
        sql.append(" ").FROM().append("(");
    }

    @Override
    protected void selectSubSelectsEnd(final Select select, final Sql sql) {
        sql.append(")");
    }

    @Override
    protected void selectStartCount(final Select select, final Sql sql) {
        sql.SELECT().append("count(*) ").FROM().append("(");
    }

    @Override
    protected void selectStartPagination(final Select select, final Sql sql) {
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

    @Override
    protected void selectEndCount(final Select select, final Sql sql) {
        sql.append(")");
    }

    @Override
    protected void selectEndPagination(final Select select, final Sql sql) {
        sql.addParam(DataType.INTEGER, select.getLastRow() + 1);
        sql.append(") ").WHERE().append("rownum <= ?");
        sql.addParam(DataType.INTEGER, select.getFirstRow() + 1);
        sql.append(") ").WHERE().append("rnm >= ?");
    }

    @Override
    protected void insertStartReturning(final Insert insert, final Sql sql) {
        if (insert.getReturning() != null) {
            sql.append("begin ");
        }
    }

    @Override
    protected void insertEndReturning(final Insert insert, final Sql sql) {
        if (insert.getReturning() != null) {
            sql.append(" returning ").append(columnSQL(true, insert.getReturning(), null)).append(" into ?;end;");
        }
    }

    private Object[] getRow(final ResultSet resultSet, final DataType... dataTypes) throws SQLException {
        final Object[] result = new Object[dataTypes.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getColumn(resultSet, i + 1, dataTypes[i], getTmpDir());
        }
        return result;
    }

    private Object getColumn(final ResultSet resultSet, final int index, final DataType dataType, final File dir) throws SQLException {
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
                    if (getBlobFile()) {
                        try {
                            final File file = File.createTempFile("SQL", ".BIN", dir);
                            final Blob blob = resultSet.getBlob(index);
                            if (blob.length() > 0) {
                                streamToFile(blob.getBinaryStream(1, blob.length()), file);
                            }
                            result = file;
                        } catch (final IOException e) {
                            throw new SQLException(e);
                        }
                    } else {
                        result = resultSet.getBytes(index);
                    }
                }
            }
        }
        return result;
    }

    private void setParams(final Connection connection, final PreparedStatement preparedStatement, final SqlParam[] params) throws SQLException {
        int i = 1;
        for (final SqlParam param : params) {
            setParam(connection, preparedStatement, i++, param);
        }
    }

    private void setParam(final Connection connection, final PreparedStatement preparedStatement, final int index, final SqlParam param) throws SQLException {
        if (param.value() != null) {
            switch (param.dataType()) {
                case BLOB -> {
                    if (getBlobFile()) {
                        final Blob blob = connection.createBlob();
                        fileToStream((File) param.value(), blob.setBinaryStream(1));
                        preparedStatement.setBlob(index, blob);
                    } else {
                        preparedStatement.setBytes(index, (byte[]) param.value());
                    }
                }
                case DATE -> preparedStatement.setDate(index, Date.valueOf(((LocalDate) param.value())));
                case TIME -> preparedStatement.setTime(index, Time.valueOf(((LocalTime) param.value())));
                case TIME_STAMP ->
                        preparedStatement.setTimestamp(index, Timestamp.valueOf(((LocalDateTime) param.value())));
                default -> preparedStatement.setObject(index, param.value());
            }
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    @Override
    public void execute(final Connection connection, final Sql sql) throws SQLException {
        LOGGER.debug(sql.toString());
        if (sql.getParams().isEmpty()) {
            SqlUtil.execute(connection, sql.toSql());
        } else {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
                setParams(connection, preparedStatement, sql.getParams().toArray(new SqlParam[0]));
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public Object execute(final Connection connection, final Sql sql, final DataType dataType) throws SQLException {
        LOGGER.debug(sql.toString());
        Object result = null;
        try (final CallableStatement callableStatement = connection.prepareCall(sql.toSql())) {
            final int index = sql.getParams().size() + 1;
            setParams(connection, callableStatement, sql.getParams().toArray(new SqlParam[0]));

            switch (dataType) {
                case BOOLEAN -> callableStatement.registerOutParameter(index, Types.BOOLEAN);
                case STRING -> callableStatement.registerOutParameter(index, Types.VARCHAR);
                case SHORT -> callableStatement.registerOutParameter(index, Types.SMALLINT);
                case INTEGER -> callableStatement.registerOutParameter(index, Types.INTEGER);
                case LONG -> callableStatement.registerOutParameter(index, Types.BIGINT);
                case BIG_DECIMAL -> callableStatement.registerOutParameter(index, Types.NUMERIC);
                case DATE -> callableStatement.registerOutParameter(index, Types.DATE);
                case TIME -> callableStatement.registerOutParameter(index, Types.TIME);
                case TIME_STAMP -> callableStatement.registerOutParameter(index, Types.TIMESTAMP);
                case BLOB -> callableStatement.registerOutParameter(index, Types.BLOB);
            }

            callableStatement.execute();

            switch (dataType) {
                case BOOLEAN -> result = callableStatement.getBoolean(index);
                case STRING -> result = callableStatement.getString(index);
                case SHORT -> result = callableStatement.getShort(index);
                case INTEGER -> result = callableStatement.getInt(index);
                case LONG -> result = callableStatement.getLong(index);
                case BIG_DECIMAL -> result = callableStatement.getBigDecimal(index);
                case DATE -> result = callableStatement.getDate(index).toLocalDate();
                case TIME -> result = callableStatement.getTime(index).toLocalTime();
                case TIME_STAMP -> result = callableStatement.getTimestamp(index).toLocalDateTime();
                case BLOB -> {
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", getTmpDir());
                        final Blob blob = callableStatement.getBlob(index);
                        streamToFile(blob.getBinaryStream(1, blob.length()), file);
                        result = file;
                    } catch (final IOException e) {
                        throw new SQLException(e);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<Object[]> executeQuery(final Connection connection, final Sql sql, final DataType... dataTypes) throws SQLException {
        LOGGER.debug(sql.toString());
        final List<Object[]> result = new LinkedList<>();
        ResultSet resultSet = null;
        try (final PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
            setParams(connection, preparedStatement, sql.getParams().toArray(new SqlParam[0]));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Object[] row = getRow(resultSet, dataTypes);
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
