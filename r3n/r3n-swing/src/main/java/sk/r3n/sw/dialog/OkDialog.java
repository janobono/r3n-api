package sk.r3n.sw.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.UIActionKey;
import sk.r3n.sw.util.UISWAction;

public abstract class OkDialog extends R3NDialog {

    protected R3NButton okButton;

    public OkDialog() {
        super();
        init();
    }

    public OkDialog(Frame frame) {
        super(frame);
        init();
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        lastActionKey = actionKey;
        if (actionKey instanceof UISWAction) {
            switch ((UISWAction) actionKey) {
                case OK:
                    if (!isInputValid()) {
                        break;
                    }
                    dispose();
                    break;
                case CLOSE:
                    dispose();
                    break;
            }
        }
    }

    private void init() {
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, UISWAction.OK, this);
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, UISWAction.CLOSE, this);

        ButtonPanel buttonPanel = new ButtonPanel(1, true);
        okButton = new R3NButton(UISWAction.OK, this);
        buttonPanel.addButton(okButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}
