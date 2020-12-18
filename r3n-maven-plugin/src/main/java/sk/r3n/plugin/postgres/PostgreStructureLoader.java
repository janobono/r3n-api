/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin.postgres;

import org.apache.maven.plugin.logging.Log;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.plugin.Structure;
import sk.r3n.plugin.StructureLoader;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgreStructureLoader extends StructureLoader {

    private enum DATA_TYPE {
        SMALLINT, INTEGER, BIGINT, DECIMAL, NUMERIC, CHARACTER, TEXT, BYTEA, TIMESTAMP, TIME, DATE, BOOLEAN
    }

    @Override
    protected void loadSequences(Log log, Connection connection, Structure structure) {
        log.info("Sequences loading");

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT c.relname FROM pg_class c WHERE c.relkind = ?");
            statement.setString(1, "S");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Sequence sequence = new Sequence(resultSet.getString(1));
                structure.getSequences().add(sequence);
                log.info("Sequence found: " + sequence.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }

        log.info("Sequences loaded");
    }

    @Override
    protected void loadTables(Log log, Connection connection, Structure structure) {
        log.info("Tables loading");

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema=? AND table_type =?"
            );
            statement.setString(1, "public");
            statement.setString(2, "BASE TABLE");
            resultSet = statement.executeQuery();
            int alias = 1;
            while (resultSet.next()) {
                Table table = new Table(resultSet.getString(1), "T" + alias++);
                structure.getTables().add(table);
                log.info("Table found: " + table.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }

        log.info("Tables loaded");
    }

    @Override
    protected List<Column> loadColumns(Log log, Connection connection, Table table) {
        log.info("Columns loading: " + table);
        List<Column> result = new ArrayList<>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(
                    "SELECT column_name, data_type FROM information_schema.columns WHERE table_name  = ? order by ordinal_position"
            );
            statement.setString(1, table.getName().toLowerCase());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Column column = new Column(resultSet.getString(1), table, getDataType(resultSet.getString(2)));
                result.add(column);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }
        log.info("Columns loaded: " + Arrays.toString(result.toArray()));
        return result;
    }

    private DataType getDataType(String typeName) {
        DataType result = null;

        DATA_TYPE data_type = null;
        for (DATA_TYPE dt : DATA_TYPE.values()) {
            if (typeName.toLowerCase().startsWith(dt.name().toLowerCase())) {
                data_type = dt;
                break;
            }
        }
        if (data_type == null) {
            throw new RuntimeException("Unsupported data type");
        }
        switch (data_type) {
            case SMALLINT:
                result = DataType.SHORT;
                break;
            case INTEGER:
                result = DataType.INTEGER;
                break;
            case BIGINT:
                result = DataType.LONG;
                break;
            case DECIMAL:
            case NUMERIC:
                result = DataType.BIG_DECIMAL;
                break;
            case CHARACTER:
            case TEXT:
                result = DataType.STRING;
                break;
            case BYTEA:
                result = DataType.BLOB;
                break;
            case TIMESTAMP:
                result = DataType.TIME_STAMP;
                break;
            case TIME:
                result = DataType.TIME;
                break;
            case DATE:
                result = DataType.DATE;
                break;
            case BOOLEAN:
                result = DataType.BOOLEAN;
                break;
        }
        return result;
    }
}
