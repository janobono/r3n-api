package sk.r3n.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DbTable {

    protected String tableName;
    protected SQLColumn[] ids;
    protected SQLColumn[] values;
    protected SQLColumn[] state;
    protected SQLColumn[] columns;
    protected SQLColumn[] valuesAndStates;

    public DbTable() {
        super();
    }

    public SQLColumn[] getColumns() {
        if (columns == null) {
            List<SQLColumn> list = new ArrayList<>();
            list.addAll(Arrays.asList(ids));
            list.addAll(Arrays.asList(values));
            list.addAll(Arrays.asList(state));
            columns = new SQLColumn[list.size()];
            columns = list.toArray(columns);
        }
        return columns;
    }

    public SQLColumn[] getValuesAndStates() {
        if (valuesAndStates == null) {
            List<SQLColumn> list = new ArrayList<>();
            list.addAll(Arrays.asList(values));
            list.addAll(Arrays.asList(state));
            valuesAndStates = new SQLColumn[list.size()];
            valuesAndStates = list.toArray(valuesAndStates);
        }
        return valuesAndStates;
    }

    public SQLColumn[] getIds() {
        return ids;
    }

    public SQLColumn[] getState() {
        return state;
    }

    public String getTableName() {
        return tableName;
    }

    public SQLColumn[] getValues() {
        return values;
    }
}
