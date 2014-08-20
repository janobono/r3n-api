package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Criteria implements Serializable {

    private final List<Object> content;

    private Operator operator;

    private Criteria parent;

    public Criteria() {
        super();
        content = new ArrayList<Object>();
        operator = Operator.AND;
    }

    public void addCriterion(Column column, Condition condition, Object value, String representation, Operator operator) {
        content.add(new Criterion(column, condition, value, representation, operator));
    }

    public boolean isCriteria() {
        return isCriteria(this);
    }

    private boolean isCriteria(Criteria criteria) {
        boolean result = false;
        for (Object contentObject : criteria.getContent()) {
            if (contentObject instanceof Criterion) {
                result = true;
            } else {
                result |= isCriteria((Criteria) contentObject);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public boolean contains(Column column) {
        return contains(this, column);
    }

    private boolean contains(Criteria criteria, Column column) {
        boolean result = false;
        for (Object contentObject : criteria.getContent()) {
            if (contentObject instanceof Criterion) {
                result |= ((Criterion) contentObject).getColumn().equals(column);
            } else {
                result |= contains((Criteria) contentObject, column);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public boolean contains(Table table) {
        return contains(this, table);
    }

    private boolean contains(Criteria criteria, Table table) {
        boolean result = false;
        for (Object contentObject : criteria.getContent()) {
            if (contentObject instanceof Criterion) {
                result |= ((Criterion) contentObject).getColumn().getTable().equals(table);
            } else {
                result |= contains((Criteria) contentObject, table);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public List<String> aliasList(String tableName) {
        List<String> result = new ArrayList<String>();
        aliasList(this, tableName, result);
        return result;
    }

    private void aliasList(Criteria criteria, String tableName, List<String> aliasList) {
        for (Object contentObject : criteria.getContent()) {
            if (contentObject instanceof Criterion) {
                if (((Criterion) contentObject).getColumn().getTable().getName().equals(tableName)) {
                    if (!aliasList.contains(((Criterion) contentObject).getColumn().getTable().getAlias())) {
                        aliasList.add(((Criterion) contentObject).getColumn().getTable().getAlias());
                    }
                }
            } else {
                aliasList((Criteria) contentObject, tableName, aliasList);
            }
        }
    }

    public List<Object> getContent() {
        return content;
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

}
