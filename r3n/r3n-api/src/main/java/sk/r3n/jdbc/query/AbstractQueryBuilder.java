package sk.r3n.jdbc.query;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import sk.r3n.jdbc.SqlUtil;
import static sk.r3n.jdbc.query.QueryAttributeDateType.DATE;
import static sk.r3n.jdbc.query.QueryAttributeDateType.TIME;
import static sk.r3n.jdbc.query.QueryAttributeDateType.TIME_STAMP;
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

    public static String criteriaToWhere(QueryCriteria criteria, List<Object> params) {
        StringBuilder where = new StringBuilder();
        where.append(" WHERE ");
        QueryAttribute[] attribs =
                criteria.getCriteriaAttributes().toArray(new QueryAttribute[criteria.getCriteriaAttributes().size()]);
        for (int i = 0; i < attribs.length; i++) {
            QueryAttribute attribute = attribs[i];
            QueryCondition condition = criteria.getCondition(attribute);
            Object value = criteria.getValue(attribute);
            switch (condition) {
                case EQUALS:
                case EQUALS_MORE:
                case EQUALS_LESS:
                case EQUALS_NOT:
                case MORE:
                case LESS:
                    appendStandardValue(criteria, where, params, attribute, condition, value);
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
            }
            if (i < attribs.length - 1) {
                where.append(" AND ");
            }
        }
        return where.toString();
    }

    public static String criteriaToOrderBy(QueryAttribute[] resultColumns, QueryCriteria criteria) {
        List<QueryAttribute> cols = Arrays.asList(resultColumns);
        List<QueryAttribute> orders = new ArrayList<>();

        QueryAttribute[] attribs =
                criteria.getOrderAttributes().toArray(new QueryAttribute[criteria.getOrderAttributes().size()]);
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

    protected static void appendStandardValue(QueryCriteria criteria, StringBuilder sql, List<Object> params,
            QueryAttribute attribute, QueryCondition condition, Object value) {
        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            value = calendar.getTime();
        }
        if (value instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) value;
            QueryAttributeDateType dateType = criteria.getDateType(attribute);
            switch (dateType) {
                case DATE:
                    date = DateUtil.getDateOnly(date);
                    value = new Date(date.getTime());
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

    protected static void appendLikeValue(StringBuilder sql, List<Object> params, QueryAttribute attribute,
            QueryCondition condition, Object value, boolean scdf) {
        String likeValue = (String) value;
        if (scdf) {
            likeValue = ScDf.toScDf(likeValue);
        }
        likeValue = toLike(likeValue);
        appendAttribute(sql, params, attribute, condition, likeValue);
    }

    protected static void appendAttribute(StringBuilder sb, List<Object> list, QueryAttribute attribute,
            QueryCondition condition, Object value) {
        sb.append(attribute.nameWithAlias());
        sb.append(condition.condition());
        sb.append("?");
        list.add(value);
    }

    protected static String toLike(String string) {
        StringBuilder sb = new StringBuilder();
        sb.append('%');
        if (!string.equals("")) {
            sb.append(string);
            sb.append('%');
        }
        return sb.toString();
    }
}
