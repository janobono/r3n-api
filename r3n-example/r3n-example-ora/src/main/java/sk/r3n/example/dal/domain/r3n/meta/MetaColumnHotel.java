package sk.r3n.example.dal.domain.r3n.meta;

import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

import java.util.ArrayList;
import java.util.List;

public enum MetaColumnHotel {

    ID("id", DataType.LONG),
    NAME("name", DataType.STRING),
    NOTE("note", DataType.STRING);

    private final String columnName;

    private final DataType dataType;

    MetaColumnHotel(final String columnName, final DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return Column.column(columnName, dataType, MetaTable.HOTEL.table());
    }

    public Column column(final String tableAlias) {
        return Column.column(columnName, dataType, MetaTable.HOTEL.table(tableAlias));
    }

    public static Column[] columns() {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnHotel metaColumnHotel : values()) {
            columnList.add(metaColumnHotel.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(final String tableAlias) {
        final List<Column> columnList = new ArrayList<>();
        for (final MetaColumnHotel metaColumnHotel : values()) {
            columnList.add(metaColumnHotel.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
