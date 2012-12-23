package sk.r3n.jdbc;

import java.sql.*;
import java.util.logging.Level;
import org.apache.commons.dbcp.BasicDataSource;
import sk.r3n.util.R3NException;

public class PostgreConnectionService extends DataSourceConnectionService {

    public PostgreConnectionService() {
        super();
    }

    @Override
    public Connection getConnection() throws R3NException {
        if (!isInitialized()) {
            throw new R3NException(DbStatus.NOT_INIT.value(), DbStatus.NOT_INIT.code());
        }
        Connection connection = null;
        try {
            if (ds == null) {
                ds = new BasicDataSource();
                ds.setDriverClassName(DbType.POSTGRE.driver());
                ds.setUsername(getParameter(ConnectionParameter.USER.code()));
                ds.setPassword(getParameter(ConnectionParameter.PASSWORD.code()));
                ds.setUrl(getConnectionURL());
                ds.setMinIdle(5);
            }
            LOGGER.log(Level.FINER, "NumActive: {0}", ds.getNumActive());
            LOGGER.log(Level.FINER, "NumIdle: {0}", ds.getNumIdle());
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (Exception exception) {
            SqlUtil.close(connection);
            close();
            if (exception instanceof SQLException) {
                SQLException sqlException = (SQLException) exception;
                String sqlState = sqlException.getSQLState();
                switch (sqlState) {
                    case "28P01":
                    case "28000":
                        throw new R3NException(DbStatus.AUTH_ERR.value(), DbStatus.AUTH_ERR.code(), exception);
                    case "3D000":
                        throw new R3NException(DbStatus.DB_ERR.value(), DbStatus.DB_ERR.code(), exception);
                    case "08004":
                    case "08001":
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
        buff.append("jdbc:postgresql://");
        buff.append(getParameter(ConnectionParameter.HOST.code()));
        buff.append(":");
        buff.append(getParameter(ConnectionParameter.PORT.code()));
        buff.append("/");
        buff.append(getParameter(ConnectionParameter.NAME.code()));
        return buff.toString();
    }

}
