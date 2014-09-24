package sk.r3n.sw.util;

import java.util.ArrayList;
import java.util.List;

public abstract class InputStatusValidator {

    protected List<InputComponent> components;

    public InputStatusValidator() {
        super();
        components = new ArrayList<InputComponent>();
    }

    public void add(InputComponent inputComponent) {
        components.add(inputComponent);
    }

    public void remove(InputComponent inputComponent) {
        components.remove(inputComponent);
    }

    public abstract boolean isInputValid();

}
