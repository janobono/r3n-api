package sk.r3n.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author jan
 */
public interface Sequence extends Serializable {

    public String nextval();

    public long nextVal(Connection connection) throws SQLException;

}
