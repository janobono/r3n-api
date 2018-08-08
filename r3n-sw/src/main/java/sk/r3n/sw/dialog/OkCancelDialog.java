package sk.r3n.sw.dialog;

import sk.r3n.sw.R3NAction;
import sk.r3n.sw.UIActionKey;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

public abstract class OkCancelDialog extends R3NDialog {

    protected R3NButton cancelButton;

    protected R3NButton okButton;

    public OkCancelDialog() {
        super();
        init();
    }

    public OkCancelDialog(Frame frame) {
        super(frame);
        init();
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (lastActionKey instanceof R3NAction) {
            switch ((R3NAction) actionKey) {
                case OK:
                    if (!isInputValid()) {
                        break;
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

    private void init() {
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, R3NAction.OK, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, R3NAction.CANCEL, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, R3NAction.CLOSE, this);

        ButtonPanel buttonPanel = new ButtonPanel(2, true);
        okButton = new R3NButton(R3NAction.OK, this);
        buttonPanel.addButton(okButton);
        cancelButton = new R3NButton(R3NAction.CANCEL, this);
        buttonPanel.addButton(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}
