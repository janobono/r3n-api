package sk.r3n.ui.component;

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
import sk.r3n.ui.component.field.DateField;
import sk.r3n.ui.dialog.R3NOkCancelDialog;
import sk.r3n.ui.util.UIServiceManager;

public final class R3NDatePanel extends JPanel implements
        R3NInputComponent<Date>, IdActionExecutor {

    private class DatePickerDialog extends R3NOkCancelDialog {

        public DatePickerDialog() {
            super(UIServiceManager.getDefaultUIService().getFrameForComponent(
                    R3NDatePanel.this));
            setModal(true);
            setTitle(ResourceBundle.getBundle(
                    R3NDatePanel.class.getCanonicalName()).getString("TITLE"));
        }

        public boolean initDialog(R3NDatePicker datePicker) {
            add(datePicker, BorderLayout.CENTER);
            pack();
            setVisible(true);
            return lastGroup.equals(UIService.class.getCanonicalName())
                    && lastAction == UIService.ACTION_OK;
        }
    }
    public DateField dateField;
    public R3NButton selectButton;

    public R3NDatePanel(boolean canBeNull) {
        super();
        dateField = new DateField(canBeNull);
        init(dateField);
    }

    public R3NDatePanel(boolean canBeNull, char separator) {
        super();
        dateField = new DateField(canBeNull, separator);
        init(dateField);
    }

    @Override
    public int contentValid() {
        return dateField.contentValid();
    }

    @Override
    public void execute(String groupId, int actionId, Object source) {
        if (groupId.equals(UIService.class.getCanonicalName())) {
            switch (actionId) {
                case UIService.ACTION_SELECT:
                    DatePickerDialog dateDialog = new DatePickerDialog();
                    R3NDatePicker datePicker = new R3NDatePicker(
                            dateField.getValue());
                    if (dateDialog.initDialog(datePicker)) {
                        dateField.setValue(datePicker.getDate());
                    }
                    break;
            }
        }
    }

    @Override
    public Date getValue() {
        return dateField.getValue();
    }

    private void init(DateField dateField) {
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
        return dateField.isContentNull();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateField.setEnabled(enabled);
        selectButton.setEnabled(enabled);
    }

    @Override
    public void setValue(Date value) {
        dateField.setValue(value);
    }
}
