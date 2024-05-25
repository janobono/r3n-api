/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import sk.r3n.sql.DataType;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Sequence;

/**
 * Oracle sql builder implementation.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class OraSqlBuilder extends SqlBuilder {

    public OraSqlBuilder() {
    }

    @Override
    public Sql nextVal(final Sequence sequence) {
        final Sql sql = new Sql();
        sql.SELECT().append(sequenceSQL(sequence)).append(" ").FROM().append("dual");
        return sql;
    }

    @Override
    protected String sequenceSQL(final Sequence sequence) {
        return sequence.name() + "." + "nextval";
    }

    @Override
    protected void selectSubSelectsStart(final Select select, final Sql sql) {
        sql.append(" ").FROM().append("(");
    }

    @Override
    protected void selectSubSelectsEnd(final Select select, final Sql sql) {
        sql.append(")");
    }

    @Override
    protected void selectStartCount(final Select select, final Sql sql) {
        sql.SELECT().append("count(*) ").FROM().append("(");
    }

    @Override
    protected void selectStartPagination(final Select select, final Sql sql) {
        sql.SELECT();
        for (int index = 0; index < select.getColumns().length; index++) {
            sql.append("col").append(Integer.toString(index));
            if (index < select.getColumns().length - 1) {
                sql.append(", ");
            }
        }
        sql.append(" ").FROM().append("(").SELECT();
        for (int index = 0; index < select.getColumns().length; index++) {
            sql.append("col").append(Integer.toString(index)).append(", ");
        }
        sql.append("rownum rnm ").FROM().append("(");
    }

    @Override
    protected void selectEndCount(final Select select, final Sql sql) {
        sql.append(")");
    }

    @Override
    protected void selectEndPagination(final Select select, final Sql sql) {
        sql.addParam(DataType.INTEGER, select.getLastRow() + 1);
        sql.append(") ").WHERE().append("rownum <= ?");
        sql.addParam(DataType.INTEGER, select.getFirstRow() + 1);
        sql.append(") ").WHERE().append("rnm >= ?");
    }

    @Override
    protected void insertStartReturning(final Insert insert, final Sql sql) {
        if (insert.getReturning() != null) {
            sql.append("begin ");
        }
    }

    @Override
    protected void insertEndReturning(final Insert insert, final Sql sql) {
        if (insert.getReturning() != null) {
            sql.append(" returning ").append(columnSQL(true, insert.getReturning(), null)).append(" into ?;end;");
        }
    }
}
