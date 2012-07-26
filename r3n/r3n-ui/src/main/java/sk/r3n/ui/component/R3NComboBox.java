package sk.r3n.ui.component;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import sk.r3n.ui.UIService;
import sk.r3n.ui.util.UIServiceManager;

public class R3NComboBox<T> extends JComboBox<T> {

    private Action togglePopup;

    public R3NComboBox() {
        super();
        // Modifikacia klavesovych skratiek
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke[] ks = im.allKeys();
        InputMap im2 = new InputMap();
        if (ks != null) {
            for (int x = 0; x < ks.length; x++) {
                if (ks[x].equals(KeyStroke.getKeyStroke("pressed DOWN"))) {
                    UIServiceManager.getDefaultUIService().setKeyStroke(
                            UIService.class.getCanonicalName(),
                            UIService.ACTION_DOWN, im2, im.get(ks[x]));
                    UIServiceManager.getDefaultUIService().setKeyStroke(
                            UIService.class.getCanonicalName(),
                            UIService.ACTION_NEXT, im2, im.get(ks[x]));
                }
                if (ks[x].equals(KeyStroke.getKeyStroke("pressed UP"))) {
                    UIServiceManager.getDefaultUIService().setKeyStroke(
                            UIService.class.getCanonicalName(),
                            UIService.ACTION_UP, im2, im.get(ks[x]));
                    UIServiceManager.getDefaultUIService().setKeyStroke(
                            UIService.class.getCanonicalName(),
                            UIService.ACTION_PREVIOUS, im2, im.get(ks[x]));
                }
                if (ks[x].equals(KeyStroke.getKeyStroke("pressed HOME"))) {
                    UIServiceManager.getDefaultUIService().setKeyStroke(
                            UIService.class.getCanonicalName(),
                            UIService.ACTION_FIRST, im2, im.get(ks[x]));
                }
                if (ks[x].equals(KeyStroke.getKeyStroke("alt pressed UP"))) {
                    togglePopup = getActionMap().get(im.get(ks[x]));
                }
                if (ks[x].equals(KeyStroke.getKeyStroke("pressed END"))) {
                    UIServiceManager.getDefaultUIService().setKeyStroke(
                            UIService.class.getCanonicalName(),
                            UIService.ACTION_LAST, im2, im.get(ks[x]));
                }
            }
            setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im2);
        }
        UIServiceManager.getDefaultUIService().setKeyStroke(
                UIService.class.getCanonicalName(), UIService.ACTION_CELL_OK,
                WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this, new Action() {

            @Override
            public void actionPerformed(ActionEvent e) {
                R3NComboBox.this.transferFocus();
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
        // Modifikácia fokusov
        UIServiceManager.getDefaultUIService().modifyFocus(this);
    }

    public void togglePopup() {
        togglePopup.actionPerformed(new ActionEvent(this,
                ActionEvent.ACTION_PERFORMED, ""));
    }
}
