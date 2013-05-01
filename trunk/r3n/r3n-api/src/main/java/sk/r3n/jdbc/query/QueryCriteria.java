package sk.r3n.jdbc.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jan
 */
public class QueryCriteria implements Serializable {

    private int firstRow;

    private int lastRow;

    private Map<QueryAttribute, Object[]> attributeMap;

    private Map<QueryAttribute, QueryOrder> orderMap;

    public QueryCriteria() {
        super();
        attributeMap = new HashMap<QueryAttribute, Object[]>();
        orderMap = new HashMap<QueryAttribute, QueryOrder>();
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition) {
        addCriterium(attribute, condition, null);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value) {
        attributeMap.put(attribute, new Object[]{condition, value, null});
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value,
            QueryAttributeDateType dateType) {
        attributeMap.put(attribute, new Object[]{condition, value, dateType});
    }

    public void addOrder(QueryAttribute attribute, QueryOrder order) {
        orderMap.put(attribute, order);
    }

    public boolean isCriteria() {
        return !attributeMap.isEmpty();
    }

    public boolean isOrder() {
        return !orderMap.isEmpty();
    }

    public Collection<QueryAttribute> getCriteriaAttributes() {
        return attributeMap.keySet();
    }

    public QueryCondition getCondition(QueryAttribute attribute) {
        return (QueryCondition) attributeMap.get(attribute)[0];
    }

    public Object getValue(QueryAttribute attribute) {
        return attributeMap.get(attribute)[1];
    }

    public QueryAttributeDateType getDateType(QueryAttribute attribute) {
        return (QueryAttributeDateType) attributeMap.get(attribute)[2];
    }

    public Collection<QueryAttribute> getOrderAttributes() {
        return orderMap.keySet();
    }

    public QueryOrder getOrder(QueryAttribute attribute) {
        return orderMap.get(attribute);
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

    public void setInterval(int start, int count) {
        if (start <= 0) {
            start = 1;
        }
        if (count < 0) {
            count = 0;
        }
        this.firstRow = start;
        this.lastRow = firstRow + count;
    }

    public void setPage(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        this.firstRow = page * size + 1;
        this.lastRow = firstRow + size;
    }

    public int getPageSize() {
        return getLastRow() - getFirstRow();
    }

    public Map<QueryAttribute, Object[]> getAttributeMap() {
        return attributeMap;
    }

    public Map<QueryAttribute, QueryOrder> getOrderMap() {
        return orderMap;
    }

    @Override
    public String toString() {
        return "QueryCriteria{" + "firstRow=" + firstRow + ", lastRow=" + lastRow + ", attributeMap=" + attributeMap
                + ", orderMap=" + orderMap + '}';
    }
}
