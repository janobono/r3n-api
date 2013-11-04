package sk.r3n.jdbc.query;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import sk.r3n.jdbc.Sequence;
import sk.r3n.jdbc.SqlUtil;
import static sk.r3n.jdbc.query.DataType.DATE;
import static sk.r3n.jdbc.query.DataType.TIME;
import static sk.r3n.jdbc.query.DataType.TIME_STAMP;
import static sk.r3n.jdbc.query.QueryCondition.EQUALS;
import static sk.r3n.jdbc.query.QueryCondition.EQUALS_LESS;
import static sk.r3n.jdbc.query.QueryCondition.EQUALS_MORE;
import static sk.r3n.jdbc.query.QueryCondition.EQUALS_NOT;
import static sk.r3n.jdbc.query.QueryCondition.IN;
import static sk.r3n.jdbc.query.QueryCondition.IS_NOT_NULL;
import static sk.r3n.jdbc.query.QueryCondition.IS_NULL;
import static sk.r3n.jdbc.query.QueryCondition.LESS;
import static sk.r3n.jdbc.query.QueryCondition.LIKE;
import static sk.r3n.jdbc.query.QueryCondition.LIKE_SCDF;
import static sk.r3n.jdbc.query.QueryCondition.MORE;
import static sk.r3n.jdbc.query.QueryCondition.NOT_IN;
import sk.r3n.util.DateUtil;
import sk.r3n.util.ScDf;

public abstract class AbstractQueryBuilder {

    public void criteriaToCountSQL(QueryAttribute[] resultColumns, String fromSQL,
            QueryCriteria criteria, StringBuilder countSQL, List<Object> countParams) {
        criteriaToCountSQL(resultColumns, fromSQL, false, criteria, countSQL, countParams);
    }

