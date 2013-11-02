package sk.r3n.jdbc.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryCriteria implements Serializable {

    protected int firstRow;

    protected int lastRow;

    private QueryCriteriaGroup criteriaGroup;

    private final List<QueryCriteriaGroup> criteriaGroups;

    private final Map<QueryAttribute, QueryOrder> orderMap;

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
        criteriaGroup.addCriterium(attribute, condition, null, QueryOperator.AND);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, QueryOperator operator) {
        criteriaGroup.addCriterium(attribute, condition, null, operator);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value) {
        criteriaGroup.addCriterium(attribute, condition, value, QueryOperator.AND);
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value, QueryOperator operator) {
        criteriaGroup.addCriterium(attribute, condition, value, operator);
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

    public boolean contains(QueryAttribute attribute) {
        boolean contains = false;
        for (QueryCriteriaGroup queryCriteriaGroup : criteriaGroups) {
            contains = queryCriteriaGroup.contains(attribute);
            if (contains) {
                break;
            }
        }
        return contains;
    }

    public boolean contains(QueryTable queryTable) {
        boolean contains = false;
        for (QueryCriteriaGroup queryCriteriaGroup : criteriaGroups) {
            contains = queryCriteriaGroup.contains(queryTable);
            if (contains) {
                break;
            }
        }
        return contains;
    }

    public List<String> aliasList(String tableName) {
        List<String> tableList = new ArrayList<>();
        for (QueryCriteriaGroup queryCriteriaGroup : criteriaGroups) {
            queryCriteriaGroup.aliasList(tableName, tableList);
        }
        return tableList;
    }

    public boolean isOrder() {
        return !orderMap.isEmpty();
    }

    public List<QueryCriteriaGroup> getQueryCriteriaGroups() {
        return criteriaGroups;
    }

    public List<QueryAttribute> getOrderAttributes() {
        List<QueryAttribute> result = new ArrayList<>();
        result.addAll(orderMap.keySet());
        return result;
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
        this.firstRow = 0;
        this.lastRow = 0;
        if (start < 0) {
            start = 0;
        }
        if (count < 0) {
            count = 0;
        }
        if (count != 0) {
            this.firstRow = start;
            this.lastRow = start + count;
        }
    }

    public void setPage(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        this.firstRow = page * size;
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
