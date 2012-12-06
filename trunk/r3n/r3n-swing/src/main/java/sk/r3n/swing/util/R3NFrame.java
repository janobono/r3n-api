package sk.r3n.swing.util;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import sk.r3n.swing.component.R3NInputComponent;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NFrame extends JFrame implements R3NActionExecutor, WindowListener {

    private List<R3NInputComponent<?>> inputComponents;

    public R3NFrame() {
        super();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setLayout(new BorderLayout());
        inputComponents = new ArrayList<>();
    }

    public List<R3NInputComponent<?>> getInputComponents() {
        return inputComponents;
    }

    public boolean isInputValid() {
        return UIServiceManager.getDefaultUIService().isInputValid(inputComponents);
    }

    public abstract void refreshUI();

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            R3NSwingUtil.positionCenterWindow(getOwner(), this);
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
        execute(UIService.class.getCanonicalName(), UIService.ACTION_CLOSE, windowEvent.getSource());
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
