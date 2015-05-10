package sk.r3n.jdbc;

import sk.r3n.app.AppException;
import java.sql.*;

public class PostgreConnectionService extends BaseConnectionService {

    public PostgreConnectionService() {
        super();
    }

    @Override
    public Connection getConnection() throws AppException {
        if (!isInitialized()) {
            throw new AppException(DbStatus.NOT_INIT.value(), DbStatus.NOT_INIT.code());
        }
        Connection connection = null;
        try {
            Class.forName(DbType.POSTGRE.driver());
            connection = DriverManager.getConnection(getConnectionURL(),
                    getProperty(DbProperty.USER.name()), getProperty(DbProperty.PASSWORD.name()));
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (Exception exception) {
            SqlUtil.close(connection);
            if (exception instanceof SQLException) {
                SQLException sqlException = (SQLException) exception;
                String sqlState = sqlException.getSQLState();
                if (sqlState.equals("28P01") || sqlState.equals("28000")) {
                    throw new AppException(DbStatus.AUTH_ERR.value(), DbStatus.AUTH_ERR.code(), exception);
                } else if (sqlState.equals("3D000")) {
                    throw new AppException(DbStatus.DB_ERR.value(), DbStatus.DB_ERR.code(), exception);
                } else if (sqlState.equals("08004") || sqlState.equals("08001")) {
                    throw new AppException(DbStatus.SERVER_ERR.value(), DbStatus.SERVER_ERR.code(), exception);
                }
            }
            throw new AppException(DbStatus.UNKNOWN.value(), DbStatus.UNKNOWN.code(), exception);
        }
        return connection;
    }

    @Override
    public String getConnectionURL() {
        StringBuilder buff = new StringBuilder();
        buff.append("jdbc:postgresql://");
        buff.append(getProperty(DbProperty.HOST.name()));
        buff.append(":");
        buff.append(getProperty(DbProperty.PORT.name()));
        buff.append("/");
        buff.append(getProperty(DbProperty.NAME.name()));
        return buff.toString();
    }

}
