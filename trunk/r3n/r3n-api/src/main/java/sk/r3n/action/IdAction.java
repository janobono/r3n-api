package sk.r3n.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class IdAction extends AbstractAction {

	private static final long serialVersionUID = 4066776274795322336L;

	public static IdActionService idActionService;

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

	public void actionPerformed(ActionEvent e) {
		idActionService.processAction(groupId, actionId, idActionExecutor,
				e.getSource());
	}

}
