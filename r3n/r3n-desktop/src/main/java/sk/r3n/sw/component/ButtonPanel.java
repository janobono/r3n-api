package sk.r3n.sw.component;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.R3NAction;
import sk.r3n.sw.util.UIActionExecutor;
import sk.r3n.sw.util.UIActionKey;

public class ButtonPanel extends JPanel implements UIActionExecutor {

    private final boolean row;

    private final List<JButton> buttons;

    public ButtonPanel(int size, boolean row) {
        super();
        buttons = new ArrayList<JButton>();
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
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, R3NAction.LEFT, this);
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, R3NAction.RIGHT, this);
        } else {
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, R3NAction.UP, this);
            SwingUtil.setKeyStroke(button, JComponent.WHEN_FOCUSED, null, R3NAction.DOWN, this);
        }
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        if (actionKey instanceof R3NAction) {
            int index = buttons.indexOf(source);
            if (index != -1) {
                switch ((R3NAction) actionKey) {
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
