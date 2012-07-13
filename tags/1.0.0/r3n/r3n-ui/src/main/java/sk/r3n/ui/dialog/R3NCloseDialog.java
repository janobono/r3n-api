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

public class R3NCloseDialog extends R3NDialog {

	private static final long serialVersionUID = 2213986741039088458L;

	protected R3NButton closeButton;

	public R3NCloseDialog() {
		super();
		init();
	}

	public R3NCloseDialog(Frame frame) {
		super(frame);
		init();
	}

	private void init() {
		UIServiceManager.getDefaultUIService().setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
				(JPanel) getContentPane(), this);

		ButtonPanel buttonPanel = new ButtonPanel(1, 1);
		closeButton = new R3NButton(UIService.class.getCanonicalName(),
				UIService.ACTION_CLOSE);
		closeButton.addActionListener(new IdAction(UIService.class
				.getCanonicalName(), UIService.ACTION_CLOSE, this));
		buttonPanel.addButton(closeButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

}
