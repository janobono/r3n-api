/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import sk.r3n.sql.impl.ColumnBase;
import sk.r3n.sql.impl.ColumnFunction;
import sk.r3n.sql.impl.ColumnSelect;

/**
 * Base column definition object.
 *
 * @author janobono
 * @since 18 August 2014
 */
public interface Column {

    static Column column(final String name, final DataType dataType, final Table table) {
        return new ColumnBase(name, dataType, table);
    }

    static Column column(final String columnId, final DataType dataType, final String function, final Column... members) {
        return new ColumnFunction(columnId, dataType, function, members);
    }

    static Column column(final String columnId, final DataType dataType, final Query.Select select) {
        return new ColumnSelect(columnId, dataType, select);
    }

    String columnId();

    DataType dataType();
}
