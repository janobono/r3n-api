package sk.r3n.db;

import java.util.Properties;
import sk.r3n.app.AppProperties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.ConnectionServiceFactory;
import sk.r3n.jdbc.DbStatus;
import sk.r3n.jdbc.DbType;

public class DbManagerUtil {

    public static Properties getProperties(AppProperties appProperties) {
        Properties properties = new Properties();
        properties.put(DbManagerProperties.DRIVER.connCode(),
                appProperties.get(DbManagerProperties.DRIVER.appCode(), ""));
        properties.put(DbManagerProperties.HOST.connCode(), appProperties.get(DbManagerProperties.HOST.appCode(), ""));
        properties.put(DbManagerProperties.PORT.connCode(), appProperties.get(DbManagerProperties.PORT.appCode(), ""));
        properties.put(DbManagerProperties.NAME.connCode(), appProperties.get(DbManagerProperties.NAME.appCode(), ""));
        properties.put(DbManagerProperties.USER.connCode(), appProperties.get(DbManagerProperties.USER.appCode(), ""));
        properties.put(DbManagerProperties.PASSWORD.connCode(), appProperties.decrypt(
                appProperties.get(DbManagerProperties.PASSWORD.appCode(), "")));
        properties.put(DbManagerProperties.ADMIN_NAME.connCode(), "");
        properties.put(DbManagerProperties.ADMIN_USER.connCode(), "");
        properties.put(DbManagerProperties.ADMIN_PASSWORD.connCode(), "");
        return properties;
    }

    public static void setProperties(AppProperties appProperties, Properties properties) {
        appProperties.set(DbManagerProperties.DRIVER.appCode(),
                properties.getProperty(DbManagerProperties.DRIVER.connCode()));
        appProperties.set(DbManagerProperties.HOST.appCode(),
                properties.getProperty(DbManagerProperties.HOST.connCode()));
        appProperties.set(DbManagerProperties.PORT.appCode(),
                properties.getProperty(DbManagerProperties.PORT.connCode()));
        appProperties.set(DbManagerProperties.NAME.appCode(),
                properties.getProperty(DbManagerProperties.NAME.connCode()));
        appProperties.set(DbManagerProperties.USER.appCode(),
                properties.getProperty(DbManagerProperties.USER.connCode()));
        appProperties.set(DbManagerProperties.PASSWORD.appCode(),
                appProperties.encrypt(properties.getProperty(DbManagerProperties.PASSWORD.connCode())));
    }

    public static ConnectionService getConnectionService(Properties properties) {
        ConnectionService result = null;
        String driver = properties.getProperty(DbManagerProperties.DRIVER.connCode(), "");
        DbType dbType = DbType.get(driver);
        if (dbType != null) {
            result = ConnectionServiceFactory.createConnectionService(dbType);
            result.setProperties(properties);
        }
        return result;
    }

    public static boolean isNotSet(Properties properties) {
        return properties.getProperty(DbManagerProperties.DRIVER.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.HOST.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.PORT.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.NAME.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.USER.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.PASSWORD.connCode(), "").equals("");
    }

    public static boolean testProperties(Properties properties) {
        boolean result = false;
        ConnectionService connectionService = getConnectionService(properties);
        if (connectionService == null) {
            return result;
        }
        switch (connectionService.getConnectionStatus()) {
            case OK:
                result = true;
                return result;
        }
        return result;
    }

    public static DbStatus getConnectionStatus(Properties properties) {
        ConnectionService connectionService = getConnectionService(properties);
        if (connectionService == null) {
            return DbStatus.NOT_INIT;
        }
        DbStatus result = connectionService.getConnectionStatus();
        return result;
    }

    public static DbManagerServiceIO getDbManagerServiceIO(Properties properties) {
        DbManagerServiceIO dbManagerServiceIO = null;
        String driver = properties.getProperty(DbManagerProperties.DRIVER.connCode(), "");
        DbType dbType = DbType.get(driver);
        if (dbType != null) {
            switch (dbType) {
                case POSTGRE:
                    dbManagerServiceIO = new PostgreDbManagerServiceIO();
                    break;
                case SQL_SERVER:
                    dbManagerServiceIO = new SQLServerDbManagerServiceIO();
                    break;
            }
        }
        return dbManagerServiceIO;
    }
}
