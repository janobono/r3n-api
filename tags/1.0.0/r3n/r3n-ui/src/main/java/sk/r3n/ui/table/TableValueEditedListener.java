package sk.r3n.ui.table;

import java.util.EventListener;

public interface TableValueEditedListener extends EventListener {

	public void tableValueEdited(TableValueEditedEvent<?> tableValueEditedEvent);

}
