package sk.r3n.sql;

import java.util.ArrayList;
import java.util.List;

public class CriteriaManager {

    private Criteria criteria;

    private final List<Criteria> criteriaList;

    public CriteriaManager() {
        super();
        criteriaList = new ArrayList<Criteria>();
        criteria = new Criteria();
        criteriaList.add(criteria);
    }

    public void addCriterion(Column column, Condition condition, Object value, String representation, Operator operator) {
        setCriterionOperator(operator);
        criteria.addCriterion(column, condition, value, representation, Operator.AND);
    }

    public void next(Operator operator) {
        setCriteriaOperator(operator);
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
        setCriterionOperator(operator);
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

    private void setCriterionOperator(Operator operator) {
        setLastCriterionOperator(operator);
    }

    private void setCriteriaOperator(Operator operator) {
        if (criteria.getParent() == null) {
            if (!criteriaList.isEmpty()) {
                criteriaList.get(criteriaList.size() - 1).setOperator(operator);
            }
        } else {
            setLastCriterionOperator(operator);
        }
    }

    private void setLastCriterionOperator(Operator operator) {
        Criterion criterion = null;
        for (Object object : criteria.getContent()) {
            if (object instanceof Criterion) {
                criterion = (Criterion) object;
            }
        }
        if (criterion != null) {
            criterion.setOperator(operator);
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
