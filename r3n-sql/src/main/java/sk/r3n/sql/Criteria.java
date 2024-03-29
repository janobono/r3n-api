/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.sql;

import sk.r3n.sql.impl.ColumnBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Criteria definition object.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class Criteria implements CriteriaContent {

    private final List<CriteriaContent> content;

    private Operator operator;

    private Criteria parent;

    public Criteria() {
        super();
        content = new ArrayList<>();
        operator = Operator.AND;
    }

    public List<CriteriaContent> getContent() {
        return content;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    public Criteria getParent() {
        return parent;
    }

    public void setParent(final Criteria parent) {
        this.parent = parent;
    }

    public void addCriterion(final Column column, final Condition condition, final Object value, final String representation, final Operator operator) {
        content.add(new Criterion(column, condition, value, representation, operator));
    }

    public boolean isCriteria() {
        return isCriteria(this);
    }

    private boolean isCriteria(final Criteria criteria) {
        boolean result = false;
        for (final Object contentObject : criteria.getContent()) {
            if (contentObject instanceof Criterion) {
                result = true;
            } else {
                result = isCriteria((Criteria) contentObject);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public boolean contains(final Column column) {
        return contains(this, column);
    }

    private boolean contains(final Criteria criteria, final Column column) {
        boolean result = false;
        for (final Object contentObject : criteria.getContent()) {
            if (contentObject instanceof Criterion) {
                result = ((Criterion) contentObject).getColumn().columnId().equals(column.columnId());
            } else {
                result = contains((Criteria) contentObject, column);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public boolean contains(final Table table) {
        return contains(this, table);
    }

    private boolean contains(final Criteria criteria, final Table table) {
        boolean result = false;
        for (final Object contentObject : criteria.getContent()) {
            if (contentObject instanceof Criterion) {
                final Column column = ((Criterion) contentObject).getColumn();
                if (column instanceof ColumnBase) {
                    result = ((ColumnBase) column).table().name().equals(table.name());
                }
            } else {
                result = contains((Criteria) contentObject, table);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public List<String> aliasList(final String tableName) {
        final List<String> result = new ArrayList<>();
        aliasList(this, tableName, result);
        return result;
    }

    private void aliasList(final Criteria criteria, final String tableName, final List<String> aliasList) {
        criteria.getContent().forEach((contentObject) -> {
            if (contentObject instanceof Criterion) {
                final Column column = ((Criterion) contentObject).getColumn();
                if (column instanceof ColumnBase) {
                    if (((ColumnBase) column).table().name().equals(tableName)) {
                        if (!aliasList.contains(((ColumnBase) column).table().alias())) {
                            aliasList.add(((ColumnBase) column).table().alias());
                        }
                    }
                }
            } else {
                aliasList((Criteria) contentObject, tableName, aliasList);
            }
        });
    }
}
