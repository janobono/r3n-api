package sk.r3n.ui.table.editor;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import sk.r3n.ui.component.R3NButton;

public abstract class R3NButtonTableEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener, ItemListener, Serializable {

    private static final long serialVersionUID = 5944120201409497744L;
    protected R3NButton button;
    protected Object oldValue;

    public R3NButtonTableEditor() {
        button = new R3NButton("...");
        button.setOpaque(true);
        button.addActionListener(this);
        button.setRequestFocusEnabled(false);
    }

    public R3NButton getButton() {
        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        button.setFont(table.getFont());
        oldValue = value;
        return button;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getClickCount() >= 2;
        }
        return true;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        stopCellEditing();
    }
}
