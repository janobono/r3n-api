package sk.r3n.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.sql.DELETE;
import sk.r3n.sql.INSERT;
import sk.r3n.sql.SELECT;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.UPDATE;

public abstract class SqlBuilder {

    private static final Log LOG = LogFactory.getLog(SqlBuilder.class);

    private List<Object> params;

    private File tmpDir;

    public File getTmpDir() {
        if (tmpDir == null) {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
            LOG.warn("Default tmp dir will be used - " + tmpDir.getAbsolutePath());
        }
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    public abstract String nextVal(Sequence sequence);

    public abstract long nextVal(Connection connection, Sequence sequence) throws SQLException;

    public List<Object> params() {
        if (params == null) {
            params = new ArrayList<Object>();
        }
        return params;
    }

    public abstract String select(SELECT select);

    public abstract List<Object[]> select(Connection connection, SELECT select);

    public abstract String selectCount(SELECT select);

    public abstract int selectCount(Connection connection, SELECT select);

    public abstract String insert(INSERT insert);

    public abstract Object insert(Connection connection, INSERT insert);

    public abstract String update(UPDATE update);

    public abstract void update(Connection connection, UPDATE update);

    public abstract String delete(DELETE delete);

    public abstract void delete(Connection connection, DELETE delete);

}
