package sk.r3n.jdbc.query;

import java.util.List;
import static sk.r3n.jdbc.query.AbstractQueryBuilder.criteriaToWhere;
import static sk.r3n.jdbc.query.OraQueryBuilder.criteriaToCountSQL;
import static sk.r3n.jdbc.query.OraQueryBuilder.criteriaToSQL;

public class PosQueryBuilder extends AbstractQueryBuilder{

    public static void criteriaToCountSQL(QueryAttribute[] resultColumns, String fromSQL,
            QueryCriteria criteria, StringBuilder countSQL, List<Object> countParams) {
        criteriaToCountSQL(resultColumns, fromSQL, false, criteria, countSQL, countParams);
    }

    public static void criteriaToCountSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct,
            QueryCriteria criteria, StringBuilder countSQL, List<Object> countParams) {
        countSQL.append("select count(*) from (select ");
        if (distinct) {
            countSQL.append("distinct ");
        }
        for (int i = 0; i < resultColumns.length; i++) {
            QueryAttribute column = resultColumns[i];
            countSQL.append(column.nameWithAlias()).append(" col").append(i);
            if (i < resultColumns.length - 1) {
                countSQL.append(", ");
            }
        }
        countSQL.append(" from ").append(fromSQL);
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

        sql.append("select ");
        if (distinct) {
            sql.append("distinct ");
        }
        for (int i = 0; i < resultColumns.length; i++) {
            sql.append(resultColumns[i].nameWithAlias());
            if (i < resultColumns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(" from ").append(fromSQL);
        if (where != null) {
            sql.append(where);
        }
        if (orderBy != null) {
            sql.append(orderBy);
        }
        sql.append(" offset ? limit ?");
        params.add(criteria.getFirstRow());
        params.add(criteria.getPageSize());
    }
}
