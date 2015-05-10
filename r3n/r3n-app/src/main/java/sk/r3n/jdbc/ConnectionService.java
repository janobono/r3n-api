package sk.r3n.jdbc;

import sk.r3n.app.AppException;
import java.sql.Connection;
import java.util.Properties;

public interface ConnectionService {

    public String getProperty(String key);

    public void setProperty(String key, String value);

    public void setProperties(Properties properties);

    public boolean isInitialized();

    public Connection getConnection() throws AppException;

    public DbStatus getConnectionStatus();

    public String getConnectionURL();

}
