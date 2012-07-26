package sk.r3n.ui.table.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.field.R3NField;
import sk.r3n.ui.util.UIServiceManager;

public class R3NFieldTableEditor<T> extends AbstractCellEditor implements
        TableCellEditor, ItemListener, Serializable {

    private static final long serialVersionUID = -106525087322054239L;
    protected R3NField<T> baseField;
    protected T oldValue;

    public R3NFieldTableEditor(R3NField<T> baseField) {
        this.baseField = baseField;
        baseField.setOpaque(true);

        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK,
                R3NField.WHEN_FOCUSED, baseField, new Action() {

            @Override
            public void actionPerformed(ActionEvent e) {
                R3NFieldTableEditor.this.stopCellEditing();
            }

            @Override
            public void setEnabled(boolean b) {
            }

            @Override
            public void removePropertyChangeListener(
                    PropertyChangeListener listener) {
            }

            @Override
            public void putValue(String key, Object value) {
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void addPropertyChangeListener(
                    PropertyChangeListener listener) {
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        if (baseField.contentValid() == R3NField.VALID) {
            return baseField.getValue();
        }
        UIServiceManager.getDefaultUIService().getBuzzer().buzz(baseField);
        return oldValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        baseField.setFont(table.getFont());
        if (value == null) {
            baseField.setValue(null);
            oldValue = null;
        } else {
            baseField.setValue((T) value);
            oldValue = (T) value;
        }
        return baseField;
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
