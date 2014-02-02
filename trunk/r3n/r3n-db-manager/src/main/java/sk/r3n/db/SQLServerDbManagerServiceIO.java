package sk.r3n.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Properties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.util.R3NException;

public class SQLServerDbManagerServiceIO implements DbManagerServiceIO {

    @Override
    public void createDB(ConnectionService connectionService, Properties properties) throws R3NException {
        Connection connection = null;
        try {
            connectionService.setProperty(DbManagerProperties.NAME.name(),
                    properties.getProperty(DbManagerProperties.NAME.name()));
            connectionService.setProperty(DbManagerProperties.USER.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.name()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.name()));
            connection = connectionService.getConnection();
        } catch (R3NException e) {
        }
        if (connection != null) {
            SqlUtil.close(connection);
            DbManagerException.CREATE_DB_ERR.raise();
        }

        CallableStatement cs = null;
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
            sql.append("CREATE DATABASE [").append(properties.getProperty(DbManagerProperties.NAME.name()));
            sql.append("] COLLATE Slovak_CS_AS");
            SqlUtil.execute(connection, sql.toString());
            sql = new StringBuilder();
            sql.append("ALTER DATABASE [").append(properties.getProperty(DbManagerProperties.NAME.name()));
            sql.append("] SET READ_COMMITTED_SNAPSHOT ON");
            SqlUtil.execute(connection, sql.toString());
            SqlUtil.close(connection);

            connectionService.setProperty(DbManagerProperties.NAME.name(),
                    properties.getProperty(DbManagerProperties.NAME.name()));
            connectionService.setProperty(DbManagerProperties.USER.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_USER.name()));
            connectionService.setProperty(DbManagerProperties.PASSWORD.name(),
                    properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.name()));
            connection = connectionService.getConnection();
            connection.setAutoCommit(true);

            sql = new StringBuilder();
            sql.append("CREATE USER [").append(properties.getProperty(DbManagerProperties.USER.name()));
            sql.append("] FOR LOGIN [").append(properties.getProperty(DbManagerProperties.USER.name()));
            sql.append("]");
            SqlUtil.execute(connection, sql.toString());

            cs = connection.prepareCall("{call sp_addrolemember(?,?)}");
            cs.setString(1, "db_owner");
            cs.setString(2, properties.getProperty(DbManagerProperties.USER.name()));
            cs.execute();
            cs.setString(1, "db_datawriter");
            cs.setString(2, properties.getProperty(DbManagerProperties.USER.name()));
            cs.execute();
            cs.setString(1, "db_datareader");
            cs.setString(2, properties.getProperty(DbManagerProperties.USER.name()));
            cs.execute();
        } catch (Exception e) {
            DbManagerException.CREATE_DB_ERR.raise(e);
        } finally {
            SqlUtil.close(cs);
            SqlUtil.close(connection);
        }
    }

    @Override
    public void createUser(ConnectionService connectionService, Properties properties) throws R3NException {
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
                sql.append("ALTER LOGIN [").append(properties.getProperty(DbManagerProperties.USER.name()));
                sql.append("] WITH PASSWORD='").append(properties.getProperty(DbManagerProperties.PASSWORD.name()));
                sql.append("', DEFAULT_DATABASE=[master], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON");
                SqlUtil.execute(connection, sql.toString());
            } catch (Exception e) {
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE LOGIN [").append(properties.getProperty(DbManagerProperties.USER.name()));
                sql.append("] WITH PASSWORD='").append(properties.getProperty(DbManagerProperties.PASSWORD.name()));
                sql.append("', DEFAULT_DATABASE=[master], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON");
                SqlUtil.execute(connection, sql.toString());
            }
        } catch (Exception e) {
            DbManagerException.CREATE_USER_ERR.raise(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

}
