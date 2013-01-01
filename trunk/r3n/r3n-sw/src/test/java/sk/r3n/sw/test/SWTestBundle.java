package sk.r3n.sw.test;

import sk.r3n.util.BundleEnum;
import sk.r3n.util.BundleResolver;

public enum SWTestBundle implements BundleEnum {

    MAIN_TITLE,
    STARTUP_TEST,
    STARTUP_TEST_P,
    
    CONFIG_TAB,
    LOCALE,
    LOOK_AND_FEEL,
    LOOK_AND_FEEL_DECORATED,
    COEFICIENT,
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
    
    TABLE_TAB;
    
        
    @Override
    public String value() {
        return BundleResolver.resolve(SWTestBundle.class.getCanonicalName(), name());
    }

    @Override
    public String value(Object[] parameters) {
        return BundleResolver.resolve(SWTestBundle.class.getCanonicalName(), name(), parameters);
    }

}
