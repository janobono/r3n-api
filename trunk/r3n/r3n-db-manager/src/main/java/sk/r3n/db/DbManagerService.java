package sk.r3n.db;

import java.util.Properties;
import sk.r3n.app.AppHelp;
import sk.r3n.app.AppProperties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.DbStatus;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.Answer;
import sk.r3n.ui.MessageType;
import sk.r3n.util.R3NException;

public abstract class DbManagerService {

    private AppProperties appProperties;

    private AppHelp appHelp;

    private String helpKey;

    private String defaultName;

    public DbManagerService(AppProperties appProperties, AppHelp appHelp, String helpKey, String defaultName) {
        super();
        this.appProperties = appProperties;
        this.appHelp = appHelp;
        this.helpKey = helpKey;
        this.defaultName = defaultName;
    }

    public void checkDB() throws R3NException {
        Properties properties = DbManagerUtil.getProperties(appProperties);
        DbStatus dbStatus = DbManagerUtil.getConnectionStatus(properties);
        int count = 0;
        while (!dbStatus.equals(DbStatus.OK)) {
            switch (dbStatus) {
                case DB_ERR:
                    createDB(properties);
                    if (DbManagerUtil.getConnectionStatus(properties).equals(DbStatus.AUTH_ERR)) {
                        createUser(properties);
                    }
                    break;
                case AUTH_ERR:
                    createUser(properties);
                    if (DbManagerUtil.getConnectionStatus(properties).equals(DbStatus.DB_ERR)) {
                        createDB(properties);
                    }
                    break;
                case SERVER_ERR:
                    if (showYesNoDialog(DbManagerBundle.MESSAGE_TITLE.value(),
                            DbManagerBundle.NOT_RUN_QUESTION.value(),
                            MessageType.WARNING).equals(Answer.YES)) {
                        break;
                    }
                    properties = edit(properties);
                    if (properties == null) {
                        DbManagerException.CANCELLED.raise();
                    }
                    break;
                default:
                    properties = edit(properties);
                    if (properties == null) {
                        DbManagerException.CANCELLED.raise();
                    }
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

    protected abstract void checkStructure(Properties properties) throws R3NException;

    protected Answer showYesNoDialog(String title, String message, MessageType messageType) {
        return SwingUtil.showYesNoDialog(title, message, messageType);
    }

    protected void createDB(Properties properties) throws R3NException {
        ConnectionService cs = DbManagerUtil.getConnectionService(properties);
        DbManagerUtil.getDbManagerServiceIO(properties).createDB(cs, properties);
    }

    protected void createUser(Properties properties) throws R3NException {
        ConnectionService cs = DbManagerUtil.getConnectionService(properties);
        DbManagerUtil.getDbManagerServiceIO(properties).createUser(cs, properties);
    }

    protected Properties edit(Properties properties) {
        Properties result = null;
        DbManagerSWDialog dbManagerSWDialog = new DbManagerSWDialog(
                SwingUtil.getRootFrame(), appHelp, helpKey, defaultName);
        if (dbManagerSWDialog.init(properties)) {
            result = dbManagerSWDialog.getProperties();
        }
        return result;
    }

}
