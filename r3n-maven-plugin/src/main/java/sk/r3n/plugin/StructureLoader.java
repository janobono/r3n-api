/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin;

import org.apache.maven.plugin.logging.Log;
import sk.r3n.plugin.oracle.OracleStructureLoader;
import sk.r3n.plugin.postgres.PostgreStructureLoader;
import sk.r3n.sql.Column;
import sk.r3n.sql.Table;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

public abstract class StructureLoader implements Serializable {

    static StructureLoader getInstance(String jdbcDriver) {
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

    Structure load(Log log, Connection connection) {
        Structure structure = new Structure();

        loadSequences(log, connection, structure);
        loadTables(log, connection, structure);

        structure.getTables().forEach(table -> structure.getColumns(table).addAll(loadColumns(log, connection, table)));
        return structure;
    }

    protected abstract void loadSequences(Log log, Connection connection, Structure structure);

    protected abstract void loadTables(Log log, Connection connection, Structure structure);

    protected abstract List<Column> loadColumns(Log log, Connection connection, Table table);

}
