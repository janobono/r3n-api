package sk.r3n.app;

public interface AppProperties {

    public String get(AppProperty appProperty);

    public String get(String key, String defaultValue);

    public void set(String key, String value);

    public String decrypt(String string);

    public String encrypt(String string);

}
