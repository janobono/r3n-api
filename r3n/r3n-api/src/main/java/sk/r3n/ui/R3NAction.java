package sk.r3n.ui;

import sk.r3n.util.BundleResolver;

public enum R3NAction implements UIActionKey {

    CLOSE(0),
    RESTART(1),
    REFRESH(2),
    OK(10),
    CANCEL(11),
    YES(12),
    NO(13),
    UP(20),
    DOWN(21),
    LEFT(22),
    RIGHT(23),
    FIRST(40),
    PREVIOUS(41),
    NEXT(42),
    LAST(43),
    PREVIOUS_ROWS(45),
    NEXT_ROWS(46),
    ADD(70),
    COPY(71),
    EDIT(72),
    REMOVE(73),
    SELECT(110),
    PREVIEW(111),
    PRINT(112),
    PROPERTIES(120),
    SEARCH(130),
    FILE_NEW(140),
    FILE_OPEN(141),
    FILE_SAVE(142),
    FILE_SAVE_AS(143),
    FILE_DELETE(144),
    DIR_NEW(150),
    DIR_OPEN(151),
    DIR_DELETE(152),
    DEFAULT(160),
    ENABLE(165),
    DISABLE(166),
    ABOUT(170),
    HELP(171),
    LICENSE(172),
    LOG_PREVIEW(173);

    private static final String BUNDLE = R3NAction.class.getCanonicalName();

    private final int code;

    private R3NAction(int code) {
        this.code = code;
    }

    @Override
    public String group() {
        return R3NAction.class.getCanonicalName();
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
