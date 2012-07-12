package sk.r3n.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import sk.r3n.ui.UIService;
import sk.r3n.ui.component.field.R3NPasswordField;

public class R3NPasswordDialog extends R3NOkCancelDialog {

	private static final long serialVersionUID = 3552011098751179179L;

	private R3NPasswordField passwordField;

	public R3NPasswordDialog(Frame frame) {
		super(frame);
		setModal(true);
		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass()
				.getCanonicalName());
		this.setTitle(bundle.getString("TITLE"));
		// Formular -------------------------------------------------------
		JPanel form = new JPanel(new GridBagLayout());
		// PASSWORD
		passwordField = new R3NPasswordField();
		passwordField.setColumns(7);
		addInputComponent(passwordField);
		form.add(new JLabel(bundle.getString("PASSWORD"), JLabel.RIGHT),
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(10, 10, 10, 5), 0, 0));
		form.add(passwordField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						10, 0, 10, 10), 0, 0));
		this.add(form, BorderLayout.CENTER);
	}

	public byte[] getPassword() {
		return passwordField.getValue();
	}

	public boolean initDialog() {
		passwordField.setValue(null);
		pack();
		setVisible(true);
		return lastGroup.equals(UIService.class.getCanonicalName())
				&& lastAction == UIService.ACTION_OK;
	}

}
