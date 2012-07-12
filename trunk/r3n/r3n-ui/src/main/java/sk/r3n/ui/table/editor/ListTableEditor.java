package sk.r3n.ui.table.editor;

import java.awt.event.ActionEvent;

import sk.r3n.ui.list.R3NList;
import sk.r3n.ui.util.UIServiceManager;

public class ListTableEditor<T> extends R3NButtonTableEditor {

	private static final long serialVersionUID = 4704227499313094189L;

	protected R3NList<T> list;

	public ListTableEditor(R3NList<T> list) {
		super();
		this.list = list;
	}

	public Object getCellEditorValue() {
		return oldValue;
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		ListTableEditorDialog<T> listTableEditorDialog = null;
		listTableEditorDialog = new ListTableEditorDialog<T>(UIServiceManager
				.getDefaultUIService().getFrameForComponent(button), list);
		if (listTableEditorDialog.initDialog((T) oldValue)) {
			oldValue = listTableEditorDialog.getSelectedValue();
			fireEditingStopped();
			return;
		}
		fireEditingCanceled();
	}
}
