package sk.r3n.example.test.postgres;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import sk.r3n.dto.Dto;
import sk.r3n.example.postgres.SEQUENCE;
import sk.r3n.example.postgres.TABLE;
import sk.r3n.example.postgres.T_BASE_TYPES;
import sk.r3n.example.postgres.T_JOIN;
import sk.r3n.example.postgres.dto.TBaseTypes;
import sk.r3n.example.postgres.dto.TJoin;
import sk.r3n.jdbc.PostgreSqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.ColumnFunction;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;
import sk.r3n.util.DateUtil;
import sk.r3n.util.FileUtil;
import sk.r3n.util.ScDf;

public class PostgresTest {

    private static final Log LOG = LogFactory.getLog(PostgresTest.class);

    public PostgresTest() {
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
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://10.0.0.8:5432/test", "test", "test");

            SqlUtil.runSqlScript(connection, PostgresTest.class.getResourceAsStream("/clean_postgres.sql"));
            SqlUtil.runSqlScript(connection, PostgresTest.class.getResourceAsStream("/install_postgres.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Test
    public void postgresTest() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://10.0.0.8:5432/test", "test", "test");

            assertTrue(connection.getAutoCommit());

            insertTest(connection);

            selectsTest(connection);

            updateTest(connection);

            deleteTest(connection);

        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            SqlUtil.close(connection);
        }
    }

    private void insertTest(Connection connection) throws SQLException, IOException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();
        Dto dto = new Dto();

        for (int i = 0; i < 10; i++) {
            TBaseTypesSO tBaseTypesSO = new TBaseTypesSO();
            tBaseTypesSO.setTShort((short) i);
            tBaseTypesSO.setTInteger(i);
            tBaseTypesSO.setTLong((long) i);
            tBaseTypesSO.setTBigDecimal(new BigDecimal(i));
            tBaseTypesSO.setTStringChar("char" + i);
            tBaseTypesSO.setTStringText("text" + i);
            tBaseTypesSO.setTStringVarchar("varcharč" + i);
            tBaseTypesSO.setTStringScdf(ScDf.toScDf(tBaseTypesSO.getTStringVarchar()));
            tBaseTypesSO.setTBlob(createFile(i));
            tBaseTypesSO.setTTimeStamp(new Date());
            tBaseTypesSO.setTTime(DateUtil.getTimeOnly(tBaseTypesSO.getTTimeStamp()));
            tBaseTypesSO.setTDate(DateUtil.getDateOnly(tBaseTypesSO.getTTimeStamp()));
            tBaseTypesSO.setTBoolean(i % 2 == 0);

            TBaseTypes tBaseTypes = new TBaseTypes();
            dto.objToObj(tBaseTypesSO, tBaseTypes);

            Object[] row = dto.toArray(tBaseTypes, T_BASE_TYPES.columns());
            row[0] = SEQUENCE.TEST_SEQUENCE();

            Query query = new Query();
            query.INSERT().INTO(TABLE.T_BASE_TYPES(), T_BASE_TYPES.columns()).VALUES(row).RETURNING(T_BASE_TYPES.ID());
            Long id = (Long) sqlBuilder.executeUpdate(connection, query);

            for (int j = 0; j < 10; j++) {
                TJoinSO tJoinSO = new TJoinSO();
                tJoinSO.setTBaseTypesFk(id);
                tJoinSO.setTJoinString("join" + i);

                TJoin tJoin = new TJoin();
                dto.objToObj(tJoinSO, tJoin);

                row = dto.toArray(tJoin, T_JOIN.columns());
                row[0] = SEQUENCE.TEST_SEQUENCE();

                query = new Query();
                query.INSERT().INTO(TABLE.T_JOIN(), T_JOIN.columns()).VALUES(row);
                sqlBuilder.executeUpdate(connection, query);
            }
        }
    }

    private void selectsTest(Connection connection) throws SQLException {
        paginatedSubSelectTest(connection);
        subSelectTest(connection);
        paginatedUnionSelectTest(connection);
        unionSelectTest(connection);
        paginatedSelectTest(connection);
        selectTest(connection);
    }

    private void paginatedSubSelectTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Query query = new Query();
        query.SELECT(T_BASE_TYPES.columns()).page(0, 5)
                .FROM(new Query().SELECT(T_BASE_TYPES.columns()).FROM(TABLE.T_BASE_TYPES()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        List<Object[]> rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 5);

        query = new Query();
        query.SELECT(T_BASE_TYPES.columns()).page(0, 5)
                .FROM(new Query().SELECT(T_BASE_TYPES.columns())
                        .FROM(TABLE.T_BASE_TYPES())
                        .INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 5);
    }

