/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Criteria management object.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class CriteriaManager implements Serializable {

    private Criteria criteria;

    private final List<Criteria> criteriaList;

    public CriteriaManager() {
        super();
        criteriaList = new LinkedList<>();
        criteria = new Criteria();
        criteriaList.add(criteria);
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public void addCriterion(final Column column, final Condition condition, final Object value, final String representation, final Operator operator) {
        setLastOperator(operator);
        criteria.addCriterion(column, condition, value, representation, Operator.AND);
    }

    public void next(final Operator operator) {
        criteria.setOperator(operator);
        final Criteria next = new Criteria();
        if (criteria.getParent() == null) {
            criteriaList.add(next);
        } else {
            next.setParent(criteria.getParent());
            criteria.getParent().getContent().add(next);
        }
        criteria = next;
    }

    public void in(final Operator operator) {
        setLastOperator(operator);
        final Criteria in = new Criteria();
        criteria.getContent().add(in);
        in.setParent(criteria);
        criteria = in;
    }

    public void out() {
        final Criteria out = criteria.getParent();
        if (out != null) {
            criteria = out;
        }
    }

    private void setLastOperator(final Operator operator) {
        if (!criteria.getContent().isEmpty()) {
            final Object object = criteria.getContent().getLast();
            if (object instanceof Criterion) {
                ((Criterion) object).setOperator(operator);
            } else {
                ((Criteria) object).setOperator(operator);
            }
        }
    }

    public boolean isCriteria() {
        boolean result = false;
        for (final Criteria c : criteriaList) {
            result = c.isCriteria();
            if (result) {
                break;
            }
        }
        return result;
    }
}
