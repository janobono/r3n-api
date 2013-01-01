package sk.r3n.sw.test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JPanel;
import sk.r3n.sw.component.R3NButton;
import sk.r3n.sw.component.field.ValueField;
import sk.r3n.ui.R3NAction;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public class R3NActionPanel extends JPanel implements UIActionExecutor {

    private ValueField<File> resultField;

    public R3NActionPanel() {
        super(new GridBagLayout());

        R3NAction[] actions = R3NAction.values();
        int columns = actions.length / 10;
        if (actions.length % 10 != 0) {
            columns++;
        }
        JPanel panel = new JPanel(new GridLayout(10, columns));
        for (R3NAction action : actions) {
            panel.add(new R3NButton(action, this));
        }
        add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        resultField = new ValueField<>();
        add(resultField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
    }

    @Override
    public void execute(UIActionKey actionKey, Object source) {
        resultField.setText(actionKey.actionName());
    }

}
