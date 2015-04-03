package sk.r3n.example.test.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import sk.r3n.example.test.h2.H2Test;
import sk.r3n.jdbc.PostgreSqlBuilder;
import sk.r3n.jdbc.SqlUtil;

public class PostgresTest {

    private static final Log LOG = LogFactory.getLog(H2Test.class);

    public PostgresTest() {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void init() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "test", "test");

            SqlUtil.runSqlScript(connection, PostgresTest.class.getResourceAsStream("/clean_postgres.sql"));
            SqlUtil.runSqlScript(connection, PostgresTest.class.getResourceAsStream("/install_postgres.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Test
    public void postgresTest() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "test", "test");

            assertTrue(connection.getAutoCommit());

            PostgreSqlBuilder postgreSqlBuilder = new PostgreSqlBuilder();

        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            SqlUtil.close(connection);
        }
    }
}
