package sk.r3n.ui.component;

import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public class R3NButton extends JButton {

	private static final long serialVersionUID = 2437454286290859927L;

	public R3NButton() {
		super();
		// Modifikacia klavesovych skratiek
		KeyStroke keyStroke = UIServiceManager
				.getDefaultUIService()
				.getIdActionService()
				.getKeyStroke(UIService.class.getCanonicalName(),
						UIService.ACTION_BUTTON);
		if (keyStroke != null
				&& (keyStroke.getKeyCode() != KeyEvent.VK_SPACE || keyStroke
						.getModifiers() != 0)) {
			InputMap im = getInputMap(JComponent.WHEN_FOCUSED);
			KeyStroke[] ks = im.allKeys();
			InputMap im2 = new InputMap();
			if (ks != null) {
				for (int x = 0; x < ks.length; x++) {
					if (ks[x].equals(KeyStroke.getKeyStroke("pressed SPACE"))) {
						im2.put(ks[x], im.get(ks[x]));
						im2.put(KeyStroke.getKeyStroke(keyStroke.getKeyCode(),
								keyStroke.getModifiers(), false), im.get(ks[x]));
					}
					if (ks[x].equals(KeyStroke.getKeyStroke("released SPACE"))) {
						im2.put(ks[x], im.get(ks[x]));
						im2.put(KeyStroke.getKeyStroke(keyStroke.getKeyCode(),
								keyStroke.getModifiers(), true), im.get(ks[x]));
					}
				}
				setInputMap(JComponent.WHEN_FOCUSED, im2);
			}
		}
		// Fokus
		UIServiceManager.getDefaultUIService().modifyFocus(this);
	}

	public R3NButton(String text) {
		this();
		setText(text);
	}

	public R3NButton(String groupId, int actionId) {
		this();
		Icon icon = null;
		URL url = UIServiceManager.getDefaultUIService().getIdActionService()
				.getIcon(groupId, actionId);
		if (url != null)
			icon = UIServiceManager.getDefaultUIService().getIcon(url);
		String text = UIServiceManager.getDefaultUIService()
				.getIdActionService().getName(groupId, actionId);
		if (icon != null) {
			setIcon(icon);
		} else {
			if (text != null)
				setText(text);
		}
		if (text != null)
			setToolTipText(text);

		icon = null;
		url = UIServiceManager.getDefaultUIService().getIdActionService()
				.getDisabledIcon(groupId, actionId);
		if (url != null)
			icon = UIServiceManager.getDefaultUIService().getIcon(url);
		if (icon != null)
			setDisabledIcon(icon);

		icon = null;
		url = UIServiceManager.getDefaultUIService().getIdActionService()
				.getPressedIcon(groupId, actionId);
		if (url != null)
			icon = UIServiceManager.getDefaultUIService().getIcon(url);
		if (icon != null)
			setPressedIcon(icon);
	}
}
