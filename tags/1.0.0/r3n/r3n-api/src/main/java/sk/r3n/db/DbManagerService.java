package sk.r3n.db;

import sk.r3n.util.R3NException;

public interface DbManagerService {

	public int ACTION_TEST = 10;

	public int ERR_UNKNOWN = 10;
	public int ERR_NOT_SET = 20;
	public int ERR_NOT_RUN = 30;
	public int ERR_NOT_EXIST = 40;
	public int ERR_AUTHENTICATION = 50;
	public int ERR_CANCEL = 60;
	
	public int ERR_CREATE_DB = 70;
	public int ERR_CREATE_USER = 80;
	public int ERR_STRUCTURE = 90;
	public int ERR_IO_INIT = 100;
	
	public String DRIVER = "sk.r3n.db.DRIVER";
	public String HOST = "sk.r3n.db.HOST";
	public String PORT = "sk.r3n.db.PORT";
	public String NAME = "sk.r3n.db.NAME";
	public String USER = "sk.r3n.db.USER";
	public String PASSWORD = "sk.r3n.db.PASSWORD";

	public void init() throws R3NException;
	
	public ConnectionCreator getConnectionCreator();
	
	public SQLGenerator getSQLGenerator();
	
}
