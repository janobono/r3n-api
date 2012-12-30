package sk.r3n.sw.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;
import sk.r3n.ui.R3NAction;

public abstract class R3NDialog extends JDialog implements WindowListener, UIActionExecutor {

    protected UIActionKey lastActionKey = R3NAction.CLOSE;

    public R3NDialog() {
        super();
        init();
    }

    public R3NDialog(Frame owner) {
        super(owner);
        init();
    }

    public UIActionKey getLastActionKey() {
        return lastActionKey;
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (lastActionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case CLOSE:
                    dispose();
                    break;
            }
        }
    }

    private void init() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setLayout(new BorderLayout());
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                R3NAction.CLOSE, this);
    }

    public abstract boolean isInputValid();

    @Override
    public void pack() {
        super.pack();
        SwingUtil.modifyDimensions(this);
    }

    @Override
    public void setVisible(boolean visible) {
        lastActionKey = null;
        if (visible) {
            SwingUtil.positionCenterWindow(getOwner(), this);
        }
        super.setVisible(visible);
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        execute(R3NAction.CLOSE, windowEvent.getSource());
    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {
    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
    }

}
