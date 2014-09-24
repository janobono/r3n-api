package sk.r3n.sw.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JComponent;
import javax.swing.JPanel;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.R3NAction;

public abstract class CloseDialog extends R3NDialog {

    protected R3NButton closeButton;

    public CloseDialog() {
        super();
        init();
    }

    public CloseDialog(Frame frame) {
        super(frame);
        init();
    }

    private void init() {
        SwingUtil.setKeyStroke((JPanel) getContentPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                R3NAction.CLOSE, this);
        ButtonPanel buttonPanel = new ButtonPanel(1, true);
        closeButton = new R3NButton(R3NAction.CLOSE, this);
        buttonPanel.addButton(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}
