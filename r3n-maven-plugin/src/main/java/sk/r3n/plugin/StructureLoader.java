/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin;

import java.io.Serializable;
import java.sql.Connection;
import org.apache.maven.plugin.logging.Log;
import sk.r3n.plugin.oracle.OracleStructureLoader;
import sk.r3n.plugin.postgres.PostgreStructureLoader;

public abstract class StructureLoader implements Serializable {

    public static StructureLoader getInstance(String jdbcDriver) {
        StructureLoader result;
        switch (jdbcDriver) {
            case "oracle.jdbc.driver.OracleDriver":
                result = new OracleStructureLoader();
                break;
            case "org.postgresql.Driver":
                result = new PostgreStructureLoader();
                break;
            default:
                throw new RuntimeException("Unsupported driver!");
        }
        return result;
    }

    public abstract Structure load(Log log, Connection connection, String jdbcUser) throws Exception;

}
