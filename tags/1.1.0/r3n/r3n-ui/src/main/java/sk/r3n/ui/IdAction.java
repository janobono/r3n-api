package sk.r3n.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import sk.r3n.action.IdActionExecutor;

public class IdAction extends AbstractAction {

    protected String groupId;
    protected int actionId;
    protected IdActionExecutor idActionExecutor;

    public IdAction(String groupId, int actionId,
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
