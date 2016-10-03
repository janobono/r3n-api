package sk.r3n.plugin.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.plugin.logging.Log;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.plugin.Structure;
import sk.r3n.plugin.StructureLoader;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;

public class H2StructureLoader extends StructureLoader {

    private enum DATA_TYPE {

        INT, BOOLEAN, TINYINT, SMALLINT, BIGINT, IDENTITY, DECIMAL, TIMESTAMP, TIME, DATE, BLOB, VARCHAR;

    }

    @Override
    public Structure load(Log log, Connection connection, String jdbcUser) throws Exception {
        Structure structure = new Structure();

        loadSequences(log, connection, structure);
        loadTables(log, connection, structure);

        for (Table table : structure.getTables()) {
            structure.getColumns(table).addAll(loadColumns(log, connection, table));
        }

        return structure;
    }

    private void loadSequences(Log log, Connection connection, Structure structure) throws SQLException {
        log.info("Sequences loading");

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA = SCHEMA()");
            while (resultSet.next()) {
                Sequence sequence = new Sequence(resultSet.getString(1));
                structure.getSequences().add(sequence);
                log.info("Sequnce found: " + sequence.getName());
            }

        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }

        log.info("Sequences loaded");
    }

    private void loadTables(Log log, Connection connection, Structure structure) throws SQLException {
        log.info("Tables loading");

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = SCHEMA()");
            int alias = 1;
            while (resultSet.next()) {
                Table table = new Table(resultSet.getString(1), "T" + alias++);
                structure.getTables().add(table);
                log.info("Table found: " + table.getName());
            }

        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }

        log.info("Tables loaded");
    }

    private List<Column> loadColumns(Log log, Connection connection, Table table) throws SQLException {
        log.info("Columns loading: " + table);
        List<Column> result = new ArrayList<Column>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT COLUMN_NAME, TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = SCHEMA() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION");
            statement.setString(1, table.getName());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Column column = new Column(resultSet.getString(1), table, getDataType(resultSet.getString(2)));
                result.add(column);
            }

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
            case TINYINT:
            case SMALLINT:
                result = DataType.SHORT;
                break;
            case INT:
                result = DataType.INTEGER;
                break;
            case BIGINT:
            case IDENTITY:
                result = DataType.LONG;
                break;
            case DECIMAL:
                result = DataType.BIG_DECIMAL;
                break;
            case VARCHAR:
                result = DataType.STRING;
                break;
            case BLOB:
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
