package sk.r3n.ui.component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;

import sk.r3n.ui.util.UIServiceManager;

public class R3NTextArea extends JTextArea implements FocusListener {

	private static final long serialVersionUID = -2780456938663066090L;

	public R3NTextArea() {
		super();
		addFocusListener(this);
		// Fokus
		UIServiceManager.getDefaultUIService().modifyFocus(this);
	}

	public void focusGained(FocusEvent e) {
		if (isFocusable()) {
			selectAll();
		}
	}

	public void focusLost(FocusEvent e) {
	}

}
