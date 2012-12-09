package sk.r3n.sw.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class Action extends AbstractAction {

    protected ActionKey actionKey;

    protected ActionExecutor actionExecutor;

    public Action(ActionKey actionKey, ActionExecutor actionExecutor) {
        super();
        this.actionKey = actionKey;
        this.actionExecutor = actionExecutor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionExecutor.execute(actionKey, e.getSource());
    }

}
