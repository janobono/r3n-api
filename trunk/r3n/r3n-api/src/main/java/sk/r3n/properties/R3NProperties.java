package sk.r3n.properties;

public interface R3NProperties {

    public String FILE_NAME = "sk.r3n.properties.R3NProperties.file_name";

    public String get(String key, String defaultValue);

    public void set(String key, String value);

    public String decrypt(String string);

    public String encrypt(String string);
}
