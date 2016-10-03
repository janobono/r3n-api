package sk.r3n.plugin;

import java.io.Serializable;
import java.sql.Connection;
import org.apache.maven.plugin.logging.Log;
import sk.r3n.plugin.h2.H2StructureLoader;
import sk.r3n.plugin.oracle.OracleStructureLoader;
import sk.r3n.plugin.postgres.PostgreStructureLoader;

public abstract class StructureLoader implements Serializable {

    public static StructureLoader getInstance(String jdbcDriver) {
        StructureLoader result;
        if (jdbcDriver.equals("oracle.jdbc.driver.OracleDriver")) {
            result = new OracleStructureLoader();
        } else if (jdbcDriver.equals("org.postgresql.Driver")) {
            result = new PostgreStructureLoader();
        } else {
            result = new H2StructureLoader();
        }
        return result;
    }

    public abstract Structure load(Log log, Connection connection, String jdbcUser) throws Exception;

}
