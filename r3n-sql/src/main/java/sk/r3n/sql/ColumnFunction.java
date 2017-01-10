package sk.r3n.sql;

import java.util.Arrays;

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
