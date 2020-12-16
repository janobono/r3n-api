package sk.r3n.jdbc.ora;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnTBaseTypes {

    ID("id", DataType.LONG),
    T_SHORT("t_short", DataType.SHORT),
    T_INTEGER("t_integer", DataType.INTEGER),
    T_LONG("t_long", DataType.LONG),
    T_BIG_DECIMAL("t_big_decimal", DataType.BIG_DECIMAL),
    T_STRING_CHAR("t_string_char", DataType.STRING),
    T_STRING_CLOB("t_string_clob", DataType.STRING),
    T_STRING_VARCHAR2("t_string_varchar2", DataType.STRING),
    T_STRING_SCDF("t_string_scdf", DataType.STRING),
    T_BLOB("t_blob", DataType.BLOB),
    T_TIME_STAMP("t_time_stamp", DataType.TIME_STAMP),
    T_DATE("t_date", DataType.DATE),
    T_BOOLEAN("t_boolean", DataType.BOOLEAN);

    private final String columnName;

    private final DataType dataType;

    MetaColumnTBaseTypes(String columnName, DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return new Column(columnName, MetaTable.T_BASE_TYPES.table(), dataType);
    }

    public Column column(String tableAlias) {
        return new Column(columnName, MetaTable.T_BASE_TYPES.table(tableAlias), dataType);
    }

    public static Column[] columns() {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnTBaseTypes metaColumnTBaseTypes : values()) {
            columnList.add(metaColumnTBaseTypes.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(String tableAlias) {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnTBaseTypes metaColumnTBaseTypes : values()) {
            columnList.add(metaColumnTBaseTypes.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
