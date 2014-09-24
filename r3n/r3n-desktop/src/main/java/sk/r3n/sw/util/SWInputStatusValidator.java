package sk.r3n.sw.util;

import java.awt.Toolkit;
import java.util.ResourceBundle;
import javax.swing.JComponent;

public class SWInputStatusValidator extends InputStatusValidator {

    private static final String NOT_VALID = "NOT_VALID";

    @Override
    public boolean isInputValid() {
        for (InputComponent inputComponent : components) {
            InputStatus inputStatus = inputComponent.inputStatus();
            if (!inputStatus.equals(InputStatus.VALID)) {
                if (inputComponent instanceof JComponent) {
                    JComponent component = (JComponent) inputComponent;
                    if (component.isEnabled()) {
                        if (component.isFocusable()) {
                            component.requestFocus();
                        }
                        Toolkit.getDefaultToolkit().beep();
                        SwingUtil.showMessageDialog(null,
                                ResourceBundle.getBundle(SWInputStatusValidator.class.getCanonicalName())
                                .getString(NOT_VALID),
                                MessageType.WARNING);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
