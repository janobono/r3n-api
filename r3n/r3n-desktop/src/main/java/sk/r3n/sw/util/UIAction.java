package sk.r3n.sw.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class UIAction extends AbstractAction {

    protected UIActionKey actionKey;

    protected UIActionExecutor actionExecutor;

    public UIAction(UIActionKey actionKey, UIActionExecutor actionExecutor) {
        super();
        this.actionKey = actionKey;
        this.actionExecutor = actionExecutor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionExecutor.execute(actionKey, e.getSource());
    }

}