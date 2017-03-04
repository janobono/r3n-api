/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Query;

/**
 * Sql command representation object.
 */
public class Sql {

    private final StringBuilder sql = new StringBuilder();

    private final List<SqlParam> params = new ArrayList<>();

    /**
     * Adds parameter.
     *
     * @param dataType Parameter data type.
     * @param param Parameter value.
     * @return Sql command representation object.
     */
    public Sql addParam(DataType dataType, Object param) {
        params.add(new SqlParam(dataType, param));
        return this;
    }

    /**
     * @return List of parameters.
     */
    public List<SqlParam> getParams() {
        return params;
    }

    /**
     * @return Sql command string.
     */
    public String toSql() {
        return sql.toString();
    }

    /**
     * Appends to command.
     *
     * @param string String.
     * @return Sql command representation object.
     */
    public Sql append(String string) {
        sql.append(string);
        return this;
    }

    /**
     * Appends to command.
     *
     * @param strings String array.
     * @return Sql command representation object.
     */
    public Sql appendArray(String... strings) {
        for (String string : strings) {
            if (string != null && string.length() > 0) {
                append(string).append(" ");
            }
        }
        return this;
    }

    /**
     * Appends to command.
     *
     * @param sql Sql command representation object.
     * @return Sql command representation object.
     */
    public Sql append(Sql sql) {
        append(sql.toSql());
        sql.getParams().forEach((param) -> {
            addParam(param.getDataType(), param.getValue());
        });
        return this;
    }

    /**
     * Appends to command.
     *
     * @param sqlBuilder Sql builder instance.
     * @param select Select definition object.
     * @return Sql command representation object.
     */
    public Sql append(SqlBuilder sqlBuilder, Query.Select select) {
        return append(sqlBuilder.select(select));
    }

    /**
     * Appends to command.
     *
     * @param sqlBuilder Sql builder instance.
     * @param insert Insert definition object.
     * @return Sql command representation object.
     */
    public Sql append(SqlBuilder sqlBuilder, Query.Insert insert) {
        return append(sqlBuilder.insert(insert));
    }

    /**
     * Appends to command.
     *
     * @param sqlBuilder Sql builder instance.
     * @param update Update definition object.
     * @return Sql command representation object.
     */
    public Sql append(SqlBuilder sqlBuilder, Query.Update update) {
        return append(sqlBuilder.update(update));
    }

    /**
     * Appends to command.
     *
     * @param sqlBuilder Sql builder instance.
     * @param delete Delete definition object.
     * @return Sql command representation object.
     */
    public Sql append(SqlBuilder sqlBuilder, Query.Delete delete) {
        return append(sqlBuilder.delete(delete));
    }

    /**
     * Appends delete to command.
     *
     * @return Sql command representation object.
     */
    public Sql DELETE() {
        return append("delete ");
    }

    /**
     * Appends update to command.
     *
     * @return Sql command representation object.
     */
    public Sql UPDATE() {
        return append("update ");
    }

    /**
     * Appends set to command.
     *
     * @return Sql command representation object.
     */
    public Sql SET() {
        return SET(null);
    }

    /**
     * Appends set to command.
     *
     * @param condition Condition string.
     * @return Sql command representation object.
     */
    public Sql SET(String condition) {
        return appendArray("set", condition);
    }

    /**
     * Appends insert to command.
     *
     * @return Sql command representation object.
     */
    public Sql INSERT() {
        return append("insert ");
    }

    /**
     * Appends into to command.
     *
     * @return Sql command representation object.
     */
    public Sql INTO() {
        return append("into ");
    }

    /**
     * Appends values to command.
     *
     * @return Sql command representation object.
     */
    public Sql VALUES() {
        return append("values ");
    }

    /**
     * Appends select to command.
     *
     * @return Sql command representation object.
     */
    public Sql SELECT() {
        return append("select ");
    }

    /**
     * Appends distinct to command.
     *
     * @return Sql command representation object.
     */
    public Sql DISTINCT() {
        return append("distinct ");
    }

    /**
     * Appends from to command.
     *
     * @return Sql command representation object.
     */
    public Sql FROM() {
        return append("from ");
    }

    /**
     * Appends join to command.
     *
     * @return Sql command representation object.
     */
    public Sql JOIN() {
        return append("join ");
    }

    /**
     * Appends on to command.
     *
     * @return Sql command representation object.
     */
    public Sql ON() {
        return ON(null);
    }

    /**
     * Appends on to command.
     *
     * @param condition Condition string.
     * @return Sql command representation object.
     */
    public Sql ON(String condition) {
        return appendArray("on", condition);
    }

    /**
     * Appends where to command.
     *
     * @return Sql command representation object.
     */
    public Sql WHERE() {
        return WHERE(null);
    }

    /**
     * Appends where to command.
     *
     * @param condition Condition string.
     * @return Sql command representation object.
     */
    public Sql WHERE(String condition) {
        return appendArray("where", condition);
    }

    /**
     * Appends and to command.
     *
     * @return Sql command representation object.
     */
    public Sql AND() {
        return AND(null);
    }

    /**
     * Appends and to command.
     *
     * @param condition Condition string.
     * @return Sql command representation object.
     */
    public Sql AND(String condition) {
        return appendArray("and", condition);
    }

    /**
     * Appends or to command.
     *
     * @return Sql command representation object.
     */
    public Sql OR() {
        return OR(null);
    }

    /**
     * Appends or to command.
     *
     * @param condition Condition string.
     * @return Sql command representation object.
     */
    public Sql OR(String condition) {
        return appendArray("or", condition);
    }

    /**
     * Appends group by to command.
     *
     * @return Sql command representation object.
     */
    public Sql GROUP_BY() {
        return append("group by ");
    }

    /**
     * Appends order by to command.
     *
     * @return Sql command representation object.
     */
    public Sql ORDER_BY() {
        return append("order by ");
    }

    /**
     * Appends having to command.
     *
     * @return Sql command representation object.
     */
    public Sql HAVING() {
        return append("having ");
    }

    @Override
    public String toString() {
        return "Sql{" + "sql=" + toSql() + ", params=" + Arrays.toString(params.toArray()) + '}';
    }

}
