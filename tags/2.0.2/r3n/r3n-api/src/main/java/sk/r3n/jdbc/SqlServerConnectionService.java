package sk.r3n.jdbc;

import java.sql.*;
import sk.r3n.util.R3NException;

public class SqlServerConnectionService extends BaseConnectionService {

    public SqlServerConnectionService() {
        super();
    }

    @Override
    public Connection getConnection() throws R3NException {
        if (!isInitialized()) {
            throw new R3NException(DbStatus.NOT_INIT.value(), DbStatus.NOT_INIT.code());
        }
        Connection connection = null;
        try {
            Class.forName(DbType.SQL_SERVER.driver());
            connection = DriverManager.getConnection(getConnectionURL(),
                    getProperty(DbProperty.USER.name()), getProperty(DbProperty.PASSWORD.name()));
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (ClassNotFoundException | SQLException exception) {
            SqlUtil.close(connection);
            if (exception instanceof SQLException) {
                SQLException sqlException = (SQLException) exception;
                String sqlState = sqlException.getSQLState();
                switch (sqlState) {
                    case "28000":
                        throw new R3NException(DbStatus.AUTH_ERR.value(), DbStatus.AUTH_ERR.code(), exception);
                    case "S1000":
                        throw new R3NException(DbStatus.DB_ERR.value(), DbStatus.DB_ERR.code(), exception);
                    case "08S01":
                        throw new R3NException(DbStatus.SERVER_ERR.value(), DbStatus.SERVER_ERR.code(), exception);
                }
            }
            throw new R3NException(DbStatus.UNKNOWN.value(), DbStatus.UNKNOWN.code(), exception);
        }
        return connection;
    }

    @Override
    public String getConnectionURL() {
        StringBuilder buff = new StringBuilder();
        buff.append("jdbc:jtds:sqlserver://");
        buff.append(getProperty(DbProperty.HOST.name()));
        buff.append(":");
        buff.append(getProperty(DbProperty.PORT.name()));
        buff.append("/");
        buff.append(getProperty(DbProperty.NAME.name()));
        return buff.toString();
    }

}
