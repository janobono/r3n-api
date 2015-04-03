package sk.r3n.example.test.ora;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import sk.r3n.example.test.h2.H2Test;
import sk.r3n.jdbc.OraSqlBuilder;
import sk.r3n.jdbc.SqlUtil;

public class OraTest {

    private static final Log LOG = LogFactory.getLog(H2Test.class);

    public OraTest() {
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
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.0.0.9:1521:xe", "test", "test");

            SqlUtil.runSqlScript(connection, OraTest.class.getResourceAsStream("/clean_ora.sql"));
            SqlUtil.runSqlScript(connection, OraTest.class.getResourceAsStream("/install_ora.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Test
    public void oraTest() throws Exception {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.0.0.9:1521:xe", "test", "test");

            assertTrue(connection.getAutoCommit());

            OraSqlBuilder oraSqlBuilder = new OraSqlBuilder();

        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            SqlUtil.close(connection);
        }
    }
}
