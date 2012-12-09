package sk.r3n.sw.util;

public enum UIServiceAction implements ActionKey {

    CLOSE(UIService.class.getCanonicalName(), 0),
    RESTART(UIService.class.getCanonicalName(), 1),
    REFRESH(UIService.class.getCanonicalName(), 2),
    OK(UIService.class.getCanonicalName(), 10),
    CANCEL(UIService.class.getCanonicalName(), 11),
    YES(UIService.class.getCanonicalName(), 12),
    NO(UIService.class.getCanonicalName(), 13),
    UP(UIService.class.getCanonicalName(), 20),
    DOWN(UIService.class.getCanonicalName(), 21),
    LEFT(UIService.class.getCanonicalName(), 22),
    RIGHT(UIService.class.getCanonicalName(), 23),
    FIRST(UIService.class.getCanonicalName(), 40),
    PREVIOUS(UIService.class.getCanonicalName(), 41),
    NEXT(UIService.class.getCanonicalName(), 42),
    LAST(UIService.class.getCanonicalName(), 43),
    PREVIOUS_ROWS(UIService.class.getCanonicalName(), 45),
    NEXT_ROWS(UIService.class.getCanonicalName(), 46),
    ADD(UIService.class.getCanonicalName(), 70),
    COPY(UIService.class.getCanonicalName(), 71),
    EDIT(UIService.class.getCanonicalName(), 72),
    REMOVE(UIService.class.getCanonicalName(), 73),
    SELECT(UIService.class.getCanonicalName(), 110),
    PREVIEW(UIService.class.getCanonicalName(), 111),
    PRINT(UIService.class.getCanonicalName(), 112),
    PROPERTIES(UIService.class.getCanonicalName(), 120),
    SEARCH(UIService.class.getCanonicalName(), 130),
    SWITCH_SEARCH_KEY(UIService.class.getCanonicalName(), 131),
    FILE_NEW(UIService.class.getCanonicalName(), 140),
    FILE_OPEN(UIService.class.getCanonicalName(), 141),
    FILE_SAVE(UIService.class.getCanonicalName(), 142),
    FILE_SAVE_AS(UIService.class.getCanonicalName(), 143),
    FILE_DELETE(UIService.class.getCanonicalName(), 144),
    DIR_NEW(UIService.class.getCanonicalName(), 150),
    DIR_OPEN(UIService.class.getCanonicalName(), 151),
    DIR_DELETE(UIService.class.getCanonicalName(), 152),
    DEFAULT(UIService.class.getCanonicalName(), 160),
    ENABLE(UIService.class.getCanonicalName(), 165),
    DISABLE(UIService.class.getCanonicalName(), 166),
    ABOUT(UIService.class.getCanonicalName(), 170),
    HELP(UIService.class.getCanonicalName(), 171),
    LICENSE(UIService.class.getCanonicalName(), 172),
    LOG_PREVIEW(UIService.class.getCanonicalName(), 173);

    private final String group;

    private final int code;

    private UIServiceAction(String group, int code) {
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

}
