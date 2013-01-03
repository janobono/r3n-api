package sk.r3n.sw.test;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import sk.r3n.sw.component.table.R3NTable;
import sk.r3n.sw.component.table.R3NTableConfig;

public class TablePanel extends JPanel {

    public TablePanel() {
        super(new BorderLayout());

        R3NTableConfig tableConfig = new R3NTableConfig() {
            @Override
            public int autoResizeMode() {
                return JTable.AUTO_RESIZE_ALL_COLUMNS;
            }

            @Override
            public int columnCount() {
                return 3;
            }

            @Override
            public String columnName(int column) {
                return "C" + column;
            }

        };
        R3NTable table = new R3NTable(tableConfig) {
            @Override
            protected Object getValue(int index, Object row) {
                return "R" + row + "C" + index;
            }

        };

        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        table.setValues(list);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

}
