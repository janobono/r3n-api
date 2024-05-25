/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PostgreStructureLoader extends StructureLoader {

    final static Logger log = LoggerFactory.getLogger(PostgreStructureLoader.class);

    private enum DATA_TYPE {
        SMALLINT, INTEGER, BIGINT, DECIMAL, NUMERIC, CHARACTER, TEXT, BYTEA, TIMESTAMP, TIME, DATE, BOOLEAN
    }

    @Override
    protected void loadSequences(final Connection connection, final Structure structure) {
        log.info("Sequences loading");

        try (final PreparedStatement statement = connection.prepareStatement("SELECT c.relname FROM pg_class c WHERE c.relkind = ?")) {
            statement.setString(1, "S");
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Sequence sequence = new Sequence(resultSet.getString(1));
                structure.getSequences().add(sequence);
                log.info("Sequence found: " + sequence.name());
            }
            resultSet.close();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        log.info("Sequences loaded");
    }

    @Override
    protected void loadTables(final Connection connection, final Structure structure) {
        log.info("Tables loading");

        try (
                final PreparedStatement statement = connection.prepareStatement(
                        "SELECT table_name FROM information_schema.tables WHERE table_schema=? AND (table_type =? OR table_type =?)"
                )
        ) {
            statement.setString(1, "public");
            statement.setString(2, "BASE TABLE");
            statement.setString(3, "VIEW");
            final ResultSet resultSet = statement.executeQuery();
            int alias = 1;
            while (resultSet.next()) {
                final Table table = new Table(resultSet.getString(1), "T" + alias++);
                structure.getTables().add(table);
                log.info("Table found: " + table.name());
            }
            resultSet.close();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        log.info("Tables loaded");
    }

    @Override
    protected List<Column> loadColumns(final Connection connection, final Table table) {
        log.info("Columns loading: " + table);
        final List<Column> result = new LinkedList<>();

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT column_name, data_type FROM information_schema.columns WHERE table_name  = ? order by ordinal_position"
                )
        ) {
            statement.setString(1, table.name().toLowerCase());
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Column column = Column.column(resultSet.getString(1), getDataType(resultSet.getString(2)), table);
                result.add(column);
            }
            resultSet.close();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        log.info("Columns loaded: " + Arrays.toString(result.toArray()));
        return result;
    }

    private DataType getDataType(final String typeName) {
        final DataType result;

        DATA_TYPE data_type = null;
        for (final DATA_TYPE dt : DATA_TYPE.values()) {
            if (typeName.toLowerCase().startsWith(dt.name().toLowerCase())) {
                data_type = dt;
                break;
            }
        }
        if (data_type == null) {
            throw new RuntimeException("Unsupported data type");
        }
        result = switch (data_type) {
            case SMALLINT -> DataType.SHORT;
            case INTEGER -> DataType.INTEGER;
            case BIGINT -> DataType.LONG;
            case DECIMAL, NUMERIC -> DataType.BIG_DECIMAL;
            case CHARACTER, TEXT -> DataType.STRING;
            case BYTEA -> DataType.BLOB;
            case TIMESTAMP -> DataType.TIME_STAMP;
            case TIME -> DataType.TIME;
            case DATE -> DataType.DATE;
            case BOOLEAN -> DataType.BOOLEAN;
        };
        return result;
    }
}
