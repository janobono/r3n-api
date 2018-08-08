package sk.r3n.sw.dialog;

import sk.r3n.sw.R3NAction;
import sk.r3n.sw.component.ButtonPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

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
