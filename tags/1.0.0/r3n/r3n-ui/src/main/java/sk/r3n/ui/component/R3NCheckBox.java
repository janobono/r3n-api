package sk.r3n.ui.component;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JCheckBox;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public class R3NCheckBox extends JCheckBox {

    public R3NCheckBox() {
        this(null);
    }

    public R3NCheckBox(String text) {
        super(text);
        // Modifikacia klavesovych skratiek
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK,
                WHEN_FOCUSED, this, new Action() {

            @Override
            public void actionPerformed(ActionEvent e) {
                R3NCheckBox.this.transferFocus();
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
    }
}
