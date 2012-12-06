package sk.r3n.swing.component;

public interface R3NInputComponent<T> {

    public int VALID = 0;
    public int SIZE = 10;
    public int NULL = 20;
    public int FORMAT = 30;
    public int SCOPE = 40;

    public T getValue();

    public boolean isContentNull();

    public int contentValid();

    public void setValue(T value);
}
