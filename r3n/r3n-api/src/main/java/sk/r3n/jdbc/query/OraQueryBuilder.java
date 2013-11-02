package sk.r3n.jdbc.query;

import sk.r3n.jdbc.Sequence;
import java.util.List;

public class OraQueryBuilder extends AbstractQueryBuilder {

    public static final String ROWNUM = "ROWNUM rnm";

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
        countSQL.append(")");
    }

    @Override
    public void criteriaToSQL(QueryAttribute[] resultColumns, String fromSQL, boolean distinct,
            QueryCriteria criteria, StringBuilder sql, List<Object> params) {
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
        if (criteria.isCriteria()) {
            sql.append(criteriaToWhere(criteria, params));
        }
        if (criteria.isOrder()) {
            sql.append(criteriaToOrderBy(resultColumns, criteria));
        }
        sql.append(") WHERE ROWNUM <= ? ) WHERE rnm >= ?");
        params.add(criteria.getLastRow());
        params.add(criteria.getFirstRow());
    }

    @Override
    public String insert(QueryTable table, Sequence sequence, QueryAttribute[] attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN INSERT INTO ");
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
        sb.append(") RETURNING ").append(attributes[0].name()).append(" INTO ?; END;");
        return sb.toString();
    }
}
