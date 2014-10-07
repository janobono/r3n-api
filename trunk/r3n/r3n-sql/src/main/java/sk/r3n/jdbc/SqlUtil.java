package sk.r3n.jdbc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.sql.Column;
import sk.r3n.util.FileUtil;

public class SqlUtil {

    private static final Log LOG = LogFactory.getLog(SqlUtil.class);

    private static final String DEFAULT_DELIMITER = ";";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static File tmpDir;

    public static File getTmpDir() {
        if (tmpDir == null) {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            LOG.warn("Default tmp dir will be used - " + tmpDir.getAbsolutePath());
        }
        return tmpDir;
    }

    public static void setTmpDir(File tmpDir) {
        SqlUtil.tmpDir = tmpDir;
    }

    public static void runSqlScript(Connection connection, InputStream is) throws Exception {
        runSqlScript(connection, is, null, DEFAULT_DELIMITER, null);
    }

    public static void runSqlScript(Connection connection, InputStream is, String fileEncoding, String commandsDelimiter)
            throws Exception {
        runSqlScript(connection, is, fileEncoding, commandsDelimiter, null);
    }

    public static void runSqlScript(Connection connection, InputStream is, String fileEncoding, String commandsDelimiter,
            Map<String, String> replacementParametersMap) throws Exception {
        final Reader reader = getScriptReader(is, fileEncoding);
        final List<String> commands = getSqlCommands(reader, commandsDelimiter);
        final List<String> adaptedCommands = (replacementParametersMap == null)
                ? commands
                : adaptSql(commands, replacementParametersMap);

        runSqlCommands(connection, adaptedCommands);
    }

    private static Reader getScriptReader(InputStream is, String fileEncoding) throws Exception {
        if (fileEncoding == null) {
            fileEncoding = DEFAULT_ENCODING;
        }
        return new InputStreamReader(is, fileEncoding);
    }

    private static List<String> getSqlCommands(Reader reader, String commandsDelimiter) throws Exception {
        List<String> commands = new ArrayList<String>();

        LineNumberReader lineReader = new LineNumberReader(reader);
        String line;

        StringBuffer command = new StringBuffer();
        while ((line = lineReader.readLine()) != null) {
            String trimmedLine = line.trim();

            if (trimmedLine.length() < 1
                    || trimmedLine.startsWith("//")
                    || trimmedLine.startsWith("--")) {
                // Pass
            } else if (trimmedLine.endsWith("\\" + commandsDelimiter)) {
                command.append(line.replace("\\" + commandsDelimiter, ";")).append(" ");
            } else if (trimmedLine.endsWith(commandsDelimiter)) {
                commands.add(command.append(line.substring(0, line.lastIndexOf(commandsDelimiter))).append(" ").toString());

                command = new StringBuffer();
            } else {
                command.append(line).append(" ");
            }
        }

        return commands;
    }

    private static List<String> adaptSql(List<String> sqls, Map<String, String> stringsToReplaceMap) {
        List<String> adaptedSqls = new ArrayList<String>();

        for (String sql : sqls) {
            String adaptedSql = sql;
            for (Map.Entry<String, String> entry : stringsToReplaceMap.entrySet()) {
                adaptedSql = adaptedSql.replace(entry.getKey(), entry.getValue());
            }
            adaptedSqls.add(adaptedSql);
        }

        return adaptedSqls;
    }

    private static void runSqlCommands(Connection connection, List<String> commands) throws Exception {
        SQLException se = null;
        for (String command : commands) {
            try {
                execute(connection, command);
            } catch (SQLException e) {
                LOG.warn(e, e);
                if (se == null) {
                    se = e;
                }
            }
        }

        if (se != null) {
            throw se;
        }
    }

    public static void execute(Connection connection, String command) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(command);
        } finally {
            close(statement);
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public static String arrayToString(Object[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i < array.length; i++) {
            if (array[i] instanceof String) {
                sb.append('\'');
            }
            sb.append(array[i]);
            if (array[i] instanceof String) {
                sb.append('\'');
            }
            if (i < array.length - 1) {
                sb.append(',');
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public static Object getColumn(ResultSet resultSet, int index, Column column) throws SQLException {
        Object result = null;

        if (resultSet.getObject(index) != null) {
            switch (column.getDataType()) {
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
                        file = File.createTempFile("SQL", ".BIN", getTmpDir());
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

    public static void setParams(Connection connection, PreparedStatement preparedStatement, SqlParam[] params) throws SQLException {
        int i = 1;
        for (SqlParam param : params) {
            if (param.getValue() != null) {
                switch (param.getDataType()) {
                    case BLOB:
                        Blob blob = connection.createBlob();
                        FileUtil.fileToStream((File) param.getValue(), blob.setBinaryStream(1));
                        preparedStatement.setBlob(i++, blob);
                        break;
                    case DATE:
                        preparedStatement.setDate(i++, new java.sql.Date(((java.util.Date) param.getValue()).getTime()));
                        break;
                    case TIME:
                        preparedStatement.setTime(i++, new java.sql.Time(((java.util.Date) param.getValue()).getTime()));
                        break;
                    case TIME_STAMP:
                        preparedStatement.setTimestamp(i++, new java.sql.Timestamp(((java.util.Date) param.getValue()).getTime()));
                        break;
                    default:
                        preparedStatement.setObject(i++, param.getValue());
                        break;
                }
            } else {
                preparedStatement.setNull(i++, Types.NULL);
            }
        }
    }

    public static void setParam(Connection connection, PreparedStatement preparedStatement, int index, SqlParam param) throws SQLException {
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
}
