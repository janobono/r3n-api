/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

/**
 * Select as column definition object.
 */
public class ColumnSelect extends Column {

    private final String columnId;

    private final Query.Select select;

    public ColumnSelect(String columnId, Query.Select select, DataType dataType) {
        super(null, null, dataType);
        this.columnId = columnId;
        this.select = select;
    }

    @Override
    public String getColumnId() {
        return columnId;
    }

    public Query.Select getSelect() {
        return select;
    }

    @Override
    public String toString() {
        return "ColumnSelect{" + "select=" + select + ", dataType=" + getDataType() + '}';
    }
}
