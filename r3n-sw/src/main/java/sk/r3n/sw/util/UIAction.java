package sk.r3n.sw.util;

import sk.r3n.sw.UIActionExecutor;
import sk.r3n.sw.UIActionKey;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
