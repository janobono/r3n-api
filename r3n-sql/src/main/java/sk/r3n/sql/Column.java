/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import java.io.Serializable;

/**
 * Base column definition object.
 */
public class Column implements Serializable {

    private String name;

    private Table table;

    private DataType dataType;

    public Column(String name, Table table, DataType dataType) {
        this.name = name;
        this.table = table;
        this.dataType = dataType;
    }

    public String getColumnId() {
        StringBuilder sb = new StringBuilder();
        if (table != null) {
            sb.append(table.getAlias()).append(".");
        }
        sb.append(name);
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "Column{" + "name=" + name + ", table=" + table + ", dataType=" + dataType + '}';
    }

}
