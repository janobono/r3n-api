package sk.r3n.db;

public class SQLCondition {

    protected SQLColumn column;
    protected short condition;
    protected short operator;
    protected String expression;

    public SQLCondition(SQLColumn column, short condition, short operator) {
        this(column, null, condition, operator);
    }

    public SQLCondition(SQLColumn column, String expression, short condition,
            short operator) {
        super();
        this.column = column;
        this.expression = expression;
        this.condition = condition;
        this.operator = operator;
    }

    public SQLColumn getColumn() {
        return column;
    }

    public short getCondition() {
        return condition;
    }

    public String getExpression() {
        return expression;
    }

    public short getOperator() {
        return operator;
    }

    public void setColumn(SQLColumn column) {
        this.column = column;
    }

    public void setCondition(short condition) {
        this.condition = condition;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setOperator(short operator) {
        this.operator = operator;
    }
}
