package sk.r3n.sw.component;

import javax.swing.*;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.sw.util.UIActionListener;
import sk.r3n.ui.IconType;
import sk.r3n.ui.UIActionExecutor;
import sk.r3n.ui.UIActionKey;

public class R3NButton extends JButton {

    public R3NButton(UIActionKey actionKey) {
        super();
        Icon icon = SwingUtil.getIcon(actionKey, IconType.ENABLED);
        String text = actionKey.actionName();
        if (icon != null) {
            setIcon(icon);
        } else {
            setText(text);
        }
        setToolTipText(text);

        icon = SwingUtil.getIcon(actionKey, IconType.DISABLED);
        if (icon != null) {
            setDisabledIcon(icon);
        }

        icon = SwingUtil.getIcon(actionKey, IconType.PRESSED);
        if (icon != null) {
            setPressedIcon(icon);
        }
    }

    public R3NButton(UIActionKey actionKey, UIActionExecutor actionExecutor) {
        this(actionKey);
        addActionListener(new UIActionListener(actionKey, actionExecutor));
    }

}