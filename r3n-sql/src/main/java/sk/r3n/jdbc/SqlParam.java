/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import java.io.Serializable;
import sk.r3n.sql.DataType;

/**
 * Object to store parameters used in sql queries.
 */
public class SqlParam implements Serializable {

    /**
     * Parameter data type.
     */
    private DataType dataType;

    /**
     * Parameter value.
     */
    private Object value;

    public SqlParam(DataType dataType, Object value) {
        this.dataType = dataType;
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SqlParam{" + "dataType=" + dataType + ", value=" + value + '}';
    }

}
