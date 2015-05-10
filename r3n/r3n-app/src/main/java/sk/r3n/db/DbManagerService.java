package sk.r3n.db;

import java.util.List;
import java.util.Properties;
import sk.r3n.app.AppException;
import sk.r3n.app.AppHelp;
import sk.r3n.app.AppProperties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.DbStatus;
import sk.r3n.jdbc.DbType;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.Answer;
import sk.r3n.ui.MessageType;

public abstract class DbManagerService {

    protected AppProperties appProperties;

    protected AppHelp appHelp;

    protected String helpKey;

    protected String defaultName;

    protected List<DbType> supportedDbs;

    public DbManagerService(AppProperties appProperties) {
        super();
        this.appProperties = appProperties;
    }

    public void checkDB() throws AppException {
        Properties properties = DbManagerUtil.getProperties(appProperties);
        DbStatus dbStatus = DbManagerUtil.getConnectionStatus(properties);
        int count = 0;
        while (!dbStatus.equals(DbStatus.OK)) {
            switch (dbStatus) {
                case DB_ERR:
                    properties = createDB(properties);
                    if (DbManagerUtil.getConnectionStatus(properties).equals(DbStatus.AUTH_ERR)) {
                        properties = createUser(properties);
                    }
                    break;
                case AUTH_ERR:
                    properties = createUser(properties);
                    if (DbManagerUtil.getConnectionStatus(properties).equals(DbStatus.DB_ERR)) {
                        properties = createDB(properties);
                    }
                    break;
                case SERVER_ERR:
                    if (showYesNoDialog(DbManagerBundle.MESSAGE_TITLE.value(),
                            DbManagerBundle.NOT_RUN_QUESTION.value(),
                            MessageType.WARNING).equals(Answer.YES)) {
                        break;
                    }
                    properties = edit(properties);
                    break;
                default:
                    properties = edit(properties);
                    break;
            }
            dbStatus = DbManagerUtil.getConnectionStatus(properties);
            count++;
            if (!dbStatus.equals(DbStatus.OK) && count > 2) {
                if (showYesNoDialog(DbManagerBundle.MESSAGE_TITLE.value(),
                        DbManagerBundle.AGAIN_QUESTION.value(new Object[]{dbStatus.value()}),
                        MessageType.WARNING).equals(Answer.NO)) {
                    DbManagerException.CANCELLED.raise();
                }
            }
        }
        try {
            checkStructure(properties);
        } catch (Exception e) {
            DbManagerException.CHECK_STRUCTURE_ERR.raise(e);
        }
        DbManagerUtil.setProperties(appProperties, properties);
    }

    protected abstract void checkStructure(Properties properties) throws Exception;

    protected Answer showYesNoDialog(String title, String message, MessageType messageType) {
        return SwingUtil.showYesNoDialog(title, message, messageType);
    }

    protected Properties createDB(Properties properties) throws AppException {
        if (properties.getProperty(DbManagerProperties.ADMIN_NAME.name()).equals("")) {
            properties = edit(properties);
        }
        ConnectionService cs = DbManagerUtil.getConnectionService(properties);
        DbManagerUtil.getDbManagerServiceIO(properties).createDB(cs, properties);
        return properties;
    }

    protected Properties createUser(Properties properties) throws AppException {
        if (properties.getProperty(DbManagerProperties.ADMIN_NAME.name()).equals("")) {
            properties = edit(properties);
        }
        ConnectionService cs = DbManagerUtil.getConnectionService(properties);
        DbManagerUtil.getDbManagerServiceIO(properties).createUser(cs, properties);
        return properties;
    }

    protected Properties edit(Properties properties) throws AppException {
        Properties result = null;
        DbManagerSWDialog dbManagerSWDialog = new DbManagerSWDialog(SwingUtil.getRootFrame(), supportedDbs);
        dbManagerSWDialog.setAppHelp(appHelp);
        dbManagerSWDialog.setHelpKey(helpKey);
        dbManagerSWDialog.setDefaultName(defaultName);
        if (dbManagerSWDialog.init(properties)) {
            result = dbManagerSWDialog.getProperties();
        }
        if (result == null) {
            DbManagerException.CANCELLED.raise();
        }
        return result;
    }

    public void setAppHelp(AppHelp appHelp) {
        this.appHelp = appHelp;
    }

    public void setHelpKey(String helpKey) {
        this.helpKey = helpKey;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public void setSupportedDbs(List<DbType> supportedDbs) {
        this.supportedDbs = supportedDbs;
    }
}
