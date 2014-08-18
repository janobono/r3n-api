package sk.r3n.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jan
 */
public class SELECT implements Serializable {

    private boolean distinct = false;

    private int offset = -1;

    private int limit = -1;

    private Column[] attributes;

    private Table[] tables;

    private Criteria critera;

    private final List<Criteria> criteriaList;

    public SELECT() {
        super();
        criteriaList = new ArrayList<Criteria>();
        critera = new Criteria();
        criteriaList.add(critera);
    }

    public SELECT DISTINCT() {
        distinct = true;
        return this;
    }

    public SELECT offset(int offset) {
        this.offset = offset;
        return this;
    }

    public SELECT limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SELECT COLUMNS(Column... attributes) {
        this.attributes = attributes;
        return this;
    }

    public SELECT COLUMNS(List<Column> attributes) {
        this.attributes = new Column[attributes.size()];
        this.attributes = attributes.toArray(this.attributes);
        return this;
    }

    public SELECT FROM(Table... tables) {
        this.tables = tables;
        return this;
    }

    public SELECT FROM(List<Table> tables) {
        this.tables = new Table[tables.size()];
        this.tables = tables.toArray(this.tables);
        return this;
    }

    public SELECT WHERE(Column column, Condition condition, Object value, String representation) {
        critera.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public SELECT WHERE(Column column, Condition condition, Object value) {
        critera.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public SELECT AND(Column column, Condition condition, Object value, String representation) {
        setCriterionOperator(Operator.AND);
        critera.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public SELECT AND(Column column, Condition condition, Object value) {
        setCriterionOperator(Operator.AND);
        critera.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public SELECT OR(Column column, Condition condition, Object value, String representation) {
        setCriterionOperator(Operator.OR);
        critera.addCriterion(column, condition, value, representation, Operator.AND);
        return this;
    }

    public SELECT OR(Column column, Condition condition, Object value) {
        setCriterionOperator(Operator.OR);
        critera.addCriterion(column, condition, value, null, Operator.AND);
        return this;
    }

    public SELECT AND_NEXT() {
        setCriteriaOperator(Operator.AND);
        Criteria next = new Criteria();
        if (critera.getParent() == null) {
            criteriaList.add(next);
        } else {
            next.setParent(critera.getParent());
            critera.getParent().getChildren().add(next);
        }
        critera = next;
        return this;
    }

    public SELECT OR_NEXT() {
        setCriteriaOperator(Operator.OR);
        Criteria next = new Criteria();
        if (critera.getParent() == null) {
            criteriaList.add(next);
        } else {
            next.setParent(critera.getParent());
            critera.getParent().getChildren().add(next);
        }
        critera = next;
        return this;
    }

    public SELECT AND_IN() {
        setCriterionOperator(Operator.AND);
        Criteria in = new Criteria();
        critera.getChildren().add(in);
        in.setParent(critera);
        critera = in;
        return this;
    }

    public SELECT OR_IN() {
        setCriterionOperator(Operator.OR);
        Criteria in = new Criteria();
        critera.getChildren().add(in);
        in.setParent(critera);
        critera = in;
        return this;
    }

    public SELECT OUT() {
        Criteria out = critera.getParent();
        if (out != null) {
            critera = out;
        }
        return this;
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

}
