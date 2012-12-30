package sk.r3n.db;

import java.util.Properties;
import sk.r3n.app.AppHelp;
import sk.r3n.app.AppProperties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.ConnectionServiceFactory;
import sk.r3n.jdbc.DbStatus;
import sk.r3n.jdbc.DbType;
import sk.r3n.sw.util.Answer;
import sk.r3n.sw.util.MessageType;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.UIActionExecutor;
import sk.r3n.sw.util.UIActionKey;
import sk.r3n.sw.util.UISWAction;
import sk.r3n.util.R3NException;

public class DbManagerService implements UIActionExecutor {

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

    private AppProperties appProperties;

    private DbManagerServiceUI dbManagerServiceUI;

    private DbManagerServiceIO dbManagerServiceIO;

    private AppHelp appHelp;

    public DbManagerService(DbManagerServiceUI dbManagerServiceUI, DbManagerServiceIO dbManagerServiceIO,
            AppProperties appProperties, AppHelp appHelp) {
        super();
        this.appProperties = appProperties;
        this.appHelp = appHelp;
        this.dbManagerServiceIO = dbManagerServiceIO;
        this.dbManagerServiceUI = dbManagerServiceUI;
    }

    private Properties getProperties() {
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

    private void setProperties(Properties properties) {
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

    private boolean isNotSet(Properties properties) {
        return properties.getProperty(DbManagerProperties.DRIVER.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.HOST.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.PORT.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.NAME.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.USER.connCode(), "").equals("")
                || properties.getProperty(DbManagerProperties.PASSWORD.connCode(), "").equals("");
    }

    private Properties editProperties(Properties properties) throws R3NException {
        Properties result = dbManagerServiceUI.edit(properties);
        if (result == null) {
            DbManagerException.CANCELLED.raise();
        }
        return result;
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof UISWAction) {
            switch ((UISWAction) actionKey) {
                case HELP:
                    appHelp.showHelp(ConnectionPropertiesDialog.class.getCanonicalName());
                    return;
                case DEFAULT:
                    //TODO
                    return;
            }
        }
        if (actionKey.equals(DbManagerAction.TEST)) {
            testProperties(getProperties(), false);
        }
    }

    private boolean testProperties(Properties properties, boolean silent) {
        boolean result = false;
        ConnectionService connectionService = getConnectionService(properties);
        if (connectionService == null) {
            if (!silent) {
                SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), DbManagerBundle.UNSUPPORTED.value(),
                        MessageType.ERROR);
            }
            return result;
        }
        switch (connectionService.getConnectionStatus()) {
            case UNKNOWN:
                if (!silent) {
                    SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), DbStatus.UNKNOWN.value(),
                            MessageType.ERROR);
                }
                return result;
            case SERVER_ERR:
                if (!silent) {
                    SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), DbStatus.SERVER_ERR.value(),
                            MessageType.ERROR);
                }
                return result;
            case DB_ERR:
                if (!silent) {
                    SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), DbStatus.DB_ERR.value(),
                            MessageType.ERROR);
                }
                return result;
            case AUTH_ERR:
                if (!silent) {
                    SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), DbStatus.AUTH_ERR.value(),
                            MessageType.ERROR);
                }
                return result;
            case OK:
                if (!silent) {
                    SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), DbStatus.OK.value(),
                            MessageType.ERROR);
                }
                result = true;
                return result;
        }
        connectionService.close();
        return result;
    }

    public void checkDB() throws R3NException {
        Properties properties = getProperties();
        try {
            if (isNotSet(properties)) {
                ConnectionService connectionService = null;
                do {
                    do {
                        properties = editProperties(properties);
                        connectionService = getConnectionService(properties);
                        if (connectionService == null) {
                            SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(),
                                    DbManagerBundle.UNSUPPORTED.value(), MessageType.ERROR);
                        }
                    } while (connectionService == null);
                    try {
                        switch (connectionService.getConnectionStatus()) {
                            case OK:
                                break;
                            case DB_ERR:
                                dbManagerServiceIO.createDB(connectionService, properties);
                                if (connectionService.getConnectionStatus() == DbStatus.AUTH_ERR) {
                                    dbManagerServiceIO.createUser(connectionService, properties);
                                }
                                break;
                            case AUTH_ERR:
                                dbManagerServiceIO.createUser(connectionService, properties);
                                if (connectionService.getConnectionStatus() == DbStatus.DB_ERR) {
                                    dbManagerServiceIO.createDB(connectionService, properties);
                                }
                                break;
                            default:
                                testProperties(properties, false);
                                break;
                        }
                    } catch (R3NException e) {
                        SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), e.getLocalizedMessage(),
                                MessageType.ERROR);
                    } finally {
                        connectionService.close();
                    }
                } while (!testProperties(properties, true));
                setProperties(properties);
            } else {
                while (!testProperties(properties, true)) {
                    testProperties(properties, false);
                    boolean edit;
                    ConnectionService connectionService = getConnectionService(properties);
                    if (connectionService == null) {
                        edit = true;
                    } else {
                        connectionService.close();
                        switch (connectionService.getConnectionStatus()) {
                            case SERVER_ERR:
                                edit = SwingUtil.showYesNoDialog(DbManagerBundle.TITLE.value(),
                                        DbManagerBundle.NOT_RUN_QUESTION.value(),
                                        MessageType.WARNING).equals(Answer.YES);
                                if (!edit) {
                                    continue;
                                } else {
                                    break;
                                }
                            default:
                                edit = true;
                                break;
                        }
                    }
                    if (edit) {
                        if (SwingUtil.showYesNoDialog(DbManagerBundle.TITLE.value(),
                                DbManagerBundle.SET_PROP_QUESTION.value(),
                                MessageType.WARNING).equals(Answer.YES)) {
                            do {
                                do {
                                    properties = editProperties(properties);
                                    connectionService = getConnectionService(properties);
                                    if (connectionService == null) {
                                        SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(),
                                                DbManagerBundle.UNSUPPORTED.value(), MessageType.ERROR);
                                    }
                                } while (connectionService == null);
                                try {
                                    switch (connectionService.getConnectionStatus()) {
                                        case OK:
                                            break;
                                        case DB_ERR:
                                            dbManagerServiceIO.createDB(connectionService, properties);
                                            if (connectionService.getConnectionStatus() == DbStatus.AUTH_ERR) {
                                                dbManagerServiceIO.createUser(connectionService, properties);
                                            }
                                            break;
                                        case AUTH_ERR:
                                            dbManagerServiceIO.createUser(connectionService, properties);
                                            if (connectionService.getConnectionStatus() == DbStatus.DB_ERR) {
                                                dbManagerServiceIO.createDB(connectionService, properties);
                                            }
                                            break;
                                        default:
                                            testProperties(properties, false);
                                            break;
                                    }
                                } catch (R3NException e) {
                                    SwingUtil.showMessageDialog(DbManagerBundle.TITLE.value(), e.getLocalizedMessage(),
                                            MessageType.ERROR);
                                } finally {
                                    connectionService.close();
                                }
                            } while (!testProperties(properties, true));
                            setProperties(properties);
                        } else {
                            DbManagerException.CANCELLED.raise();
                        }
                    }
                }
            }
        } catch (R3NException e) {
            throw e;
        } catch (Exception e) {
            DbManagerException.UNKNOWN.raise(e);
        }
        ConnectionService connectionService = null;
        try {
            connectionService = getConnectionService(properties);
            dbManagerServiceIO.checkStructure(connectionService, properties);
        } catch (Exception e) {
            DbManagerException.CHECK_STRUCTURE_ERR.raise();
        } finally {
            connectionService.close();
        }
    }

}