    public abstract void criteriaToCountSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct,
            QueryCriteria criteria, StringBuilder countSQL, List<Object> countParams);

    public void criteriaToSQL(QueryAttribute[] resultColumns, String fromSQL,
            QueryCriteria criteria, StringBuilder sql, List<Object> params) {
        criteriaToSQL(resultColumns, fromSQL, false, criteria, sql, params);
    }

    public abstract void criteriaToSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct,
            QueryCriteria criteria, StringBuilder sql, List<Object> params);

    public String criteriaToWhere(QueryCriteria criteria, List<Object> params) {
        StringBuilder where = new StringBuilder();
        where.append(" WHERE ");
        criteriaToWhere(where, criteria.getQueryCriteriaGroups(), params);
        return where.toString();
    }

    public String criteriaToOrderBy(QueryAttribute[] resultColumns, QueryCriteria criteria) {
        List<QueryAttribute> cols = Arrays.asList(resultColumns);
        List<QueryAttribute> orders = new ArrayList<>();

        QueryAttribute[] attribs
                = criteria.getOrderAttributes().toArray(new QueryAttribute[criteria.getOrderAttributes().size()]);
        for (QueryAttribute attribute : attribs) {
            if (cols.contains(attribute)) {
                orders.add(attribute);
            }
        }

        StringBuilder orderBy = new StringBuilder();
        if (!orders.isEmpty()) {
            orderBy.append(" ORDER BY ");
            for (int i = 0; i < orders.size(); i++) {
                QueryAttribute column = orders.get(i);
                orderBy.append(column.nameWithAlias());
                if (criteria.getOrder(orders.get(i)) != QueryOrder.ASCENDING) {
                    orderBy.append(" DESC");
                }
                if (i < orders.size() - 1) {
                    orderBy.append(",");
                }
            }
        }
        return orderBy.toString();
    }

    protected void criteriaToWhere(StringBuilder where, List<QueryCriteriaGroup> criteriaGroups, List<Object> params) {
        int index = 0;
        for (QueryCriteriaGroup criteriaGroup : criteriaGroups) {
            if (criteriaGroup.isCriteria()) {
                if (!criteriaGroup.getAttributeMap().isEmpty()) {
                    where.append("(");
                    if (isCriteria(criteriaGroup.getChildren())) {
                        where.append("(");
                    }
                    where.append(attributesToWhere(criteriaGroup, params));
                    if (isCriteria(criteriaGroup.getChildren())) {
                        where.append(")");
                        appendOperator(where, criteriaGroup.getGroupOperator());
                    }
                }
                if (isCriteria(criteriaGroup.getChildren())) {
                    where.append("(");
                    criteriaToWhere(where, criteriaGroup.getChildren(), params);
                    where.append(")");
                }
                if (!criteriaGroup.getAttributeMap().isEmpty()) {
                    where.append(")");
                }
                if (isCriteria(index, criteriaGroups)) {
                    appendOperator(where, criteriaGroup.getGroupOperator());
                }
            }
            index++;
        }
    }

    private String attributesToWhere(QueryCriteriaGroup criteriaGroup, List<Object> params) {
        StringBuilder sb = new StringBuilder();
        int size = criteriaGroup.getCriteriaAttributes().size();
        int index = 0;
        for (QueryAttribute attribute : criteriaGroup.getCriteriaAttributes()) {
            QueryCondition condition = criteriaGroup.getCondition(attribute);
            Object value = criteriaGroup.getValue(attribute);
            switch (condition) {
                case EQUALS:
                case EQUALS_MORE:
                case EQUALS_LESS:
                case EQUALS_NOT:
                case MORE:
                case LESS:
                    appendStandardValue(criteriaGroup, sb, params, attribute, condition, value);
                    break;
                case LIKE:
                    appendLikeValue(sb, params, attribute, condition, value, false);
                    break;
                case LIKE_SCDF:
                    appendLikeValue(sb, params, attribute, condition, value, true);
                    break;
                case IS_NULL:
                case IS_NOT_NULL:
                    sb.append(attribute.nameWithAlias());
                    sb.append(condition.condition());
                    break;
                case IN:
                case NOT_IN:
                    String arrayString;
                    if (value instanceof List<?>) {
                        arrayString = SqlUtil.arrayToString(((List<?>) value).toArray());
                    } else {
                        arrayString = SqlUtil.arrayToString((Object[]) value);
                    }
                    sb.append(attribute.nameWithAlias());
                    sb.append(condition.condition());
                    sb.append(arrayString);
                    break;
                case DIRECT:
                    sb.append(value);
                    break;
            }
            if (index < size - 1) {
                appendOperator(sb, criteriaGroup.getOperator(attribute));
            } else {
                for (QueryCriteriaGroup child : criteriaGroup.getChildren()) {
                    if (child.isCriteria()) {
                        appendOperator(sb, criteriaGroup.getOperator(attribute));
                        break;
                    }
                }
            }
            index++;
        }
        return sb.toString();
    }

    private void appendOperator(StringBuilder sb, QueryOperator operator) {
        if (operator == QueryOperator.AND) {
            sb.append(" AND ");
        } else {
            sb.append(" OR ");
        }
    }

    private void appendStandardValue(QueryCriteriaGroup criteria, StringBuilder sql, List<Object> params,
            QueryAttribute attribute, QueryCondition condition, Object value) {
        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            value = calendar.getTime();
        }
        if (value instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) value;
            switch (attribute.dataType()) {
                case DATE:
                    date = DateUtil.getDateOnly(date);
                    value = new java.sql.Date(date.getTime());
                    break;
                case TIME:
                    date = DateUtil.getTimeOnly(date);
                    value = new Time(date.getTime());
                    break;
                case TIME_STAMP:
                    value = new Timestamp(date.getTime());
                    break;
            }
        }
        appendAttribute(sql, params, attribute, condition, value);
    }

    private void appendLikeValue(StringBuilder sql, List<Object> params, QueryAttribute attribute,
            QueryCondition condition, Object value, boolean scdf) {
        String likeValue = (String) value;
        if (scdf) {
            likeValue = ScDf.toScDf(likeValue);
        }
        likeValue = toLike(likeValue);
        appendAttribute(sql, params, attribute, condition, likeValue);
    }

    private void appendAttribute(StringBuilder sb, List<Object> list, QueryAttribute attribute,
            QueryCondition condition, Object value) {
        sb.append(attribute.nameWithAlias());
        sb.append(condition.condition());
        sb.append("?");
        list.add(value);
    }

    private String toLike(String string) {
        StringBuilder sb = new StringBuilder();
        sb.append('%');
        if (!string.equals("")) {
            sb.append(string);
            sb.append('%');
        }
        return sb.toString();
    }

    public Calendar dateToCalendar(java.util.Date date) {
        Calendar result = null;
        if (date != null) {
            result = Calendar.getInstance();
            result.setTime(date);
        }
        return result;
    }

    public Timestamp calendarToTimestamp(Calendar calendar) {
        Timestamp result = null;
        if (calendar != null) {
            result = new Timestamp(calendar.getTimeInMillis());
        }
        return result;
    }

    public String select(boolean distinct, QueryTable table, QueryAttribute[] attributes, boolean nameWithAlias) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (distinct) {
            sb.append("DISTINCT ");
        }
        sb.append(getColumns(attributes, nameWithAlias, ", ")).append(" FROM ");
        if (nameWithAlias) {
            sb.append(table.nameWithAlias());
        } else {
            sb.append(table.name());
        }
        return sb.toString();
    }

    public String insert(QueryTable table, QueryAttribute[] attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(table.name());
        sb.append("(").append(getColumns(attributes, false, ", "));
        sb.append(") values (");
        for (int i = 0; i < attributes.length; i++) {
            sb.append("?");
            if (i < attributes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public abstract String insert(QueryTable table, Sequence sequence, QueryAttribute[] attributes);

    public String update(QueryTable table, QueryAttribute[] attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(table.name());
        sb.append(" SET ");
        for (int i = 1; i < attributes.length; i++) {
            sb.append(attributes[i].name()).append(" = ?");
            if (i < attributes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(" WHERE ").append(attributes[0].name()).append(" = ?");
        return sb.toString();
    }

    protected String getColumns(QueryAttribute[] attributes, boolean nameWithAlias, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attributes.length; i++) {
            if (nameWithAlias) {
                sb.append(attributes[i].nameWithAlias());
            } else {
                sb.append(attributes[i].name());
            }
            if (i < attributes.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    private boolean isCriteria(List<QueryCriteriaGroup> queryCriteriaGroups) {
        boolean result = false;
        for (QueryCriteriaGroup queryCriteriaGroup : queryCriteriaGroups) {
            if (queryCriteriaGroup.isCriteria()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isCriteria(int index, List<QueryCriteriaGroup> list) {
        boolean result = false;
        if (index < list.size() - 1) {
            for (int i = index + 1; i < list.size(); i++) {
                if (list.get(i).isCriteria()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
