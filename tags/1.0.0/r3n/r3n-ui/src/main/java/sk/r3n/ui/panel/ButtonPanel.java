package sk.r3n.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

import sk.r3n.ui.UIService;
import sk.r3n.ui.component.R3NButton;
import sk.r3n.ui.util.UIServiceManager;

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = -2314578457438081030L;

	private int columns;
	private int rows;

	private int actual_column;
	private int actual_row;

	private KeyStroke backward;
	private KeyStroke forward;

	public ButtonPanel(int rows, int columns) {
		super(new GridBagLayout());
		this.rows = rows;
		this.columns = columns;
		actual_row = 0;
		actual_column = 0;
		if (rows <= columns) {
			this.backward = UIServiceManager
					.getDefaultUIService()
					.getIdActionService()
					.getKeyStroke(UIService.class.getCanonicalName(),
							UIService.ACTION_LEFT);
			this.forward = UIServiceManager
					.getDefaultUIService()
					.getIdActionService()
					.getKeyStroke(UIService.class.getCanonicalName(),
							UIService.ACTION_RIGHT);
		} else {
			this.backward = UIServiceManager
					.getDefaultUIService()
					.getIdActionService()
					.getKeyStroke(UIService.class.getCanonicalName(),
							UIService.ACTION_UP);
			this.forward = UIServiceManager
					.getDefaultUIService()
					.getIdActionService()
					.getKeyStroke(UIService.class.getCanonicalName(),
							UIService.ACTION_DOWN);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addButton(R3NButton button) {
		if (actual_row < rows) {
			this.add(button, new GridBagConstraints(actual_column, actual_row,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			actual_column++;
			if (actual_column == columns) {
				actual_column = 0;
				actual_row++;
			}
			button.setFocusTraversalKeysEnabled(true);
			if (backward != null && forward != null) {
				Set keys = new HashSet();
				keys.addAll(button
						.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
				keys.add(backward);
				button.setFocusTraversalKeys(
						KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, keys);
				keys.clear();
				keys.addAll(button
						.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
				keys.add(forward);
				button.setFocusTraversalKeys(
						KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);
			}
		}
	}

}
