/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

/**
 * Supported SQL conditions.
 *
 * @author janobono
 * @since 18 August 2014
 */
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

    public static Condition byCondition(final String condition) {
        Condition result = null;
        for (final Condition cond : Condition.values()) {
            if (cond.condition().equals(condition)) {
                result = cond;
                break;
            }
        }
        return result;
    }

    Condition(final String condition) {
        this.condition = condition;
    }

    public String condition() {
        return condition;
    }
}
