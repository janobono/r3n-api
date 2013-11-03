package sk.r3n.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class SqlUtilTest {

    private ConnectionService connectionService;

    @Before
    public void tearUp() {
        connectionService = ConnectionServiceFactory.createConnectionService(DbType.H2);
        connectionService.setProperty(DbProperty.NAME.name(), "testDb");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void dbTest() throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionService.getConnection();
            SqlUtil.execute(connection, "CREATE TABLE TEST_DATA(ID INTEGER, VAL VARCHAR(32))");
            SqlUtil.execute(connection, "INSERT INTO TEST_DATA(ID, VAL) VALUES (?,?)", new Object[]{1, "value"});
            preparedStatement = SqlUtil.prepare(connection, "SELECT * FROM TEST_DATA", new Object[]{});
            preparedStatement.close();
            preparedStatement = SqlUtil.prepare(connection, "SELECT * FROM TEST_DATA WHERE ID = ?", new Object[]{1});
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                assertEquals("value", resultSet.getString(2));
            }
        } finally {
            SqlUtil.close(resultSet);
            SqlUtil.close(preparedStatement);
            SqlUtil.close(connection);
        }
    }

}
