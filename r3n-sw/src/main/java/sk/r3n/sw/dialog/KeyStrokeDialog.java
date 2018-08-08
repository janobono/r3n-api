package sk.r3n.sw.dialog;

import sk.r3n.sw.*;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class KeyStrokeDialog extends JDialog implements UIActionExecutor, WindowListener, KeyListener {

    private static final String TITLE = "TITLE";

    private static final String EXISTS = "EXISTS";

    protected KeyStroke keyStroke;

    protected JTextField textField;

    protected List<KeyStroke> keyStrokes;

    protected UIActionKey lastActionKey = R3NAction.CLOSE;

    public KeyStrokeDialog() {
        super();
        init();
    }

    public KeyStrokeDialog(Frame owner) {
        super(owner);
        init();
    }

    private void init() {
        setTitle(ResourceBundle.getBundle(KeyStrokeDialog.class.getCanonicalName()).getString(TITLE));
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        JPanel form = new JPanel(new GridBagLayout());

        textField = new JTextField();
        textField.setColumns(15);
        textField.setEditable(false);
        textField.addKeyListener(this);
        Set<AWTKeyStroke> keys = new HashSet<>();
        textField.setFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, keys);
        textField.setFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);

        form.add(textField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 10, 10), 0, 0));

        add(form, BorderLayout.CENTER);

        ButtonPanel buttonPanel = new ButtonPanel(2, true);
        R3NButton okButton = new R3NButton(R3NAction.OK, this);
        buttonPanel.addButton(okButton);
        R3NButton cancelButton = new R3NButton(R3NAction.CANCEL, this);
        buttonPanel.addButton(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (lastActionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case OK:
                    if (keyStroke == null) {
                        lastActionKey = R3NAction.CANCEL;
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

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    public boolean initDialog(List<KeyStroke> keyStrokes) {
        this.keyStrokes = keyStrokes;
        pack();
        SwingUtil.positionCenterWindow(SwingUtil.getRootFrame(), this);
        setVisible(true);
        return lastActionKey.equals(R3NAction.OK);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        keyStroke = KeyStroke.getKeyStrokeForEvent(keyEvent);
        if (keyStrokes != null) {
            for (KeyStroke exStroke : keyStrokes) {
                if (keyStroke.getKeyCode() == exStroke.getKeyCode()
                        && keyStroke.getModifiers() == exStroke.getModifiers()
                        && SwingUtil.showYesNoDialog(null, MessageFormat.format(ResourceBundle.getBundle(
                        KeyStrokeDialog.class.getCanonicalName()).getString(EXISTS), new Object[]{keyStroke}),
                        MessageType.WARNING) != Answer.YES) {
                    keyStroke = null;
                    break;
                }
            }
        }
        textField.setText("");
        if (keyStroke != null) {
            textField.setText(keyStroke.toString());
            switch (keyStroke.getKeyCode()) {
                case KeyEvent.VK_ALT:
                case KeyEvent.VK_ALT_GRAPH:
                case KeyEvent.VK_SHIFT:
                case KeyEvent.VK_CAPS_LOCK:
                case KeyEvent.VK_CONTROL:
                    break;
                default:
                    execute(R3NAction.OK, this);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        execute(R3NAction.CLOSE, e.getSource());
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }
}
