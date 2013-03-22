package sk.r3n.search;

public enum SearchCondition {

    EQUALS("="),
    EQUALS_MORE(">="),
    EQUALS_LESS("<="),
    EQUALS_NOT("!="),
    MORE(">"),
    LESS("<"),
    LIKE("like"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null"),
    IN("in"),
    NOT_IN("not in");

    private String textValue;

    public static SearchCondition byTextValue(String textValue) {
        SearchCondition result = null;
        for (SearchCondition condition : SearchCondition.values()) {
            if (condition.textValue().equals(textValue)) {
                result = condition;
                break;
            }
        }
        return result;
    }

    SearchCondition(String textValue) {
        this.textValue = textValue;
    }

    public String textValue() {
        return textValue;
    }
}
