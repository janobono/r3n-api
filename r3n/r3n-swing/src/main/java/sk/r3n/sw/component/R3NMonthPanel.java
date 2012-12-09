package sk.r3n.sw.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import sk.r3n.action.IdActionExecutor;
import sk.r3n.ui.IdActionListener;
import sk.r3n.ui.UIService;
import sk.r3n.sw.component.field.MonthField;
import sk.r3n.sw.dialog.R3NOkCancelDialog;
import sk.r3n.sw.util.UIServiceManager;

public final class R3NMonthPanel extends JPanel implements R3NInputComponent<Date>, IdActionExecutor {

    private class MonthPickerDialog extends R3NOkCancelDialog {

        public MonthPickerDialog() {
            super(UIServiceManager.getDefaultUIService().getFrameForComponent(
                    R3NMonthPanel.this));
            setModal(true);
            setTitle(ResourceBundle.getBundle(R3NMonthPanel.class.getCanonicalName()).getString("TITLE"));
        }

        public boolean initDialog(R3NMonthPicker monthPicker) {
            add(monthPicker, BorderLayout.CENTER);
            pack();
            setVisible(true);
            return lastGroup.equals(UIService.class.getCanonicalName()) && lastAction == UIService.ACTION_OK;
        }

    }

    public MonthField monthField;

    public R3NButton selectButton;

    public R3NMonthPanel(boolean canBeNull) {
        super();
        monthField = new MonthField(canBeNull);
        init(monthField);
    }

    public R3NMonthPanel(boolean canBeNull, char separator) {
        super();
        monthField = new MonthField(canBeNull, separator);
        init(monthField);
    }

    @Override
    public int contentValid() {
        return monthField.contentValid();
    }

    @Override
    public void execute(String groupId, int actionId, Object source) {
        if (groupId.equals(UIService.class.getCanonicalName())) {
            switch (actionId) {
                case UIService.ACTION_SELECT:
                    MonthPickerDialog monthDialog = new MonthPickerDialog();
                    R3NMonthPicker monthPicker = new R3NMonthPicker(monthField.getValue());
                    if (monthDialog.initDialog(monthPicker)) {
                        monthField.setValue(monthPicker.getDate());
                    }
                    break;
            }
        }
    }

    @Override
    public Date getValue() {
        return monthField.getValue();
    }

    private void init(MonthField dateField) {
        setLayout(new GridBagLayout());
        add(dateField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        selectButton = new R3NButton(UIService.class.getCanonicalName(),
                UIService.ACTION_SELECT);
        selectButton.setText("...");
        selectButton.addActionListener(new IdActionListener(UIService.class.getCanonicalName(), UIService.ACTION_SELECT, this));
        add(selectButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
    }

    @Override
    public boolean isContentNull() {
        return monthField.isContentNull();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        monthField.setEnabled(enabled);
        selectButton.setEnabled(enabled);
    }

    @Override
    public void setValue(Date value) {
        monthField.setValue(value);
    }

}
