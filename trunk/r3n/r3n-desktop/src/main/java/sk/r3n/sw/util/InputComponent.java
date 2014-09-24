package sk.r3n.sw.util;

public interface InputComponent<T> {

    public T getValue();

    public boolean isContentNull();

    public InputStatus inputStatus();

    public void setValue(T value);
    
}
