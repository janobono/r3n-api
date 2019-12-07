/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.example.ora;

import org.junit.Ignore;
import org.junit.Test;
import sk.r3n.jdbc.SqlUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.LogManager;

@Ignore
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
