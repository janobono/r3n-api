package sk.r3n.example.dal.domain.r3n.meta;

import sk.r3n.sql.Table;

public enum MetaTable {

    HOTEL("hotel", "t3");

    private final String tableName;

    private final String tableAlias;

    MetaTable(final String tableName, final String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    public Table table() {
        return new Table(tableName, tableAlias);
    }

    public Table table(final String tableAlias) {
        return new Table(tableName, tableAlias);
    }
}
