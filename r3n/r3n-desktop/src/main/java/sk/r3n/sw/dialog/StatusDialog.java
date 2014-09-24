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
import javax.swing.SwingWorker;
import sk.r3n.sw.util.LongTermJobListener;
import sk.r3n.sw.util.UIActionKey;

public abstract class StatusDialog extends R3NDialog implements LongTermJobListener {

    protected class JobWorker extends SwingWorker<Void, Void> {

        public JobWorker() {
        }

        @Override
        protected Void doInBackground() throws Exception {
            jobStarted();
            try {
                executeLongTermJob();
            } finally {
                jobFinished();
            }
            return Void.TYPE.newInstance();
        }
    }

    private final JProgressBar progressBar;

    private final JLabel statusLabel;

    private boolean asc;

    public StatusDialog(Frame frame) {
        super(frame);
        setModal(true);

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

    @Override
    public boolean isInputValid() {
        return true;
    }

    public void jobStarted() {
        asc = true;
        progressBar.setValue(0);
        progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    public void jobStarted(String title) {
        setTitle(title);
        jobStarted();
    }

    @Override
    public void jobInProgress() {
        int value = progressBar.getValue();
        if (asc) {
            if (value < 100) {
                progressBar.setValue(value + 1);
            } else {
                asc = false;
                progressBar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                jobInProgress();
            }
        } else {
            if (value > 0) {
                progressBar.setValue(value - 1);
            } else {
                asc = true;
                progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                jobInProgress();
            }
        }
    }

    @Override
    public void jobInProgress(int value) {
        for (int i = 0; i < value; i++) {
            jobInProgress();
        }
    }

    @Override
    public void jobInProgress(String message) {
        statusLabel.setText(message);
        jobInProgress();
    }

    @Override
    public void jobInProgress(String message, int value) {
        jobInProgress(message);
        jobInProgress(value);
    }

    public void jobFinished() {
        dispose();
    }

    public void init() {
        JobWorker jobWorker = new JobWorker();
        jobWorker.execute();
        pack();
        setVisible(true);
    }

    protected abstract void executeLongTermJob();
}
