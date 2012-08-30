package sk.r3n.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import sk.r3n.util.R3NException;

public interface ConnectionCreator {

    public int CONN_STATUS_UNKNOWN = DbManagerService.ERR_UNKNOWN;
    public int CONN_STATUS_NOT_SET = DbManagerService.ERR_NOT_SET;
    public int CONN_STATUS_NOT_RUN = DbManagerService.ERR_NOT_RUN;
    public int CONN_STATUS_NOT_EXIST = DbManagerService.ERR_NOT_EXIST;
    public int CONN_STATUS_AUTHENTICATION = DbManagerService.ERR_AUTHENTICATION;
    
    public String DRIVER = "driver";
    public String HOST = "host";
    public String PORT = "port";
    public String NAME = "name";
    public String USER = "user";
    public String PASSWORD = "password";
    public String ADMIN_DB = "admin_db";
    public String ADMIN_USER = "admin_user";
    public String ADMIN_PASSWORD = "admin_password";

    public void close();

    public void close(Connection connection);

    public void close(ResultSet resultSet);

    public void close(Statement statement);

    public Connection getConnection() throws R3NException;

    public Connection getConnection(String name, String user, String password)
            throws R3NException;

    public String getConnectionURL();

    public int getConnStatus();

    public void rollback(Connection connection);
}