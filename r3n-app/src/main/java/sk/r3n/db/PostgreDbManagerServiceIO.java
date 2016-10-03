package sk.r3n.db;

import java.sql.Connection;
import java.util.Properties;
import sk.r3n.app.AppException;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.SqlUtil;

public class PostgreDbManagerServiceIO implements DbManagerServiceIO {

    @Override
    public void createDB(ConnectionService connectionService, Properties properties) throws AppException {
        Connection connection = null;
        try {
            connectionService.setProperty(DbManagerProperties.NAME.name(),
                    properties.getProperty(DbManagerProperties.NAME.name()));
            connectionService.setProperty(DbManagerProperties.USER.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.name()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.name()));
            connection = connectionService.getConnection();
        } catch (AppException e) {
        }
        if (connection != null) {
            SqlUtil.close(connection);
            DbManagerException.CREATE_DB_ERR.raise();
        }
        try {
            connectionService.setProperty(DbManagerProperties.NAME.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_NAME.name()));
            connectionService.setProperty(DbManagerProperties.USER.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.name()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.name()));
            connection = connectionService.getConnection();
            connection.setAutoCommit(true);

            StringBuilder sql = new StringBuilder();
            sql.append("CREATE DATABASE ").append(properties.getProperty(DbManagerProperties.NAME.name()));
            sql.append(" WITH OWNER = ").append(properties.getProperty(DbManagerProperties.USER.name()));

            SqlUtil.execute(connection, sql.toString());
        } catch (Exception e) {
            DbManagerException.CREATE_DB_ERR.raise(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Override
    public void createUser(ConnectionService connectionService, Properties properties) throws AppException {
        Connection connection = null;
        try {
            connectionService.setProperty(DbManagerProperties.NAME.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_NAME.name()));
            connectionService.setProperty(DbManagerProperties.USER.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.name()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.name()));
            connection = connectionService.getConnection();
            connection.setAutoCommit(true);
            try {
                StringBuilder sql = new StringBuilder();
                sql.append("ALTER USER ").append(properties.getProperty(DbManagerProperties.USER.name()));
                sql.append(" PASSWORD '").append(properties.getProperty(DbManagerProperties.PASSWORD.name()));
                sql.append("'");
                SqlUtil.execute(connection, sql.toString());
            } catch (Exception ex) {
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE USER ").append(properties.getProperty(DbManagerProperties.USER.name()));
                sql.append(" PASSWORD '").append(properties.getProperty(DbManagerProperties.PASSWORD.name()));
                sql.append("'");
                SqlUtil.execute(connection, sql.toString());
            }
        } catch (Exception e) {
            DbManagerException.CREATE_USER_ERR.raise(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

}
