package sk.r3n.sw.util;

import java.awt.Toolkit;
import javax.swing.JComponent;
import sk.r3n.ui.InputComponent;
import sk.r3n.ui.InputStatus;
import sk.r3n.ui.InputStatusValidator;
import sk.r3n.ui.MessageType;
import sk.r3n.util.BundleResolver;

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
                                BundleResolver.resolve(SWInputStatusValidator.class.getCanonicalName(), NOT_VALID),
                                MessageType.WARNING);
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
