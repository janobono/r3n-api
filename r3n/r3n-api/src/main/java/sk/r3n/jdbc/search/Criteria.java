package sk.r3n.jdbc.search;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Criteria implements Serializable {

   private int firstRow;

    private int lastRow;

    private Map<Attribute, Object[]> attributes;

    private Map<Attribute, Order> orderBy;

    public Criteria() {
        super();
        attributes = new HashMap<>();
        orderBy = new HashMap<>();
    }

    public void addSearchCriterium(Attribute attribute, Condition condition) {
        addSearchCriterium(attribute, condition, null);
    }

    public void addSearchCriterium(Attribute attribute, Condition condition, Object value) {
        attributes.put(attribute, new Object[]{condition, value, null});
    }

    public void addSearchCriterium(Attribute attribute, Condition condition, Object value, DateType dateType) {
        attributes.put(attribute, new Object[]{condition, value, dateType});
    }

    public void addOrderBy(Attribute attribute, Order order) {
        orderBy.put(attribute, order);
    }

    public boolean containsCriteria() {
        return !attributes.isEmpty();
    }

    public boolean isOrderBy() {
        return !orderBy.isEmpty();
    }

    public Collection<Attribute> getSearchCriteria() {
        return attributes.keySet();
    }

    public Condition getCondition(Attribute attribute) {
        return (Condition) attributes.get(attribute)[0];
    }

    public Object getValue(Attribute attribute) {
        return attributes.get(attribute)[1];
    }

    public DateType getDateType(Attribute attribute) {
        return (DateType) attributes.get(attribute)[2];
    }

    public Collection<Attribute> getOrderByAttributes() {
        return orderBy.keySet();
    }

    public Order getOrder(Attribute attribute) {
        return orderBy.get(attribute);
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    public void setInterval(int page, int pageSize) {
        if (page < 0) {
            page = 0;
        }
        if (pageSize < 0) {
            pageSize = 0;
        }
        page++;
        setLastRow(page * pageSize);
        setFirstRow(getLastRow() - pageSize);
    }
    
    public int getPageSize(){
        return getLastRow() - getFirstRow();
    }
}
