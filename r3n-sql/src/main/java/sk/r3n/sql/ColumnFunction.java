/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import lombok.Getter;
import lombok.ToString;

/**
 * Function definition object.
 *
 * @author janobono
 * @since 26 September 2014
 */
@Getter
@ToString(callSuper = true)
public class ColumnFunction extends Column {

    private final String columnId;

    private final Column[] members;

    public ColumnFunction(String columnId, String function, DataType dataType, Column... members) {
        super(function, null, dataType);
        this.columnId = columnId;
        this.members = members;
    }
}
