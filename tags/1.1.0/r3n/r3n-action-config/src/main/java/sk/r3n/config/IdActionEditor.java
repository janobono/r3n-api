package sk.r3n.config;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NTextArea;
import sk.r3n.ui.list.R3NList;
import sk.r3n.ui.list.renderer.LabelListCellRenderer;
import sk.r3n.ui.table.editor.R3NButtonTableEditor;
import sk.r3n.ui.table.renderer.LabelTableCellRenderer;
import sk.r3n.ui.util.SVGIcon;
import sk.r3n.ui.util.UIServiceManager;
import sk.r3n.util.ConfigUtil;

public class IdActionEditor extends JFrame {

    private class KeyStrokeTableEditor extends R3NButtonTableEditor {

        private JTextField textField;
        private JDialog keyCodeDialog;

        @Override
        public KeyStroke getCellEditorValue() {
            return (KeyStroke) oldValue;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            keyCodeDialog = new JDialog(IdActionEditor.this);
            keyCodeDialog.setModal(true);
            keyCodeDialog.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {
                }

                @Override
                public void windowIconified(WindowEvent e) {
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    close(true);
                }

                @Override
                public void windowClosed(WindowEvent e) {
                }

                @Override
                public void windowActivated(WindowEvent e) {
                }
            });
            keyCodeDialog.setLayout(new BorderLayout());
            textField = new JTextField();
            textField.setColumns(15);
            textField.setEditable(false);
            Set<AWTKeyStroke> keys = new HashSet<>();
            textField.setFocusTraversalKeys(
                    KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, keys);
            textField.setFocusTraversalKeys(
                    KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);
            if (oldValue != null) {
                textField.setText(oldValue.toString());
            }
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
                    textField.setText("");
                    if (keyStroke != null) {
                        textField.setText(keyStroke.toString());
                    }
                    oldValue = keyStroke;
                }
            });
            keyCodeDialog.add(textField, BorderLayout.CENTER);

            JPanel panel = new JPanel(new GridLayout(1, 2));
            JButton btn = new JButton("clear");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    textField.setText("");
                    oldValue = null;
                }
            });
            panel.add(btn);
            btn = new JButton("ok");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    close(false);
                }
            });
            panel.add(btn);
            keyCodeDialog.add(panel, BorderLayout.SOUTH);
            keyCodeDialog.pack();
            UIServiceManager.getDefaultUIService().positionCenterWindow(IdActionEditor.this,
                    keyCodeDialog);
            keyCodeDialog.setVisible(true);
        }

        private void close(boolean cancel) {
            if (cancel) {
                cancelCellEditing();
            } else {
                stopCellEditing();
            }
            keyCodeDialog.dispose();
        }
    }

    private class IconTableRenderer extends LabelTableCellRenderer<URL> {

        @Override
        public String getNullText() {
            setIcon(null);
            return super.getNullText();
        }

        @Override
        public String getText(URL value) {
            setIcon(null);
            try {
                if (value.getFile().toLowerCase().endsWith(".svg")) {
                    setIcon(new SVGIcon(value, new Dimension(24, 24)));
                } else {
                    setIcon(new ImageIcon(value));
                }
            } catch (Exception e) {
                return "ERR!";
            }
            return "";
        }
    }

    private class IconTableEditor extends R3NButtonTableEditor {

        private class IconListRenderer extends LabelListCellRenderer<URL> {

            @Override
            public String getNullText() {
                setIcon(null);
                return super.getNullText();
            }

            @Override
            public String getText(URL value) {
                try {
                    if (value.getFile().toLowerCase().endsWith(".svg")) {
                        setIcon(new SVGIcon(value, new Dimension(24, 24)));
                    } else {
                        setIcon(new ImageIcon(value));
                    }
                } catch (Exception e) {
                    setIcon(null);
                }
                return value.toString();
            }
        }
        private JDialog iconDialog;
        private R3NList<URL> iconList;

        @Override
        public Object getCellEditorValue() {
            return oldValue;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            iconDialog = new JDialog(IdActionEditor.this);
            iconDialog.setModal(true);
            iconDialog.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {
                }

                @Override
                public void windowIconified(WindowEvent e) {
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    close(true);
                }

                @Override
                public void windowClosed(WindowEvent e) {
                }

                @Override
                public void windowActivated(WindowEvent e) {
                }
            });
            iconDialog.setLayout(new BorderLayout());
            iconList = new R3NList<>();
            iconList.setCellRenderer(new IconListRenderer());

            List<URL> iconResource = new ArrayList<>();
            try {
                File resDir = new File(resField.getText());
                File[] files = resDir.listFiles();
                for (File file : files) {
                    if (file.getName().toLowerCase().endsWith(".jar")) {
                        ZipFile zipFile = new ZipFile(file);
                        Enumeration<?> enumeration = zipFile.entries();
                        while (enumeration.hasMoreElements()) {
                            ZipEntry entry = (ZipEntry) enumeration.nextElement();
                            if (entry.getName().toLowerCase().endsWith(".svg")
                                    || entry.getName().toLowerCase().endsWith(".png")
                                    || entry.getName().toLowerCase().endsWith(".gif")
                                    || entry.getName().toLowerCase().endsWith(".jpg")) {
                                iconResource.add(new URL("jar:file:"
                                        + resField.getText() + "/"
                                        + file.getName() + "!/"
                                        + entry.getName()));
                            }
                        }
                    }
                }
            } catch (Exception exception) {
            }
            iconList.setValues(iconResource);
            iconList.setSelectedValue((URL) oldValue);
            iconDialog.add(new JScrollPane(iconList), BorderLayout.CENTER);

            JPanel panel = new JPanel(new GridLayout(1, 2));
            JButton btn = new JButton("clear");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    iconList.setSelectedValue(null);
                    oldValue = null;
                }
            });
            panel.add(btn);
            btn = new JButton("ok");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    close(false);
                }
            });
            panel.add(btn);
            iconDialog.add(panel, BorderLayout.SOUTH);
            iconDialog.pack();
            UIServiceManager.getDefaultUIService().positionCenterWindow(IdActionEditor.this, iconDialog);
            iconDialog.setVisible(true);
        }

        private void close(boolean cancel) {
            if (cancel) {
                cancelCellEditing();
            } else {
                oldValue = iconList.getSelectedValue();
                stopCellEditing();
            }
            iconDialog.dispose();
        }
    }

    private class PropertiesTableRenderer extends LabelTableCellRenderer<Properties> {

        @Override
        public String getText(Properties value) {
            List<String> lines = new ArrayList<>();
            for (Object key : value.keySet()) {
                lines.add(key + "=" + value.getProperty(key.toString()));
            }
            return getLines(lines);
        }
    }

    private class PropertiesTableEditor extends R3NButtonTableEditor {

        private JDialog propertiesDialog;
        private R3NTextArea propertiesArea;

        @Override
        public Object getCellEditorValue() {
            return oldValue;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            propertiesDialog = new JDialog(IdActionEditor.this);
            propertiesDialog.setModal(true);
            propertiesDialog.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {
                }

                @Override
                public void windowIconified(WindowEvent e) {
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    close(true);
                }

                @Override
                public void windowClosed(WindowEvent e) {
                }

                @Override
                public void windowActivated(WindowEvent e) {
                }
            });
            propertiesDialog.setLayout(new BorderLayout());
            propertiesArea = new R3NTextArea();

            String text = "";
            Properties properties = (Properties) oldValue;
            for (Object key : properties.keySet()) {
                text += key + "=" + properties.getProperty(key.toString())
                        + ";\n";
            }
            propertiesArea.setText(text);

            propertiesDialog.add(new JScrollPane(propertiesArea),
                    BorderLayout.CENTER);

            JButton btn = new JButton("ok");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    close(false);
                }
            });
            propertiesDialog.add(btn, BorderLayout.SOUTH);
            propertiesDialog.pack();
            UIServiceManager.getDefaultUIService().positionCenterWindow(IdActionEditor.this, propertiesDialog);
            propertiesDialog.setVisible(true);
        }

        private void close(boolean cancel) {
            if (cancel) {
                cancelCellEditing();
            } else {
                Properties properties = new Properties();
                try {
                    String[] lines = propertiesArea.getText().split(";");
                    for (String line : lines) {
                        if (line.trim().length() == 0) {
                            continue;
                        }
                        String[] pair = line.split("=");
                        properties.setProperty(pair[0].trim(), pair[1].trim());
                    }
                } catch (Exception e) {
                    return;
                }
                Properties old = (Properties) oldValue;
                old.clear();
                old.putAll(properties);
                stopCellEditing();
            }
            propertiesDialog.dispose();
        }
    }

    private class IdServiceTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return allActions.size();
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "id";
                case 1:
                    return "name";
                case 2:
                    return "key stroke";
                case 3:
                    return "icon";
                case 4:
                    return "disable icon";
                case 5:
                    return "pressed icon";
                case 6:
                    return "properties";
            }
            return null;
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String key = allActions.get(rowIndex);
            Object[] data = ConfigUtil.parseKey(key);
            String groupId = (String) data[0];
            int actionId = (Integer) data[1];
            switch (columnIndex) {
                case 0:
                    return key;
                case 1:
                    return R3NActionConfig.idActionService.getName(groupId,
                            actionId);
                case 2:
                    return R3NActionConfig.idActionService.getKeyStroke(groupId,
                            actionId);
                case 3:
                    return R3NActionConfig.idActionService.getIcon(groupId,
                            actionId);
                case 4:
                    return R3NActionConfig.idActionService.getDisabledIcon(groupId,
                            actionId);
                case 5:
                    return R3NActionConfig.idActionService.getPressedIcon(groupId,
                            actionId);
                case 6:
                    Properties properties = R3NActionConfig.idActionService.getProperties(groupId, actionId);
                    if (properties == null) {
                        properties = new Properties();
                    }
                    return properties;
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            String key = allActions.get(rowIndex);
            Object[] data = ConfigUtil.parseKey(key);
            String groupId = (String) data[0];
            int actionId = (Integer) data[1];
            switch (columnIndex) {
                case 1:
                    if (aValue != null) {
                        R3NActionConfig.idActionService.setName(groupId, actionId,
                                (String) aValue);
                    }
                    break;
                case 2:
                    R3NActionConfig.idActionService.setKeyStroke(groupId, actionId,
                            (KeyStroke) aValue);
                    break;
                case 3:
                    R3NActionConfig.idActionService.setIcon(groupId, actionId,
                            (URL) aValue);
                    break;
                case 4:
                    R3NActionConfig.idActionService.setDisabledIcon(groupId,
                            actionId, (URL) aValue);
                    break;
                case 5:
                    R3NActionConfig.idActionService.setPressedIcon(groupId,
                            actionId, (URL) aValue);
                    break;
                case 6:
                    R3NActionConfig.idActionService.setProperties(groupId,
                            actionId, (Properties) aValue);
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0;
        }
    }
    private List<String> allActions;
    private JTable codeTable;
    private JTextField confField;
    private JTextField locField;
    private JTextField resField;

    public IdActionEditor() {
        super();
        allActions = R3NActionConfig.idActionService.getActions();
        setTitle("Id action editor");
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        confField = new JTextField();
        confField.setText(System.getProperty("user.dir") + File.separatorChar
                + "conf" + File.separatorChar + "actions.conf");
        JButton button = new JButton("...");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = UIServiceManager.getDefaultUIService().openFile(UIService.FILE_FILES_ONLY,
                        "Actions configuration file", new File(System.getProperty("user.dir")), null, null);
                if (file != null) {
                    try {
                        IdActionEditor.this.confField.setText(file.getCanonicalPath());
                    } catch (Exception e1) {
                    }
                }
            }
        });
        panel.add(confField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        panel.add(button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));

        locField = new JTextField();
        locField.setText(System.getProperty("user.dir") + File.separatorChar
                + "conf" + File.separatorChar + "actions.properties");
        button = new JButton("...");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = UIServiceManager.getDefaultUIService().openFile(UIService.FILE_FILES_ONLY,
                        "Actions locale file", new File(System.getProperty("user.dir")), null, null);
                if (file != null) {
                    try {
                        IdActionEditor.this.locField.setText(file.getCanonicalPath());
                    } catch (Exception e1) {
                    }
                }
            }
        });
        panel.add(locField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        panel.add(button, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        resField = new JTextField();
        resField.setText("lib");
        button = new JButton("...");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = UIServiceManager.getDefaultUIService().openFile(UIService.FILE_DIRECTORIES_ONLY,
                        "Icon archive directory", new File(System.getProperty("user.dir")), null, null);
                if (file != null) {
                    try {
                        IdActionEditor.this.resField.setText(file.getCanonicalPath());
                    } catch (Exception e1) {
                    }
                }
            }
        });
        panel.add(resField, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        panel.add(button, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));

        add(panel, BorderLayout.NORTH);

        codeTable = new JTable();
        codeTable.setModel(new IdServiceTableModel());
        codeTable.getColumnModel().getColumn(2).setCellEditor(new KeyStrokeTableEditor());
        codeTable.getColumnModel().getColumn(3).setCellRenderer(new IconTableRenderer());
        codeTable.getColumnModel().getColumn(3).setCellEditor(new IconTableEditor());
        codeTable.getColumnModel().getColumn(4).setCellRenderer(new IconTableRenderer());
        codeTable.getColumnModel().getColumn(4).setCellEditor(new IconTableEditor());
        codeTable.getColumnModel().getColumn(5).setCellRenderer(new IconTableRenderer());
        codeTable.getColumnModel().getColumn(5).setCellEditor(new IconTableEditor());
        codeTable.getColumnModel().getColumn(6).setCellRenderer(new PropertiesTableRenderer());
        codeTable.getColumnModel().getColumn(6).setCellEditor(new PropertiesTableEditor());
        add(new JScrollPane(codeTable), BorderLayout.CENTER);

        panel = new JPanel(new GridLayout(1, 3));
        button = new JButton("sync");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sync();
            }
        });
        panel.add(button);
        button = new JButton("load");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                load();
            }
        });
        panel.add(button);
        button = new JButton("save");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                save();
            }
        });
        panel.add(button);
        add(panel, BorderLayout.SOUTH);
    }

    private void load() {
        FileInputStream conf = null;
        FileInputStream loc = null;
        try {
            conf = new FileInputStream(confField.getText());
            loc = new FileInputStream(locField.getText());
            R3NActionConfig.idActionService.load(conf, loc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conf.close();
            } catch (Exception e) {
            }
            try {
                loc.close();
            } catch (Exception e) {
            }
        }
        allActions = R3NActionConfig.idActionService.getActions();
        ((IdServiceTableModel) codeTable.getModel()).fireTableDataChanged();
    }

    private void save() {
        FileOutputStream conf = null;
        FileOutputStream loc = null;
        try {
            conf = new FileOutputStream(confField.getText());
            loc = new FileOutputStream(locField.getText());
            R3NActionConfig.idActionService.save(conf, loc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conf.close();
            } catch (Exception e) {
            }
            try {
                loc.close();
            } catch (Exception e) {
            }
        }
    }

    private void sync() {
        FileInputStream conf = null;
        FileInputStream loc = null;
        try {
            conf = new FileInputStream(confField.getText());
            loc = new FileInputStream(locField.getText());
            R3NActionConfig.idActionService.sync(conf, loc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conf.close();
            } catch (Exception e) {
            }
            try {
                loc.close();
            } catch (Exception e) {
            }
        }
        ((IdServiceTableModel) codeTable.getModel()).fireTableDataChanged();
    }
}
