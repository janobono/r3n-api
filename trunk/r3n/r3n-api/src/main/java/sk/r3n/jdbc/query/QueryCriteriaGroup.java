package sk.r3n.jdbc.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryCriteriaGroup implements Serializable {

    private Map<QueryAttribute, Object[]> attributeMap;

    private QueryOperator groupOperator;

    private List<QueryCriteriaGroup> children;

    public QueryCriteriaGroup() {
        super();
        attributeMap = new HashMap<>();
        groupOperator = QueryOperator.AND;
    }

    public void addCriterium(QueryAttribute attribute, QueryCondition condition, Object value,
            QueryAttributeDateType dateType, QueryOperator operator) {
        attributeMap.put(attribute, new Object[]{condition, value, dateType, operator});
    }

    public boolean isCriteria() {
        return isCriteria(this);
    }

    private boolean isCriteria(QueryCriteriaGroup queryCriteriaGroup) {
        boolean result = !queryCriteriaGroup.getAttributeMap().isEmpty();
        for (QueryCriteriaGroup child : queryCriteriaGroup.getChildren()) {
            result |= isCriteria(child);
        }
        return result;
    }

    public boolean contains(QueryAttribute attribute) {
        return contains(this, attribute);
    }

    private boolean contains(QueryCriteriaGroup queryCriteriaGroup, QueryAttribute attribute) {
        boolean result = queryCriteriaGroup.getAttributeMap().containsKey(attribute);
        if (!result) {
            for (QueryCriteriaGroup child : queryCriteriaGroup.getChildren()) {
                result |= contains(child, attribute);
            }
        }
        return result;
    }

    public boolean contains(QueryTable queryTable) {
        return contains(this, queryTable);
    }

    private boolean contains(QueryCriteriaGroup queryCriteriaGroup, QueryTable queryTable) {
        boolean result = false;
        for (QueryAttribute queryAttribute : queryCriteriaGroup.getCriteriaAttributes()) {
            result |= queryAttribute.table().equals(queryTable);
            if (result) {
                break;
            }
        }
        if (!result) {
            for (QueryCriteriaGroup child : queryCriteriaGroup.getChildren()) {
                result |= contains(child, queryTable);
            }
        }
        return result;
    }

    public void aliasList(String tableName, List<String> tableList) {
        aliasList(this, tableName, tableList);
    }

    private void aliasList(QueryCriteriaGroup queryCriteriaGroup, String tableName, List<String> aliasList) {
        for (QueryAttribute queryAttribute : queryCriteriaGroup.getCriteriaAttributes()) {
            if (queryAttribute.table().name().equals(tableName)) {
                if (!aliasList.contains(queryAttribute.table().alias())) {
                    aliasList.add(queryAttribute.table().alias());
                }
            }
        }
        for (QueryCriteriaGroup child : queryCriteriaGroup.getChildren()) {
            aliasList(child, tableName, aliasList);
        }
    }

    public List<QueryAttribute> getCriteriaAttributes() {
        List<QueryAttribute> result = new ArrayList<>();
        result.addAll(attributeMap.keySet());
        return result;
    }

    public QueryCondition getCondition(QueryAttribute attribute) {
        QueryCondition result = null;
        if (attributeMap.containsKey(attribute)) {
            result = (QueryCondition) attributeMap.get(attribute)[0];
        }
        return result;
    }

    public Object getValue(QueryAttribute attribute) {
        Object result = null;
        if (attributeMap.containsKey(attribute)) {
            result = attributeMap.get(attribute)[1];
        }
        return result;
    }

    public QueryAttributeDateType getDateType(QueryAttribute attribute) {
        QueryAttributeDateType result = null;
        if (attributeMap.containsKey(attribute)) {
            result = (QueryAttributeDateType) attributeMap.get(attribute)[2];
        }
        return result;
    }

    public QueryOperator getOperator(QueryAttribute attribute) {
        QueryOperator result = null;
        if (attributeMap.containsKey(attribute)) {
            result = (QueryOperator) attributeMap.get(attribute)[3];
        }
        return result;
    }

    public Map<QueryAttribute, Object[]> getAttributeMap() {
        return attributeMap;
    }

    public QueryOperator getGroupOperator() {
        return groupOperator;
    }

    public void setGroupOperator(QueryOperator groupOperator) {
        this.groupOperator = groupOperator;
    }

    public List<QueryCriteriaGroup> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<QueryCriteriaGroup> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("QueryCriteriaGroup{attributeMap=[");
        Object[] keys = attributeMap.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            QueryAttribute key = (QueryAttribute) keys[i];
            sb.append(key).append("=").append(Arrays.toString(attributeMap.get(key)));
            if (i < keys.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("], groupOperator=");
        sb.append(groupOperator);
        sb.append("}");
        return "QueryCriteriaGroup{" + "attributeMap=" + attributeMap + ", groupOperator=" + groupOperator + '}';
    }
}
