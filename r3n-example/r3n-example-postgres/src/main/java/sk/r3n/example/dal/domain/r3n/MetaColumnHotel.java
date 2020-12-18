package sk.r3n.example.dal.domain.r3n;

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

    MetaColumnHotel(String columnName, DataType dataType) {
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Column column() {
        return new Column(columnName, MetaTable.HOTEL.table(), dataType);
    }

    public Column column(String tableAlias) {
        return new Column(columnName, MetaTable.HOTEL.table(tableAlias), dataType);
    }

    public static Column[] columns() {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnHotel metaColumnHotel : values()) {
            columnList.add(metaColumnHotel.column());
        }
        return columnList.toArray(new Column[0]);
    }

    public static Column[] columns(String tableAlias) {
        List<Column> columnList = new ArrayList<>();
        for (MetaColumnHotel metaColumnHotel : values()) {
            columnList.add(metaColumnHotel.column(tableAlias));
        }
        return columnList.toArray(new Column[0]);
    }
}
