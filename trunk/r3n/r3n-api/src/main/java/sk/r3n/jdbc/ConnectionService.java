package sk.r3n.jdbc;

import java.sql.Connection;
import sk.r3n.util.R3NException;

public interface ConnectionService {

    public String getParameter(String key);

    public void setParameter(String key, String value);

    public boolean isInitialized();

    public Connection getConnection() throws R3NException;

    public DbStatus getConnectionStatus();

    public void close();

    public String getConnectionURL();

}