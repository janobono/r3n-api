/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Sequence;

/**
 * PostgreSQL sql builder implementation.
 */
public class PostgreSqlBuilder extends SqlBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlBuilder.class);

    @Override
    public Sql nextVal(final Sequence sequence) {
        final Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence));
        return sql;
    }

    @Override
    protected String sequenceSQL(final Sequence sequence) {
        return "nextval('" + sequence.name() + "')";
    }

    @Override
    protected void selectSubSelectsStart(final Select select, final Sql sql) {
        sql.FROM().append("(");
    }

    @Override
    protected void selectSubSelectsEnd(final Select select, final Sql sql) {
        sql.append(") as union_result ");
    }

    @Override
    protected void selectStartCount(final Select select, final Sql sql) {
        sql.SELECT().append("count(*) ").FROM().append("(");
    }

    @Override
    protected void selectStartPagination(final Select select, final Sql sql) {
        sql.SELECT().append("* ").FROM().append("(");
    }

    @Override
    protected void selectEndCount(final Select select, final Sql sql) {
        sql.append(") as count_result");
    }

    @Override
    protected void selectEndPagination(final Select select, final Sql sql) {
        sql.addParam(DataType.INTEGER, select.getPageSize());
        sql.append(") as paginated_result limit ?");
        sql.addParam(DataType.INTEGER, select.getFirstRow());
        sql.append(" offset ?");
    }

    @Override
    protected void insertStartReturning(final Insert insert, final Sql sql) {
    }

    @Override
    protected void insertEndReturning(final Insert insert, final Sql sql) {
        if (insert.getReturning() != null) {
            sql.append(" returning ").append(columnSQL(true, insert.getReturning(), null));
        }
    }
}
