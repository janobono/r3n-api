package sk.r3n.db.impl;

import java.sql.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;
import sk.r3n.db.ConnectionCreator;
import sk.r3n.db.DbManagerService;
import sk.r3n.util.R3NException;

public class PostgreSQLConnectionCreator implements ConnectionCreator {

    private static final Logger LOGGER = Logger.getLogger(ConnectionCreator.class.getCanonicalName());
    private Properties properties;
    private BasicDataSource ds;

    public PostgreSQLConnectionCreator(Properties properties) {
        super();
        this.properties = new Properties();
        this.properties.putAll(properties);
    }

    @Override
    public void close() {
        try {
            if (ds != null) {
                ds.close();
            }
        } catch (Exception e) {
        }
        ds = null;
    }

    @Override
    public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public Connection getConnection() throws R3NException {
        Connection connection = null;
        try {
            if (ds == null) {
                ds = new BasicDataSource();
                ds.setDriverClassName(DbManagerServiceImpl.POSTGRES_DRIVER);
                ds.setUsername(properties.getProperty(USER));
                ds.setPassword(properties.getProperty(PASSWORD));
                ds.setUrl(getConnectionURL());
                ds.setMinIdle(5);
            }
            LOGGER.log(Level.FINER, "NumActive: {0}", ds.getNumActive());
            LOGGER.log(Level.FINER, "NumIdle: {0}", ds.getNumIdle());
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (Exception exception) {
            close(connection);
            close();
            if (exception instanceof SQLException) {
                SQLException sqlException = (SQLException) exception;
                String sqlState = sqlException.getSQLState();
                switch (sqlState) {
                    case "28P01":
                    case "28000":
                        throw new R3NException(
                                ResourceBundle.getBundle(
                                DbManagerService.class.getCanonicalName()).getString(
                                Integer.toString(DbManagerService.ERR_AUTHENTICATION)),
                                DbManagerService.ERR_AUTHENTICATION, exception);
                    case "3D000":
                        throw new R3NException(
                                ResourceBundle.getBundle(
                                DbManagerService.class.getCanonicalName()).getString(
                                Integer.toString(DbManagerService.ERR_NOT_EXIST)),
                                DbManagerService.ERR_NOT_EXIST, exception);
                    case "08004":
                    case "08001":
                        throw new R3NException(
                                ResourceBundle.getBundle(
                                DbManagerService.class.getCanonicalName()).getString(
                                Integer.toString(DbManagerService.ERR_NOT_RUN)),
                                DbManagerService.ERR_NOT_RUN, exception);
                }
            }
            throw new R3NException(ResourceBundle.getBundle(
                    DbManagerService.class.getCanonicalName()).getString(
                    Integer.toString(DbManagerService.ERR_UNKNOWN)),
                    DbManagerService.ERR_UNKNOWN, exception);
        }
        return connection;
    }

    @Override
    public Connection getConnection(String name, String user, String password)
            throws R3NException {
        Connection connection = null;
        try {
            Class.forName(DbManagerServiceImpl.POSTGRES_DRIVER);
            Properties props = new Properties();
            props.put(USER, user);
            props.put(PASSWORD, password);
            connection = DriverManager.getConnection(getConnectionURL(name),
                    props);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (Exception exception) {
            close(connection);
            if (exception instanceof SQLException) {
                SQLException sqlException = (SQLException) exception;
                String sqlState = sqlException.getSQLState();
                switch (sqlState) {
                    case "28P01":
                    case "28000":
                        throw new R3NException(
                                ResourceBundle.getBundle(
                                DbManagerService.class.getCanonicalName()).getString(
                                Integer.toString(DbManagerService.ERR_AUTHENTICATION)),
                                DbManagerService.ERR_AUTHENTICATION, exception);
                    case "3D000":
                        throw new R3NException(
                                ResourceBundle.getBundle(
                                DbManagerService.class.getCanonicalName()).getString(
                                Integer.toString(DbManagerService.ERR_NOT_EXIST)),
                                DbManagerService.ERR_NOT_EXIST, exception);
                    case "08004":
                    case "08001":
                        throw new R3NException(
                                ResourceBundle.getBundle(
                                DbManagerService.class.getCanonicalName()).getString(
                                Integer.toString(DbManagerService.ERR_NOT_RUN)),
                                DbManagerService.ERR_NOT_RUN, exception);
                }
            }
            throw new R3NException(ResourceBundle.getBundle(
                    DbManagerService.class.getCanonicalName()).getString(
                    Integer.toString(DbManagerService.ERR_UNKNOWN)),
                    DbManagerService.ERR_UNKNOWN, exception);
        }
        return connection;
    }

    @Override
    public String getConnectionURL() {
        return getConnectionURL(properties.getProperty(NAME));
    }

    private String getConnectionURL(String name) {
        StringBuilder buff = new StringBuilder();
        buff.append("jdbc:postgresql://");
        buff.append(properties.getProperty(HOST));
        buff.append(":");
        buff.append(properties.getProperty(PORT));
        buff.append("/");
        buff.append(name);
        return buff.toString();
    }

    @Override
    public int getConnStatus() {
        Connection connection = null;
        try {
            connection = getConnection(properties.getProperty(NAME),
                    properties.getProperty(USER),
                    properties.getProperty(PASSWORD));
            return 0;
        } catch (R3NException e) {
            return e.getErrorCode();
        } finally {
            close(connection);
        }
    }

    @Override
    public void rollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (Exception e) {
        }
    }
}
