/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import java.util.Arrays;

/**
 * Function definition object.
 */
public class ColumnFunction extends Column {

    private final String columnId;

    private final Column[] members;

    public ColumnFunction(String columnId, String function, DataType dataType, Column... members) {
        super(function, null, dataType);
        this.columnId = columnId;
        this.members = members;
    }

    @Override
    public String getColumnId() {
        return columnId;
    }

    public Column[] getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "ColumnFunction{" + "function=" + getName() + ", members=" + Arrays.toString(members) + ", dataType=" + getDataType() + '}';
    }

}
