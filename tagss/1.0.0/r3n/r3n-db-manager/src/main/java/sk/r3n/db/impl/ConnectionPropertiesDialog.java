package sk.r3n.db.impl;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sk.r3n.action.IdAction;
import sk.r3n.db.ConnectionCreator;
import sk.r3n.db.DbManagerService;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NButton;
import sk.r3n.ui.component.R3NComboBox;
import sk.r3n.ui.component.field.IntegerField;
import sk.r3n.ui.component.field.R3NPasswordField;
import sk.r3n.ui.component.field.VarcharField;
import sk.r3n.ui.dialog.R3NDialog;
import sk.r3n.ui.list.renderer.LabelListCellRenderer;
import sk.r3n.ui.panel.ButtonPanel;
import sk.r3n.util.PasswordGenerator;

public class ConnectionPropertiesDialog extends R3NDialog {

	private static final long serialVersionUID = 5586945199157834974L;

	protected R3NComboBox<String> driverBox;
	protected VarcharField hostField;
	protected IntegerField portField;
	protected VarcharField nameField;
	protected VarcharField userField;
	protected R3NPasswordField passwordField;
	protected VarcharField adminNameField;
	protected VarcharField adminUserField;
	protected R3NPasswordField adminPasswordField;

	public ConnectionPropertiesDialog(Frame owner) {
		super(owner);
		setModal(true);
		ResourceBundle bundle = ResourceBundle
				.getBundle(ConnectionPropertiesDialog.class.getCanonicalName());
		setTitle(bundle.getString("TITLE"));

		JPanel form = new JPanel(new GridBagLayout());

		// Base -----------------------------------------------------
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(bundle
				.getString("BASE")));
		// driver
		driverBox = new R3NComboBox<>();
		driverBox.setRenderer(new LabelListCellRenderer<String>() {

			private static final long serialVersionUID = -1693887358393701538L;

			@Override
			public String getText(String value) {
				return ResourceBundle.getBundle(
						ConnectionPropertiesDialog.class.getCanonicalName())
						.getString(value);
			}

		});

