package sk.r3n.sw;

public interface InputComponent<T> {

    T getValue();

    void setValue(T value);

    boolean isContentNull();

    InputStatus inputStatus();
}
