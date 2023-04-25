/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.r3n.sql.DataType;
import sk.r3n.sql.Query.Delete;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Query.Update;
import sk.r3n.sql.Sequence;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Sql builder base class.
 *
 * @author janobono
 * @since 18 August 2014
 */
public abstract class SqlBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlBuilder.class);

    public SqlBuilder() {
    }

    public SqlBuilder(final Boolean blobFile) {
        this.blobFile = blobFile;
    }

    /**
     * Blob data type as File.
     */
    private Boolean blobFile;

    public Boolean getBlobFile() {
        if (blobFile == null) {
            blobFile = true;
        }
        return blobFile;
    }

    public void setBlobFile(final Boolean blobFile) {
        this.blobFile = blobFile;
    }

    /**
     * Temporary directory used to store BLOB data from database.
     */
    private File tmpDir;

    public File getTmpDir() {
        if (tmpDir == null) {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            LOGGER.debug("Default tmp dir will be used - {}", tmpDir);
        }
        return tmpDir;
    }

    public void setTmpDir(final File tmpDir) {
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
     * @param sequence   Sequence definition object.
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
     * @param select     Select definition object.
     * @return List of result rows like arrays of objects.
     * @throws SQLException
     */
    public abstract List<Object[]> select(Connection connection, Select select) throws SQLException;

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
     * @param insert     Insert definition object.
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
     * @param update     Update definition object.
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
     * @param delete     Delete definition object.
     * @throws SQLException
     */
    public abstract void delete(Connection connection, Delete delete) throws SQLException;

    /**
     * Executes sql representation.
     *
     * @param connection Connection.
     * @param sql        Sql representation object.
     * @throws SQLException
     */
    public abstract void execute(Connection connection, Sql sql) throws SQLException;

    /**
     * Executes sql representation.
     *
     * @param connection Connection.
     * @param sql        Sql representation object.
     * @param dataType   Returning data type.
     * @return Returning value.
     * @throws SQLException
     */
    public abstract Object execute(Connection connection, Sql sql, DataType dataType) throws SQLException;

    /**
     * Executes sql representation.
     *
     * @param connection Connection.
     * @param sql        Sql representation object.
     * @param dataTypes  Returning data types.
     * @return List of result rows like arrays of objects.
     * @throws SQLException
     */
    public abstract List<Object[]> executeQuery(Connection connection, Sql sql, DataType... dataTypes) throws SQLException;

    /**
     * Write file to stream.
     *
     * @param source source file
     * @param target target output stream
     */
    protected void fileToStream(final File source, final OutputStream target) {
        try (final InputStream is = new BufferedInputStream(new FileInputStream(source));
             final OutputStream os = new BufferedOutputStream(target)) {
            final byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write stream to file.
     *
     * @param source source input stream
     * @param target target file
     */
    protected void streamToFile(final InputStream source, final File target) {
        try (
                final InputStream is = new BufferedInputStream(source);
                final OutputStream os = new BufferedOutputStream(new FileOutputStream(target, false))
        ) {
            final byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete file.
     *
     * @param file file to delete
     */
    protected void delete(final File file) {
        if (file != null) {
            if (file.isDirectory()) {
                final File[] subFiles = file.listFiles();
                for (final File subFile : subFiles != null ? subFiles : new File[0]) {
                    delete(subFile);
                }
            }
            if (!file.delete()) {
                throw new RuntimeException("Can't delete file!");
            }
        }
    }

    /**
     * Close closeable.
     *
     * @param closeable object to close
     */
    protected void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException ex) {
                LOGGER.warn("close", ex);
            }
        }
    }
}
