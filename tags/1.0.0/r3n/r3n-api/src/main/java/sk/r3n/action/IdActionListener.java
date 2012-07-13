package sk.r3n.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IdActionListener implements ActionListener {

	public static IdActionService idActionService;

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

	public void actionPerformed(ActionEvent e) {
		idActionService.processAction(groupId, actionId, idActionExecutor,
				e.getSource());
	}

}
