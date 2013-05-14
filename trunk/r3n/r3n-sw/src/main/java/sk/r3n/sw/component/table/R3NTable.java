package sk.r3n.sw.component.table;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public abstract class R3NTable<T> extends JTable {

    protected class BaseTableModel extends AbstractTableModel {

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column].columnName();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            try {
                return getValue(columns[columnIndex], rows.get(rowIndex));
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
    protected R3NTableColumn[] columns;

    protected boolean focusable;

    protected List<T> rows;

    protected T selectedRow;

    protected int selectedColumn;

    public R3NTable(R3NTableColumn[] columns) {
        super();
        this.columns = columns;
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        focusable = super.isFocusable();
        rows = new ArrayList<>();
        setModel(new BaseTableModel());
    }

    public void addValue(T row) {
        if (row == null) {
            return;
        }
        this.rows.add(row);
        getBaseTableModel().fireTableDataChanged();
        setSelectedValue(row);
    }

    public void down() {
        if (getRowCount() > 0) {
            int uDown = getSelectedRow();
            if (uDown < getRowCount() - 1) {
                setRowSelectionInterval(uDown + 1, uDown + 1);
                getSelectedValue();
                scrollRectToVisible();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected BaseTableModel getBaseTableModel() {
        return (BaseTableModel) getModel();
    }

    public T getSelectedValue() {
        int index = getSelectedRow();
        if (index == -1 || rows.size() <= index) {
            return null;
        }
        try {
            index = convertRowIndexToModel(index);
            selectedRow = rows.get(index);
            return selectedRow;
        } catch (Exception e) {
            return null;
        }
    }

    public List<T> getSelectedValues() {
        List<T> list = new ArrayList<>();
        int[] indexes = getSelectedRows();
        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            if (index == -1 || rows.size() <= index) {
                continue;
            }
            try {
                index = convertRowIndexToModel(index);
                list.add(rows.get(index));
            } catch (Exception e) {
                continue;
            }
        }
        return list;
    }

    protected abstract Object getValue(R3NTableColumn column, T row);

    public List<T> getValues() {
        return rows;
    }

    @Override
    public boolean isFocusable() {
        return super.isFocusable() && focusable;
    }

    public void scrollRectToVisible() {
        int row = getSelectedRow();
        if (row == -1) {
            row = 0;
        }
        int column = getSelectedColumn();
        if (column == -1) {
            column = 0;
        }
        scrollRectToVisible(getCellRect(row, column, true));
    }

    @Override
    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
        super.setFocusable(focusable);
    }

    public void setSelectedValue(T row) {
        if (row != null) {
            if (this.rows.contains(row)) {
                int index = this.rows.indexOf(row);
                try {
                    index = convertRowIndexToView(index);
                } catch (Exception e) {
                }
                getSelectionModel().setSelectionInterval(index, index);
                selectedRow = this.rows.get(this.rows.indexOf(row));
            }
        } else {
            selectedRow = null;
            getSelectionModel().removeSelectionInterval(0, rows.size());
        }
    }

    public void setValues(List<T> rows) {
        if (getSelectedColumn() != -1) {
            selectedColumn = getSelectedColumn();
        }
        setEnabled(rows != null);
        if (rows == null) {
            this.rows = new ArrayList<>();
        } else {
            this.rows = rows;
        }
        if (this.rows.size() > 0) {
            super.setFocusable(focusable);
        } else {
            super.setFocusable(false);
        }
        getBaseTableModel().fireTableDataChanged();
        setSelectedValue(selectedRow);
        addColumnSelectionInterval(selectedColumn, selectedColumn);
    }
}
