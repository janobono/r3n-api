package sk.r3n.db;

import java.awt.*;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sk.r3n.jdbc.DbType;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.InputStatusValidator;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.IntegerField;
import sk.r3n.sw.component.field.R3NPasswordField;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.sw.component.list.R3NListCellRenderer;
import sk.r3n.sw.dialog.R3NDialog;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.UIActionExecutor;
import sk.r3n.sw.util.UIActionKey;
import sk.r3n.sw.util.UISWAction;
import sk.r3n.util.PasswordGenerator;

public class ConnectionPropertiesDialog extends R3NDialog {

    protected JComboBox<DbType> driverBox;

    protected VarcharField hostField;

    protected IntegerField portField;

    protected VarcharField nameField;

    protected VarcharField userField;

    protected R3NPasswordField passwordField;

    protected VarcharField adminNameField;

    protected VarcharField adminUserField;

    protected R3NPasswordField adminPasswordField;

    public ConnectionPropertiesDialog(Frame owner, UIActionExecutor dbActionExecutor) {
        super(owner);
        setModal(true);
        setTitle(DbManagerBundle.TITLE.value());

        JPanel form = new JPanel(new GridBagLayout());

        // Base -----------------------------------------------------
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(DbManagerBundle.BASE.value()));
        // driver
        driverBox = new JComboBox<>();
        driverBox.setRenderer(new R3NListCellRenderer<DbType>() {
            @Override
            public String getText(DbType value) {
                return value.value();
            }

        });

