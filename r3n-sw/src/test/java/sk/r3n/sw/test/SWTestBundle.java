package sk.r3n.sw.test;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public enum SWTestBundle {
    
    MAIN_TITLE,
    STARTUP_TEST,
    STARTUP_TEST_P,
    CONFIG_TAB,
    LOCALE,
    LOOK_AND_FEEL,
    LOOK_AND_FEEL_DECORATED,
    CONFIG_INFO,
    FILE_OPEN_TAB,
    FILE_SAVE_TAB,
    FILTER,
    DEFAULT_DIRECTORY,
    TITLE,
    FILE_NAME,
    RESULT,
    MESSAGE_TAB,
    TYPE,
    MESSAGE,
    QUESTION_TAB,
    YES_NO_CANCEL,
    QUESTION,
    ACTION_TAB,
    TABLE_TAB,
    TREE_TAB,
    COMPONENT_TAB,
    DATE,
    MONTH,
    TIME,
    TIMESTAMP,
    BIG_DECIMAL,
    LONG,
    INTEGER,
    SHORT,
    VARCHAR,
    DIR,
    FILE;
    
    public String value() {
        return ResourceBundle.getBundle(SWTestBundle.class.getCanonicalName()).getString(name());
    }
    
    public String value(Object[] parameters) {
        return MessageFormat.format(value(), parameters);
    }
}
