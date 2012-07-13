package sk.r3n.ui.component;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JCheckBox;

import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public class R3NCheckBox extends JCheckBox {

	private static final long serialVersionUID = 7832131890198634514L;

	public R3NCheckBox() {
		this(null);
	}

	public R3NCheckBox(String text) {
		super(text);
		// Modifikacia klavesovych skratiek
		UIServiceManager.getDefaultUIService().setKeyStroke(
				UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK,
				WHEN_FOCUSED, this, new Action() {

					public void actionPerformed(ActionEvent e) {
						R3NCheckBox.this.transferFocus();
					}

					public void setEnabled(boolean b) {
					}

					public void removePropertyChangeListener(
							PropertyChangeListener listener) {
					}

					public void putValue(String key, Object value) {
					}

					public boolean isEnabled() {
						return true;
					}

					public Object getValue(String key) {
						return null;
					}

					public void addPropertyChangeListener(
							PropertyChangeListener listener) {
					}
				});
		// Fokus
		UIServiceManager.getDefaultUIService().modifyFocus(this);
	}

}
