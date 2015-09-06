package sk.r3n.example.test.h2;

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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.r3n.dto.Dto;
import sk.r3n.example.h2.TABLE;
import sk.r3n.example.h2.dto.TBaseTypes;
import sk.r3n.example.h2.dto.TJoin;
import sk.r3n.example.h2.T_BASE_TYPES;
import sk.r3n.example.h2.T_JOIN;
import sk.r3n.jdbc.H2SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.Column;
import sk.r3n.sql.ColumnFunction;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;
import sk.r3n.util.DateUtil;
import sk.r3n.util.FileUtil;
import sk.r3n.util.ScDf;

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
        File file = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + "test.mv.db");
        if (file.exists()) {
            FileUtil.delete(file);
        }

        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/test", "sa", "sa");

            SqlUtil.runSqlScript(connection, H2Test.class.getResourceAsStream("/install_h2.sql"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Test
    public void h2Test() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + System.getProperty("java.io.tmpdir") + "/test", "sa", "sa");

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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();
        Dto dto = new Dto();

        Column[] baseTypesColumns = new Column[]{
            T_BASE_TYPES.T_SHORT_SMALLINT(),
            T_BASE_TYPES.T_SHORT_TINYINT(),
            T_BASE_TYPES.T_INTEGER(),
            T_BASE_TYPES.T_LONG(),
            T_BASE_TYPES.T_BIG_DECIMAL(),
            T_BASE_TYPES.T_STRING(),
            T_BASE_TYPES.T_STRING_SCDF(),
            T_BASE_TYPES.T_BLOB(),
            T_BASE_TYPES.T_TIME_STAMP(),
            T_BASE_TYPES.T_TIME(),
            T_BASE_TYPES.T_DATE(),
            T_BASE_TYPES.T_BOOLEAN()
        };
        Column[] joinColumns = new Column[]{
            T_JOIN.T_BASE_TYPES_FK(),
            T_JOIN.T_JOIN_STRING()
        };

        for (int i = 0; i < 10; i++) {
            TBaseTypesSO tBaseTypesSO = new TBaseTypesSO();
            tBaseTypesSO.setTShortSmallint((short) i);
            tBaseTypesSO.setTShortTinyint((short) i);
            tBaseTypesSO.setTInteger(i);
            tBaseTypesSO.setTLong((long) i);
            tBaseTypesSO.setTBigDecimal(new BigDecimal(i));
            tBaseTypesSO.setTString("strá" + i);
            tBaseTypesSO.setTStringScdf(ScDf.toScDf(tBaseTypesSO.getTString()));
            tBaseTypesSO.setTBlob(createFile(i));
            tBaseTypesSO.setTTimeStamp(new Date());
            tBaseTypesSO.setTTime(DateUtil.getTimeOnly(tBaseTypesSO.getTTimeStamp()));
            tBaseTypesSO.setTDate(DateUtil.getDateOnly(tBaseTypesSO.getTTimeStamp()));
            tBaseTypesSO.setTBoolean(i % 2 == 0);

            TBaseTypes tBaseTypes = new TBaseTypes();
            dto.objToObj(tBaseTypesSO, tBaseTypes);

            Query query = new Query();
            query.INSERT().INTO(TABLE.T_BASE_TYPES(), baseTypesColumns)
                    .VALUES(dto.toArray(tBaseTypes, baseTypesColumns));
            sqlBuilder.executeUpdate(connection, query);

            for (int j = 0; j < 10; j++) {
                TJoinSO tJoinSO = new TJoinSO();
                tJoinSO.setTBaseTypesFk((long) i + 1);
                tJoinSO.setTJoinString("join" + i);

                TJoin tJoin = new TJoin();
                dto.objToObj(tJoinSO, tJoin);

                query = new Query();
                query.INSERT().INTO(TABLE.T_JOIN(), joinColumns)
                        .VALUES(dto.toArray(tJoin, joinColumns));
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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();

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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();

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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();

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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();

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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();
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
                    .ORDER_BY(T_BASE_TYPES.ID(), Order.ASC)
                    .ORDER_BY(T_JOIN.ID(), Order.ASC);

            rows = sqlBuilder.executeQuery(connection, query);
            assertTrue(rows.size() == 10);
        }
    }

    private void selectTest(Connection connection) throws SQLException {
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();

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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();

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
        H2SqlBuilder sqlBuilder = new H2SqlBuilder();

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
