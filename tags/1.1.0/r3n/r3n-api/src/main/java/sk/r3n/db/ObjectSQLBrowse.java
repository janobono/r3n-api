package sk.r3n.db;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ObjectSQLBrowse<T> extends ObjectSQL<T> {

    public static void prepare(PreparedStatement preparedStatement, int index,
            SQLColumn column, Object value) throws Exception {
        switch (column.getDataType()) {
            case DataType.DATA_TYPE_STRING:
                preparedStatement.setString(index, (String) value);
                break;
            case DataType.DATA_TYPE_INTEGER:
                preparedStatement.setInt(index, (Integer) value);
                break;
            case DataType.DATA_TYPE_SHORT:
                preparedStatement.setShort(index, (Short) value);
                break;
            case DataType.DATA_TYPE_LONG:
                preparedStatement.setLong(index, (Long) value);
                break;
            case DataType.DATA_TYPE_BIG_DECIMAL:
                preparedStatement.setBigDecimal(index, (BigDecimal) value);
                break;
            case DataType.DATA_TYPE_TIMESTAMP:
                preparedStatement.setTimestamp(index, new Timestamp(
                        ((java.util.Date) value).getTime()));
                break;
            case DataType.DATA_TYPE_DATE:
                preparedStatement.setDate(index,
                        new Date(((java.util.Date) value).getTime()));
                break;
            case DataType.DATA_TYPE_TIME:
                preparedStatement.setTime(index,
                        new Time(((java.util.Date) value).getTime()));
                break;
            case DataType.DATA_TYPE_BOOLEAN:
                preparedStatement.setBoolean(index, (Boolean) value);
                break;
            default:
                throw new RuntimeException("UNSUPPORTED DATA TYPE!");
        }
    }
    private Connection actualConnection;
    private SQLGenerator actualSQLGenerator;
    private PreparedStatement actualPreparedStatement;
    private ResultSet actualResultSet;
    private int maxDBrows;
    private int page;
    private boolean next;
    private boolean autoClose;

    public ObjectSQLBrowse() {
        super();
        autoClose = true;
    }

    public void close() {
        actualSQLGenerator = null;
        close(actualPreparedStatement);
        actualPreparedStatement = null;
        close(actualResultSet);
        actualResultSet = null;
        try {
            if (actualConnection != null && autoClose) {
                actualConnection.close();
            }
        } catch (Exception e) {
        } finally {
            actualConnection = null;
        }
        page = 0;
        next = false;
    }

    public List<T> getActual() throws Exception {
        List<T> list = new ArrayList<>();
        if (maxDBrows == 0 || actualResultSet == null) {
            return list;
        }
        try {
            int position = (page * maxDBrows);
            if (actualResultSet != null) {
                actualResultSet.close();
                actualResultSet = null;
            }
            actualResultSet = actualPreparedStatement.executeQuery();
            if (position == 0) {
                actualResultSet.beforeFirst();
            } else {
                actualResultSet.absolute(position);
            }
            for (int i = 0; i < maxDBrows; i++) {
                if (actualResultSet.next()) {
                    list.add(getRow(actualConnection, actualSQLGenerator,
                            actualResultSet));
                } else {
                    break;
                }
            }
            next = actualResultSet.next();
            if (next) {
                actualResultSet.previous();
            }
        } catch (Exception e) {
            close();
            throw e;
        }
        return list;
    }

    public Connection getActualConnection() {
        return actualConnection;
    }

    public SQLGenerator getActualSQLGenerator() {
        return actualSQLGenerator;
    }

    public List<T> getNext() throws Exception {
        List<T> list = new ArrayList<>();
        try {
            if (maxDBrows == 0) {
                return list;
            }
            if (actualResultSet == null) {
                actualResultSet = actualPreparedStatement.executeQuery();
                page = 0;
            } else {
                page++;
            }
            for (int i = 0; i < maxDBrows; i++) {
                if (actualResultSet.next()) {
                    list.add(getRow(actualConnection, actualSQLGenerator,
                            actualResultSet));
                } else {
                    break;
                }
            }
            next = actualResultSet.next();
            if (next) {
                actualResultSet.previous();
            }
        } catch (Exception e) {
            close();
            throw e;
        }
        return list;
    }

    public List<T> getPrevious() throws Exception {
        List<T> list = new ArrayList<>();
        if (maxDBrows == 0 || actualResultSet == null) {
            return list;
        }
        try {
            page--;
            int position = (page * maxDBrows);
            if (position == 0) {
                actualResultSet.beforeFirst();
            } else {
                actualResultSet.absolute(position);
            }
            for (int i = 0; i < maxDBrows; i++) {
                if (actualResultSet.next()) {
                    list.add(getRow(actualConnection, actualSQLGenerator,
                            actualResultSet));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            close();
            throw e;
        }
        next = true;
        return list;
    }

    public boolean isNext() {
        return next;
    }

    public boolean isPrevious() {
        return page > 0;
    }

    public PreparedStatement open(Connection connection,
            SQLGenerator sqlGenerator, String sql) throws Exception {
        close();
        maxDBrows = getMaxDBRows();
        actualConnection = connection;
        actualSQLGenerator = sqlGenerator;
        actualPreparedStatement = actualConnection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return actualPreparedStatement;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }
}
