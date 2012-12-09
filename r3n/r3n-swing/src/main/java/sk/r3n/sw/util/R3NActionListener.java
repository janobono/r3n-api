package sk.r3n.sw.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class R3NActionListener implements ActionListener {

    protected ActionKey actionKey;

    protected ActionExecutor actionExecutor;

    public R3NActionListener(ActionKey actionKey, ActionExecutor actionExecutor) {
        super();
        this.actionKey = actionKey;
        this.actionExecutor = actionExecutor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionExecutor.execute(actionKey, e.getSource());
    }

}
