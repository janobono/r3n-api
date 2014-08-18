package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Criteria implements Serializable {

    private final List<Criterion> criteria;

    private Operator operator;

    private Criteria parent;

    private List<Criteria> children;

    public Criteria() {
        super();
        criteria = new ArrayList<Criterion>();
        operator = Operator.AND;
    }

    public void addCriterion(Column column, Condition condition, Object value, String representation, Operator operator) {
        criteria.add(new Criterion(column, condition, value, representation, operator));
    }

    public boolean isCriteria() {
        return isCriteria(this);
    }

    private boolean isCriteria(Criteria criteria) {
        boolean result = !criteria.getCriteria().isEmpty();
        for (Criteria child : criteria.getChildren()) {
            result |= isCriteria(child);
        }
        return result;
    }

    public boolean contains(Column column) {
        return contains(this, column);
    }

    private boolean contains(Criteria criteria, Column column) {
        boolean result = false;
        for (Criterion criterion : criteria.getCriteria()) {
            result |= criterion.getColumn().equals(column);
        }
        if (!result) {
            for (Criteria child : criteria.getChildren()) {
                result |= contains(child, column);
            }
        }
        return result;
    }

    public boolean contains(Table table) {
        return contains(this, table);
    }

    private boolean contains(Criteria criteria, Table table) {
        boolean result = false;
        for (Criterion criterion : criteria.getCriteria()) {
            result |= criterion.getColumn().getTable().equals(table);
            if (result) {
                break;
            }
        }
        if (!result) {
            for (Criteria child : criteria.getChildren()) {
                result |= contains(child, table);
            }
        }
        return result;
    }

    public void aliasList(String tableName, List<String> tableList) {
        aliasList(this, tableName, tableList);
    }

    private void aliasList(Criteria criteria, String tableName, List<String> aliasList) {
        for (Criterion criterion : criteria.getCriteria()) {
            if (criterion.getColumn().getTable().getName().equals(tableName)) {
                if (!aliasList.contains(criterion.getColumn().getTable().getAlias())) {
                    aliasList.add(criterion.getColumn().getTable().getAlias());
                }
            }
        }
        for (Criteria child : criteria.getChildren()) {
            aliasList(child, tableName, aliasList);
        }
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Criteria getParent() {
        return parent;
    }

    public void setParent(Criteria parent) {
        this.parent = parent;
    }

    public List<Criteria> getChildren() {
        if (children == null) {
            children = new ArrayList<Criteria>();
        }
        return children;
    }

    public void setChildren(List<Criteria> children) {
        this.children = children;
    }

}
