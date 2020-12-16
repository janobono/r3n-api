package sk.r3n.jdbc.postgre;

import sk.r3n.sql.Table;

public enum MetaTable {

    T_BASE_TYPES("t_base_types", "t1"),
    T_JOIN("t_join", "t2");

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
