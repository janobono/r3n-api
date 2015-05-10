package sk.r3n.db;

import java.util.Properties;
import sk.r3n.app.AppException;
import sk.r3n.jdbc.ConnectionService;

public interface DbManagerServiceIO {

    public void createDB(ConnectionService connectionService, Properties properties) throws AppException;

    public void createUser(ConnectionService connectionService, Properties properties) throws AppException;

}
