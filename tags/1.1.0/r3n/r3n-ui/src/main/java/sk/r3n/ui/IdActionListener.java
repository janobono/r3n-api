package sk.r3n.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sk.r3n.action.IdActionExecutor;

public class IdActionListener implements ActionListener {

    protected String groupId;
    protected int actionId;
    protected IdActionExecutor idActionExecutor;

    public IdActionListener(String groupId, int actionId,
            IdActionExecutor idActionExecutor) {
        super();
        this.groupId = groupId;
        this.actionId = actionId;
        this.idActionExecutor = idActionExecutor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        idActionExecutor.execute(groupId, actionId, e.getSource());
    }
}
