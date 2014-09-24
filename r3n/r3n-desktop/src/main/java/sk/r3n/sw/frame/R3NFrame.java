package sk.r3n.sw.frame;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.R3NAction;
import sk.r3n.sw.util.UIActionExecutor;

public abstract class R3NFrame extends JFrame implements UIActionExecutor, WindowListener {

    public R3NFrame() {
        super();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setLayout(new BorderLayout());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            SwingUtil.positionCenterScreen(this);
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
