package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CriteriaManager implements Serializable {

    private Criteria criteria;

    private final List<Criteria> criteriaList;

    public CriteriaManager() {
        super();
        criteriaList = new ArrayList<Criteria>();
        criteria = new Criteria();
        criteriaList.add(criteria);
    }

    public void addCriterion(Column column, Condition condition, Object value, String representation, Operator operator) {
        setLastOperator(operator);
        criteria.addCriterion(column, condition, value, representation, Operator.AND);
    }

    public void next(Operator operator) {
        criteria.setOperator(operator);
        Criteria next = new Criteria();
        if (criteria.getParent() == null) {
            criteriaList.add(next);
        } else {
            next.setParent(criteria.getParent());
            criteria.getParent().getContent().add(next);
        }
        criteria = next;
    }

    public void in(Operator operator) {
        setLastOperator(operator);
        Criteria in = new Criteria();
        criteria.getContent().add(in);
        in.setParent(criteria);
        criteria = in;
    }

    public void out() {
        Criteria out = criteria.getParent();
        if (out != null) {
            criteria = out;
        }
    }

    private void setLastOperator(Operator operator) {
        if (!criteria.getContent().isEmpty()) {
            Object object = criteria.getContent().get(criteria.getContent().size() - 1);
            if (object instanceof Criterion) {
                ((Criterion) object).setOperator(operator);
            } else {
                ((Criteria) object).setOperator(operator);
            }
        }
    }

    public Criteria getCritera() {
        return criteria;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public boolean isCriteria() {
        boolean result = false;
        for (Criteria c : criteriaList) {
            result |= c.isCriteria();
            if (result) {
                break;
            }
        }
        return result;
    }

}
