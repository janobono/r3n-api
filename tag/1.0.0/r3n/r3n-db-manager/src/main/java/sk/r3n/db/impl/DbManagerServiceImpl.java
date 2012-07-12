package sk.r3n.db.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;

import org.osgi.service.component.ComponentContext;

import sk.r3n.action.IdActionService;
import sk.r3n.action.IdEvent;
import sk.r3n.action.IdEventListener;
import sk.r3n.app.AppHelp;
import sk.r3n.db.ConnectionCreator;
import sk.r3n.db.DbManagerService;
import sk.r3n.db.DbManagerServiceIO;
import sk.r3n.db.SQLGenerator;
import sk.r3n.properties.R3NProperties;
import sk.r3n.ui.UIService;
import sk.r3n.util.R3NException;

public class DbManagerServiceImpl implements DbManagerService, IdEventListener {

	public static final String POSTGRES_DRIVER = org.postgresql.Driver.class
			.getCanonicalName();
	public static final String MS_SQL_DRIVER = net.sourceforge.jtds.jdbc.Driver.class
			.getCanonicalName();

	protected static ComponentContext context;

	protected static R3NProperties r3nProperties;
	protected static AppHelp appHelp;
	protected static IdActionService idActionService;
	protected static UIService uiService;

	private DbManagerServiceIO dbManagerServiceIO;

	private ConnectionCreator connectionCreator;
	private SQLGenerator sqlGenerator;

	public DbManagerServiceImpl() {
		super();
	}

	protected void activate(ComponentContext context) {
		DbManagerServiceImpl.context = context;

		r3nProperties = (R3NProperties) context.locateService("R3NProperties");
		appHelp = (AppHelp) context.locateService("AppHelp");
		idActionService = (IdActionService) context
				.locateService("IdActionService");
		idActionService.add(DbManagerService.class.getCanonicalName(),
				ACTION_TEST);
		idActionService.addIdEventListener(
				DbManagerService.class.getCanonicalName(), this);
		uiService = (UIService) context.locateService("UIService");
		dbManagerServiceIO = (DbManagerServiceIO) context
				.locateService("DbManagerServiceIO");
	}

