package sk.r3n.sw.util;

import sk.r3n.sw.UIActionExecutor;
import sk.r3n.sw.UIActionKey;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIActionListener implements ActionListener {

    protected UIActionKey actionKey;

    protected UIActionExecutor actionExecutor;

    public UIActionListener(UIActionKey actionKey, UIActionExecutor actionExecutor) {
        super();
        this.actionKey = actionKey;
        this.actionExecutor = actionExecutor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        actionExecutor.execute(actionKey, e.getSource());
    }

}
