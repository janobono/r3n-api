package sk.r3n.db;

import java.util.Properties;
import sk.r3n.app.AppProperties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.jdbc.DbStatus;
import sk.r3n.ui.Answer;
import sk.r3n.ui.MessageType;
import sk.r3n.util.R3NException;

public class DbManagerService {

    private AppProperties appProperties;

    public DbManagerService(AppProperties appProperties) {
        super();
        this.appProperties = appProperties;
    }

    public void createDB(DbManagerServiceIO dbManagerServiceIO, Properties properties) throws R3NException {
        ConnectionService connectionService = getConnectionService(properties);
        try {
            switch (connectionService.getConnectionStatus()) {
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
            }
        } catch (Exception e) {
            DbManagerException.CREATE_DB_ERR.raise(e);
        } finally {
            connectionService.close();
        }
    }

    public void checkDB() throws R3NException {
        getProperties();
        try {
            if (isNotSet()) {
                testAndSet();
            } else {
                testAndEdit();
            }
        } catch (R3NException e) {
            throw e;
        } catch (Exception e) {
            DbManagerException.UNKNOWN.raise(e);
        }
        ConnectionService connectionService = null;
        try {
            connectionService = getConnectionService(properties);
            checkStructure(connectionService, properties);
        } catch (Exception e) {
            DbManagerException.CHECK_STRUCTURE_ERR.raise(e);
        } finally {
            if (connectionService != null) {
                connectionService.close();
            }
        }
    }

    private void testAndEdit() throws Exception {
        while (!testProperties(false)) {
            boolean edit;
            ConnectionService connectionService = getConnectionService(properties);
            if (connectionService == null) {
                edit = true;
            } else {
                connectionService.close();
                switch (connectionService.getConnectionStatus()) {
                    case SERVER_ERR:
                        edit = dialogUI.showYesNoDialog(DbManagerBundle.TITLE.value(),
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
                if (dialogUI.showYesNoDialog(DbManagerBundle.TITLE.value(),
                        DbManagerBundle.SET_PROP_QUESTION.value(),
                        MessageType.WARNING).equals(Answer.YES)) {
                    testAndSet();
                } else {
                    DbManagerException.CANCELLED.raise();
                }
            }
        }
    }

    private void testAndSet() throws Exception {
        ConnectionService connectionService;
        do {
            connectionService = prepare();
            create(connectionService);
        } while (!testProperties(true));
        setProperties();
    }

    private ConnectionService prepare() throws R3NException {
        ConnectionService connectionService = null;
        do {
            properties = editProperties(properties);
            connectionService = getConnectionService(properties);
            if (connectionService == null) {
                dialogUI.showMessageDialog(DbManagerBundle.TITLE.value(),
                        DbManagerBundle.UNSUPPORTED.value(), MessageType.ERROR);
            }
        } while (connectionService == null);
        return connectionService;
    }

    private Properties editProperties(Properties properties) throws R3NException {
        Properties result = edit(properties);
        if (result == null) {
            DbManagerException.CANCELLED.raise();
        }
        return result;
    }

    protected abstract Properties edit(Properties properties);

    protected abstract void createDB(Properties properties) throws Exception;

    protected abstract void createUser(Properties properties) throws Exception;

    protected abstract void checkStructure(Properties properties) throws Exception;

}
