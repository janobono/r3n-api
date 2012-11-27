package sk.r3n.jdbc;

import java.sql.*;

public class SqlUtil {

    public static void execute(Connection connection, String command) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.execute(command);
        } finally {
            close(statement);
        }
    }

    public static void execute(Connection connection, String sql, Object[] params) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            prepare(statement, params);
            statement.executeUpdate();
        } finally {
            close(statement);
        }
    }

    public static PreparedStatement prepare(Connection connection, String sql, Object[] params)
            throws SQLException {
        PreparedStatement result = connection.prepareStatement(sql);
        prepare(result, params);
        return result;
    }

    public static PreparedStatement prepare(PreparedStatement statement, Object[] params)
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
