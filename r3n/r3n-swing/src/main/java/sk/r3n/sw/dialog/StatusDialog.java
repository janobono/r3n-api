package sk.r3n.sw.dialog;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import sk.r3n.sw.util.UIActionKey;

public class StatusDialog extends R3NDialog {

    private JProgressBar progressBar;

    private JLabel statusLabel;

    private boolean asc;

    public StatusDialog(Frame frame) {
        super(frame);
        setModal(true);
        // Formular -------------------------------------------------------
        JTextField textField = new JTextField();
        textField.setColumns(25);

        setLayout(new GridBagLayout());
        statusLabel = new JLabel();
        statusLabel.setPreferredSize(new Dimension(
                textField.getPreferredSize().width, 3 * textField.getPreferredSize().height));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        add(statusLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        progressBar = new JProgressBar();
        progressBar.setMaximum(100);
        progressBar.setPreferredSize(textField.getPreferredSize());
        add(progressBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
    }

    public void finishProgress() {
        progressBar.setValue(100);
    }

    public void incrementProgress() {
        incProgress();
    }

    private void incProgress() {
        int value = progressBar.getValue();
        if (asc) {
            if (value < 100) {
                progressBar.setValue(value + 1);
            } else {
                asc = false;
                progressBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                incProgress();
            }
        } else {
            if (value > 0) {
                progressBar.setValue(value - 1);
            } else {
                asc = true;
                progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                incProgress();
            }
        }
    }

    public void setText(String text) {
        if (text != null) {
            statusLabel.setText(text);
        }
    }

    public void startProgress() {
        asc = true;
        progressBar.setValue(0);
        progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    public void statusHide() {
        dispose();
    }

    public void statusShow() {
        asc = true;
        setVisible(true);
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

}
