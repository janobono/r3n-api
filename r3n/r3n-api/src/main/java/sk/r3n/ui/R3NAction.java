package sk.r3n.ui;

import sk.r3n.util.BundleResolver;

public enum R3NAction implements UIActionKey {

    CLOSE(R3NAction.class.getCanonicalName(), 0),
    RESTART(R3NAction.class.getCanonicalName(), 1),
    REFRESH(R3NAction.class.getCanonicalName(), 2),
    OK(R3NAction.class.getCanonicalName(), 10),
    CANCEL(R3NAction.class.getCanonicalName(), 11),
    YES(R3NAction.class.getCanonicalName(), 12),
    NO(R3NAction.class.getCanonicalName(), 13),
    UP(R3NAction.class.getCanonicalName(), 20),
    DOWN(R3NAction.class.getCanonicalName(), 21),
    LEFT(R3NAction.class.getCanonicalName(), 22),
    RIGHT(R3NAction.class.getCanonicalName(), 23),
    FIRST(R3NAction.class.getCanonicalName(), 40),
    PREVIOUS(R3NAction.class.getCanonicalName(), 41),
    NEXT(R3NAction.class.getCanonicalName(), 42),
    LAST(R3NAction.class.getCanonicalName(), 43),
    PREVIOUS_ROWS(R3NAction.class.getCanonicalName(), 45),
    NEXT_ROWS(R3NAction.class.getCanonicalName(), 46),
    ADD(R3NAction.class.getCanonicalName(), 70),
    COPY(R3NAction.class.getCanonicalName(), 71),
    EDIT(R3NAction.class.getCanonicalName(), 72),
    REMOVE(R3NAction.class.getCanonicalName(), 73),
    SELECT(R3NAction.class.getCanonicalName(), 110),
    PREVIEW(R3NAction.class.getCanonicalName(), 111),
    PRINT(R3NAction.class.getCanonicalName(), 112),
    PROPERTIES(R3NAction.class.getCanonicalName(), 120),
    SEARCH(R3NAction.class.getCanonicalName(), 130),
    FILE_NEW(R3NAction.class.getCanonicalName(), 140),
    FILE_OPEN(R3NAction.class.getCanonicalName(), 141),
    FILE_SAVE(R3NAction.class.getCanonicalName(), 142),
    FILE_SAVE_AS(R3NAction.class.getCanonicalName(), 143),
    FILE_DELETE(R3NAction.class.getCanonicalName(), 144),
    DIR_NEW(R3NAction.class.getCanonicalName(), 150),
    DIR_OPEN(R3NAction.class.getCanonicalName(), 151),
    DIR_DELETE(R3NAction.class.getCanonicalName(), 152),
    DEFAULT(R3NAction.class.getCanonicalName(), 160),
    ENABLE(R3NAction.class.getCanonicalName(), 165),
    DISABLE(R3NAction.class.getCanonicalName(), 166),
    ABOUT(R3NAction.class.getCanonicalName(), 170),
    HELP(R3NAction.class.getCanonicalName(), 171),
    LICENSE(R3NAction.class.getCanonicalName(), 172),
    LOG_PREVIEW(R3NAction.class.getCanonicalName(), 173);

    private static final String BUNDLE = R3NAction.class.getCanonicalName();

    private final String group;

    private final int code;

    private R3NAction(String group, int code) {
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
