package sk.r3n.db;

public class ConnectionServiceFactory {

    public static ConnectionService createConnectionService(DbType dbType) {
        switch (dbType) {
            case POSTGRE:
                return new PostgreConnectionService();
            case H2:
                return new H2ConnectionService();
        }
        return null;
    }

}
