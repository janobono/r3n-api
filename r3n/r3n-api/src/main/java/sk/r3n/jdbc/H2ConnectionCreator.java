package sk.r3n.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import sk.r3n.util.R3NException;

public class H2ConnectionCreator implements ConnectionService {

    public static final String DB_CLOSE_DELAY = ";DB_CLOSE_DELAY=-1;MVCC=TRUE";

    protected static final Logger LOGGER = Logger.getLogger(ConnectionService.class.getCanonicalName());

    private String name;

    public H2ConnectionCreator() {
        super();
        name = "";
    }

    @Override
    public String getParameter(String key) {
        if (key.equals(ConnectionParameter.NAME.key())) {
            return name;
        }
        return null;
    }

    @Override
    public void setParameter(String key, String value) {
        if (key.equals(ConnectionParameter.NAME.key())) {
            name = value;
        }
    }

    @Override
    public boolean isInitialized() {
        return name != null;
    }

    @Override
    public Connection getConnection() throws R3NException {
        try {
            Class.forName(DbType.H2.driver());
            return DriverManager.getConnection(getConnectionURL(), "sa", "");
        } catch (ClassNotFoundException e) {
            throw new R3NException(DbStatus.UNKNOWN.value(), DbStatus.UNKNOWN.code(), e);
        } catch (SQLException e) {
            throw new R3NException(DbStatus.DB_ERR.value(), DbStatus.DB_ERR.code(), e);
        }
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
    }

    @Override
    public String getConnectionURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:h2:mem:");
        if (name != null && !name.equals("")) {
            sb.append(name);
        }
        return sb.toString();
    }

}
