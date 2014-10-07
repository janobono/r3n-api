package sk.r3n.jdbc;

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

public class SqlUtil {

    private static final Log LOG = LogFactory.getLog(SqlUtil.class);

    private static final String DEFAULT_DELIMITER = ";";

    private static final String DEFAULT_ENCODING = "UTF-8";

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
}
