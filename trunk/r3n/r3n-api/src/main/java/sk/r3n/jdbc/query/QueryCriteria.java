package sk.r3n.jdbc.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryCriteria implements Serializable {

    private int firstRow;

    private int lastRow;

    private QueryCriteriaGroup criteriaGroup;

    private List<QueryCriteriaGroup> criteriaGroups;

    private Map<QueryAttribute, QueryOrder> orderMap;

    public QueryCriteria() {
        super();
        criteriaGroups = new ArrayList<>();
        orderMap = new HashMap<>();
        nextGroup();
    }

    public final void nextGroup() {
        criteriaGroup = new QueryCriteriaGroup();
        criteriaGroups.add(criteriaGroup);
    }

    public QueryCriteriaGroup getCriteriaGroup() {
        return criteriaGroup;
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition) {
        criteriaGroup.addCriterium(attribute, condition, null, null, QueryOperator.AND);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, QueryOperator operator) {
        criteriaGroup.addCriterium(attribute, condition, null, null, operator);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value) {
        criteriaGroup.addCriterium(attribute, condition, value, null, QueryOperator.AND);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value, QueryOperator operator) {
        criteriaGroup.addCriterium(attribute, condition, value, null, operator);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value,
            QueryAttributeDateType dateType) {
        criteriaGroup.addCriterium(attribute, condition, value, dateType, QueryOperator.AND);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value,
            QueryAttributeDateType dateType, QueryOperator operator) {
        criteriaGroup.addCriterium(attribute, condition, value, dateType, operator);
    }

    public void addOrder(QueryAttribute attribute, QueryOrder order) {
        orderMap.put(attribute, order);
    }

    public boolean isCriteria() {
        boolean criteria = false;
        for (QueryCriteriaGroup queryCriteriaGroup : criteriaGroups) {
            criteria = queryCriteriaGroup.isCriteria();
            if (criteria) {
                break;
            }
        }
        return criteria;
    }

    public boolean isOrder() {
        return !orderMap.isEmpty();
    }

    public Collection<QueryCriteriaGroup> getQueryCriteriaGroups() {
        return criteriaGroups;
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

    public Map<QueryAttribute, QueryOrder> getOrderMap() {
        return orderMap;
    }

    @Override
    public String toString() {
        return "QueryCriteria{" + "firstRow=" + firstRow + ", lastRow=" + lastRow + ", criteriaGroup=" + criteriaGroup
                + ", criteriaGroups=" + criteriaGroups + ", orderMap=" + orderMap + '}';
    }
}
