package sk.r3n.ui.table;

import java.util.EventObject;

public class TableValueEditedEvent<T> extends EventObject {

	private static final long serialVersionUID = 4219582505449167951L;

	private T row;

	public TableValueEditedEvent(Object source, T row) {
		super(source);
		this.row = row;
	}

	public T getRow() {
		return row;
	}

}
