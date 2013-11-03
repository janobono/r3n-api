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
        for (int i = 0; i < criteria.getQueryCriteriaGroups().size(); i++) {
            QueryCriteriaGroup criteriaGroup = criteria.getQueryCriteriaGroups().get(i);
            if (criteriaGroup.isCriteria()) {
                criteriaToWhere(where, criteriaGroup, params);
                appendGroupOperator(where, criteriaGroup.getGroupOperator(), i, criteria.getQueryCriteriaGroups());
            }
        }

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

    protected void criteriaToWhere(StringBuilder where, QueryCriteriaGroup criteriaGroup, List<Object> params) {
        where.append("(");
        attributesToWhere(where, criteriaGroup, params);
        for (int i = 0; i < criteriaGroup.getChildren().size(); i++) {
            QueryCriteriaGroup child = criteriaGroup.getChildren().get(i);
            if (child.isCriteria()) {
                criteriaToWhere(where, child, params);
                appendGroupOperator(where, criteriaGroup.getGroupOperator(), i, criteriaGroup.getChildren());
            }
        }
        where.append(")");
    }

    private void attributesToWhere(StringBuilder where, QueryCriteriaGroup criteriaGroup, List<Object> params) {
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
                    appendStandardValue(criteriaGroup, where, params, attribute, condition, value);
                    break;
                case LIKE:
                    appendLikeValue(where, params, attribute, condition, value, false);
                    break;
                case LIKE_SCDF:
                    appendLikeValue(where, params, attribute, condition, value, true);
                    break;
                case IS_NULL:
                case IS_NOT_NULL:
                    where.append(attribute.nameWithAlias());
                    where.append(condition.condition());
                    break;
                case IN:
                case NOT_IN:
                    String arrayString;
                    if (value instanceof List<?>) {
                        arrayString = SqlUtil.arrayToString(((List<?>) value).toArray());
                    } else {
                        arrayString = SqlUtil.arrayToString((Object[]) value);
                    }
                    where.append(attribute.nameWithAlias());
                    where.append(condition.condition());
                    where.append(arrayString);
                    break;
                case DIRECT:
                    where.append(value);
                    break;
            }
            if (index < size - 1) {
                appendOperator(where, criteriaGroup.getOperator(attribute));
            } else {
                for (QueryCriteriaGroup child : criteriaGroup.getChildren()) {
                    if (child.isCriteria()) {
                        appendOperator(where, criteriaGroup.getOperator(attribute));
                        break;
                    }
                }
            }
            index++;
        }
    }

    private void appendOperator(StringBuilder sb, QueryOperator operator) {
        if (operator == QueryOperator.AND) {
            sb.append(" AND ");
        } else {
            sb.append(" OR ");
        }
    }

    private void appendGroupOperator(StringBuilder sb, QueryOperator operator, int index,
            List<QueryCriteriaGroup> list) {
        if (index < list.size() - 1) {
            for (int i = index + 1; i < list.size(); i++) {
                if (list.get(i).isCriteria()) {
                    appendOperator(sb, operator);
                    break;
                }
            }
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
}
