package sk.r3n.example.test.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.r3n.jdbc.H2SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.util.FileUtil;

public class H2Test {

    private static final Log LOG = LogFactory.getLog(H2Test.class);

    public H2Test() {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void init() {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "test.mv.db");
        if (file.exists()) {
            FileUtil.delete(file);
        }

        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/test", "sa", "sa");

            SqlUtil.runSqlScript(connection, H2Test.class.getResourceAsStream("/install_h2.sql"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Test
    public void h2Test() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/test", "sa", "sa");

            assertTrue(connection.getAutoCommit());

            H2SqlBuilder sqlBuilder = new H2SqlBuilder();


        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            SqlUtil.close(connection);
        }
    }

}
