package sk.r3n.sql;

public enum Condition {

    EQUALS(" = "),
    EQUALS_MORE(" >= "),
    EQUALS_LESS(" <= "),
    EQUALS_NOT(" != "),
    MORE(" > "),
    LESS(" < "),
    LIKE(" LIKE "),
    NOT_LIKE(" NOT LIKE "),
    IS_NULL(" IS NULL "),
    IS_NOT_NULL(" IS NOT NULL "),
    IN(" IN "),
    NOT_IN(" NOT IN "),
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
