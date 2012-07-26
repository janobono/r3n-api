package sk.r3n.ui.table;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private int autoResizeMode;
    private List<Column> columns;

    public Table() {
        super();
    }

    public int getAutoResizeMode() {
        return autoResizeMode;
    }

    public List<Column> getColumns() {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        return columns;
    }

    public void setAutoResizeMode(int autoResizeMode) {
        this.autoResizeMode = autoResizeMode;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
