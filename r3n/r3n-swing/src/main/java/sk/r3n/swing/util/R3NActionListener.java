package sk.r3n.swing.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class R3NActionListener implements ActionListener {

    protected R3NActionKey actionKey;

    protected R3NActionExecutor actionExecutor;

    public R3NActionListener(R3NActionKey actionKey, R3NActionExecutor actionExecutor) {
        super();
        this.actionKey = actionKey;
        this.actionExecutor = actionExecutor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionExecutor.execute(actionKey, e.getSource());
    }

}
