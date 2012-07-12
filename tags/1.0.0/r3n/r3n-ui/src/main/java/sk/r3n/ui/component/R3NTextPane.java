package sk.r3n.ui.component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextPane;

import sk.r3n.ui.util.UIServiceManager;

public class R3NTextPane extends JTextPane implements FocusListener {

	private static final long serialVersionUID = -8755785488471601370L;

	public R3NTextPane() {
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
