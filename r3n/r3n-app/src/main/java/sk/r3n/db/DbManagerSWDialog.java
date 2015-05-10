package sk.r3n.db;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sk.r3n.app.AppHelp;
import sk.r3n.jdbc.DbStatus;
import sk.r3n.jdbc.DbType;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.IntegerField;
import sk.r3n.sw.component.field.R3NPasswordField;
import sk.r3n.sw.component.field.VarcharField;
import sk.r3n.sw.component.list.R3NListCellRenderer;
import sk.r3n.sw.dialog.R3NDialog;
import sk.r3n.sw.util.SWInputStatusValidator;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.MessageType;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionKey;
import sk.r3n.util.PasswordGenerator;

public class DbManagerSWDialog extends R3NDialog {

    protected JComboBox driverBox;

    protected VarcharField hostField;

    protected IntegerField portField;

    protected VarcharField nameField;

    protected VarcharField userField;

    protected R3NPasswordField passwordField;

    protected VarcharField adminNameField;

    protected VarcharField adminUserField;

    protected R3NPasswordField adminPasswordField;

    protected AppHelp appHelp;

    protected String helpKey;

    protected String defaultName;

    public DbManagerSWDialog(Frame owner, List<DbType> supportedDbs) {
        super(owner);
        setModal(true);
        setTitle(DbManagerBundle.TITLE.value());

        JPanel form = new JPanel(new GridBagLayout());

        // Base -----------------------------------------------------
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(DbManagerBundle.BASE.value()));
        // driver
        driverBox = new JComboBox();
        driverBox.setRenderer(new R3NListCellRenderer<DbType>() {
            @Override
            public String getText(DbType value) {
                return value.value();
            }
        });

        if (supportedDbs != null) {
            for (DbType dbType : supportedDbs) {
                driverBox.addItem(dbType);
            }
        } else {
            driverBox.addItem(DbType.POSTGRE);
            driverBox.addItem(DbType.SQL_SERVER);
        }
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
        buttonPanel.addButton(new R3NButton(R3NAction.DEFAULT, this));
        buttonPanel.addButton(new R3NButton(DbManagerAction.TEST, this));
        buttonPanel.addButton(new R3NButton(R3NAction.OK, this));
        buttonPanel.addButton(new R3NButton(R3NAction.CANCEL, this));
        add(buttonPanel, BorderLayout.SOUTH);

        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                R3NAction.HELP, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                R3NAction.OK, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                R3NAction.CANCEL, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                R3NAction.CLOSE, this);
    }

    public boolean init(Properties properties) {
        DbType dbType = DbType.get(
                properties.getProperty(DbManagerProperties.DRIVER.name(), DbType.POSTGRE.driver()));
        if (dbType != null) {
            driverBox.setSelectedItem(dbType);
        }
        hostField.setText(properties.getProperty(DbManagerProperties.HOST.name(), ""));
        portField.setText(properties.getProperty(DbManagerProperties.PORT.name(), ""));
        nameField.setText(properties.getProperty(DbManagerProperties.NAME.name(), ""));
        userField.setText(properties.getProperty(DbManagerProperties.USER.name(), ""));
        passwordField.setValue(properties.getProperty(DbManagerProperties.PASSWORD.name(), "").getBytes());
        adminNameField.setText(properties.getProperty(DbManagerProperties.ADMIN_NAME.name(), ""));
        adminUserField.setText(properties.getProperty(DbManagerProperties.ADMIN_USER.name(), ""));
        adminPasswordField.setValue(properties.getProperty(DbManagerProperties.ADMIN_PASSWORD.name(), "").getBytes());
        pack();
        setVisible(true);
        return lastActionKey.equals(R3NAction.OK);
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (actionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case HELP:
                    if (appHelp != null && helpKey != null) {
                        appHelp.showHelp(helpKey);
                    }
                    break;
                case DEFAULT:
                    setDefault(defaultName);
                    break;
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
        if (actionKey instanceof DbManagerAction) {
            switch ((DbManagerAction) actionKey) {
                case TEST:
                    DbStatus dbStatus = DbManagerUtil.getConnectionStatus(getProperties());
                    SwingUtil.showMessageDialog(actionKey.actionName(), dbStatus.value(), MessageType.INFO);
                    break;
            }
        }
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put(DbManagerProperties.DRIVER.name(), ((DbType) driverBox.getSelectedItem()).driver());
        properties.put(DbManagerProperties.HOST.name(), hostField.getText());
        properties.put(DbManagerProperties.PORT.name(), portField.getText());
        properties.put(DbManagerProperties.NAME.name(), nameField.getText());
        properties.put(DbManagerProperties.USER.name(), userField.getText());
        if (!passwordField.isContentNull()) {
            properties.put(DbManagerProperties.PASSWORD.name(), new String(passwordField.getValue()));
        } else {
            properties.put(DbManagerProperties.PASSWORD.name(), "");
        }
        properties.put(DbManagerProperties.ADMIN_NAME.name(), adminNameField.getText());
        properties.put(DbManagerProperties.ADMIN_USER.name(), adminUserField.getText());
        if (!adminPasswordField.isContentNull()) {
            properties.put(DbManagerProperties.ADMIN_PASSWORD.name(), new String(adminPasswordField.getValue()));
        } else {
            properties.put(DbManagerProperties.ADMIN_PASSWORD.name(), "");
        }
        return properties;
    }

    public void setDefault(String dbName) {
        hostField.setText("127.0.0.1");
        nameField.setValue(dbName);
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
        SWInputStatusValidator inputStatusValidator = new SWInputStatusValidator();
        inputStatusValidator.add(hostField);
        inputStatusValidator.add(portField);
        inputStatusValidator.add(nameField);
        inputStatusValidator.add(userField);
        inputStatusValidator.add(adminNameField);
        inputStatusValidator.add(adminUserField);
        return inputStatusValidator.isInputValid();
    }

    public void setAppHelp(AppHelp appHelp) {
        this.appHelp = appHelp;
    }

    public void setHelpKey(String helpKey) {
        this.helpKey = helpKey;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
