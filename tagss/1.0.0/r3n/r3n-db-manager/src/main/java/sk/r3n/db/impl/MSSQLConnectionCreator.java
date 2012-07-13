package sk.r3n.db.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;

import sk.r3n.db.ConnectionCreator;
import sk.r3n.db.DbManagerService;
import sk.r3n.util.R3NException;

public class MSSQLConnectionCreator implements ConnectionCreator {

	private static final Logger LOGGER = Logger
			.getLogger(ConnectionCreator.class.getCanonicalName());

	private BasicDataSource ds;

	private Properties properties;

	public MSSQLConnectionCreator(Properties properties) {
		super();
		this.properties = new Properties();
		this.properties.putAll(properties);
	}

	@Override
	public void close() {
		try {
			if (ds != null)
				ds.close();
		} catch (Exception e) {
		}
		ds = null;
	}

	@Override
	public void close(Connection connection) {
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void close(ResultSet resultSet) {
		try {
			if (resultSet != null)
				resultSet.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void close(Statement statement) {
		try {
			if (statement != null)
				statement.close();
		} catch (Exception e) {
		}
	}

	@Override
	public Connection getConnection() throws R3NException {
		Connection connection = null;
		try {
			if (ds == null) {
				ds = new BasicDataSource();
				ds.setDriverClassName(DbManagerServiceImpl.MS_SQL_DRIVER);
				ds.setUsername(properties.getProperty(USER));
				ds.setPassword(properties.getProperty(PASSWORD));
				ds.setUrl(getConnectionURL());
				ds.setMinIdle(5);
			}
			LOGGER.finer("NumActive: " + ds.getNumActive());
			LOGGER.finer("NumIdle: " + ds.getNumIdle());
			connection = ds.getConnection();
			connection.setAutoCommit(false);
			connection
					.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		} catch (Exception exception) {
			close(connection);
			close();
			if (exception instanceof SQLException) {
				SQLException sqlException = (SQLException) exception;
				String sqlState = sqlException.getSQLState();
				if (sqlState.equals("28000"))
					throw new R3NException(
							ResourceBundle
									.getBundle(
											DbManagerService.class
													.getCanonicalName())
									.getString(
											Integer.toString(DbManagerService.ERR_AUTHENTICATION)),
							DbManagerService.ERR_AUTHENTICATION, exception);
				else if (sqlState.equals("S1000"))
					throw new R3NException(
							ResourceBundle
									.getBundle(
											DbManagerService.class
													.getCanonicalName())
									.getString(
											Integer.toString(DbManagerService.ERR_NOT_EXIST)),
							DbManagerService.ERR_NOT_EXIST, exception);
				else if (sqlState.equals("08S01"))
					throw new R3NException(
							ResourceBundle
									.getBundle(
											DbManagerService.class
													.getCanonicalName())
									.getString(
											Integer.toString(DbManagerService.ERR_NOT_RUN)),
							DbManagerService.ERR_NOT_RUN, exception);
			}
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(DbManagerService.ERR_UNKNOWN)),
					DbManagerService.ERR_UNKNOWN, exception);
		}
		return connection;
	}

	public Connection getConnection(String name, String user, String password)
			throws R3NException {
		Connection connection = null;
		try {
			Class.forName(DbManagerServiceImpl.MS_SQL_DRIVER);
			Properties properties = new Properties();
			properties.put(USER, user);
			properties.put(PASSWORD, password);
			connection = DriverManager.getConnection(getConnectionURL(name),
					properties);
			connection.setAutoCommit(false);
			connection
					.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		} catch (Exception exception) {
			close(connection);
			if (exception instanceof SQLException) {
				SQLException sqlException = (SQLException) exception;
				String sqlState = sqlException.getSQLState();
				if (sqlState.equals("28000"))
					throw new R3NException(
							ResourceBundle
									.getBundle(
											DbManagerService.class
													.getCanonicalName())
									.getString(
											Integer.toString(DbManagerService.ERR_AUTHENTICATION)),
							DbManagerService.ERR_AUTHENTICATION, exception);
				else if (sqlState.equals("S1000"))
					throw new R3NException(
							ResourceBundle
									.getBundle(
											DbManagerService.class
													.getCanonicalName())
									.getString(
											Integer.toString(DbManagerService.ERR_NOT_EXIST)),
							DbManagerService.ERR_NOT_EXIST, exception);
				else if (sqlState.equals("08S01"))
					throw new R3NException(
							ResourceBundle
									.getBundle(
											DbManagerService.class
													.getCanonicalName())
									.getString(
											Integer.toString(DbManagerService.ERR_NOT_RUN)),
							DbManagerService.ERR_NOT_RUN, exception);
			}
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(DbManagerService.ERR_UNKNOWN)),
					DbManagerService.ERR_UNKNOWN, exception);
		}
		return connection;
	}

	@Override
	public String getConnectionURL() {
		return getConnectionURL(properties.getProperty(NAME));
	}

	private String getConnectionURL(String name) {
		StringBuffer buff = new StringBuffer();
		buff.append("jdbc:jtds:sqlserver://");
		buff.append(properties.getProperty(HOST));
		buff.append(":");
		buff.append(properties.getProperty(PORT));
		buff.append("/");
		buff.append(name);
		return buff.toString();
	}

	public int getConnStatus() {
		Connection connection = null;
		try {
			connection = getConnection(properties.getProperty(NAME),
					properties.getProperty(USER),
					properties.getProperty(PASSWORD));
			return 0;
		} catch (R3NException e) {
			return e.getErrorCode();
		} finally {
			close(connection);
		}
	}

	@Override
	public void rollback(Connection connection) {
		try {
			if (connection != null)
				connection.rollback();
		} catch (Exception e) {
		}
	}

}
