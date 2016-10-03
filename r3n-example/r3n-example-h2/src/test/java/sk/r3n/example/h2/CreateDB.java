package sk.r3n.example.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.util.FileUtil;

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
    public void h2Test() throws Exception {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "test.mv.db");
        if (file.exists()) {
            FileUtil.delete(file);
        }

        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/test", "sa", "sa");

            SqlUtil.runSqlScript(connection, CreateDB.class.getResourceAsStream("/install_h2.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

}
