package sk.r3n.jdbc;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.r3n.example.ADDRESS;
import sk.r3n.example.PERSON;
import sk.r3n.example.TABLE;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;
import sk.r3n.util.FileUtil;

public class H2Test {

    private static final Log LOG = LogFactory.getLog(H2Test.class);

    public H2Test() {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void init() {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("user.dir") + "/target/test", "sa", "sa");

            SqlUtil.runSqlScript(connection, SqlBuilderTest.class.getResourceAsStream("/install.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @After
    public void clean() {
        FileUtil.delete(new File(System.getProperty("user.dir") + "/target/test.mv.db"));
    }

    @Test
    public void h2Test() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("user.dir") + "/target/test", "sa", "sa");

            assertTrue(connection.getAutoCommit());

            H2SqlBuilder sqlBuilder = new H2SqlBuilder();

            Column[] columns = Arrays.copyOfRange(PERSON.columns(), 1, PERSON.columns().length);
            Object[] values;
            for (int i = 0; i < 10; i++) {
                values = new Object[]{
                    new Date(), "creator", (short) 0, "012345678" + i,
                    "first_name" + i, "first_name" + i, "last_name" + i, "last_name" + i, new Date(), "note" + i};
                Query query = new Query();
                query.INSERT().INTO(TABLE.PERSON(), columns).VALUES(values).RETURNING(PERSON.ID());

                Long personId = (Long) sqlBuilder.executeUpdate(connection, query);

                if (i % 2 == 0) {
                    query = new Query();
                    query.INSERT().INTO(TABLE.ADDRESS(), Arrays.copyOfRange(ADDRESS.columns(), 1, ADDRESS.columns().length))
                            .VALUES(personId, (short) 0, "street" + i, "city" + i, "state" + i, "960 01", new Date(), null, BigDecimal.ONE);
                    sqlBuilder.executeUpdate(connection, query);
                }
            }

            Query query = new Query();
            query.UPDATE(TABLE.PERSON()).SET(PERSON.CREATOR(), "test").WHERE(PERSON.CREATOR(), Condition.EQUALS, "creator");
            sqlBuilder.executeUpdate(connection, query);

            query = new Query();
            query.SELECT(PERSON.FIRST_NAME(), ADDRESS.STREET()).interval(0, 5)
                    .FROM(TABLE.PERSON())
                    .LEFT_JOIN(TABLE.ADDRESS(), ADDRESS.PERSON_FK(), PERSON.ID());

            List<Object[]> rows = sqlBuilder.executeQuery(connection, query);
            assertTrue(rows.size() == 5);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            SqlUtil.close(connection);
        }
    }

}
