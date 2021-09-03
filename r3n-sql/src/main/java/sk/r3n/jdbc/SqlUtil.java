/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base sql utils.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class SqlUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlUtil.class);

    /**
     * A String for a default commands delimiter.
     */
    private static final String DEFAULT_DELIMITER = ";";

    /**
     * A String for a default commands encoding.
     */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Runs sql script.
     *
     * @param connection Connection to database.
     * @param is         Stream to sql script.
     * @throws Exception
     */
    public static void runSqlScript(Connection connection, InputStream is) throws Exception {
        runSqlScript(connection, is, null, DEFAULT_DELIMITER, null);
    }

    /**
     * Runs sql script.
     *
     * @param connection        Connection to database.
     * @param is                Strieam to sql script.
     * @param fileEncoding      Script encoding.
     * @param commandsDelimiter Commands delimiter.
     * @throws Exception
     */
    public static void runSqlScript(Connection connection, InputStream is, String fileEncoding, String commandsDelimiter)
            throws Exception {
        runSqlScript(connection, is, fileEncoding, commandsDelimiter, null);
    }

    /**
     * Runs sql script.
     *
     * @param connection               Connection to database.
     * @param is                       Strieam to sql script.
     * @param fileEncoding             Script encoding.
     * @param commandsDelimiter        Commands delimiter.
     * @param replacementParametersMap Map with replacement parameters. Keys in
     *                                 script will be replaced by values in map.
     * @throws Exception
     */
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
        List<String> commands = new ArrayList<>();

        LineNumberReader lineReader = new LineNumberReader(reader);
        String line;

        StringBuilder command = new StringBuilder();
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
                command = new StringBuilder();
            } else {
                command.append(line).append(" ");
            }
        }
        return commands;
    }

    private static List<String> adaptSql(List<String> sqls, Map<String, String> stringsToReplaceMap) {
        List<String> adaptedSqls = new ArrayList<>();
        sqls.stream().map((sql) -> sql).map((adaptedSql) -> {
            for (Map.Entry<String, String> entry : stringsToReplaceMap.entrySet()) {
                adaptedSql = adaptedSql.replace(entry.getKey(), entry.getValue());
            }
            return adaptedSql;
        }).forEachOrdered(adaptedSqls::add);
        return adaptedSqls;
    }

    private static void runSqlCommands(Connection connection, List<String> commands) throws Exception {
        SQLException se = null;
        for (String command : commands) {
            try {
                execute(connection, command);
            } catch (SQLException e) {
                LOGGER.warn("", e);
                if (se == null) {
                    se = e;
                }
            }
        }
        if (se != null) {
            throw se;
        }
    }

    /**
     * Executes command.
     *
     * @param connection Connection to database.
     * @param command    Command.
     * @throws SQLException
     */
    public static void execute(Connection connection, String command) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(command);
        }
    }

    /**
     * Executes command with parameters.
     *
     * @param connection Connection to database.
     * @param command    Command.
     * @param params     Parameters. Only base datatypes should be used.
     * @throws SQLException
     */
    public static void execute(Connection connection, String command, Object... params) throws SQLException {
        if (params == null || params.length < 1) {
            execute(connection, command);
        } else {
            try (PreparedStatement preparedStatement = connection.prepareStatement(command)) {
                setParams(preparedStatement, params);
                preparedStatement.execute();
            }
        }
    }

    /**
     * Sets parameters to prepared statement.
     *
     * @param preparedStatement Prepared statement.
     * @param params            Parameters. Only base datatypes should be used.
     * @throws SQLException
     */
    public static void setParams(PreparedStatement preparedStatement, Object... params) throws SQLException {
        int i = 1;
        for (Object param : params) {
            setParam(preparedStatement, i++, param);
        }
    }

    /**
     * Sets parameter to prepared statement.
     *
     * @param preparedStatement Prepared statement.
     * @param index             Parameter index.
     * @param param             Parameter. Only base datatypes should be used.
     * @throws SQLException
     */
    public static void setParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param != null) {
            preparedStatement.setObject(index, param);
        } else {
            preparedStatement.setNull(index, Types.NULL);
        }
    }

    /**
     * Closes result set and hides java.sql.SQLException.
     *
     * @param resultSet Result set.
     */
    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.warn("", e);
            }
        }
    }

    /**
     * Closes statement and hides java.sql.SQLException.
     *
     * @param statement Statement.
     */
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.warn("", e);
            }
        }
    }

    /**
     * Closes connection and hides java.sql.SQLException.
     *
     * @param connection Connection.
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.warn("", e);
            }
        }
    }

    /**
     * Calls rollback on connection and hides java.sql.SQLException.
     *
     * @param connection Connection.
     */
    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                LOGGER.warn("", e);
            }
        }
    }

    /**
     * Sets enable autocommit on connection to true and hides
     * java.sql.SQLException.
     *
     * @param connection Connection.
     */
    public static void enableAutoCommit(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.warn("", e);
            }
        }
    }
}
