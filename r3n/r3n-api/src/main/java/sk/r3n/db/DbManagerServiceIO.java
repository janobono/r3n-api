package sk.r3n.db;

import java.util.Properties;

import sk.r3n.db.ConnectionCreator;

public interface DbManagerServiceIO {

	public void createStructure(ConnectionCreator connectionCreator,
			Properties properties) throws Exception;

	public void init(ConnectionCreator connectionCreator, Properties properties)
			throws Exception;

}
