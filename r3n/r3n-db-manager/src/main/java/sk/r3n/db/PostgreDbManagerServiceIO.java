package sk.r3n.db;

import java.sql.Connection;
import java.util.Properties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.SqlUtil;

public class PostgreDbManagerServiceIO implements DbManagerServiceIO {
    
    @Override
    public void createDB(ConnectionService connectionService, Properties properties) throws Exception {
        Connection connection = null;
        try {
            connectionService.setProperty(DbManagerProperties.NAME.connCode(),
                    properties.getProperty(DbManagerProperties.NAME.connCode()));
            connectionService.setProperty(DbManagerProperties.USER.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.connCode()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.connCode()));
            connection = connectionService.getConnection();
        } catch (Exception e) {
        }
        if (connection != null) {
            SqlUtil.close(connection);
            DbManagerException.CREATE_DB_ERR.raise();
        }
        try {
            connectionService.setProperty(DbManagerProperties.NAME.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_NAME.connCode()));
            connectionService.setProperty(DbManagerProperties.USER.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.connCode()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.connCode()));
            connection = connectionService.getConnection();
            connection.setAutoCommit(true);
            
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE DATABASE ").append(properties.getProperty(DbManagerProperties.NAME.connCode()));
            sql.append(" WITH OWNER = ").append(properties.getProperty(DbManagerProperties.USER.connCode()));
            
            SqlUtil.execute(connection, sql.toString());
        } catch (Exception e) {
            DbManagerException.CREATE_DB_ERR.raise(e);
        } finally {
            SqlUtil.close(connection);
        }
    }
    
    @Override
    public void createUser(ConnectionService connectionService, Properties properties) throws Exception {
        Connection connection = null;
        try {
            connectionService.setProperty(DbManagerProperties.NAME.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_NAME.connCode()));
            connectionService.setProperty(DbManagerProperties.USER.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.connCode()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.connCode(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.connCode()));
            connection = connectionService.getConnection();
            connection.setAutoCommit(true);
            try {
                StringBuilder sql = new StringBuilder();
                sql.append("ALTER USER ").append(properties.getProperty(DbManagerProperties.USER.connCode()));
                sql.append(" PASSWORD '").append(properties.getProperty(DbManagerProperties.PASSWORD.connCode()));
                sql.append("'");
                SqlUtil.execute(connection, sql.toString());
            } catch (Exception ex) {
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE USER ").append(properties.getProperty(DbManagerProperties.USER.connCode()));
                sql.append(" PASSWORD '").append(properties.getProperty(DbManagerProperties.PASSWORD.connCode()));
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
