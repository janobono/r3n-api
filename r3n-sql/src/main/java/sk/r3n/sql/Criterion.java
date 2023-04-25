/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

/**
 * Criterion object.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class Criterion implements CriteriaContent {

    private Column column;

    private Condition condition;

    private Object value;

    private String representation;

    private Operator operator;

    public Criterion(final Column column, final Condition condition, final Object value, final String representation, final Operator operator) {
        this.column = column;
        this.condition = condition;
        this.value = value;
        this.representation = representation;
        this.operator = operator;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(final Column column) {
        this.column = column;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(final Condition condition) {
        this.condition = condition;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(final String representation) {
        this.representation = representation;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "Criterion{" +
                "column=" + column +
                ", condition=" + condition +
                ", value=" + value +
                ", representation='" + representation + '\'' +
                ", operator=" + operator +
                '}';
    }
}
