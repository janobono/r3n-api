package sk.r3n.jdbc;

public class ConnectionServiceFactory {

    public static ConnectionService createConnectionService(DbType dbType) {
        switch (dbType) {
            case POSTGRE:
                return new PostgreConnectionService();
            case SQL_SERVER:
                return new SqlServerConnectionService();
            case H2:
                return new H2ConnectionService();
        }
        return null;
    }

}
