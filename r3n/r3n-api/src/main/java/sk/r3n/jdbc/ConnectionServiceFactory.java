package sk.r3n.jdbc;

public class ConnectionServiceFactory {

    public static ConnectionService createConnectionService(DbType dbType) {
        switch (dbType) {
            case POSTGRE:
                return new PostgreConnectionCreator();
            case SQL_SERVER:
                return new SqlServerConnectionCreator();
            case H2:
                return new H2ConnectionCreator();
        }
        return null;
    }

}
