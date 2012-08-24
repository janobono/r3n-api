package sk.r3n.ui.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sk.r3n.ui.IdAction;
import sk.r3n.action.IdActionExecutor;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public abstract class ListFormField<T> extends JPanel implements
        R3NInputComponent<List<T>>, IdActionExecutor, MouseListener {

    protected R3NComboBox<T> box;
    protected R3NInputComponent<?> field;
    protected List<T> values;
    protected boolean edit;
    protected boolean edited;
    protected EventListenerList eventListenerList;
    protected boolean editEnabled;

    public ListFormField() {
        super(new GridBagLayout());
        eventListenerList = new EventListenerList();
        values = new ArrayList<>();
        // Box
        box = initBox();
        box.setVisible(true);
        box.addMouseListener(this);
        add(this.box, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_ADD,
                JComponent.WHEN_FOCUSED, box, this);
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_EDIT,
                JComponent.WHEN_FOCUSED, box, this);
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_REMOVE,
                JComponent.WHEN_FOCUSED, box, this);

        // Field
        field = initField();
        ((JComponent) field).setVisible(false);
        add(((JComponent) field), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        setFieldKeyStroke(UIService.class.getCanonicalName(),
                UIService.ACTION_CELL_OK,
                new IdAction(UIService.class.getCanonicalName(),
                UIService.ACTION_CELL_OK, this));
        setFieldKeyStroke(UIService.class.getCanonicalName(),
                UIService.ACTION_CELL_CANCEL,
                new IdAction(UIService.class.getCanonicalName(),
                UIService.ACTION_CELL_CANCEL, this));
        setValue(null);
    }

    protected abstract T add();

    public void addChangeListener(ChangeListener changeListener) {
        eventListenerList.add(ChangeListener.class, changeListener);
    }

    public abstract void setColumns(int columns);

    @Override
    public void execute(String groupId, int actionId, Object source) {
        if (groupId.equals(UIService.class.getCanonicalName()) && editEnabled) {
            switch (actionId) {
                case UIService.ACTION_ADD:
                case UIService.ACTION_EDIT:
                    edit = actionId == UIService.ACTION_EDIT;
                    if (edit && box.getSelectedItem() == null) {
                        return;
                    }
                    box.setVisible(false);
                    if (box.getSelectedItem() != null && edit) {
                        set((T) box.getSelectedItem());
                    } else {
                        field.setValue(null);
                    }
                    ((JComponent) field).setVisible(true);
                    ((JComponent) field).requestFocus();
                    edited = false;
                    break;
                case UIService.ACTION_REMOVE:
                    if (box.getSelectedItem() != null) {
                        T t = (T) box.getSelectedItem();
                        box.removeItem(t);
                        values.remove(t);
                        fireChanged();
                    }
                    break;
                case UIService.ACTION_CELL_OK:
                    if (edit) {
                        get();
                        if (edited) {
                            fireChanged();
                        }
                        if (box.getSelectedItem() != null && field.isContentNull()) {
                            T t = (T) box.getSelectedItem();
                            box.removeItem(t);
                            values.remove(t);
                        }
                    } else {
                        T t = add();
                        if (t != null) {
                            values.add(t);
                            box.addItem(t);
                            box.setSelectedItem(t);
                            fireChanged();
                        }
                    }
                    ((JComponent) field).setVisible(false);
                    box.setVisible(true);
                    box.requestFocus();
                    break;
                case UIService.ACTION_CELL_CANCEL:
                    ((JComponent) field).setVisible(false);
                    box.setVisible(true);
                    box.requestFocus();
                    break;
            }
        }
    }

    protected void fireChanged() {
        Object[] listeners = eventListenerList.getListenerList();
        ChangeEvent changeEvent = new ChangeEvent(this);
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    protected abstract void get();

    @Override
    public List<T> getValue() {
        return values;
    }

    protected abstract R3NComboBox<T> initBox();

    protected abstract R3NInputComponent<?> initField();

    @Override
    public boolean isContentNull() {
        return values == null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (box.getSelectedItem() != null) {
                execute(UIService.class.getCanonicalName(),
                        UIService.ACTION_EDIT, e.getSource());
            } else {
                execute(UIService.class.getCanonicalName(),
                        UIService.ACTION_ADD, e.getSource());
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public void removeChangeListener(ChangeListener changeListener) {
        eventListenerList.remove(ChangeListener.class, changeListener);
    }

    @Override
    public void requestFocus() {
        if (box.isVisible()) {
            box.requestFocus();
        } else {
            ((JComponent) field).requestFocus();
        }
    }

    protected abstract void set(T t);

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        box.setEnabled(enabled);
        ((JComponent) field).setEnabled(enabled);
    }

    protected abstract void setFieldKeyStroke(String groupId, int actionId,
            IdAction idAction);

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        if (preferredSize.height < box.getPreferredSize().height) {
            preferredSize.height = box.getPreferredSize().height;
        }
        super.setPreferredSize(preferredSize);
        setMinimumSize(getPreferredSize());
    }

    @Override
    public void setValue(List<T> values) {
        this.values = values;
        if (values == null) {
            this.values = new ArrayList<>();
        }
        box.removeAllItems();
        for (T t : this.values) {
            box.addItem(t);
        }
    }

    public void setEditEnabled(boolean editEnabled) {
        this.editEnabled = editEnabled;
    }
}
