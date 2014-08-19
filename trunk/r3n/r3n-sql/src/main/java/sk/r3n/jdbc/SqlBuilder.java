package sk.r3n.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sk.r3n.jdbc.query.OraQueryBuilder;
import sk.r3n.sql.Column;
import sk.r3n.sql.DELETE;
import sk.r3n.sql.INSERT;
import sk.r3n.sql.SELECT;
import sk.r3n.sql.Sequence;
import sk.r3n.sql.UPDATE;
import sk.r3n.util.FileUtil;

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

    public abstract Result<Object[]> select(Connection connection, SELECT select);

    public abstract String selectCount(SELECT select);

    public abstract int selectCount(Connection connection, SELECT select);

    public abstract String insert(INSERT insert);

    public abstract Object insert(Connection connection, INSERT insert);

    public abstract String update(UPDATE update);

    public abstract void update(Connection connection, UPDATE update);

    public abstract String delete(DELETE delete);

    public abstract void delete(Connection connection, DELETE delete);

    protected Object[] getRow(ResultSet resultSet, Column... columns) throws SQLException {
        Object[] result = new Result[columns.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = getColumn(resultSet, i + 1, columns[i]);
        }

        return result;
    }

    protected Object getColumn(ResultSet resultSet, int index, Column column) throws SQLException {
        Object result = null;

        if (resultSet.getObject(index) != null) {
            switch (column.getDataType()) {
                case BOOLEAN:
                    result = resultSet.getBoolean(index);
                    break;
                case STRING:
                    result = resultSet.getString(index);
                    break;
                case SHORT:
                    result = resultSet.getShort(index);
                    break;
                case INTEGER:
                    result = resultSet.getInt(index);
                    break;
                case LONG:
                    result = resultSet.getLong(index);
                    break;
                case BIG_DECIMAL:
                    result = resultSet.getBigDecimal(index);
                    break;
                case DATE:
                    java.sql.Date date = resultSet.getDate(index);
                    result = new Date(date.getTime());
                    break;
                case TIME:
                    java.sql.Time time = resultSet.getTime(index);
                    result = new Date(time.getTime());
                    break;
                case TIME_STAMP:
                    java.sql.Timestamp timestamp = resultSet.getTimestamp(index);
                    result = new Date(timestamp.getTime());
                    break;
                case BLOB:
                    File file = null;
                    try {
                        file = File.createTempFile("SQL", ".BIN", getTmpDir());
                        Blob blob = resultSet.getBlob(index);
                        FileUtil.streamToFile(blob.getBinaryStream(1, blob.length()), file);
                        result = file;
                    } catch (IOException e) {
                        if (file != null) {
                            file.delete();
                        }
                        throw new SQLException(e);
                    }
                    break;
            }
        }
        return result;
    }

}
