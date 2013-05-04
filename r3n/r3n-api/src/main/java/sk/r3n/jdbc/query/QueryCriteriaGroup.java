package sk.r3n.jdbc.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class QueryCriteriaGroup implements Serializable {

    private Map<QueryAttribute, Object[]> attributeMap;

    private QueryOperator groupOperator;

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
        return !attributeMap.isEmpty();
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

    public QueryOperator getOperator(QueryAttribute attribute) {
        return (QueryOperator) attributeMap.get(attribute)[3];
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

    @Override
    public String toString() {
        return "QueryCriteriaGroup{" + "attributeMap=" + attributeMap + ", groupOperator=" + groupOperator + '}';
    }
}
