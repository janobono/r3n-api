package sk.r3n.ui.dialog;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import sk.r3n.ui.UIService;

public class R3NStatusDialog extends R3NDialog {

	private static final long serialVersionUID = -7298129661777426215L;

	private JProgressBar progressBar;
	private JLabel statusLabel;

	private boolean auto;
	private boolean asc;

	public R3NStatusDialog(Frame frame) {
		super(frame);
		setModal(true);
		// Formular -------------------------------------------------------
		JTextField textField = new JTextField();
		textField.setColumns(25);

		setLayout(new GridBagLayout());
		statusLabel = new JLabel();
		statusLabel.setPreferredSize(new Dimension(
				textField.getPreferredSize().width, 3 * textField
						.getPreferredSize().height));
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
	public void execute(String groupId, int actionId, Object source) {
		lastGroup = groupId;
		lastAction = actionId;
	}

	public void finishProgress() {
		auto = false;
		progressBar.setValue(100);
	}

	public void incrementProgress() {
		auto = false;
		incProgress();
	}

	private void incProgress() {
		int value = progressBar.getValue();
		if (asc) {
			if (value < 100) {
				progressBar.setValue(value + 1);
			} else {
				asc = false;
				progressBar
						.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				incProgress();
			}
		} else {
			if (value > 0) {
				progressBar.setValue(value - 1);
			} else {
				asc = true;
				progressBar
						.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
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
		auto = false;
		asc = true;
		progressBar.setValue(0);
		progressBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}

	public void statusHide() {
		auto = false;
		dispose();
	}

	public void statusShow() {
		auto = false;
		asc = true;
		lastGroup = UIService.class.getCanonicalName();
		lastAction = UIService.ACTION_OK;
		pack();
		new Thread(new Runnable() {

			public void run() {
				setVisible(true);
			}
		}).start();
		while (!isVisible()) {
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			} catch (Exception e) {
			}
		}
	}

	public void autoProgress() {
		if (!auto) {
			auto = true;
			new Thread(new Runnable() {
				public void run() {
					while (auto) {
						incProgress();
						try {
							Thread.sleep(25);
						} catch (Exception e) {
							auto = false;
						}
					}
				}
			}).start();
		}
	}

}
