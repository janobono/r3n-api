package sk.r3n.db;

import java.util.Properties;

public interface DbManagerServiceIO {

    public void createStructure(ConnectionCreator connectionCreator,
            Properties properties) throws Exception;

    public void init(ConnectionCreator connectionCreator, SQLGenerator sqlGenerator, Properties properties)
            throws Exception;
}
