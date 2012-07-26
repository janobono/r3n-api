package sk.r3n.ui.table.editor;

import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import sk.r3n.ui.UIService;
import sk.r3n.ui.dialog.R3NOkCancelDialog;
import sk.r3n.ui.list.R3NList;
import sk.r3n.ui.util.UIServiceManager;

public class ListTableEditorDialog<T> extends R3NOkCancelDialog implements
        ListSelectionListener {

    private R3NList<T> baseList;

    public ListTableEditorDialog(Frame frame, R3NList<T> baseList) {
        super(frame);
        init(baseList);
    }

    @Override
    public void execute(String groupId, int actionId, Object source) {
        if (groupId.equals(UIService.class.getCanonicalName())) {
            switch (actionId) {
                case UIService.ACTION_UP:
                    baseList.up();
                    return;
                case UIService.ACTION_DOWN:
                    baseList.down();
                    return;
            }
        }
        super.execute(groupId, actionId, source);
    }

    public Object getSelectedValue() {
        return baseList.getSelectedValue();
    }

    private void init(R3NList<T> baseList) {
        setModal(true);
        this.baseList = baseList;
        JScrollPane scrollPane = new JScrollPane(baseList);
        scrollPane.setPreferredSize(new Dimension(0, 0));
        add(scrollPane);

        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_UP,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                (JPanel) getContentPane(), this);
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_DOWN,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                (JPanel) getContentPane(), this);
    }

    public boolean initDialog(T value) {
        baseList.setSelectedValue(value);
        pack();
        setVisible(true);
        return lastGroup.equals(UIService.class.getCanonicalName())
                && lastAction == UIService.ACTION_OK;
    }

    @Override
    public boolean isInputValid() {
        if (baseList.getSelectedValue() == null) {
            UIServiceManager.getDefaultUIService().getBuzzer().buzz(this);
            baseList.requestFocus();
            return false;
        }
        return super.isInputValid();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        okButton.setEnabled(baseList.getSelectedValue() != null);
    }
}
