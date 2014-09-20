package sk.r3n.plugin;

import java.io.Serializable;
import java.sql.Connection;
import org.apache.maven.plugin.logging.Log;
import sk.r3n.plugin.oracle.OracleStructureLoader;
import sk.r3n.plugin.postgres.PostgreStructureLoader;

/**
 *
 * @author jan
 */
public abstract class StructureLoader implements Serializable {

    public static StructureLoader getInstance(String jdbcDriver) {
        StructureLoader result;
        if (jdbcDriver.equals("oracle.jdbc.driver.OracleDriver")) {
            result = new OracleStructureLoader();
        } else {
            result = new PostgreStructureLoader();
        }
        return result;
    }

    public abstract Structure load(Log log, Connection connection, String jdbcUser) throws Exception;

}
