package sk.r3n.sql;

import java.util.Arrays;

public class ColumnFunction extends Column {

    private final Column[] members;

    private final Object hashObj;

    public ColumnFunction(String function, DataType dataType, Column... members) {
        super(function, null, dataType);
        this.members = members;
        hashObj = new Object();
    }

    public Column[] getMembers() {
        return members;
    }

    @Override
    public int hashCode() {
        return hashObj.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Column) {
            result = hashCode() == ((Column) obj).hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "ColumnFunction{" + "function=" + getName() + ", members=" + Arrays.toString(members) + ", dataType=" + getDataType() + '}';
    }

}
