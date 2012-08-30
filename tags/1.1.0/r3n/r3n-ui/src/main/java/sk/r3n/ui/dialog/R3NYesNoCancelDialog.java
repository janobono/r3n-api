package sk.r3n.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import sk.r3n.ui.IdAction;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NButton;
import sk.r3n.ui.panel.ButtonPanel;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NYesNoCancelDialog extends R3NDialog {

    protected R3NButton noButton;
    protected R3NButton yesButton;
    protected R3NButton cancelButton;

    public R3NYesNoCancelDialog() {
        super();
        init();
    }

    public R3NYesNoCancelDialog(Frame frame) {
        super(frame);
        init();
    }

    @Override
    public void execute(String groupId, int actionId, Object source) {
        lastGroup = groupId;
        lastAction = actionId;
        if (groupId.equals(UIService.class.getCanonicalName())) {
            switch (actionId) {
                case UIService.ACTION_YES:
                    if (!isInputValid()) {
                        break;
                    }
                    dispose();
                    break;
                case UIService.ACTION_CLOSE:
                    lastAction = UIService.ACTION_CANCEL;
                case UIService.ACTION_NO:
                case UIService.ACTION_CANCEL:
                    dispose();
                    break;
            }
        }
    }

    private void init() {
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_YES,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                (JPanel) getContentPane(), this);
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_NO,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                (JPanel) getContentPane(), this);
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_CANCEL,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                (JPanel) getContentPane(), this);
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                (JPanel) getContentPane(), this);

        ButtonPanel buttonPanel = new ButtonPanel(1, 3);
        yesButton = new R3NButton(UIService.class.getCanonicalName(),
                UIService.ACTION_YES);
        yesButton.addActionListener(new IdAction(UIService.class.getCanonicalName(), UIService.ACTION_YES, this));
        buttonPanel.addButton(yesButton);
        noButton = new R3NButton(UIService.class.getCanonicalName(),
                UIService.ACTION_NO);
        noButton.addActionListener(new IdAction(UIService.class.getCanonicalName(), UIService.ACTION_NO, this));
        buttonPanel.addButton(noButton);
        cancelButton = new R3NButton(UIService.class.getCanonicalName(),
                UIService.ACTION_CANCEL);
        cancelButton.addActionListener(new IdAction(UIService.class.getCanonicalName(), UIService.ACTION_CANCEL, this));
        buttonPanel.addButton(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
