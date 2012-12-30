package sk.r3n.sw.component;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import sk.r3n.sw.util.MessageType;
import sk.r3n.sw.util.SwingUtil;
import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public class InputStatusValidator {

    private enum Bundle implements BundleEnum {

        NO_VALID;

        @Override
        public String value() {
            return BundleResolver.resolve(InputStatusValidator.class.getCanonicalName(), name());
        }

        @Override
        public String value(Object[] parameters) {
            return BundleResolver.resolve(InputStatusValidator.class.getCanonicalName(), name(), parameters);
        }

    }
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
                        SwingUtil.showMessageDialog(null, Bundle.NO_VALID.value(), MessageType.WARNING);
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