	private void createDB(ConnectionCreator connectionCreator,
			Properties properties) throws R3NException {
		// Existence check ------------------------------------------------
		Connection connection = null;
		try {
			connection = connectionCreator.getConnection(
					properties.getProperty(ConnectionCreator.NAME),
					properties.getProperty(ConnectionCreator.ADMIN_USER),
					properties.getProperty(ConnectionCreator.ADMIN_PASSWORD));
		} catch (Exception e) {
		}
		if (connection != null) {
			connectionCreator.close(connection);
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(ERR_CREATE_DB)), ERR_CREATE_DB);
		}
		// DB create ------------------------------------------------------
		if (properties.getProperty(ConnectionCreator.DRIVER).equals(
				POSTGRES_DRIVER)) {
			Statement statement = null;
			try {
				connection = connectionCreator.getConnection(properties
						.getProperty(ConnectionCreator.ADMIN_DB), properties
						.getProperty(ConnectionCreator.ADMIN_USER), properties
						.getProperty(ConnectionCreator.ADMIN_PASSWORD));
				connection.setAutoCommit(true);
				statement = connection.createStatement();
				statement.executeUpdate("CREATE DATABASE "
						+ properties.getProperty(ConnectionCreator.NAME)
						+ " WITH OWNER = "
						+ properties.getProperty(ConnectionCreator.USER));
			} catch (R3NException e) {
				throw e;
			} catch (Exception e) {
				throw new R3NException(ResourceBundle.getBundle(
						DbManagerService.class.getCanonicalName()).getString(
						Integer.toString(ERR_CREATE_DB)), ERR_CREATE_DB, e);
			} finally {
				connectionCreator.close(statement);
				connectionCreator.close(connection);
			}
		} else {
			Statement statement = null;
			CallableStatement cs = null;
			try {
				// DB
				connection = connectionCreator.getConnection(properties
						.getProperty(ConnectionCreator.ADMIN_DB), properties
						.getProperty(ConnectionCreator.ADMIN_USER), properties
						.getProperty(ConnectionCreator.ADMIN_PASSWORD));
				connection.setAutoCommit(true);
				statement = connection.createStatement();
				statement.executeUpdate("CREATE DATABASE ["
						+ properties.getProperty(ConnectionCreator.NAME)
						+ "] COLLATE Slovak_CS_AS");
				statement.executeUpdate("ALTER DATABASE ["
						+ properties.getProperty(ConnectionCreator.NAME)
						+ "] SET READ_COMMITTED_SNAPSHOT ON");
				connectionCreator.close(statement);
				connectionCreator.close(connection);
				// User
				connection = connectionCreator.getConnection(properties
						.getProperty(ConnectionCreator.NAME), properties
						.getProperty(ConnectionCreator.ADMIN_USER), properties
						.getProperty(ConnectionCreator.ADMIN_PASSWORD));
				connection.setAutoCommit(true);
				statement = connection.createStatement();
				statement.executeUpdate("CREATE USER ["
						+ properties.getProperty(ConnectionCreator.USER)
						+ "] FOR LOGIN ["
						+ properties.getProperty(ConnectionCreator.USER) + "]");
				cs = connection.prepareCall("{call sp_addrolemember(?,?)}");
				cs.setString(1, "db_owner");
				cs.setString(2, properties.getProperty(ConnectionCreator.USER));
				cs.execute();
				cs.setString(1, "db_datawriter");
				cs.setString(2, properties.getProperty(ConnectionCreator.USER));
				cs.execute();
				cs.setString(1, "db_datareader");
				cs.setString(2, properties.getProperty(ConnectionCreator.USER));
				cs.execute();
			} catch (Exception e) {
				throw new R3NException(ResourceBundle.getBundle(
						DbManagerService.class.getCanonicalName()).getString(
						Integer.toString(ERR_CREATE_DB)), ERR_CREATE_DB, e);
			} finally {
				connectionCreator.close(cs);
				connectionCreator.close(statement);
				connectionCreator.close(connection);
			}
		}
		// STRUCTURE
		try {
			dbManagerServiceIO.createStructure(connectionCreator, properties);
		} catch (Exception e) {
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(ERR_STRUCTURE)), ERR_STRUCTURE, e);
		}
	}

	private void createUser(ConnectionCreator connectionCreator,
			Properties properties) throws R3NException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = connectionCreator.getConnection(
					properties.getProperty(ConnectionCreator.ADMIN_DB),
					properties.getProperty(ConnectionCreator.ADMIN_USER),
					properties.getProperty(ConnectionCreator.ADMIN_PASSWORD));
			connection.setAutoCommit(true);
			statement = connection.createStatement();
			if (properties.getProperty(ConnectionCreator.DRIVER).equals(
					POSTGRES_DRIVER)) {
				try {
					statement.executeUpdate("ALTER USER "
							+ properties.getProperty(ConnectionCreator.USER)
							+ " PASSWORD '"
							+ properties
									.getProperty(ConnectionCreator.PASSWORD)
							+ "'");
				} catch (Exception ex) {
					statement.executeUpdate("CREATE USER "
							+ properties.getProperty(ConnectionCreator.USER)
							+ " PASSWORD '"
							+ properties
									.getProperty(ConnectionCreator.PASSWORD)
							+ "'");
				}
			} else {
				try {
					statement
							.executeUpdate("ALTER LOGIN ["
									+ properties
											.getProperty(ConnectionCreator.USER)
									+ "] WITH PASSWORD=N'"
									+ properties
											.getProperty(ConnectionCreator.PASSWORD)
									+ "', DEFAULT_DATABASE=[master], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON");
				} catch (Exception e) {
					statement
							.executeUpdate("CREATE LOGIN ["
									+ properties
											.getProperty(ConnectionCreator.USER)
									+ "] WITH PASSWORD=N'"
									+ properties
											.getProperty(ConnectionCreator.PASSWORD)
									+ "', DEFAULT_DATABASE=[master], CHECK_EXPIRATION=OFF, CHECK_POLICY=ON");
				}
			}
		} catch (R3NException e) {
			throw e;
		} catch (Exception e) {
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(ERR_CREATE_USER)), ERR_CREATE_USER, e);
		} finally {
			connectionCreator.close(statement);
			connectionCreator.close(connection);
		}
	}

	protected void deactivate(ComponentContext context) {
		try {
			if (connectionCreator != null)
				connectionCreator.close();
		} catch (Exception e) {
		}
		connectionCreator = null;

		DbManagerServiceImpl.context = null;

		r3nProperties = null;
		appHelp = null;
		idActionService.removeIdEventListener(
				DbManagerService.class.getCanonicalName(), this);
		idActionService.remove(DbManagerService.class.getCanonicalName(),
				ACTION_TEST);
		idActionService = null;
		uiService = null;
	}

	private Properties editProperties(Properties properties) throws Exception {
		Properties result = null;
		ConnectionPropertiesDialog connectionPropertiesDialog;
		connectionPropertiesDialog = new ConnectionPropertiesDialog(
				uiService.getRootFrame());
		if (connectionPropertiesDialog.init(properties)) {
			result = connectionPropertiesDialog.getProperties();
		} else {
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(ERR_CANCEL)), ERR_CANCEL);
		}
		return result;
	}

	@Override
	public ConnectionCreator getConnectionCreator() {
		return connectionCreator;
	}

	private ConnectionCreator getConnectionCreator(Properties properties) {
		if (properties.get(ConnectionCreator.DRIVER).equals(MS_SQL_DRIVER)) {
			return new MSSQLConnectionCreator(properties);
		}
		if (properties.get(ConnectionCreator.DRIVER).equals(POSTGRES_DRIVER)) {
			return new PostgreSQLConnectionCreator(properties);
		}
		return null;
	}

	private Properties getProperties() {
		Properties properties = new Properties();
		properties.put(ConnectionCreator.DRIVER, r3nProperties.get(DRIVER, ""));
		properties.put(ConnectionCreator.HOST, r3nProperties.get(HOST, ""));
		properties.put(ConnectionCreator.PORT, r3nProperties.get(PORT, ""));
		properties.put(ConnectionCreator.NAME, r3nProperties.get(NAME, ""));
		properties.put(ConnectionCreator.USER, r3nProperties.get(USER, ""));
		properties.put(ConnectionCreator.PASSWORD,
				r3nProperties.decrypt(r3nProperties.get(PASSWORD, "")));
		properties.put(ConnectionCreator.ADMIN_DB, "");
		properties.put(ConnectionCreator.ADMIN_USER, "");
		properties.put(ConnectionCreator.ADMIN_PASSWORD, "");
		return properties;
	}

	@Override
	public SQLGenerator getSQLGenerator() {
		return sqlGenerator;
	}

	private SQLGenerator getSQLGenerator(Properties properties) {
		if (properties.get(ConnectionCreator.DRIVER).equals(MS_SQL_DRIVER)) {
			return new MSSQLGenerator();
		}
		if (properties.get(ConnectionCreator.DRIVER).equals(POSTGRES_DRIVER)) {
			return new PostgreSQLGenerator();
		}
		return null;
	}

	public void idEvent(IdEvent idEvent) {
		if (idEvent.getGroupId().equals(
				DbManagerService.class.getCanonicalName())) {
			switch (idEvent.getActionId()) {
			case ACTION_TEST:
				testProperties((Properties) idEvent.getData()[0], false);
				break;
			}
		}
	}

	public void init() throws R3NException {
		Properties properties = getProperties();
		try {
			if (isNotSet(properties)) {
				// Databaza nie je nastavena
				ConnectionCreator connectionCreator = null;
				// editacia parametrov
				do {
					// Ak nie je tvorca pripojeni opakovanie procesu
					do {
						properties = editProperties(properties);
						// Vytvorenie tvorcu pripojeni
						connectionCreator = getConnectionCreator(properties);
						if (connectionCreator == null) {
							ResourceBundle bundle = ResourceBundle
									.getBundle(DbManagerServiceImpl.class
											.getCanonicalName());
							uiService.showMessageDialog(
									bundle.getString("TITLE"),
									bundle.getString("UNSUPPORTED"),
									UIService.MESSAGE_ACTION_ERROR);
						}
					} while (connectionCreator == null);
					try {
						// Vytvorenie databazy
						switch (connectionCreator.getConnStatus()) {
						case 0:
							break;
						case ERR_NOT_EXIST:
							createDB(connectionCreator, properties);
							if (connectionCreator.getConnStatus() == DbManagerService.ERR_AUTHENTICATION)
								createUser(connectionCreator, properties);
							break;
						case ERR_AUTHENTICATION:
							createUser(connectionCreator, properties);
							if (connectionCreator.getConnStatus() == DbManagerService.ERR_NOT_EXIST)
								createDB(connectionCreator, properties);
							break;
						default:
							testProperties(properties, false);
							break;
						}
					} catch (R3NException e) {
						ResourceBundle bundle = ResourceBundle
								.getBundle(DbManagerServiceImpl.class
										.getCanonicalName());
						uiService.showMessageDialog(bundle.getString("TITLE"),
								e.getLocalizedMessage(),
								UIService.MESSAGE_ACTION_ERROR);
					} finally {
						connectionCreator.close();
					}
				} while (!testProperties(properties, true));
				setProperties(properties);
			} else {
				// Databaza je nastavena
				while (!testProperties(properties, true)) {
					testProperties(properties, false);
					boolean edit = false;
					ConnectionCreator connectionCreator = getConnectionCreator(properties);
					if (connectionCreator == null) {
						edit = true;
					} else {
						connectionCreator.close();
						switch (connectionCreator.getConnStatus()) {
						case ERR_NOT_RUN:
							ResourceBundle bundle = ResourceBundle
									.getBundle(DbManagerServiceImpl.class
											.getCanonicalName());
							edit = uiService.showYesNoDialog(
									bundle.getString("TITLE"),
									bundle.getString("NOT_RUN_QUESTION"),
									UIService.MESSAGE_ACTION_WARNING) == UIService.ANSWER_NO;
							if (!edit)
								continue;
							else
								break;
						default:
							edit = true;
							break;
						}
					}
					// Vyhodnotene ze je nutny zasah do konfiguracie
					if (edit) {
						ResourceBundle bundle = ResourceBundle
								.getBundle(DbManagerServiceImpl.class
										.getCanonicalName());
						if (uiService.showYesNoDialog(
								bundle.getString("TITLE"),
								bundle.getString("SET_PROP_QUESTION"),
								UIService.MESSAGE_ACTION_WARNING) == UIService.ANSWER_YES) {
							// editacia parametrov
							do {
								// Ak nie je tvorca pripojeni opakovanie procesu
								do {
									properties = editProperties(properties);
									// Vytvorenie tvorcu pripojeni
									connectionCreator = getConnectionCreator(properties);
									if (connectionCreator == null) {
										bundle = ResourceBundle
												.getBundle(DbManagerServiceImpl.class
														.getCanonicalName());
										uiService
												.showMessageDialog(
														bundle.getString("TITLE"),
														bundle.getString("UNSUPPORTED"),
														UIService.MESSAGE_ACTION_ERROR);
									}
								} while (connectionCreator == null);
								try {
									// Vytvorenie databazy
									switch (connectionCreator.getConnStatus()) {
									case 0:
										break;
									case ERR_NOT_EXIST:
										createDB(connectionCreator, properties);
										if (connectionCreator.getConnStatus() == DbManagerService.ERR_AUTHENTICATION)
											createUser(connectionCreator,
													properties);
										break;
									case ERR_AUTHENTICATION:
										createUser(connectionCreator,
												properties);
										if (connectionCreator.getConnStatus() == DbManagerService.ERR_NOT_EXIST)
											createDB(connectionCreator,
													properties);
										break;
									default:
										testProperties(properties, false);
										break;
									}
								} catch (R3NException e) {
									bundle = ResourceBundle
											.getBundle(DbManagerServiceImpl.class
													.getCanonicalName());
									uiService.showMessageDialog(
											bundle.getString("TITLE"),
											e.getLocalizedMessage(),
											UIService.MESSAGE_ACTION_ERROR);
								} finally {
									connectionCreator.close();
								}
							} while (!testProperties(properties, true));
							setProperties(properties);
						} else {
							throw new R3NException(ResourceBundle.getBundle(
									DbManagerService.class.getCanonicalName())
									.getString(Integer.toString(ERR_CANCEL)),
									ERR_CANCEL);
						}
					}
				}
			}
		} catch (R3NException e) {
			throw e;
		} catch (Exception e) {
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(DbManagerService.ERR_UNKNOWN)),
					DbManagerService.ERR_UNKNOWN, e);
		}
		ConnectionCreator connectionCreator = null;
		try {
			connectionCreator = getConnectionCreator(properties);
			dbManagerServiceIO.init(connectionCreator, properties);
		} catch (Exception e) {
			throw new R3NException(ResourceBundle.getBundle(
					DbManagerService.class.getCanonicalName()).getString(
					Integer.toString(DbManagerService.ERR_IO_INIT)),
					DbManagerService.ERR_IO_INIT, e);
		} finally {
			connectionCreator.close();
		}
		this.connectionCreator = getConnectionCreator(getProperties());
		this.sqlGenerator = getSQLGenerator(getProperties());
	}

	public boolean isEnabled(IdEvent idEvent) {
		if (idEvent.getGroupId().equals(
				DbManagerService.class.getCanonicalName())) {
			switch (idEvent.getActionId()) {
			case ACTION_TEST:
				return true;
			}
		}
		return false;
	}

	private boolean isNotSet(Properties properties) {
		return properties.getProperty(ConnectionCreator.DRIVER, "").equals("")
				|| properties.getProperty(ConnectionCreator.HOST, "")
						.equals("")
				|| properties.getProperty(ConnectionCreator.PORT, "")
						.equals("")
				|| properties.getProperty(ConnectionCreator.NAME, "")
						.equals("")
				|| properties.getProperty(ConnectionCreator.USER, "")
						.equals("")
				|| properties.getProperty(ConnectionCreator.PASSWORD, "")
						.equals("");
	}

	private void setProperties(Properties properties) {
		ConnectionCreator connectionCreator = getConnectionCreator(properties);
		r3nProperties.set(DRIVER,
				properties.getProperty(ConnectionCreator.DRIVER));
		r3nProperties.set(HOST, properties.getProperty(ConnectionCreator.HOST));
		r3nProperties.set(PORT, properties.getProperty(ConnectionCreator.PORT));
		r3nProperties.set(NAME, properties.getProperty(ConnectionCreator.NAME));
		r3nProperties.set(USER, properties.getProperty(ConnectionCreator.USER));
		r3nProperties.set(PASSWORD, r3nProperties.encrypt(properties
				.getProperty(ConnectionCreator.PASSWORD)));
		connectionCreator.close();
	}

	private boolean testProperties(Properties properties, boolean silent) {
		boolean result = false;
		// Vytvorenie tvorcu pripojeni
		ConnectionCreator connectionCreator = getConnectionCreator(properties);
		// Ak nie je tvorca pripojeni opakovanie procesu
		if (connectionCreator == null) {
			if (!silent) {
				ResourceBundle bundle = ResourceBundle
						.getBundle(DbManagerServiceImpl.class
								.getCanonicalName());
				uiService.showMessageDialog(bundle.getString("TITLE"),
						bundle.getString("UNSUPPORTED"),
						UIService.MESSAGE_ACTION_ERROR);
			}
			return result;
		}
		switch (connectionCreator.getConnStatus()) {
		case ConnectionCreator.CONN_STATUS_UNKNOWN:
			if (!silent) {
				uiService.showMessageDialog(
						ResourceBundle.getBundle(
								DbManagerServiceImpl.class.getCanonicalName())
								.getString("TITLE"),
						ResourceBundle.getBundle(
								DbManagerService.class.getCanonicalName())
								.getString(Integer.toString(ERR_UNKNOWN)),
						UIService.MESSAGE_ACTION_ERROR);
			}
			return result;
		case ConnectionCreator.CONN_STATUS_NOT_RUN:
			if (!silent) {
				uiService.showMessageDialog(
						ResourceBundle.getBundle(
								DbManagerServiceImpl.class.getCanonicalName())
								.getString("TITLE"),
						ResourceBundle.getBundle(
								DbManagerService.class.getCanonicalName())
								.getString(Integer.toString(ERR_NOT_RUN)),
						UIService.MESSAGE_ACTION_WARNING);
			}
			return result;
		case ConnectionCreator.CONN_STATUS_NOT_EXIST:
			if (!silent) {
				uiService.showMessageDialog(
						ResourceBundle.getBundle(
								DbManagerServiceImpl.class.getCanonicalName())
								.getString("TITLE"),
						ResourceBundle.getBundle(
								DbManagerService.class.getCanonicalName())
								.getString(Integer.toString(ERR_NOT_EXIST)),
						UIService.MESSAGE_ACTION_ERROR);
			}
			return result;
		case ConnectionCreator.CONN_STATUS_AUTHENTICATION:
			if (!silent) {
				uiService.showMessageDialog(
						ResourceBundle.getBundle(
								DbManagerServiceImpl.class.getCanonicalName())
								.getString("TITLE"),
						ResourceBundle
								.getBundle(
										DbManagerService.class
												.getCanonicalName()).getString(
										Integer.toString(ERR_AUTHENTICATION)),
						UIService.MESSAGE_ACTION_ERROR);
			}
			return result;
		case 0:
			if (!silent) {
				uiService.showMessageDialog(
						ResourceBundle.getBundle(
								DbManagerServiceImpl.class.getCanonicalName())
								.getString("TITLE"),
						ResourceBundle.getBundle(
								DbManagerService.class.getCanonicalName())
								.getString(Integer.toString(0)),
						UIService.MESSAGE_ACTION_INFORMATION);
			}
			result = true;
			return result;
		}
		connectionCreator.close();
		return result;
	}
}
