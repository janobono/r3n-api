package sk.r3n.jdbc;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlUtil {

    private static final String defaultDelimiter = ";";

    private static final String defaultEncoding = "UTF-8";

    public static void runSqlScript(Connection connection, String fileUri) throws Exception {
        runSqlScript(connection, fileUri, null, defaultDelimiter, null);
    }

    public static void runSqlScript(Connection connection, String fileUri, String fileEncoding, String commandsDelimiter)
            throws Exception {
        runSqlScript(connection, fileUri, fileEncoding, commandsDelimiter, null);
    }

    public static void runSqlScript(Connection connection, String fileUri, String fileEncoding, String commandsDelimiter,
            Map<String, String> replacementParametersMap) throws Exception {
        final Reader reader = getScriptReader(fileUri, fileEncoding);
        final List<String> commands = getSqlCommands(reader, commandsDelimiter);
        final List<String> adaptedCommands = (replacementParametersMap == null)
                ? commands
                : adaptSql(commands, replacementParametersMap);

        runSqlCommands(connection, adaptedCommands);
    }

    private static Reader getScriptReader(String fileUri, String fileEncoding) throws Exception {

        if (fileEncoding == null) {
            fileEncoding = defaultEncoding;
        }

        Reader reader = null;

        if (fileUri.contains("classpath:")) {
            String classPathFile = fileUri.split(":")[1];
            reader = new InputStreamReader(SqlUtil.class.getResourceAsStream(classPathFile), fileEncoding);
        }

        return reader;
    }

    private static List<String> getSqlCommands(Reader reader, String commandsDelimiter) throws Exception {
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
            for (Map.Entry<String, String> entry : stringsToReplaceMap.entrySet()) {
                adaptedSql = adaptedSql.replace(entry.getKey(), entry.getValue());
            }
            adaptedSqls.add(adaptedSql);
        }

        return adaptedSqls;
    }

    private static void runSqlCommands(Connection connection, List<String> commands) throws Exception {
        for (String command : commands) {
            execute(connection, command);
        }
    }

    public static void execute(Connection connection, String command) throws Exception {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(command);
        } finally {
            close(statement);
        }
    }

    public static void execute(Connection connection, String sql, Object[] params) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            prepare(statement, params);
            statement.executeUpdate();
        } finally {
            close(statement);
        }
    }

    public static PreparedStatement prepare(Connection connection, String sql, Object[] params) throws Exception {
        PreparedStatement result = connection.prepareStatement(sql);
        prepare(result, params);
        return result;
    }

    public static PreparedStatement prepare(PreparedStatement statement, Object[] params) throws Exception {
        if (params != null) {
            int i = 1;
            for (Object o : params) {
                statement.setObject(i, o);
                i++;
            }
        }
        return statement;
    }

    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (Exception e) {
            }
        }
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
}
