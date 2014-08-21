package sk.r3n.plugin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sk.r3n.sql.Column;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Table;

public class Structure implements Serializable {

    private List<Sequence> sequences;

    private List<Table> tables;

    private Map<String, List<Column>> columns;

    public List<Sequence> getSequences() {
        if (sequences == null) {
            sequences = new ArrayList<Sequence>();
        }
        return sequences;
    }

    public void setSequences(List<Sequence> sequences) {
        this.sequences = sequences;
    }

    public List<Table> getTables() {
        if (tables == null) {
            tables = new ArrayList<Table>();
        }
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public Map<String, List<Column>> getColumns() {
        if (columns == null) {
            columns = new HashMap<String, List<Column>>();
        }
        return columns;
    }

    public void setColumns(Map<String, List<Column>> columns) {
        this.columns = columns;
    }

    public List<Column> getColumns(Table table) {
        if (getColumns().get(table.getName()) == null) {
            getColumns().put(table.getName(), new ArrayList<Column>());
        }
        return getColumns().get(table.getName());
    }

}
