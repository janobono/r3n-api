/*
 * Copyright 2014 janobono. All rights reserved.
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

/**
 * Db structure loader.
 *
 * @author janobono
 * @since 21 August 2014
 */
public abstract class StructureLoader implements Serializable {

    static StructureLoader getInstance(final String jdbcDriver) {
        return switch (jdbcDriver) {
            case "oracle.jdbc.driver.OracleDriver" -> new OracleStructureLoader();
            case "org.postgresql.Driver" -> new PostgreStructureLoader();
            default -> throw new RuntimeException("Unsupported driver!");
        };
    }

    Structure load(final Log log, final Connection connection) {
        final Structure structure = new Structure();

        loadSequences(log, connection, structure);
        loadTables(log, connection, structure);

        structure.getTables().forEach(table -> structure.getColumns(table).addAll(loadColumns(log, connection, table)));
        return structure;
    }

    protected abstract void loadSequences(Log log, Connection connection, Structure structure);

    protected abstract void loadTables(Log log, Connection connection, Structure structure);

    protected abstract List<Column> loadColumns(Log log, Connection connection, Table table);
}
