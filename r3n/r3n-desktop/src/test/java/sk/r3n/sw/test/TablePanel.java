package sk.r3n.sw.test;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import sk.r3n.sw.component.table.R3NTable;
import sk.r3n.sw.component.table.R3NTableColumn;

public class TablePanel extends JPanel {

    private enum COLUMNS implements R3NTableColumn {

        C1,
        C2,
        C3;

        @Override
        public String columnName() {
            return name();
        }

        @Override
        public int width() {
            return 100;
        }
    }

    public TablePanel() {
        super(new BorderLayout());


        R3NTable table = new R3NTable(TablePanel.COLUMNS.values()) {
            @Override
            protected Object getValue(R3NTableColumn column, Object row) {
                return "R" + row + column.columnName();
            }
        };

        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        table.setValues(list);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
