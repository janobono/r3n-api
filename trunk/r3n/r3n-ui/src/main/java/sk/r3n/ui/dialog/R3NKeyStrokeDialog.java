package sk.r3n.ui.dialog;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import sk.r3n.ui.IdAction;
import sk.r3n.action.IdActionExecutor;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NButton;
import sk.r3n.ui.panel.ButtonPanel;
import sk.r3n.ui.util.UIServiceManager;

public class R3NKeyStrokeDialog extends JDialog implements IdActionExecutor,
        WindowListener, KeyListener {

    protected KeyStroke keyStroke;
    protected JTextField textField;
    protected List<KeyStroke> keyStrokes;
    protected int lastAction;

    public R3NKeyStrokeDialog() {
        super();
        init();
    }

    public R3NKeyStrokeDialog(Frame owner) {
        super(owner);
        init();
    }

    private void init() {
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setTitle(ResourceBundle.getBundle(this.getClass().getCanonicalName()).getString("TITLE"));

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

        ButtonPanel buttonPanel = new ButtonPanel(1, 2);
        R3NButton okButton = new R3NButton(UIService.class.getCanonicalName(),
                UIService.ACTION_OK);
        okButton.addActionListener(new IdAction(UIService.class.getCanonicalName(), UIService.ACTION_OK, this));
        buttonPanel.addButton(okButton);
        R3NButton cancelButton = new R3NButton(
                UIService.class.getCanonicalName(), UIService.ACTION_CANCEL);
        cancelButton.addActionListener(new IdAction(UIService.class.getCanonicalName(), UIService.ACTION_CANCEL, this));
        buttonPanel.addButton(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void execute(String groupId, int actionId, Object source) {
        lastAction = actionId;
        if (groupId.equals(UIService.class.getCanonicalName())) {
            switch (actionId) {
                case UIService.ACTION_OK:
                    if (keyStroke == null) {
                        lastAction = UIService.ACTION_CANCEL;
                    }
                    dispose();
                    break;
                case UIService.ACTION_CLOSE:
                case UIService.ACTION_CANCEL:
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
        lastAction = UIService.ACTION_CANCEL;
        pack();
        UIServiceManager.getDefaultUIService().positionCenterWindow(
                UIServiceManager.getDefaultUIService().getFrameForComponent(
                this), this);
        setVisible(true);
        return lastAction == UIService.ACTION_OK;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        keyStroke = KeyStroke.getKeyStrokeForEvent(keyEvent);
        if (keyStrokes != null) {
            for (KeyStroke exStroke : keyStrokes) {
                if (keyStroke.getKeyCode() == exStroke.getKeyCode()
                        && keyStroke.getModifiers() == exStroke.getModifiers()
                        && UIServiceManager.getDefaultUIService().showYesNoDialog(
                        null,
                        ResourceBundle.getBundle(
                        R3NKeyStrokeDialog.class.getCanonicalName()).getString("EXISTS")
                        + "\n["
                        + keyStroke.toString()
                        + "]",
                        UIService.MESSAGE_ACTION_WARNING) != UIService.ANSWER_YES) {
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
                    execute(UIService.class.getCanonicalName(),
                            UIService.ACTION_OK, this);
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
        execute(UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
                this);
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
