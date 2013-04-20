package sk.r3n.jdbc.search;

public enum Condition {

    EQUALS(" = "),
    EQUALS_MORE(" >= "),
    EQUALS_LESS(" <= "),
    EQUALS_NOT(" != "),
    MORE(" > "),
    LESS(" < "),
    LIKE(" like "),
    LIKE_SCDF(" like "),
    IS_NULL(" is null "),
    IS_NOT_NULL(" is not null "),
    IN(" in "),
    NOT_IN(" not in ");

    private String textValue;

    public static Condition byTextValue(String textValue) {
        Condition result = null;
        for (Condition condition : Condition.values()) {
            if (condition.textValue().equals(textValue)) {
                result = condition;
                break;
            }
        }
        return result;
    }

    Condition(String textValue) {
        this.textValue = textValue;
    }

    public String textValue() {
        return textValue;
    }
}
