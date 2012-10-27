package sk.r3n.db;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlUtil {

    private static final String DEFAULT_DELIMITER = ";";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final Logger LOGGER = Logger.getLogger(SqlUtil.class.getCanonicalName());

    public static Connection createDatabaseConnection(String driverName, String dbUrl, Properties connectionProperties)
            throws SQLException, ClassNotFoundException {
        Class.forName(driverName);
        return DriverManager.getConnection(dbUrl, connectionProperties);
    }

    public static void runSqlScript(Connection connection, String fileUri) throws IOException, SQLException {
        runSqlScript(connection, fileUri, null, DEFAULT_DELIMITER, null);
    }

    public static void runSqlScript(Connection connection, String fileUri, String fileEncoding, String commandsDelimiter)
            throws UnsupportedEncodingException, IOException, SQLException {
        runSqlScript(connection, fileUri, fileEncoding, commandsDelimiter, null);
    }

    public static void runSqlScript(Connection connection, String fileUri, String fileEncoding,
            String commandsDelimiter, Map<String, String> replacementParametersMap)
            throws UnsupportedEncodingException, IOException, SQLException {
        final Reader reader = getScriptReader(fileUri, fileEncoding);
        final List<String> commands = getSqlCommands(reader, commandsDelimiter);
        final List<String> adaptedCommands = (replacementParametersMap == null)
                ? commands
                : adaptSql(commands, replacementParametersMap);
        logCommads(adaptedCommands);
        runSqlCommands(connection, adaptedCommands);
    }

    public static Object runSingleValueQuery(Connection conn, String singleValueQuery, Object[] params)
            throws SQLException {
        Object returnValue = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(singleValueQuery);
            prepareStatement(statement, params);
            resultSet = statement.executeQuery();
            if (resultSet.next() == true) {
                returnValue = resultSet.getObject(1);
            }
        } finally {
            close(resultSet);
            close(statement);
        }
        return returnValue;
    }

    public static void executeStatement(Connection connection, String command) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(command);
        close(statement);
    }

    public static void executeStatement(Connection connection, String sql, Object[] params) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            prepareStatement(statement, params);
            statement.executeUpdate();
        } finally {
            close(statement);
        }
    }

    public static ResultSet executeSelect(Connection connection, String sql, Object[] params) throws SQLException {
        ResultSet resultSet;
        PreparedStatement statement = connection.prepareStatement(sql);
        prepareStatement(statement, params);
        resultSet = statement.executeQuery();
        return resultSet;
    }

    public static PreparedStatement prepareStatement(Connection connection, String sql, Object[] params)
            throws SQLException {
        PreparedStatement result = connection.prepareStatement(sql);
        prepareStatement(result, params);
        return result;
    }

    public static PreparedStatement prepareStatement(PreparedStatement statement, Object[] params)
            throws SQLException {
        if (params != null) {
            int i = 1;
            for (Object o : params) {
                statement.setObject(i, o);
                i++;
            }
        }
        return statement;
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
            }
        }
    }

    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
    }

    private static Reader getScriptReader(String fileUri, String fileEncoding)
            throws UnsupportedEncodingException {
        if (fileEncoding == null) {
            fileEncoding = DEFAULT_ENCODING;
        }
        Reader reader = null;
        if (fileUri.contains("classpath:")) {
            String classPathFile = fileUri.split(":")[1];
            reader = new InputStreamReader(SqlUtil.class.getResourceAsStream(classPathFile), fileEncoding);
        }
        return reader;
    }

    private static List<String> getSqlCommands(Reader reader, String commandsDelimiter) throws IOException {
        List<String> commands = new ArrayList<>();
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
        List<String> adaptedSqls = new ArrayList<>();
        for (String sql : sqls) {
            String adaptedSql = sql;
            for (Entry<String, String> entry : stringsToReplaceMap.entrySet()) {
                adaptedSql = adaptedSql.replace(entry.getKey(), entry.getValue());
            }
            adaptedSqls.add(adaptedSql);
        }
        return adaptedSqls;
    }

    private static void logCommads(List<String> adaptedCommands) {
        LOGGER.log(Level.INFO, "SqlUtil: Number of commands to execute -> {0}", adaptedCommands.size());
        for (int i = 0; i < adaptedCommands.size(); i++) {
            LOGGER.log(Level.INFO, "SqlUtil: Command number {0} -> {1}", new Object[]{i, adaptedCommands.get(i)});
        }
    }

    private static void runSqlCommands(Connection connection, List<String> commands)
            throws SQLException {
        for (String command : commands) {
            executeStatement(connection, command);
        }
    }

}
