package sk.r3n.plugin.oracle;

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

public class OracleStructureLoader extends StructureLoader {

    private enum DATA_TYPE {

        NUMBER, CHAR, NCHAR, VARCHAR, NVARCHAR, CLOB, NCLOB, BLOB, TIMESTAMP, DATE;

    }

    @Override
    public Structure load(Log log, Connection connection, String jdbcUser) throws Exception {
        Structure structure = new Structure();

        loadSequences(log, connection, jdbcUser, structure);
        loadTables(log, connection, jdbcUser, structure);

        for (Table table : structure.getTables()) {
            structure.getColumns(table).addAll(loadColumns(log, connection, jdbcUser, table));
        }

        return structure;
    }

    private void loadSequences(Log log, Connection connection, String jdbcUser, Structure structure) throws SQLException {
        log.info("Sequences loading");

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select sequence_name from user_sequences");
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

    private void loadTables(Log log, Connection connection, String jdbcUser, Structure structure) throws SQLException {
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
                log.info("Table found: " + table.getName());
            }

        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(statement);
        }

        log.info("Tables loaded");
    }

    private List<Column> loadColumns(Log log, Connection connection, String jdbcUser, Table table) throws SQLException {
        log.info("Columns loading: " + table);
        List<Column> result = new ArrayList<>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("select column_name, data_type, data_precision, data_scale from user_tab_columns where table_name = ? order by column_id");
            statement.setString(1, table.getName());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Column column = new Column(resultSet.getString(1), table, getDataType(resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4)));
                result.add(column);
            }

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
