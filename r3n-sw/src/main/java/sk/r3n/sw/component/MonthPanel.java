package sk.r3n.sw.component;

import sk.r3n.sw.*;
import sk.r3n.sw.component.field.MonthField;
import sk.r3n.sw.dialog.OkCancelDialog;
import sk.r3n.sw.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.ResourceBundle;

public final class MonthPanel extends JPanel implements InputComponent<Date>, UIActionExecutor {

    private static final String TITLE = "TITLE";
    public MonthField monthField;
    public R3NButton selectButton;

    public MonthPanel(boolean canBeNull) {
        super();
        monthField = new MonthField(canBeNull);
        init(monthField);
    }

    public MonthPanel(boolean canBeNull, char separator) {
        super();
        monthField = new MonthField(canBeNull, separator);
        init(monthField);
    }

    @Override
    public InputStatus inputStatus() {
        return monthField.inputStatus();
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case SELECT:
                    MonthPickerDialog monthDialog = new MonthPickerDialog();
                    MonthPicker monthPicker = new MonthPicker(monthField.getValue());
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

    @Override
    public void setValue(Date value) {
        monthField.setValue(value);
    }

    private void init(MonthField dateField) {
        setLayout(new GridBagLayout());
        add(dateField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        selectButton = new R3NButton(R3NAction.SELECT, this);
        selectButton.setText("...");
        add(selectButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
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

    private class MonthPickerDialog extends OkCancelDialog {

        public MonthPickerDialog() {
            super(SwingUtil.getFrameForComponent(MonthPanel.this));
            setModal(true);
            setTitle(ResourceBundle.getBundle(MonthPanel.class.getCanonicalName()).getString(TITLE));
        }

        public boolean initDialog(MonthPicker monthPicker) {
            add(monthPicker, BorderLayout.CENTER);
            pack();
            setVisible(true);
            return lastActionKey.equals(R3NAction.OK);
        }

        @Override
        public boolean isInputValid() {
            return true;
        }
    }
}
