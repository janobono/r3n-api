package sk.r3n.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import sk.r3n.util.R3NException;

public abstract class BaseConnectionService implements ConnectionService {

    protected static final Logger LOGGER = Logger.getLogger(ConnectionService.class.getCanonicalName());

    private final Map<DbProperty, String> dbProperties;

    public BaseConnectionService() {
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
            if (properties.containsKey(dbProperty.name())) {
                setProperty(dbProperty.name(), properties.getProperty(dbProperty.name(), ""));
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

}
