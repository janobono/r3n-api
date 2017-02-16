/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

public class Criterion implements CriteriaContent {

    private Column column;

    private Condition condition;

    private Object value;

    private String representation;

    private Operator operator;

    public Criterion(Column column, Condition condition, Object value, String representation, Operator operator) {
        this.column = column;
        this.condition = condition;
        this.value = value;
        this.representation = representation;
        this.operator = operator;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "Criterion{" + "column=" + column + ", condition=" + condition + ", value=" + value
                + ", representation=" + representation + ", operator=" + operator + '}';
    }

}
