package sk.r3n.ui.table.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NCheckBox;
import sk.r3n.ui.util.UIServiceManager;

public class CheckBoxTableEditor extends AbstractCellEditor implements
		TableCellEditor, ActionListener, ItemListener, Serializable {

	private static final long serialVersionUID = -270982443876846787L;

	protected R3NCheckBox checkBox;

	protected int clickCount;

	public CheckBoxTableEditor() {
		checkBox = new R3NCheckBox();
		checkBox.getActionMap().remove(
				UIServiceManager.getDefaultUIService().getActionMapKey(
						UIService.class.getCanonicalName(),
						UIService.ACTION_CELL_OK));
		checkBox.setOpaque(true);
		checkBox.setBorderPaintedFlat(true);
		checkBox.setBorderPainted(true);
		checkBox.setBorder(new EmptyBorder(1, 1, 1, 1));
		checkBox.setHorizontalAlignment(JCheckBox.CENTER);
		checkBox.addActionListener(this);
		checkBox.setRequestFocusEnabled(false);
		clickCount = 2;
	}

	public void actionPerformed(ActionEvent e) {
		stopCellEditing();
	}

	public Object getCellEditorValue() {
		return Boolean.valueOf(checkBox.isSelected());
	}

	public R3NCheckBox getCheckBox() {
		return checkBox;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		checkBox.setFont(table.getFont());
		if (value == null) {
			checkBox.setSelected(false);
			checkBox.setText("");
		} else
			setValue(value);
		return checkBox;
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= clickCount;
		}
		return true;
	}

	public void itemStateChanged(ItemEvent e) {
		stopCellEditing();
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	protected void setValue(Object value) {
		boolean selected = false;
		if (value instanceof Boolean) {
			selected = ((Boolean) value).booleanValue();
		} else if (value instanceof String) {
			selected = value.equals("true");
		}
		checkBox.setSelected(selected);
	}
}
