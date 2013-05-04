package sk.r3n.jdbc.query;

public enum QueryCondition {

    EQUALS(" = "),
    EQUALS_MORE(" >= "),
    EQUALS_LESS(" <= "),
    EQUALS_NOT(" != "),
    MORE(" > "),
    LESS(" < "),
    LIKE(" LIKE "),
    LIKE_SCDF(" LIKE "),
    IS_NULL(" IS NULL "),
    IS_NOT_NULL(" IS NOT NULL "),
    IN(" IN "),
    NOT_IN(" NOT IN ");

    private final String condition;

    public static QueryCondition byCondition(String condition) {
        QueryCondition result = null;
        for (QueryCondition cond : QueryCondition.values()) {
            if (cond.condition().equals(condition)) {
                result = cond;
                break;
            }
        }
        return result;
    }

    QueryCondition(String condition) {
        this.condition = condition;
    }

    public String condition() {
        return condition;
    }
}
