package sk.r3n.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import sk.r3n.action.IdActionExecutor;
import sk.r3n.ui.component.R3NInputComponent;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NDialog extends JDialog implements WindowListener,
        IdActionExecutor {

    private List<R3NInputComponent<?>> inputComponents;
    protected String lastGroup;
    protected int lastAction;

    public R3NDialog() {
        super();
        init();
    }

    public R3NDialog(Frame owner) {
        super(owner);
        init();
    }

    public void addInputComponent(R3NInputComponent<?> inputComponent) {
        inputComponents.add(inputComponent);
    }

    @Override
    public void execute(String groupId, int actionId, Object source) {
        lastGroup = groupId;
        lastAction = actionId;
        if (groupId.equals(UIService.class.getCanonicalName())) {
            switch (actionId) {
                case UIService.ACTION_CLOSE:
                    dispose();
                    break;
            }
        }
    }

    public List<R3NInputComponent<?>> getInputComponents() {
        return inputComponents;
    }

    public String getLastGroup() {
        return lastGroup;
    }

    public int getLastAction() {
        return lastAction;
    }

    private void init() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setLayout(new BorderLayout());
        inputComponents = new ArrayList<>();
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                (JPanel) getContentPane(), this);
    }

    public boolean isInputValid() {
        return UIServiceManager.getDefaultUIService().isInputValid(
                inputComponents);
    }

    @Override
    public void pack() {
        super.pack();
        UIServiceManager.getDefaultUIService().modifyDimensions(this);
    }

    public void removeInputComponent(R3NInputComponent<?> inputComponent) {
        inputComponents.remove(inputComponent);
    }

    @Override
    public void setVisible(boolean visible) {
        lastGroup = null;
        lastAction = -1;
        if (visible) {
            UIServiceManager.getDefaultUIService().positionCenterWindow(
                    getOwner(), this);
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
        execute(UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
                windowEvent.getSource());
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
