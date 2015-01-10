package sk.r3n.sql;

import java.io.Serializable;

public class Column implements Serializable {

    private String name;

    private Table table;

    private DataType dataType;

    private String alias;

    public Column(String name, Table table, DataType dataType) {
        this.name = name;
        this.table = table;
        this.dataType = dataType;
    }

    @Override
    public int hashCode() {
        return nameWithAlias().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Column) {
            Column queryAttributeObj = (Column) obj;
            result = nameWithAlias().equals(queryAttributeObj.nameWithAlias());
        }
        return result;
    }

    public String nameWithAlias() {
        StringBuilder sb = new StringBuilder();
        sb.append(table.getAlias());
        sb.append(".");
        sb.append(name);
        return sb.toString().toLowerCase();
    }

    @Override
    public String toString() {
        if (alias == null) {
            return nameWithAlias();
        } else {
            return alias;
        }
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
