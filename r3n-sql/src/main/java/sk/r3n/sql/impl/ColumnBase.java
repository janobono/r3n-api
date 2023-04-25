/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql.impl;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Table;

/**
 * Base column definition object.
 *
 * @author janobono
 * @since 18 August 2014
 */
public record ColumnBase(String name, DataType dataType, Table table) implements Column {

    public String columnId() {
        final StringBuilder sb = new StringBuilder();
        if (table != null) {
            sb.append(table.alias()).append(".");
        }
        sb.append(name);
        return sb.toString();
    }
}
