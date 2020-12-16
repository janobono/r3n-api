/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;


import lombok.Getter;
import lombok.ToString;

/**
 * Select as column definition object.
 *
 * @author janobono
 * @since 19 November 2014
 */
@Getter
@ToString(callSuper = true)
public class ColumnSelect extends Column {

    private final String columnId;

    private final Query.Select select;

    public ColumnSelect(String columnId, Query.Select select, DataType dataType) {
        super(null, null, dataType);
        this.columnId = columnId;
        this.select = select;
    }
}
