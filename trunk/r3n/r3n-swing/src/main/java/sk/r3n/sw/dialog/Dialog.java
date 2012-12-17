package sk.r3n.sw.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.UIActionExecutor;
import sk.r3n.sw.util.UIActionKey;
import sk.r3n.sw.util.UISWAction;

public abstract class Dialog extends JDialog implements WindowListener, UIActionExecutor {

    protected UIActionKey lastActionKey = UISWAction.CLOSE;

    public Dialog() {
        super();
        init();
    }

    public Dialog(Frame owner) {
        super(owner);
        init();
    }

    public UIActionKey getLastActionKey() {
        return lastActionKey;
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (lastActionKey instanceof UISWAction) {
            switch ((UISWAction) actionKey) {
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
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, UISWAction.CLOSE, this);
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
        execute(UISWAction.CLOSE, windowEvent.getSource());
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