		driverBox.addItem(DbManagerServiceImpl.POSTGRES_DRIVER);
		driverBox.addItem(DbManagerServiceImpl.MS_SQL_DRIVER);
		panel.add(new JLabel(bundle.getString("DRIVER"), JLabel.RIGHT),
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 5, 2, 5), 0, 0));
		panel.add(driverBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						0, 2, 0), 0, 0));
		// host
		hostField = new VarcharField(false);
		hostField.setColumns(10);
		addInputComponent(hostField);
		panel.add(new JLabel(bundle.getString("HOST"), JLabel.RIGHT),
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 5, 2, 5), 0, 0));
		panel.add(hostField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						0, 2, 0), 0, 0));
		// port
		portField = new IntegerField(false);
		portField.setMaxValue(9999);
		portField.setMinValue(0);
		addInputComponent(portField);
		panel.add(new JLabel(bundle.getString("PORT"), JLabel.RIGHT),
				new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 5, 2, 5), 0, 0));
		panel.add(portField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						0, 2, 0), 0, 0));
		// name
		nameField = new VarcharField(false);
		nameField.setColumns(10);
		addInputComponent(nameField);
		panel.add(new JLabel(bundle.getString("NAME"), JLabel.RIGHT),
				new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 5, 2, 5), 0, 0));
		panel.add(nameField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						0, 2, 0), 0, 0));
		// user
		userField = new VarcharField(false);
		userField.setColumns(10);
		addInputComponent(userField);
		panel.add(new JLabel(bundle.getString("USER"), JLabel.RIGHT),
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 0, 2, 5), 0, 0));
		panel.add(userField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						0, 2, 0), 0, 0));
		// password
		passwordField = new R3NPasswordField();
		passwordField.setColumns(10);
		panel.add(new JLabel(bundle.getString("PASSWORD"), JLabel.RIGHT),
				new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 0, 0, 5), 0, 0));
		panel.add(passwordField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						0, 0, 0), 0, 0));

		form.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						10, 10, 0, 10), 0, 0));
		// Admin ----------------------------------------------------
		panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(bundle
				.getString("ADMIN")));
		// db
		adminNameField = new VarcharField(false);
		adminNameField.setColumns(10);
		addInputComponent(adminNameField);
		panel.add(new JLabel(bundle.getString("NAME"), JLabel.RIGHT),
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(0, 0, 2, 5), 0, 0));
		panel.add(adminNameField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 2, 0), 0, 0));
		// user
		adminUserField = new VarcharField(false);
		adminUserField.setColumns(10);
		addInputComponent(adminUserField);
		panel.add(new JLabel(bundle.getString("USER"), JLabel.RIGHT),
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 0, 2, 5), 0, 0));
		panel.add(adminUserField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,
						0, 2, 0), 0, 0));
		// password
		adminPasswordField = new R3NPasswordField();
		adminPasswordField.setColumns(10);
		panel.add(new JLabel(bundle.getString("PASSWORD"), JLabel.RIGHT),
				new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(2, 0, 0, 5), 0, 0));
		panel.add(adminPasswordField, new GridBagConstraints(1, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(2, 0, 0, 0), 0, 0));

		form.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 10, 0, 10), 0, 0));
		add(form, BorderLayout.CENTER);

		ButtonPanel buttonPanel = new ButtonPanel(1, 3);
		R3NButton button = new R3NButton(UIService.class.getCanonicalName(),
				UIService.ACTION_DEFAULT);
		button.addActionListener(new IdAction(UIService.class
				.getCanonicalName(), UIService.ACTION_DEFAULT, this));
		buttonPanel.addButton(button);
		button = new R3NButton(DbManagerService.class.getCanonicalName(),
				DbManagerService.ACTION_TEST);
		button.addActionListener(new IdAction(DbManagerService.class
				.getCanonicalName(), DbManagerService.ACTION_TEST, this));
		buttonPanel.addButton(button);
		button = new R3NButton(UIService.class.getCanonicalName(),
				UIService.ACTION_OK);
		button.addActionListener(new IdAction(UIService.class
				.getCanonicalName(), UIService.ACTION_OK, this));
		buttonPanel.addButton(button);
		button = new R3NButton(UIService.class.getCanonicalName(),
				UIService.ACTION_CANCEL);
		button.addActionListener(new IdAction(UIService.class
				.getCanonicalName(), UIService.ACTION_CANCEL, this));
		buttonPanel.addButton(button);
		add(buttonPanel, BorderLayout.SOUTH);

		DbManagerServiceImpl.uiService.setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_HELP,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);
		DbManagerServiceImpl.uiService.setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_OK,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);
		DbManagerServiceImpl.uiService.setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_CANCEL,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);
		DbManagerServiceImpl.uiService.setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);
	}

	public boolean init(Properties properties) {
		driverBox
				.setSelectedItem(properties.getProperty(
						ConnectionCreator.DRIVER,
						DbManagerServiceImpl.POSTGRES_DRIVER));
		hostField.setText(properties.getProperty(ConnectionCreator.HOST, ""));
		portField.setText(properties.getProperty(ConnectionCreator.PORT, ""));
		nameField.setText(properties.getProperty(ConnectionCreator.NAME, ""));
		userField.setText(properties.getProperty(ConnectionCreator.USER, ""));
		passwordField.setValue(properties.getProperty(
				ConnectionCreator.PASSWORD, "").getBytes());
		adminNameField.setText(properties.getProperty(
				ConnectionCreator.ADMIN_DB, ""));
		adminUserField.setText(properties.getProperty(
				ConnectionCreator.ADMIN_USER, ""));
		adminPasswordField.setValue(properties.getProperty(
				ConnectionCreator.ADMIN_PASSWORD, "").getBytes());
		pack();
		setVisible(true);
		return lastGroup.equals(UIService.class.getCanonicalName())
				&& lastAction == UIService.ACTION_OK;
	}

	@Override
	public void execute(String groupId, int actionId, Object source) {
		lastGroup = groupId;
		lastAction = actionId;
		if (groupId.equals(UIService.class.getCanonicalName())) {
			switch (actionId) {
			case UIService.ACTION_HELP:
				DbManagerServiceImpl.appHelp
						.showHelp(ConnectionPropertiesDialog.class
								.getCanonicalName());
				return;
			case UIService.ACTION_DEFAULT:
				hostField.setText("127.0.0.1");
				nameField.setText(DbManagerServiceImpl.context
						.getBundleContext().getProperty(DbManagerService.NAME));
				userField.setText(nameField.getText());
				try {
					passwordField.setValue(new PasswordGenerator()
							.generatePassword(PasswordGenerator.ALPHA_NUMERIC,
									8).getBytes());
				} catch (Exception e) {
				}
				if (driverBox.getSelectedItem().equals(
						DbManagerServiceImpl.POSTGRES_DRIVER)) {
					portField.setText("5432");
					adminNameField.setText("postgres");
					adminUserField.setText("postgres");
				} else {
					portField.setText("1433");
					adminNameField.setText("master");
					adminUserField.setText("sa");
				}
				adminPasswordField.selectAll();
				adminPasswordField.requestFocus();
				return;
			case UIService.ACTION_OK:
				if (!isInputValid())
					return;
				dispose();
				return;
			case UIService.ACTION_CLOSE:
				lastAction = UIService.ACTION_CANCEL;
			case UIService.ACTION_CANCEL:
				dispose();
				return;
			}
		}
		if (groupId.equals(DbManagerService.class.getCanonicalName())) {
			DbManagerServiceImpl.idActionService.fireEvent(this, groupId,
					actionId, new Object[] { getProperties() });
		}
	}

	public Properties getProperties() {
		Properties properties = new Properties();
		properties.put(ConnectionCreator.DRIVER, driverBox.getSelectedItem());
		properties.put(ConnectionCreator.HOST, hostField.getText());
		properties.put(ConnectionCreator.PORT, portField.getText());
		properties.put(ConnectionCreator.NAME, nameField.getText());
		properties.put(ConnectionCreator.USER, userField.getText());
		if (!passwordField.isContentNull())
			properties.put(ConnectionCreator.PASSWORD,
					new String(passwordField.getValue()));
		else
			properties.put(ConnectionCreator.PASSWORD, "");
		properties.put(ConnectionCreator.ADMIN_DB, adminNameField.getText());
		properties.put(ConnectionCreator.ADMIN_USER, adminUserField.getText());
		if (!adminPasswordField.isContentNull())
			properties.put(ConnectionCreator.ADMIN_PASSWORD, new String(
					adminPasswordField.getValue()));
		else
			properties.put(ConnectionCreator.ADMIN_PASSWORD, "");
		return properties;
	}

}
