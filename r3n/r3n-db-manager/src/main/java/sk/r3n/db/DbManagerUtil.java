package sk.r3n.db;

import java.util.Properties;
import sk.r3n.app.AppProperties;

public class DbManagerUtil {

    public static Properties getProperties(AppProperties appProperties) {
        Properties properties = new Properties();
        properties.put(DbManagerProperties.DRIVER.name(),
                appProperties.get(DbManagerProperties.DRIVER.name(), ""));
        properties.put(DbManagerProperties.HOST.name(), appProperties.get(DbManagerProperties.HOST.name(), ""));
        properties.put(DbManagerProperties.PORT.name(), appProperties.get(DbManagerProperties.PORT.name(), ""));
        properties.put(DbManagerProperties.NAME.name(), appProperties.get(DbManagerProperties.NAME.name(), ""));
        properties.put(DbManagerProperties.USER.name(), appProperties.get(DbManagerProperties.USER.name(), ""));
        properties.put(DbManagerProperties.PASSWORD.name(), appProperties.decrypt(
                appProperties.get(DbManagerProperties.PASSWORD.name(), "")));
        properties.put(DbManagerProperties.ADMIN_NAME.name(), "");
        properties.put(DbManagerProperties.ADMIN_USER.name(), "");
        properties.put(DbManagerProperties.ADMIN_PASSWORD.name(), "");
        return properties;
    }

    public static void setProperties(AppProperties appProperties, Properties properties) {
        appProperties.set(DbManagerProperties.DRIVER.name(),
                properties.getProperty(DbManagerProperties.DRIVER.name()));
        appProperties.set(DbManagerProperties.HOST.name(),
                properties.getProperty(DbManagerProperties.HOST.name()));
        appProperties.set(DbManagerProperties.PORT.name(),
                properties.getProperty(DbManagerProperties.PORT.name()));
        appProperties.set(DbManagerProperties.NAME.name(),
                properties.getProperty(DbManagerProperties.NAME.name()));
        appProperties.set(DbManagerProperties.USER.name(),
                properties.getProperty(DbManagerProperties.USER.name()));
        appProperties.set(DbManagerProperties.PASSWORD.name(),
                appProperties.encrypt(properties.getProperty(DbManagerProperties.PASSWORD.name())));
    }

    public static ConnectionService getConnectionService(Properties properties) {
        ConnectionService result = null;
        String driver = properties.getProperty(DbManagerProperties.DRIVER.name(), "");
        DbType dbType = DbType.get(driver);
        if (dbType != null) {
            result = ConnectionServiceFactory.createConnectionService(dbType);
            result.setProperties(properties);
        }
        return result;
    }

    public static boolean isNotSet(Properties properties) {
        return properties.getProperty(DbManagerProperties.DRIVER.name(), "").equals("")
                || properties.getProperty(DbManagerProperties.HOST.name(), "").equals("")
                || properties.getProperty(DbManagerProperties.PORT.name(), "").equals("")
                || properties.getProperty(DbManagerProperties.NAME.name(), "").equals("")
                || properties.getProperty(DbManagerProperties.USER.name(), "").equals("")
                || properties.getProperty(DbManagerProperties.PASSWORD.name(), "").equals("");
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
        String driver = properties.getProperty(DbManagerProperties.DRIVER.name(), "");
        DbType dbType = DbType.get(driver);
        if (dbType != null) {
            switch (dbType) {
                case POSTGRE:
                    dbManagerServiceIO = new PostgreDbManagerServiceIO();
                    break;
            }
        }
        return dbManagerServiceIO;
    }
}
