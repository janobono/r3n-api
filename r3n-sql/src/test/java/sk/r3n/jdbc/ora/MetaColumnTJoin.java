package sk.r3n.jdbc.ora;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnTJoin {

    ID("id", DataType.LONG),
    T_BASE_TYPES_FK("t_base_types_fk", DataType.LONG),
    T_JOIN_STRING("t_join_string", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnTJoin(String columnName, DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return new Column(columnName, MetaTable.T_JOIN.table(), dataType);
    }

    public Column column(String tableAlias) {
        return new Column(columnName, MetaTable.T_JOIN.table(tableAlias), dataType);
    }

    public static Column[] columns() {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnTJoin metaColumnTJoin : values()) {
            columnList.add(metaColumnTJoin.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(String tableAlias) {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnTJoin metaColumnTJoin : values()) {
            columnList.add(metaColumnTJoin.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
