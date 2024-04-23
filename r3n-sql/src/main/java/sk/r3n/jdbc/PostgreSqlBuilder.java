/*
 * Copyright 2016 janobono. All rights reserved.
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * PostgreSQL sql builder implementation.
 */
public class PostgreSqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlBuilder.class);

    public PostgreSqlBuilder() {
    }

    public PostgreSqlBuilder(final Boolean blobFile) {
        super(blobFile);
    }

    @Override
    public Sql nextVal(final Sequence sequence) {
        final Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence));
        return sql;
    }

    @Override
    protected String sequenceSQL(final Sequence sequence) {
        return "nextval('" + sequence.name() + "')";
    }

    @Override
    protected void selectSubSelectsStart(final Select select, final Sql sql) {
        sql.FROM().append("(");
    }

    @Override
    protected void selectSubSelectsEnd(final Select select, final Sql sql) {
        sql.append(") as union_result ");
    }

    @Override
    protected void selectStartCount(final Select select, final Sql sql) {
        sql.SELECT().append("count(*) ").FROM().append("(");
    }

    @Override
    protected void selectStartPagination(final Select select, final Sql sql) {
        sql.SELECT().append("* ").FROM().append("(");
    }

    @Override
    protected void selectEndCount(final Select select, final Sql sql) {
        sql.append(") as count_result");
    }

    @Override
    protected void selectEndPagination(final Select select, final Sql sql) {
        sql.addParam(DataType.INTEGER, select.getPageSize());
        sql.append(") as paginated_result limit ?");
        sql.addParam(DataType.INTEGER, select.getFirstRow());
        sql.append(" offset ?");
    }

    @Override
    protected void insertStartReturning(final Insert insert, final Sql sql) {
    }

    @Override
    protected void insertEndReturning(final Insert insert, final Sql sql) {
        if (insert.getReturning() != null) {
            sql.append(" returning ").append(columnSQL(true, insert.getReturning(), null));
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
                        InputStream is = null;
                        try {
                            final File file = File.createTempFile("SQL", ".BIN", dir);
                            is = resultSet.getBinaryStream(index);
                            streamToFile(is, file);
                            result = file;
                        } catch (final IOException e) {
                            throw new SQLException(e);
                        } finally {
                            close(is);
                        }
                    } else {
                        result = resultSet.getBytes(index);
                    }
                }
            }
        }
        return result;
    }

    private void setParams(final PreparedStatement preparedStatement, final SqlParam[] params, final List<InputStream> streams) throws SQLException {
        int i = 1;
        for (final SqlParam param : params) {
            setParam(preparedStatement, i++, param, streams);
        }
    }

    private void setParam(final PreparedStatement preparedStatement, final int index, final SqlParam param, final List<InputStream> streams) throws SQLException {
        if (param.value() != null) {
            switch (param.dataType()) {
                case BLOB -> {
                    if (getBlobFile()) {
                        try {
                            final File file = (File) param.value();
                            final InputStream is = new FileInputStream(file);
                            streams.add(is);
                            preparedStatement.setBinaryStream(index, is, (int) file.length());
                        } catch (final IOException e) {
                            throw new SQLException(e);
                        }
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
            final List<InputStream> streams = new LinkedList<>();
            try (final PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
                setParams(preparedStatement, sql.getParams().toArray(new SqlParam[0]), streams);
                preparedStatement.executeUpdate();
            } finally {
                streams.forEach(this::close);
            }
        }
    }

    @Override
    public Object execute(final Connection connection, final Sql sql, final DataType dataType) throws SQLException {
        LOGGER.debug(sql.toString());
        final List<InputStream> streams = new LinkedList<>();
        Object result;
        ResultSet resultSet = null;
        try (final PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
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
    public List<Object[]> executeQuery(final Connection connection, final Sql sql, final DataType... dataTypes) throws SQLException {
        LOGGER.debug(sql.toString());
        final List<Object[]> result = new LinkedList<>();
        final List<InputStream> streams = new LinkedList<>();
        ResultSet resultSet = null;
        try (final PreparedStatement preparedStatement = connection.prepareStatement(sql.toSql())) {
            setParams(preparedStatement, sql.getParams().toArray(new SqlParam[0]), streams);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final Object[] row = getRow(resultSet, dataTypes);
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
