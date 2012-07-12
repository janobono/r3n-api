package sk.r3n.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public abstract class ObjectSQL<T> {

	private static int maxDBRows = 250;

	public static int getMaxDBRows() {
		return maxDBRows;
	}

	public static void setMaxDBRows(int maxDBRows) {
		ObjectSQL.maxDBRows = maxDBRows;
	}

	public ObjectSQL() {
		super();
	}

	protected void close(ResultSet resultSet) {
		try {
			if (resultSet != null)
				resultSet.close();
		} catch (Exception e) {
		}
	}

	protected void close(Statement statement) {
		try {
			if (statement != null)
				statement.close();
		} catch (Exception e) {
		}
	}

	protected abstract T getRow(Connection connection,
			SQLGenerator sqlGenerator, ResultSet resultSet) throws Exception;

}
