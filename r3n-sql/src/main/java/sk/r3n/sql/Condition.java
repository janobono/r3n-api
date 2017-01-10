package sk.r3n.sql;

public enum Condition {

    EQUALS(" = "),
    EQUALS_MORE(" >= "),
    EQUALS_LESS(" <= "),
    EQUALS_NOT(" != "),
    MORE(" > "),
    LESS(" < "),
    LIKE(" like "),
    NOT_LIKE(" not like "),
    IS_NULL(" is null "),
    IS_NOT_NULL(" is not null "),
    IN(" in "),
    NOT_IN(" not in "),
    DIRECT("");

    private final String condition;

    public static Condition byCondition(String condition) {
        Condition result = null;
        for (Condition cond : Condition.values()) {
            if (cond.condition().equals(condition)) {
                result = cond;
                break;
            }
        }
        return result;
    }

    Condition(String condition) {
        this.condition = condition;
    }

    public String condition() {
        return condition;
    }
}
