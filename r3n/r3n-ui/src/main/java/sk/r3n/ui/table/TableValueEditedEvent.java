package sk.r3n.ui.table;

import java.util.EventObject;

public class TableValueEditedEvent<T> extends EventObject {

    private T row;

    public TableValueEditedEvent(Object source, T row) {
        super(source);
        this.row = row;
    }

    public T getRow() {
        return row;
    }
}
