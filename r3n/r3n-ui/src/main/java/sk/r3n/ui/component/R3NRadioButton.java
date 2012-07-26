package sk.r3n.ui.component;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JRadioButton;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public abstract class R3NRadioButton<T> extends JRadioButton {

    private T value;

    public R3NRadioButton(T value) {
        super();
        // Modifikacia klavesovych skratiek
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK,
                WHEN_FOCUSED, this, new Action() {

            @Override
            public void actionPerformed(ActionEvent e) {
                R3NRadioButton.this.transferFocus();
            }

            @Override
            public void setEnabled(boolean b) {
            }

            @Override
            public void removePropertyChangeListener(
                    PropertyChangeListener listener) {
            }

            @Override
            public void putValue(String key, Object value) {
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void addPropertyChangeListener(
                    PropertyChangeListener listener) {
            }
        });
        // Fokus
        UIServiceManager.getDefaultUIService().modifyFocus(this);
        this.value = value;
        if (value != null) {
            setNameFromValue(value);
        }
    }

    public T getValue() {
        return value;
    }

    protected abstract void setNameFromValue(T value);
}
