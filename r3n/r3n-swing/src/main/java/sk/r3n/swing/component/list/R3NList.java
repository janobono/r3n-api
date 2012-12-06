package sk.r3n.swing.component.list;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class R3NList<T> extends JList<T> {

    protected class BaseListModel extends AbstractListModel<T> {

        protected void fireContentsChanged() {
            int start = 0;
            int end = values.size();
            super.fireContentsChanged(this, start, end);
        }

        @Override
        public T getElementAt(int index) {
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
        values = new ArrayList<>();
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
        T selectedValue = getSelectedValue();
        this.values = values;
        if (values == null) {
            this.values = new ArrayList<>();
        }
        listModel.fireContentsChanged();
        setSelectedValue(selectedValue);
    }

}
