package sk.r3n.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.r3n.sql.Delete;
import sk.r3n.sql.Insert;
import sk.r3n.sql.Select;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.Update;

public abstract class SqlBuilder {

    private static final Logger LOGGER = Logger.getLogger(SqlBuilder.class.getCanonicalName());

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

    public abstract QueryResult nextVal(Sequence sequence);

    public abstract long nextVal(Connection connection, Sequence sequence) throws SQLException;

    public abstract QueryResult select(Select select);

    public abstract List<Object[]> select(Connection connection, Select select) throws SQLException;

    public abstract QueryResult insert(Insert insert);

    public abstract Object insert(Connection connection, Insert insert) throws SQLException;

    public abstract QueryResult update(Update update);

    public abstract void update(Connection connection, Update update) throws SQLException;

    public abstract QueryResult delete(Delete delete);

    public abstract void delete(Connection connection, Delete delete) throws SQLException;

}
