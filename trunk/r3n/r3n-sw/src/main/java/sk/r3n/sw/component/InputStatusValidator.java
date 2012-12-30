package sk.r3n.sw.component;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.ui.MessageType;
import sk.r3n.util.BundleResolver;

public class InputStatusValidator {

    private static final String NOT_VALID = "NOT_VALID";

    private List<InputComponent> components;

    public InputStatusValidator() {
        super();
        components = new ArrayList<>();
    }

    public void add(InputComponent inputComponent) {
        components.add(inputComponent);
    }

    public void remove(InputComponent inputComponent) {
        components.remove(inputComponent);
    }

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
                                BundleResolver.resolve(InputStatusValidator.class.getCanonicalName(), NOT_VALID),
                                MessageType.WARNING);
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
