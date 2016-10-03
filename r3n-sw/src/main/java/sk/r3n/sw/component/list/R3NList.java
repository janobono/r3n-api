package sk.r3n.sw.component.list;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class R3NList<T> extends JList {

    protected class BaseListModel extends AbstractListModel {

        protected void fireContentsChanged() {
            int start = 0;
            int end = values.size();
            super.fireContentsChanged(this, start, end);
        }

        @Override
        public Object getElementAt(int index) {
            if (index < values.size()) {
                return values.get(index);
            }
            return null;
        }

        @Override
        public int getSize() {
            return values.size();
        }
    }
    protected BaseListModel listModel;

    private List<T> values;

    public R3NList() {
        super();
        values = new ArrayList<T>();
        listModel = new BaseListModel();
        setModel(listModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public List<T> getValues() {
        return values;
    }

    public void setSelectedValue(T value) {
        if (value != null) {
            if (values.contains(value)) {
                setSelectedIndex(values.indexOf(value));
            }
        } else {
            removeSelectionInterval(0, values.size());
        }
    }

    public void setValues(List<T> values) {
        T selectedValue = (T) getSelectedValue();
        this.values = values;
        if (values == null) {
            this.values = new ArrayList<T>();
        }
        listModel.fireContentsChanged();
        setSelectedValue(selectedValue);
    }

    public void addValue(T value) {
        if (value == null) {
            return;
        }
        this.values.add(value);
        listModel.fireContentsChanged();
        setSelectedValue(value);
    }

    public void removeValue(T value) {
        if (value == null) {
            return;
        }
        T selectedValue = (T) getSelectedValue();
        if (value.equals(selectedValue)) {
            selectedValue = null;
        }
        this.values.remove(value);
        listModel.fireContentsChanged();
        if (selectedValue == null && values.size() > 0) {
            selectedValue = values.get(0);
        }
        setSelectedValue(selectedValue);
    }

    public void up() {
        if (values.size() > 0) {
            int lUp = getSelectedIndex();
            if (lUp == -1) {
                lUp = 1;
            }
            if (lUp > 0) {
                setSelectedIndex(lUp - 1);
                getSelectedValue();
                scrollRectToVisible();
            }
        }
    }

    public void down() {
        if (values.size() > 0) {
            int uDown = getSelectedIndex();
            if (uDown < values.size() - 1) {
                setSelectedIndex(uDown + 1);
                getSelectedValue();
                scrollRectToVisible();
            }
        }
    }

    public void scrollRectToVisible() {
        int row = getSelectedIndex();
        if (row == -1) {
            row = 0;
        }
        scrollRectToVisible(getCellBounds(row, row));
    }
}