        driverBox.addItem(DbType.POSTGRE);
        driverBox.addItem(DbType.SQL_SERVER);
        panel.add(new JLabel(DbManagerBundle.DRIVER.value()), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        panel.add(driverBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        // host
        hostField = new VarcharField(false);
        hostField.setColumns(10);
        panel.add(new JLabel(DbManagerBundle.HOST.value(), JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        panel.add(hostField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        // port
        portField = new IntegerField(false);
        portField.setMaxValue(9999);
        portField.setMinValue(0);
        panel.add(new JLabel(DbManagerBundle.PORT.value(), JLabel.RIGHT), new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        panel.add(portField, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        // name
        nameField = new VarcharField(false);
        nameField.setColumns(10);
        panel.add(new JLabel(DbManagerBundle.NAME.value(), JLabel.RIGHT), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 5, 2, 5), 0, 0));
        panel.add(nameField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        // user
        userField = new VarcharField(false);
        userField.setColumns(10);
        panel.add(new JLabel(DbManagerBundle.USER.value(), JLabel.RIGHT), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 2, 5), 0, 0));
        panel.add(userField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        // password
        passwordField = new R3NPasswordField();
        passwordField.setColumns(10);
        panel.add(new JLabel(DbManagerBundle.PASSWORD.value(), JLabel.RIGHT), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 0, 5), 0, 0));
        panel.add(passwordField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));

        form.add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        // Admin ----------------------------------------------------
        panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(DbManagerBundle.ADMIN.value()));
        // db
        adminNameField = new VarcharField(false);
        adminNameField.setColumns(10);
        panel.add(new JLabel(DbManagerBundle.NAME.value(), JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 2, 5), 0, 0));
        panel.add(adminNameField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
        // user
        adminUserField = new VarcharField(false);
        adminUserField.setColumns(10);
        panel.add(new JLabel(DbManagerBundle.USER.value(), JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 2, 5), 0, 0));
        panel.add(adminUserField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        // password
        adminPasswordField = new R3NPasswordField();
        adminPasswordField.setColumns(10);
        panel.add(new JLabel(DbManagerBundle.PASSWORD.value(), JLabel.RIGHT), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 0, 0, 5), 0, 0));
        panel.add(adminPasswordField, new GridBagConstraints(1, 2, 1, 1, 0.0,
                0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));

        form.add(panel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        add(form, BorderLayout.CENTER);

        ButtonPanel buttonPanel = new ButtonPanel(4, true);
        buttonPanel.addButton(new R3NButton(UISWAction.DEFAULT, dbActionExecutor));
        buttonPanel.addButton(new R3NButton(DbManagerAction.TEST, dbActionExecutor));
        buttonPanel.addButton(new R3NButton(UISWAction.OK, this));
        buttonPanel.addButton(new R3NButton(UISWAction.CANCEL, this));
        add(buttonPanel, BorderLayout.SOUTH);

        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                UISWAction.HELP, dbActionExecutor);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                UISWAction.OK, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                UISWAction.CANCEL, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                UISWAction.CLOSE, this);
    }

    public boolean init(Properties properties) {
        driverBox.setSelectedItem(properties.getProperty(DbManagerProperties.DRIVER.connCode(), DbType.POSTGRE.driver()));
        hostField.setText(properties.getProperty(DbManagerProperties.HOST.connCode(), ""));
        portField.setText(properties.getProperty(DbManagerProperties.PORT.connCode(), ""));
        nameField.setText(properties.getProperty(DbManagerProperties.NAME.connCode(), ""));
        userField.setText(properties.getProperty(DbManagerProperties.USER.connCode(), ""));
        passwordField.setValue(properties.getProperty(DbManagerProperties.PASSWORD.connCode(), "").getBytes());
        adminNameField.setText(properties.getProperty(DbManagerProperties.ADMIN_NAME.connCode(), ""));
        adminUserField.setText(properties.getProperty(DbManagerProperties.ADMIN_USER.connCode(), ""));
        adminPasswordField.setValue(properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.connCode(), "").getBytes());
        pack();
        setVisible(true);
        return lastActionKey.equals(UISWAction.OK);
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (actionKey instanceof UISWAction) {
            switch ((UISWAction) actionKey) {
                case OK:
                    if (!isInputValid()) {
                        break;
                    }
                    dispose();
                    break;
                case CLOSE:
                case CANCEL:
                    dispose();
                    break;
            }
        }
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put(DbManagerProperties.DRIVER.connCode(), ((DbType) driverBox.getSelectedItem()).driver());
        properties.put(DbManagerProperties.HOST.connCode(), hostField.getText());
        properties.put(DbManagerProperties.PORT.connCode(), portField.getText());
        properties.put(DbManagerProperties.NAME.connCode(), nameField.getText());
        properties.put(DbManagerProperties.USER.connCode(), userField.getText());
        if (!passwordField.isContentNull()) {
            properties.put(DbManagerProperties.PASSWORD.connCode(), new String(passwordField.getValue()));
        } else {
            properties.put(DbManagerProperties.PASSWORD.connCode(), "");
        }
        properties.put(DbManagerProperties.ADMIN_NAME.connCode(), adminNameField.getText());
        properties.put(DbManagerProperties.ADMIN_USER.connCode(), adminUserField.getText());
        if (!adminPasswordField.isContentNull()) {
            properties.put(DbManagerProperties.ADMIN_PASSWORD.connCode(), new String(adminPasswordField.getValue()));
        } else {
            properties.put(DbManagerProperties.ADMIN_PASSWORD.connCode(), "");
        }
        return properties;
    }

    public void setDefault(String dbName) {
        hostField.setText("127.0.0.1");
        nameField.setText(dbName);
        userField.setText(nameField.getText());
        try {
            passwordField.setValue(new PasswordGenerator().generatePassword(
                    PasswordGenerator.Type.ALPHA_NUMERIC, 8).getBytes());
        } catch (Exception e) {
        }
        if (driverBox.getSelectedItem().equals(DbType.POSTGRE)) {
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
    }

    @Override
    public boolean isInputValid() {
        InputStatusValidator inputStatusValidator = new InputStatusValidator();
        inputStatusValidator.add(hostField);
        inputStatusValidator.add(portField);
        inputStatusValidator.add(nameField);
        inputStatusValidator.add(userField);
        inputStatusValidator.add(adminNameField);
        inputStatusValidator.add(adminUserField);
        return inputStatusValidator.isInputValid();
    }

}
