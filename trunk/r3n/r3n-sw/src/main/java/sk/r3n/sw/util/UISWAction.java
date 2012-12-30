package sk.r3n.sw.util;

import sk.r3n.util.BundleResolver;

public enum UISWAction implements UIActionKey {

    CLOSE(UISWAction.class.getCanonicalName(), 0),
    RESTART(UISWAction.class.getCanonicalName(), 1),
    REFRESH(UISWAction.class.getCanonicalName(), 2),
    OK(UISWAction.class.getCanonicalName(), 10),
    CANCEL(UISWAction.class.getCanonicalName(), 11),
    YES(UISWAction.class.getCanonicalName(), 12),
    NO(UISWAction.class.getCanonicalName(), 13),
    UP(UISWAction.class.getCanonicalName(), 20),
    DOWN(UISWAction.class.getCanonicalName(), 21),
    LEFT(UISWAction.class.getCanonicalName(), 22),
    RIGHT(UISWAction.class.getCanonicalName(), 23),
    FIRST(UISWAction.class.getCanonicalName(), 40),
    PREVIOUS(UISWAction.class.getCanonicalName(), 41),
    NEXT(UISWAction.class.getCanonicalName(), 42),
    LAST(UISWAction.class.getCanonicalName(), 43),
    PREVIOUS_ROWS(UISWAction.class.getCanonicalName(), 45),
    NEXT_ROWS(UISWAction.class.getCanonicalName(), 46),
    ADD(UISWAction.class.getCanonicalName(), 70),
    COPY(UISWAction.class.getCanonicalName(), 71),
    EDIT(UISWAction.class.getCanonicalName(), 72),
    REMOVE(UISWAction.class.getCanonicalName(), 73),
    SELECT(UISWAction.class.getCanonicalName(), 110),
    PREVIEW(UISWAction.class.getCanonicalName(), 111),
    PRINT(UISWAction.class.getCanonicalName(), 112),
    PROPERTIES(UISWAction.class.getCanonicalName(), 120),
    SEARCH(UISWAction.class.getCanonicalName(), 130),
    FILE_NEW(UISWAction.class.getCanonicalName(), 140),
    FILE_OPEN(UISWAction.class.getCanonicalName(), 141),
    FILE_SAVE(UISWAction.class.getCanonicalName(), 142),
    FILE_SAVE_AS(UISWAction.class.getCanonicalName(), 143),
    FILE_DELETE(UISWAction.class.getCanonicalName(), 144),
    DIR_NEW(UISWAction.class.getCanonicalName(), 150),
    DIR_OPEN(UISWAction.class.getCanonicalName(), 151),
    DIR_DELETE(UISWAction.class.getCanonicalName(), 152),
    DEFAULT(UISWAction.class.getCanonicalName(), 160),
    ENABLE(UISWAction.class.getCanonicalName(), 165),
    DISABLE(UISWAction.class.getCanonicalName(), 166),
    ABOUT(UISWAction.class.getCanonicalName(), 170),
    HELP(UISWAction.class.getCanonicalName(), 171),
    LICENSE(UISWAction.class.getCanonicalName(), 172),
    LOG_PREVIEW(UISWAction.class.getCanonicalName(), 173);
    
    private static final String BUNDLE = UISWAction.class.getCanonicalName();

    private final String group;

    private final int code;

    private UISWAction(String group, int code) {
        this.group = group;
        this.code = code;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String actionName() {
        return BundleResolver.resolve(BUNDLE, name());
    }

}
