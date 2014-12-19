package sk.r3n.jdbc;

import java.io.Serializable;
import sk.r3n.sql.DataType;

public class SqlParam implements Serializable {

    private DataType dataType;

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