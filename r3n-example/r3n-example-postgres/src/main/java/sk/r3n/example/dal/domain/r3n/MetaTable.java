package sk.r3n.example.dal.domain.r3n;

import sk.r3n.sql.Table;

public enum MetaTable {

    HOTEL("hotel", "t1");

    private final String tableName;

    private final String tableAlias;

    MetaTable(String tableName, String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    public Table table() {
        return new Table(tableName, tableAlias);
    }

    public Table table(String tableAlias) {
        return new Table(tableName, tableAlias);
    }
}
