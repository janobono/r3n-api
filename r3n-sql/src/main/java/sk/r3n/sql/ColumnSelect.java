package sk.r3n.sql;

public class ColumnSelect extends Column {

    private final Query.Select select;

    private final Object hashObj;

    public ColumnSelect(Query.Select select, DataType dataType) {
        super(null, null, dataType);
        this.select = select;
        hashObj = new Object();
    }

    public Query.Select getSelect() {
        return select;
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
        return "ColumnSelect{" + "select=" + select + ", dataType=" + getDataType() + '}';
    }
}
