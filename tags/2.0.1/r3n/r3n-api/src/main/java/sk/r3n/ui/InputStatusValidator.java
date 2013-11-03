package sk.r3n.ui;

import java.util.ArrayList;
import java.util.List;

public abstract class InputStatusValidator {

    protected List<InputComponent> components;

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

    public abstract boolean isInputValid();

}
