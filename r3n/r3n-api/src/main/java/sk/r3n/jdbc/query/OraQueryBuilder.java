package sk.r3n.jdbc.query;

import java.util.List;

public class OraQueryBuilder extends AbstractQueryBuilder {

    public static final String ROWNUM = "ROWNUM rnm";

    public static void criteriaToCountSQL(QueryAttribute[] resultColumns, String fromSQL,
            QueryCriteria criteria, StringBuilder countSQL, List<Object> countParams) {
        criteriaToCountSQL(resultColumns, fromSQL, false, criteria, countSQL, countParams);
    }

    public static void criteriaToCountSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct,
            QueryCriteria criteria, StringBuilder countSQL, List<Object> countParams) {
        countSQL.append("SELECT COUNT(*) FROM (SELECT ");
        if (distinct) {
            countSQL.append("DISTINCT ");
        }
        for (int i = 0; i < resultColumns.length; i++) {
            QueryAttribute column = resultColumns[i];
            countSQL.append(column.nameWithAlias()).append(" col").append(i);
            if (i < resultColumns.length - 1) {
                countSQL.append(", ");
            }
        }
        countSQL.append(" FROM ").append(fromSQL);
        if (criteria.isCriteria()) {
            countSQL.append(criteriaToWhere(criteria, countParams));
        }
        countSQL.append(")");
    }

    public static void criteriaToSQL(QueryAttribute[] resultColumns, String fromSQL,
            QueryCriteria criteria, StringBuilder sql, List<Object> params) {
        criteriaToSQL(resultColumns, fromSQL, false, criteria, sql, params);
    }

    public static void criteriaToSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct,
            QueryCriteria criteria, StringBuilder sql, List<Object> params) {
        String where = null;
        String orderBy = null;
        if (criteria.isCriteria()) {
            where = criteriaToWhere(criteria, params);
        }
        if (criteria.isOrder()) {
            orderBy = criteriaToOrderBy(resultColumns, criteria);
        }

        sql.append("SELECT * FROM (SELECT ");
        for (int i = 0; i < resultColumns.length; i++) {
            sql.append("col").append(i).append(", ");
        }
        sql.append(ROWNUM);
        sql.append(" FROM (SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        for (int i = 0; i < resultColumns.length; i++) {
            QueryAttribute column = resultColumns[i];
            sql.append(column.nameWithAlias()).append(" col").append(i);
            if (i < resultColumns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(" FROM ").append(fromSQL);
        if (where != null) {
            sql.append(where);
        }
        if (orderBy != null) {
            sql.append(orderBy);
        }
        sql.append(") WHERE ROWNUM < ? ) WHERE rnm >= ?");
        params.add(criteria.getLastRow());
        params.add(criteria.getFirstRow());
    }
}
