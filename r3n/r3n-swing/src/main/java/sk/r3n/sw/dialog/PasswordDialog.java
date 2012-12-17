package sk.r3n.sw.dialog;

import java.awt.*;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sk.r3n.sw.component.field.R3NPasswordField;
import sk.r3n.sw.util.UISWAction;

public class PasswordDialog extends OkCancelDialog {

    private R3NPasswordField passwordField;

    public PasswordDialog(Frame frame, String label) {
        super(frame);
        setModal(true);
        ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getCanonicalName());
        this.setTitle(bundle.getString("TITLE"));
        // Formular -------------------------------------------------------
        JPanel form = new JPanel(new GridBagLayout());
        // PASSWORD
        passwordField = new R3NPasswordField();
        passwordField.setColumns(7);
        form.add(new JLabel(label, JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 5), 0, 0));
        form.add(passwordField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.add(form, BorderLayout.CENTER);
    }

    public byte[] getPassword() {
        return passwordField.getValue();
    }

    public boolean initDialog() {
        passwordField.setValue(null);
        pack();
        setVisible(true);
        return lastActionKey.equals(UISWAction.OK);
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

}
