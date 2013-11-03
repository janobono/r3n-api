package sk.r3n.jdbc;

import java.sql.Connection;
import java.util.Properties;
import sk.r3n.util.R3NException;

public interface ConnectionService {

    public String getProperty(String key);

    public void setProperty(String key, String value);

    public void setProperties(Properties properties);

    public boolean isInitialized();

    public Connection getConnection() throws R3NException;

    public DbStatus getConnectionStatus();

    public String getConnectionURL();

}