package sk.r3n.ui;

public interface InputComponent<T> {

    public T getValue();

    public boolean isContentNull();

    public InputStatus inputStatus();

    public void setValue(T value);
    
}
