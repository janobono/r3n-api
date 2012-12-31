package sk.r3n.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;
import sk.r3n.util.R3NException;

public abstract class DataSourceConnectionService implements ConnectionService {

    protected static final Logger LOGGER = Logger.getLogger(ConnectionService.class.getCanonicalName());

    private Map<DbProperty, String> dbProperties;

    protected BasicDataSource ds;

    public DataSourceConnectionService() {
        super();
        dbProperties = new HashMap<>();
    }

    @Override
    public String getProperty(String key) {
        DbProperty dbProperty = DbProperty.valueOf(key);
        return dbProperties.get(dbProperty);
    }

    @Override
    public void setProperty(String key, String value) {
        DbProperty dbProperty = DbProperty.valueOf(key);
        if (!dbProperty.equals(DbProperty.DRIVER)) {
            dbProperties.put(dbProperty, value);
        }
    }

    @Override
    public void setProperties(Properties properties) {
        for (DbProperty dbProperty : DbProperty.values()) {
            if (properties.containsKey(dbProperty.code())) {
                setProperty(dbProperty.code(), properties.getProperty(dbProperty.code(), ""));
            }
        }
    }

    @Override
    public boolean isInitialized() {
        return dbProperties.containsKey(DbProperty.HOST)
                && dbProperties.containsKey(DbProperty.PORT)
                && dbProperties.containsKey(DbProperty.NAME)
                && dbProperties.containsKey(DbProperty.USER)
                && dbProperties.containsKey(DbProperty.PASSWORD);
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
