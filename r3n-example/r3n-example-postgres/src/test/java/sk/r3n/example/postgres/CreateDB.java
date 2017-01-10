package sk.r3n.example.postgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.LogManager;
import org.junit.Test;
import sk.r3n.jdbc.SqlUtil;

public class CreateDB {

    public CreateDB() {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (IOException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void oraTest() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "test", "test");

            SqlUtil.runSqlScript(connection, CreateDB.class.getResourceAsStream("/clean_postgres.sql"));
            SqlUtil.runSqlScript(connection, CreateDB.class.getResourceAsStream("/install_postgres.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

}
