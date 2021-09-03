/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin.oracle;

import org.apache.maven.plugin.logging.Log;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.plugin.Structure;
import sk.r3n.plugin.StructureLoader;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleStructureLoader extends StructureLoader {

    private enum DATA_TYPE {
        NUMBER, CHAR, NCHAR, VARCHAR, NVARCHAR, CLOB, NCLOB, BLOB, TIMESTAMP, DATE
    }

    @Override
    protected void loadSequences(Log log, Connection connection, Structure structure) {
        log.info("Sequences loading");

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select sequence_name from user_sequences");
            while (resultSet.next()) {
                Sequence sequence = new Sequence(resultSet.getString(1));
                structure.getSequences().add(sequence);
                log.info("Sequence found: " + sequence.name());
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

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select table_name from user_tables");
            int alias = 1;
            while (resultSet.next()) {
                Table table = new Table(resultSet.getString(1), "T" + alias++);
                structure.getTables().add(table);
                log.info("Table found: " + table.name());
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
                    "select column_name, data_type, data_precision, data_scale from user_tab_columns where table_name = ? order by column_id"
            );
            statement.setString(1, table.name());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Column column = Column.column(
                        resultSet.getString(1),
                        getDataType(resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4)),
                        table
                );
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

    private DataType getDataType(String typeName, int precision, int scale) {
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
            case NUMBER:
                if (precision > 0 && scale == 0) {
                    if (precision == 1) {
                        result = DataType.BOOLEAN;
                    } else if (precision <= 5) {
                        result = DataType.SHORT;
                    } else if (precision <= 10) {
                        result = DataType.INTEGER;
                    } else if (precision <= 19) {
                        result = DataType.LONG;
                    } else {
                        result = DataType.BIG_DECIMAL;
                    }
                } else {
                    result = DataType.BIG_DECIMAL;
                }
                break;
            case CHAR:
            case NCHAR:
            case VARCHAR:
            case NVARCHAR:
            case CLOB:
            case NCLOB:
                result = DataType.STRING;
                break;
            case BLOB:
                result = DataType.BLOB;
                break;
            case TIMESTAMP:
                result = DataType.TIME_STAMP;
                break;
            case DATE:
                result = DataType.DATE;
                break;
        }
        return result;
    }
}
