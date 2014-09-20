import java.sql.Connection;
import java.sql.DriverManager;
import sk.r3n.jdbc.SqlUtil;

public class CreateDB {

    public static void main(String... args) {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("user.dir") + "/target/test", "sa", "sa");

            SqlUtil.runSqlScript(connection, CreateDB.class.getResourceAsStream("/install.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }
}
