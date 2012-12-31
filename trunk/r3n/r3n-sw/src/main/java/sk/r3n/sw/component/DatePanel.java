package sk.r3n.sw.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import sk.r3n.sw.component.field.DateField;
import sk.r3n.sw.dialog.OkCancelDialog;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public final class DatePanel extends JPanel implements InputComponent<Date>, UIActionExecutor {

    private class DatePickerDialog extends OkCancelDialog {

        public DatePickerDialog() {
            super(SwingUtil.getFrameForComponent(DatePanel.this));
            setModal(true);
            setTitle(ResourceBundle.getBundle(DatePanel.class.getCanonicalName()).getString("TITLE"));
        }

        public boolean initDialog(DatePicker datePicker) {
            add(datePicker, BorderLayout.CENTER);
            pack();
            setVisible(true);
            return lastActionKey.equals(R3NAction.OK);
        }

        @Override
        public boolean isInputValid() {
            return true;
        }

    }
    public DateField dateField;

    public R3NButton selectButton;

    public DatePanel(boolean canBeNull) {
        super();
        dateField = new DateField(canBeNull);
        init(dateField);
    }

    public DatePanel(boolean canBeNull, char separator) {
        super();
        dateField = new DateField(canBeNull, separator);
        init(dateField);
    }

    @Override
    public InputStatus inputStatus() {
        return dateField.inputStatus();
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case SELECT:
                    DatePickerDialog dateDialog = new DatePickerDialog();
                    DatePicker datePicker = new DatePicker(
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
        add(dateField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        selectButton = new R3NButton(R3NAction.SELECT, this);
        selectButton.setText("...");
        add(selectButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
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
