/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.plugin;

import sk.r3n.sql.Column;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Structure implements Serializable {

    private List<Sequence> sequences;

    private List<Table> tables;

    private Map<String, List<Column>> columns;

    public List<Sequence> getSequences() {
        if (sequences == null) {
            sequences = new ArrayList<>();
        }
        return sequences;
    }

    public List<Table> getTables() {
        if (tables == null) {
            tables = new ArrayList<>();
        }
        return tables;
    }

    private Map<String, List<Column>> getColumns() {
        if (columns == null) {
            columns = new HashMap<>();
        }
        return columns;
    }

    List<Column> getColumns(Table table) {
        getColumns().computeIfAbsent(table.getName(), k -> new ArrayList<>());
        return getColumns().get(table.getName());
    }

}
