package sk.r3n.jdbc.query;

import java.util.List;
import sk.r3n.jdbc.Sequence;

/**
 *
 * @author jan
 */
public class PostgreQueryBuilder extends AbstractQueryBuilder {

    @Override
    public void criteriaToCountSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct,
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
        countSQL.append(") res");
    }

    @Override
    public void criteriaToSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct, QueryCriteria criteria,
            StringBuilder sql, List<Object> params) {
        String where = null;
        String orderBy = null;
        if (criteria.isCriteria()) {
            where = criteriaToWhere(criteria, params);
        }
        if (criteria.isOrder()) {
            orderBy = criteriaToOrderBy(resultColumns, criteria);
        }

        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        for (int i = 0; i < resultColumns.length; i++) {
            sql.append(resultColumns[i].nameWithAlias());
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
        sql.append(" OFFSET ? LIMIT ?");
        params.add(criteria.getFirstRow());
        params.add(criteria.getPageSize());
    }

    @Override
    public String insert(QueryTable table, Sequence sequence, QueryAttribute[] attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(table.name());
        sb.append("(").append(getColumns(attributes, false, ", "));
        sb.append(") VALUES (");
        sb.append(sequence.nextval()).append(", ");
        for (int i = 1; i < attributes.length; i++) {
            sb.append("?");
            if (i < attributes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(") RETURNING ").append(attributes[0].name());
        return sb.toString();
    }

}
