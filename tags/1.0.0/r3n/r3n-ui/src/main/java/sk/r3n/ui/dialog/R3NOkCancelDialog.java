package sk.r3n.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JPanel;

import sk.r3n.action.IdAction;
import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NButton;
import sk.r3n.ui.panel.ButtonPanel;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NOkCancelDialog extends R3NDialog {

	private static final long serialVersionUID = 4826628026757443539L;

	protected R3NButton cancelButton;

	protected R3NButton okButton;

	public R3NOkCancelDialog() {
		super();
		init();
	}

	public R3NOkCancelDialog(Frame frame) {
		super(frame);
		init();
	}

	public void execute(String groupId, int actionId, Object source) {
		lastGroup = groupId;
		lastAction = actionId;
		if (groupId.equals(UIService.class.getCanonicalName())) {
			switch (actionId) {
			case UIService.ACTION_OK:
				if (!isInputValid())
					return;
				dispose();
				return;
			case UIService.ACTION_CLOSE:
				lastAction = UIService.ACTION_CANCEL;
			case UIService.ACTION_CANCEL:
				dispose();
				return;
			}
		}
	}

	private void init() {
		UIServiceManager.getDefaultUIService().setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_OK,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);
		UIServiceManager.getDefaultUIService().setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_CANCEL,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);
		UIServiceManager.getDefaultUIService().setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);

		ButtonPanel buttonPanel = new ButtonPanel(1, 2);
		okButton = new R3NButton(UIService.class.getCanonicalName(),
				UIService.ACTION_OK);
		okButton.addActionListener(new IdAction(UIService.class
				.getCanonicalName(), UIService.ACTION_OK, this));
		buttonPanel.addButton(okButton);
		cancelButton = new R3NButton(UIService.class.getCanonicalName(),
				UIService.ACTION_CANCEL);
		cancelButton.addActionListener(new IdAction(UIService.class
				.getCanonicalName(), UIService.ACTION_CANCEL, this));
		buttonPanel.addButton(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}
}
