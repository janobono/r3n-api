package sk.r3n.db;

import java.util.Objects;

public class SQLColumn {

    protected short dataType;
    protected String name;
    protected String table;

    public SQLColumn(String table, String name, short dataType) {
        super();
        this.table = table;
        this.dataType = dataType;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SQLColumn) {
            SQLColumn objColumn = (SQLColumn) obj;
            return table.equals(objColumn.getTable())
                    && name.equals(objColumn.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.dataType;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.table);
        return hash;
    }

    public short getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public void setDataType(short dataType) {
        this.dataType = dataType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return table + "[" + name + "]";
    }
}