    private void subSelectTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Query query = new Query();
        query.SELECT(T_BASE_TYPES.columns())
                .FROM(new Query().SELECT(T_BASE_TYPES.columns()).FROM(TABLE.T_BASE_TYPES()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        List<Object[]> rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 10);

        query = new Query();
        query.SELECT(T_BASE_TYPES.columns())
                .FROM(new Query().SELECT(T_BASE_TYPES.columns())
                        .FROM(TABLE.T_BASE_TYPES())
                        .INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 100);
    }

    private void paginatedUnionSelectTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Query query = new Query();
        query.SELECT(T_BASE_TYPES.columns("tst1")).page(0, 5)
                .FROM(
                        new Query().SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")),
                        new Query().SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        List<Object[]> rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 5);

        query = new Query();
        query.SELECT(T_BASE_TYPES.columns("tst1")).page(0, 5)
                .FROM(
                        new Query().SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst1")),
                        new Query().SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 5);
    }

    private void unionSelectTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Query query = new Query();
        query.SELECT(T_BASE_TYPES.columns("tst1"))
                .FROM(
                        new Query().SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")),
                        new Query().SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        List<Object[]> rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 20);

        query = new Query();
        query.SELECT(T_BASE_TYPES.columns("tst1"))
                .FROM(
                        new Query().SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst1")),
                        new Query().SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 200);
    }

    private void paginatedSelectTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();
        Dto dto = new Dto();

        Query query = new Query();
        query.SELECT(T_BASE_TYPES.columns()).page(0, 1).FROM(TABLE.T_BASE_TYPES()).ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        List<Object[]> rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 1);
        TBaseTypes tBaseTypes = new TBaseTypes();
        dto.fill(tBaseTypes, rows.get(0), T_BASE_TYPES.columns());
        assertTrue(tBaseTypes.getTInteger() == 0);
        TBaseTypesSO tBaseTypesSO = new TBaseTypesSO();
        dto.objToObj(tBaseTypes, tBaseTypesSO);
        assertTrue(tBaseTypesSO.getTInteger() == 0);

        for (int i = 0; i < 10; i++) {
            query = new Query();
            query.SELECT(T_BASE_TYPES.columns()).page(i, 10)
                    .FROM(TABLE.T_BASE_TYPES())
                    .INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID())
                    .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC)
                    .ORDER_BY(T_JOIN.T_JOIN_STRING(), Order.ASC);

            rows = sqlBuilder.executeQuery(connection, query);
            assertTrue(rows.size() == 10);
        }
    }

    private void selectTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Query query = new Query();
        query.SELECT(T_BASE_TYPES.columns()).COUNT()
                .FROM(TABLE.T_BASE_TYPES());
        List<Object[]> rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 1);
        assertTrue(((Integer) rows.get(0)[0]) == 10);

        query = new Query();
        query.SELECT(new ColumnFunction("MAX({0})", T_BASE_TYPES.T_INTEGER().getDataType(), T_BASE_TYPES.T_INTEGER()))
                .FROM(TABLE.T_BASE_TYPES());
        rows = sqlBuilder.executeQuery(connection, query);
        assertTrue(rows.size() == 1);
        assertTrue(((Integer) rows.get(0)[0]) == 9);
    }

    private void updateTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Date date = new Date();

        Query query = new Query();

        query.UPDATE(TABLE.T_BASE_TYPES())
                .SET(T_BASE_TYPES.T_TIME_STAMP(), date)
                .WHERE(T_BASE_TYPES.T_INTEGER(), Condition.MORE, 5);
        sqlBuilder.executeUpdate(connection, query);

        query = new Query();

        query.UPDATE(TABLE.T_BASE_TYPES())
                .SET(T_BASE_TYPES.T_TIME_STAMP(), date)
                .WHERE(T_BASE_TYPES.ID(), Condition.IN, new Query().SELECT(T_BASE_TYPES.ID()).FROM(TABLE.T_BASE_TYPES())
                        .WHERE(T_BASE_TYPES.T_TIME_STAMP(), Condition.LESS, date));

        sqlBuilder.executeUpdate(connection, query);
    }

    private void deleteTest(Connection connection) throws SQLException {
        PostgreSqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Query query = new Query();

        query.DELETE().FROM(TABLE.T_BASE_TYPES())
                .WHERE(T_BASE_TYPES.T_INTEGER(), Condition.MORE, 5);
        sqlBuilder.executeUpdate(connection, query);

        query = new Query();

        query.DELETE().FROM(TABLE.T_BASE_TYPES())
                .WHERE(T_BASE_TYPES.ID(), Condition.IN, new Query().SELECT(T_BASE_TYPES.ID()).FROM(TABLE.T_BASE_TYPES())
                        .WHERE(T_BASE_TYPES.T_INTEGER(), Condition.EQUALS_LESS, 5));

        sqlBuilder.executeUpdate(connection, query);
    }

    private File createFile(int i) throws IOException {
        File file = File.createTempFile("file" + i, ".tst");
        FileUtil.write(file, Integer.toString(i).getBytes());
        return file;
    }
}
