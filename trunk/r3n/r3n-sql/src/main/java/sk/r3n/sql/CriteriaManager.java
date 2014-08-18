package sk.r3n.sql;

import java.util.ArrayList;
import java.util.List;

public class CriteriaManager {

    private Criteria critera;

    private final List<Criteria> criteriaList;

    public CriteriaManager() {
        super();
        criteriaList = new ArrayList<Criteria>();
        critera = new Criteria();
        criteriaList.add(critera);
    }

    public void addCriterion(Column column, Condition condition, Object value, String representation, Operator operator) {
        setCriterionOperator(operator);
        critera.addCriterion(column, condition, value, representation, Operator.AND);
    }

    public void next(Operator operator) {
        setCriteriaOperator(operator);
        Criteria next = new Criteria();
        if (critera.getParent() == null) {
            criteriaList.add(next);
        } else {
            next.setParent(critera.getParent());
            critera.getParent().getChildren().add(next);
        }
        critera = next;
    }

    public void in(Operator operator) {
        setCriterionOperator(operator);
        Criteria in = new Criteria();
        critera.getChildren().add(in);
        in.setParent(critera);
        critera = in;
    }

    public void out() {
        Criteria out = critera.getParent();
        if (out != null) {
            critera = out;
        }
    }

    private void setCriterionOperator(Operator operator) {
        if (!critera.getCriteria().isEmpty()) {
            critera.getCriteria().get(critera.getCriteria().size() - 1).setOperator(operator);
        }
    }

    private void setCriteriaOperator(Operator operator) {
        if (critera.getParent() == null) {
            if (!criteriaList.isEmpty()) {
                criteriaList.get(criteriaList.size() - 1).setOperator(operator);
            }
        } else {
            if (!critera.getChildren().isEmpty()) {
                critera.getChildren().get(critera.getChildren().size() - 1).setOperator(operator);
            }
        }
    }

    public Criteria getCritera() {
        return critera;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

}
