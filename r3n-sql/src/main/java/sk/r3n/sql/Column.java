/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Base column definition object.
 *
 * @author janobono
 * @since 18 August 2014
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Column implements Serializable {

    private String name;

    private Table table;

    private DataType dataType;

    public String getColumnId() {
        StringBuilder sb = new StringBuilder();
        if (table != null) {
            sb.append(table.getAlias()).append(".");
        }
        sb.append(name);
        return sb.toString();
    }
}
