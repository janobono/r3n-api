/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.r3n.dto.Dto;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Query.Delete;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Query.Update;
import sk.r3n.sql.Sequence;

/**
 * Sql builder base class.
 */
public abstract class SqlBuilder {

    private static final Logger LOGGER = Logger.getLogger(SqlBuilder.class.getCanonicalName());

    /**
     * Temporary directory used to store BLOB data from database.
     */
    private File tmpDir;

    public File getTmpDir() {
        if (tmpDir == null) {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "Default tmp dir will be used - {0}", tmpDir.getAbsolutePath());
            }
        }
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    /**
     * Transforms definition to representation.
     *
     * @param sequence Sequence definition object.
     * @return Sql next value from sequence representation.
     */
    public abstract Sql nextVal(Sequence sequence);

    /**
     * Executes nextval.
     *
     * @param connection Connection.
     * @param sequence Sequence definition object.
     * @return Next value from sequence.
     * @throws SQLException
     */
    public abstract long nextVal(Connection connection, Sequence sequence) throws SQLException;

    /**
     * Transforms definition to representation.
     *
     * @param select Select definition object.
     * @return Sql select representation.
     */
    public abstract Sql select(Select select);

    /**
     * Executes select.
     *
     * @param connection Connection.
     * @param select Select definition object.
     * @return List of result rows like arrays of objects.
     * @throws SQLException
     */
    public abstract List<Object[]> select(Connection connection, Select select) throws SQLException;

    /**
     * Executes select.
     *
     * @param <T> Dto object.
     * @param connection Connection.
     * @param select Select definition object.
     * @param clazz Dto object class.
     * @return List of result dto objects.
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <T> List<T> select(Connection connection, Select select, Class<T> clazz) throws SQLException, InstantiationException, IllegalAccessException {
        List<T> result = new ArrayList<>();

        List<Object[]> rows = select(connection, select);
        Dto dto = new Dto();
        for (Object[] row : rows) {
            T t = clazz.newInstance();
            dto.fill(t, row, select.getColumns());
            result.add(t);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, t.toString());
            }
        }

        return result;
    }

    /**
     * Transforms definition to representation.
     *
     * @param insert Insert definition object.
     * @return Sql insert representation.
     */
    public abstract Sql insert(Insert insert);

    /**
     * Executes insert.
     *
     * @param connection Connection.
     * @param insert Insert definition object.
     * @return Value if insert returning value else null.
     * @throws SQLException
     */
    public abstract Object insert(Connection connection, Insert insert) throws SQLException;

    /**
     * Transforms definition to representation.
     *
     * @param update Update definition object.
     * @return Sql update representation.
     */
    public abstract Sql update(Update update);

    /**
     * Executes update.
     *
     * @param connection Connection.
     * @param update Update definition object.
     * @throws SQLException
     */
    public abstract void update(Connection connection, Update update) throws SQLException;

    /**
     * Transforms definition to representation.
     *
     * @param delete Delete definition object.
     * @return Sql delete representation.
     */
    public abstract Sql delete(Delete delete);

    /**
     * Executes delete.
     *
     * @param connection Connection.
     * @param delete Delete definition object.
     * @throws SQLException
     */
    public abstract void delete(Connection connection, Delete delete) throws SQLException;

    /**
     * Executes sql representation.
     *
     * @param connection Connection.
     * @param sql Sql representation object.
     * @throws SQLException
     */
    public abstract void execute(Connection connection, Sql sql) throws SQLException;

    /**
     * Executes sql representation.
     *
     * @param connection Connection.
     * @param sql Sql representation object.
     * @param dataType Returning data type.
     * @return Returning value.
     * @throws SQLException
     */
    public abstract Object execute(Connection connection, Sql sql, DataType dataType) throws SQLException;

    /**
     * Executes sql representation.
     *
     * @param connection Connection.
     * @param sql Sql representation object.
     * @param dataTypes Returning data types.
     * @return List of result rows like arrays of objects.
     * @throws SQLException
     */
    public abstract List<Object[]> executeQuery(Connection connection, Sql sql, DataType... dataTypes) throws SQLException;

}
