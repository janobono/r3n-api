package sk.r3n.swing.component;

import javax.swing.JRadioButton;

public abstract class R3NRadioButton<T> extends JRadioButton {

    private T value;

    public R3NRadioButton(T value) {
        super();
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
