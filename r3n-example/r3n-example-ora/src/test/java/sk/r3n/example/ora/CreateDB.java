package sk.r3n.example.ora;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import sk.r3n.jdbc.SqlUtil;

public class CreateDB {

    private static final Log LOG = LogFactory.getLog(CreateDB.class);

    public CreateDB() {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void oraTest() throws Exception {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.0.0.9:1521:xe", "test", "test");

            SqlUtil.runSqlScript(connection, CreateDB.class.getResourceAsStream("/clean_ora.sql"));
            SqlUtil.runSqlScript(connection, CreateDB.class.getResourceAsStream("/install_ora.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

}
