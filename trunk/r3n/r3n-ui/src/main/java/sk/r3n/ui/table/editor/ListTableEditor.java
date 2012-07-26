package sk.r3n.ui.table.editor;

import java.awt.event.ActionEvent;
import sk.r3n.ui.list.R3NList;
import sk.r3n.ui.util.UIServiceManager;

public class ListTableEditor<T> extends R3NButtonTableEditor {

    protected R3NList<T> list;

    public ListTableEditor(R3NList<T> list) {
        super();
        this.list = list;
    }

    @Override
    public Object getCellEditorValue() {
        return oldValue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ListTableEditorDialog<T> listTableEditorDialog = null;
        listTableEditorDialog = new ListTableEditorDialog<>(UIServiceManager.getDefaultUIService().getFrameForComponent(button), list);
        if (listTableEditorDialog.initDialog((T) oldValue)) {
            oldValue = listTableEditorDialog.getSelectedValue();
            fireEditingStopped();
            return;
        }
        fireEditingCanceled();
    }
}
