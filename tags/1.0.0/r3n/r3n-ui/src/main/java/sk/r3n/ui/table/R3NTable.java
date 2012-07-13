package sk.r3n.ui.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NTable<T> extends JTable {

	private static final long serialVersionUID = -483213338828039940L;

	protected class BaseTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -5982222825929188436L;

		public int getColumnCount() {
			return table.getColumns().size();
		}

		@Override
		public String getColumnName(int column) {
			try {
				return table.getColumns().get(column).getName();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public int getRowCount() {
			return rows.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			try {
				return getValue(table.getColumns().get(columnIndex).getId(),
						rows.get(rowIndex));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			try {
				return isEditable(table.getColumns().get(columnIndex).getId(),
						rows.get(rowIndex));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			try {
				setValue(table.getColumns().get(columnIndex).getId(),
						rows.get(rowIndex), value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	protected Table table;

	protected Map<Integer, TableColumn> columnsMap;

	protected boolean editable;
	protected boolean focusable;

	private EventListenerList eventListenerList;

	protected List<T> rows;

	protected T selectedRow;

	public R3NTable(Table table) {
		super();
		this.table = table;
		setAutoResizeMode(table.getAutoResizeMode());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eventListenerList = new EventListenerList();
		focusable = super.isFocusable();
		columnsMap = new HashMap<Integer, TableColumn>();
		rows = new ArrayList<T>();
		setModel(new BaseTableModel());
		initColumns();
		if (UIManager.getLookAndFeel().getClass().getCanonicalName()
				.endsWith("NimbusLookAndFeel")) {
			setRowHeight((int) (getRowHeight() * 2f));
		}
		// Modifikacia klavesovych skratiek
		InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		KeyStroke[] ks = im.allKeys();
		InputMap im2 = new InputMap();
		if (ks != null) {
			for (int x = 0; x < ks.length; x++) {
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed DOWN"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_DOWN, im2, im.get(ks[x]));
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_NEXT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed LEFT"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_LEFT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed RIGHT"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_RIGHT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed UP"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_UP, im2, im.get(ks[x]));
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_PREVIOUS, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("ctrl pressed HOME"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_FIRST, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("ctrl pressed END"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_LAST, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed F2"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_CELL_EDIT, im2, im.get(ks[x]));
				}
				if (ks[x].equals(KeyStroke.getKeyStroke("pressed ESCAPE"))) {
					UIServiceManager.getDefaultUIService().setKeyStroke(
							UIService.class.getCanonicalName(),
							UIService.ACTION_CELL_CANCEL, im2, im.get(ks[x]));
				}
			}
			setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im2);
		}
		this.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);

		// Modifikácia fokusov
		UIServiceManager.getDefaultUIService().modifyFocus(this);
	}

	@SuppressWarnings("unchecked")
	protected void initColumns() {
		columnsMap.clear();
		((BaseTableModel) getModel()).fireTableStructureChanged();
		TableColumnModel tableColumnModel = getColumnModel();
		int index = 0;
		for (Column column : table.getColumns()) {
			TableColumn tableColumn = tableColumnModel.getColumn(index);
			customizeColumn(column.getId(), tableColumn);
			tableColumn.setPreferredWidth(column.getWidth());
			columnsMap.put(column.getId(), tableColumn);
			index++;
		}
	}

	public void addTableValueEditedListener(
			TableValueEditedListener tableValueEditedListener) {
		eventListenerList.add(TableValueEditedListener.class,
				tableValueEditedListener);
	}

	public void addValue(T row) {
		if (row == null)
			return;
		this.rows.add(row);
		getBaseTableModel().fireTableDataChanged();
		setSelectedValue(row);
	}

	protected abstract void customizeColumn(int index, TableColumn tableColumn);

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

	protected void fireTableValueEdited(T row) {
		Object[] listeners = eventListenerList.getListenerList();
		TableValueEditedEvent<T> tableValueEditedEvent = new TableValueEditedEvent<T>(
				this, row);
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == TableValueEditedListener.class) {
				((TableValueEditedListener) listeners[i + 1])
						.tableValueEdited(tableValueEditedEvent);
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
		List<T> list = new ArrayList<T>();
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

	protected abstract Object getValue(int index, T row);

	public List<T> getValues() {
		return rows;
	}

	public boolean isEditable() {
		return editable;
	}

	protected abstract boolean isEditable(int index, T row);

	@Override
	public boolean isFocusable() {
		return super.isFocusable() && focusable;
	}

	public void removeTableValueEditedListener(
			TableValueEditedListener tableValueEditedListener) {
		eventListenerList.remove(TableValueEditedListener.class,
				tableValueEditedListener);
	}

	public void removeValue(T row) {
		if (row == null)
			return;
		if (row.equals(selectedRow))
			selectedRow = null;
		this.rows.remove(row);
		getBaseTableModel().fireTableDataChanged();
		if (selectedRow == null && rows.size() > 0)
			selectedRow = rows.get(0);
		setSelectedValue(selectedRow);
	}

	public void scrollRectToVisible() {
		int row = getSelectedRow();
		if (row == -1)
			row = 0;
		int column = getSelectedColumn();
		if (column == -1)
			column = 0;
		scrollRectToVisible(getCellRect(row, column, true));
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
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

	protected abstract void setValue(int index, T row, Object value);

	protected int selectedColumn;

	public void setValues(List<T> rows) {
		if (getSelectedColumn() != -1)
			selectedColumn = getSelectedColumn();

		setEnabled(rows != null);
		if (rows == null) {
			this.rows = new ArrayList<T>();
		} else {
			this.rows = rows;
		}
		if (this.rows.size() > 0)
			super.setFocusable(focusable);
		else
			super.setFocusable(false);
		getBaseTableModel().fireTableDataChanged();
		setSelectedValue(selectedRow);

		addColumnSelectionInterval(selectedColumn, selectedColumn);
	}

	public void up() {
		if (getRowCount() > 0) {
			int lUp = getSelectedRow();
			if (lUp == -1) {
				lUp = 1;
			}
			if (lUp > 0) {
				setRowSelectionInterval(lUp - 1, lUp - 1);
				getSelectedValue();
				scrollRectToVisible();
			}
		}
	}
}
