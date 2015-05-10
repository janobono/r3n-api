package sk.r3n.jdbc;

import sk.r3n.app.AppException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class H2ConnectionService implements ConnectionService {

    public static final String DB_CLOSE_DELAY = ";DB_CLOSE_DELAY=-1;MVCC=TRUE";

    protected static final Logger LOGGER = Logger.getLogger(ConnectionService.class.getCanonicalName());

    private String name;

    public H2ConnectionService() {
        super();
        name = "";
    }

    @Override
    public String getProperty(String key) {
        if (key.equals(DbProperty.NAME.name())) {
            return name;
        }
        return null;
    }

    @Override
    public void setProperty(String key, String value) {
        if (key.equals(DbProperty.NAME.name())) {
            name = value;
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
        return name != null;
    }

    @Override
    public Connection getConnection() throws AppException {
        try {
            Class.forName(DbType.H2.driver());
            return DriverManager.getConnection(getConnectionURL(), "sa", "");
        } catch (ClassNotFoundException e) {
            throw new AppException(DbStatus.UNKNOWN.value(), DbStatus.UNKNOWN.code(), e);
        } catch (SQLException e) {
            throw new AppException(DbStatus.DB_ERR.value(), DbStatus.DB_ERR.code(), e);
        }
    }

    @Override
    public DbStatus getConnectionStatus() {
        Connection connection = null;
        try {
            connection = getConnection();
            return DbStatus.OK;
        } catch (AppException e) {
            return DbStatus.byCode(e.getErrorCode());
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Override
    public String getConnectionURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:h2:mem:");
        if (name != null) {
            sb.append(name);
        }
        return sb.toString();
    }

}
