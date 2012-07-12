package sk.r3n.ui.util;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import sk.r3n.action.IdActionExecutor;
import sk.r3n.ui.R3NInputComponent;
import sk.r3n.ui.UIService;

public abstract class R3NFrame extends JFrame implements IdActionExecutor,
		WindowListener {

	private static final long serialVersionUID = -4647620219794823452L;

	private List<R3NInputComponent<?>> inputComponents;

	public R3NFrame() {
		super();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setLayout(new BorderLayout());
		inputComponents = new ArrayList<R3NInputComponent<?>>();
	}

	public List<R3NInputComponent<?>> getInputComponents() {
		return inputComponents;
	}

	public boolean isInputValid() {
		return UIServiceManager.getDefaultUIService().isInputValid(
				inputComponents);
	}

	public abstract void refreshUI();

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			UIServiceManager.getDefaultUIService().positionCenterWindow(
					getOwner(), this);
		}
		super.setVisible(visible);
	}

	public void windowActivated(WindowEvent windowEvent) {
	}

	public void windowClosed(WindowEvent windowEvent) {
	}

	public void windowClosing(WindowEvent windowEvent) {
		execute(UIService.class.getCanonicalName(), UIService.ACTION_CLOSE,
				windowEvent.getSource());
	}

	public void windowDeactivated(WindowEvent windowEvent) {
	}

	public void windowDeiconified(WindowEvent windowEvent) {
	}

	public void windowIconified(WindowEvent windowEvent) {
	}

	public void windowOpened(WindowEvent windowEvent) {
	}
}
