package sk.r3n.sw.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionKey;

public abstract class YesNoDialog extends R3NDialog {

    protected R3NButton noButton;

    protected R3NButton yesButton;

    public YesNoDialog() {
        super();
        init();
    }

    public YesNoDialog(Frame frame) {
        super(frame);
        init();
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (lastActionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case YES:
                    if (!isInputValid()) {
                        break;
                    }
                    dispose();
                    break;
                case CLOSE:
                case NO:
                    dispose();
                    break;
            }
        }
    }

    private void init() {
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, R3NAction.YES,
                this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, R3NAction.NO,
                this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                R3NAction.CLOSE, this);

        ButtonPanel buttonPanel = new ButtonPanel(2, true);
        yesButton = new R3NButton(R3NAction.YES, this);
        buttonPanel.addButton(yesButton);
        noButton = new R3NButton(R3NAction.NO, this);
        buttonPanel.addButton(noButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}