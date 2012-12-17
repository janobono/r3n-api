package sk.r3n.sw.component;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.UIActionExecutor;
import sk.r3n.sw.util.UIActionKey;
import sk.r3n.sw.util.UISWAction;

public class ButtonPanel extends JPanel implements UIActionExecutor {

    private final boolean row;

    private List<JButton> buttons;

    public ButtonPanel(int size, boolean row) {
        super();
        buttons = new ArrayList<>();
        this.row = row;
        if (row) {
            setLayout(new GridLayout(1, size));
        } else {
            setLayout(new GridLayout(size, 1));
        }
    }

    public void addButton(JButton button) {
        this.add(button);
        button.setFocusTraversalKeysEnabled(true);
        buttons.add(button);
        if (row) {
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, UISWAction.LEFT, this);
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, UISWAction.RIGHT, this);
        } else {
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, UISWAction.UP, this);
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, UISWAction.DOWN, this);
        }
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof UISWAction) {
            int index = buttons.indexOf(source);
            if (index != -1) {
                switch ((UISWAction) actionKey) {
                    case LEFT:
                    case UP:
                        moveBackward(index, index);
                        break;
                    case RIGHT:
                    case DOWN:
                        moveForward(index, index);
                        break;
                }
            }
        }
    }

    private void moveBackward(int startIndex, int index) {
        index--;
        if (index < 0) {
            index = buttons.size() - 1;
        }
        if (index == startIndex) {
            return;
        }
        if (buttons.get(index).isEnabled()) {
            buttons.get(index).requestFocus();
        }
        moveBackward(startIndex, index);
    }

    private void moveForward(int startIndex, int index) {
        index++;
        if (index > buttons.size() - 1) {
            index = 0;
        }
        if (index == startIndex) {
            return;
        }
        if (buttons.get(index).isEnabled()) {
            buttons.get(index).requestFocus();
        }
        moveForward(startIndex, index);
    }

}
