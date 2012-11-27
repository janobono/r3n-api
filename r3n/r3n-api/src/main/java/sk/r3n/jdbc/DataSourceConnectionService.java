package sk.r3n.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;
import sk.r3n.util.R3NException;

public abstract class DataSourceConnectionService implements ConnectionService {

    protected static final Logger LOGGER = Logger.getLogger(ConnectionService.class.getCanonicalName());

    private Map<ConnectionParameter, String> connectionParameters;

    protected BasicDataSource ds;

    public DataSourceConnectionService() {
        super();
        connectionParameters = new HashMap<>();
    }

    @Override
    public String getParameter(String key) {
        ConnectionParameter connectionParameter = ConnectionParameter.valueOf(key);
        return connectionParameters.get(connectionParameter);
    }

    @Override
    public void setParameter(String key, String value) {
        ConnectionParameter connectionParameter = ConnectionParameter.valueOf(key);
        if (!connectionParameter.equals(ConnectionParameter.DRIVER)) {
            connectionParameters.put(connectionParameter, value);
        }
    }

    @Override
    public boolean isInitialized() {
        return connectionParameters.containsKey(ConnectionParameter.HOST)
                && connectionParameters.containsKey(ConnectionParameter.PORT)
                && connectionParameters.containsKey(ConnectionParameter.NAME)
                && connectionParameters.containsKey(ConnectionParameter.USER)
                && connectionParameters.containsKey(ConnectionParameter.PASSWORD);
    }

    @Override
    public DbStatus getConnectionStatus() {
        Connection connection = null;
        try {
            connection = getConnection();
            return DbStatus.OK;
        } catch (R3NException e) {
            return DbStatus.byCode(e.getErrorCode());
        } finally {
            SqlUtil.close(connection);
        }
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

}
