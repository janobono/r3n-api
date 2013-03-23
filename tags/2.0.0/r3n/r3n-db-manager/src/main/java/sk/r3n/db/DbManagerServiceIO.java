package sk.r3n.db;

import java.util.Properties;
import sk.r3n.jdbc.ConnectionService;
import sk.r3n.util.R3NException;

public interface DbManagerServiceIO {

    public void createDB(ConnectionService connectionService, Properties properties) throws R3NException;

    public void createUser(ConnectionService connectionService, Properties properties) throws R3NException;

}