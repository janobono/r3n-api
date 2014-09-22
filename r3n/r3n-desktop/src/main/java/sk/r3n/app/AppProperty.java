package sk.r3n.app;

public enum AppProperty {

    APP_NAME("sk.r3n.app.NAME"),
    APP_VERSION("sk.r3n.app.VERSION"),
    APP_ICON("sk.r3n.app.ICON"),
    APP_LOGO("sk.r3n.app.LOGO"),
    APP_DATA_DIR("sk.r3n.app.DATA_DIR"),
    PROPERTIES_FILE("sk.r3n.app.PROPERTIES_FILE"),
    HELP_DIR("sk.r3n.app.HELP_DIR"),
    HELP_MAP("sk.r3n.app.HELP_MAP");

    private final String code;

    private AppProperty(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

}
