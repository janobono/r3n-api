package sk.r3n.sw.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

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
