/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.example.test.ora;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogManager;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import sk.r3n.dto.Dto;
import sk.r3n.example.ora.SEQUENCE;
import sk.r3n.example.ora.TABLE;
import sk.r3n.example.ora.dto.TBaseTypes;
import sk.r3n.example.ora.dto.TJoin;
import sk.r3n.example.ora.T_BASE_TYPES;
import sk.r3n.example.ora.T_JOIN;
import sk.r3n.jdbc.OraSqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.ColumnFunction;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;
import sk.r3n.sql.Query.Delete;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Query.Update;
import sk.r3n.util.DateUtil;
import sk.r3n.util.FileUtil;
import sk.r3n.util.ScDf;

public class OraTest {

    public OraTest() {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (IOException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void init() {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.0.0.9:1521:xe", "test", "test");

            SqlUtil.runSqlScript(connection, OraTest.class.getResourceAsStream("/clean_ora.sql"));
            SqlUtil.runSqlScript(connection, OraTest.class.getResourceAsStream("/install_ora.sql"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Test
    public void oraTest() throws Exception {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@10.0.0.9:1521:xe", "test", "test");

            assertTrue(connection.getAutoCommit());

            insertTest(connection);

            selectsTest(connection);

            updateTest(connection);

            deleteTest(connection);

        } catch (IOException | ClassNotFoundException | SQLException e) {
            fail(e.getMessage());
        } finally {
            SqlUtil.close(connection);
        }
    }

    private void insertTest(Connection connection) throws SQLException, IOException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();
        Dto dto = new Dto();

        for (int i = 0; i < 10; i++) {
            TBaseTypesSO tBaseTypesSO = new TBaseTypesSO();
            tBaseTypesSO.setTShort((short) i);
            tBaseTypesSO.setTInteger(i);
            tBaseTypesSO.setTLong((long) i);
            tBaseTypesSO.setTBigDecimal(new BigDecimal(i));
            tBaseTypesSO.setTStringChar("char" + i);
            tBaseTypesSO.setTStringClob("clob" + i);
            tBaseTypesSO.setTStringVarchar2("steč" + i);
            tBaseTypesSO.setTStringScdf(ScDf.toScDf(tBaseTypesSO.getTStringVarchar2()));
            tBaseTypesSO.setTBlob(createFile(i));
            tBaseTypesSO.setTTimeStamp(new Date());
            tBaseTypesSO.setTDate(DateUtil.getDateOnly(tBaseTypesSO.getTTimeStamp()));
            tBaseTypesSO.setTBoolean(i % 2 == 0);

            TBaseTypes tBaseTypes = new TBaseTypes();
            dto.objToObj(tBaseTypesSO, tBaseTypes);

            Object[] row = dto.toArray(tBaseTypes, T_BASE_TYPES.columns());
            row[0] = SEQUENCE.TEST_SEQUENCE();

            Insert insert = Query.INSERT().INTO(TABLE.T_BASE_TYPES(), T_BASE_TYPES.columns()).VALUES(row).RETURNING(T_BASE_TYPES.ID());
            Long id = (Long) sqlBuilder.insert(connection, insert);

            for (int j = 0; j < 10; j++) {
                TJoinSO tJoinSO = new TJoinSO();
                tJoinSO.setTBaseTypesFk(id);
                tJoinSO.setTJoinString("join" + i);

                TJoin tJoin = new TJoin();
                dto.objToObj(tJoinSO, tJoin);

                row = dto.toArray(tJoin, T_JOIN.columns());
                row[0] = SEQUENCE.TEST_SEQUENCE();

                insert = Query.INSERT().INTO(TABLE.T_JOIN(), T_JOIN.columns()).VALUES(row);
                sqlBuilder.insert(connection, insert);
            }
        }
    }

    private void selectsTest(Connection connection) throws SQLException, InstantiationException, IllegalAccessException {
        paginatedSubSelectTest(connection);
        subSelectTest(connection);
        paginatedUnionSelectTest(connection);
        unionSelectTest(connection);
        paginatedSelectTest(connection);
        selectTest(connection);
    }

    private void paginatedSubSelectTest(Connection connection) throws SQLException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query.SELECT(T_BASE_TYPES.columns()).page(0, 5)
                .FROM(Query.SELECT(T_BASE_TYPES.columns()).FROM(TABLE.T_BASE_TYPES()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 5);

        select = Query.SELECT(T_BASE_TYPES.columns()).page(0, 5)
                .FROM(Query.SELECT(T_BASE_TYPES.columns())
                        .FROM(TABLE.T_BASE_TYPES())
                        .INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        rows = sqlBuilder.select(connection, select);

        assertTrue(rows.size() == 5);
    }

    private void subSelectTest(Connection connection) throws SQLException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query.SELECT(T_BASE_TYPES.columns())
                .FROM(Query.SELECT(T_BASE_TYPES.columns()).FROM(TABLE.T_BASE_TYPES()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 10);

        select = Query.SELECT(T_BASE_TYPES.columns())
                .FROM(Query.SELECT(T_BASE_TYPES.columns())
                        .FROM(TABLE.T_BASE_TYPES())
                        .INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID()))
                .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 100);
    }

    private void paginatedUnionSelectTest(Connection connection) throws SQLException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query.SELECT(T_BASE_TYPES.columns("tst1")).page(0, 5)
                .FROM(
                        Query.SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")),
                        Query.SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 5);

        select = Query.SELECT(T_BASE_TYPES.columns("tst1")).page(0, 5)
                .FROM(
                        Query.SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst1")),
                        Query.SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 5);
    }

    private void unionSelectTest(Connection connection) throws SQLException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query.SELECT(T_BASE_TYPES.columns("tst1"))
                .FROM(
                        Query.SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")),
                        Query.SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 20);

        select = Query.SELECT(T_BASE_TYPES.columns("tst1"))
                .FROM(
                        Query.SELECT(T_BASE_TYPES.columns("tst1")).FROM(TABLE.T_BASE_TYPES("tst1")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst1")),
                        Query.SELECT(T_BASE_TYPES.columns("tst2")).FROM(TABLE.T_BASE_TYPES("tst2")).INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID("tst2"))
                )
                .ORDER_BY(T_BASE_TYPES.T_INTEGER("tst1"), Order.ASC);

        rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 200);
    }

    private void paginatedSelectTest(Connection connection) throws SQLException, InstantiationException, IllegalAccessException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();
        Dto dto = new Dto();

        Select select = Query.SELECT(T_BASE_TYPES.columns()).page(0, 1).FROM(TABLE.T_BASE_TYPES()).ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC);

        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 1);
        TBaseTypes tBaseTypes = new TBaseTypes();
        dto.fill(tBaseTypes, rows.get(0), T_BASE_TYPES.columns());
        assertTrue(tBaseTypes.getTInteger() == 0);
        TBaseTypesSO tBaseTypesSO = new TBaseTypesSO();
        dto.objToObj(tBaseTypes, tBaseTypesSO);
        assertTrue(tBaseTypesSO.getTInteger() == 0);

        List<TBaseTypesSO> typesList = sqlBuilder.select(connection, select, TBaseTypesSO.class);
        assertTrue(typesList.size() == 1);
        TBaseTypesSO tBaseTypesSO2 = typesList.get(0);
        assertTrue(Objects.equals(tBaseTypesSO2.getId(), tBaseTypesSO.getId()));

        for (int i = 0; i < 10; i++) {
            select = Query.SELECT(T_BASE_TYPES.columns()).page(i, 10)
                    .FROM(TABLE.T_BASE_TYPES())
                    .INNER_JOIN(TABLE.T_JOIN(), T_JOIN.T_BASE_TYPES_FK(), T_BASE_TYPES.ID())
                    .ORDER_BY(T_BASE_TYPES.T_INTEGER(), Order.ASC)
                    .ORDER_BY(T_JOIN.T_JOIN_STRING(), Order.ASC);

            rows = sqlBuilder.select(connection, select);
            assertTrue(rows.size() == 10);
        }
    }

    private void selectTest(Connection connection) throws SQLException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query.SELECT(T_BASE_TYPES.columns()).COUNT()
                .FROM(TABLE.T_BASE_TYPES());
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 1);
        assertTrue(((Integer) rows.get(0)[0]) == 10);

        select = Query.SELECT(new ColumnFunction("FUNC1", "MAX({0})", T_BASE_TYPES.T_INTEGER().getDataType(), T_BASE_TYPES.T_INTEGER()))
                .FROM(TABLE.T_BASE_TYPES());
        rows = sqlBuilder.select(connection, select);
        assertTrue(rows.size() == 1);
        assertTrue(((Integer) rows.get(0)[0]) == 9);
    }

    private void updateTest(Connection connection) throws SQLException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();

        Date date = new Date();

        Update update = Query.UPDATE(TABLE.T_BASE_TYPES())
                .SET(T_BASE_TYPES.T_TIME_STAMP(), date)
                .WHERE(T_BASE_TYPES.T_INTEGER(), Condition.MORE, 5);
        sqlBuilder.update(connection, update);

        update = Query.UPDATE(TABLE.T_BASE_TYPES())
                .SET(T_BASE_TYPES.T_TIME_STAMP(), date)
                .WHERE(T_BASE_TYPES.ID(), Condition.IN, Query.SELECT(T_BASE_TYPES.ID()).FROM(TABLE.T_BASE_TYPES())
                        .WHERE(T_BASE_TYPES.T_TIME_STAMP(), Condition.LESS, date));

        sqlBuilder.update(connection, update);
    }

    private void deleteTest(Connection connection) throws SQLException {
        OraSqlBuilder sqlBuilder = new OraSqlBuilder();

        Delete delete = Query.DELETE().FROM(TABLE.T_BASE_TYPES())
                .WHERE(T_BASE_TYPES.T_INTEGER(), Condition.MORE, 5);
        sqlBuilder.delete(connection, delete);

        delete = Query.DELETE().FROM(TABLE.T_BASE_TYPES())
                .WHERE(T_BASE_TYPES.ID(), Condition.IN, Query.SELECT(T_BASE_TYPES.ID()).FROM(TABLE.T_BASE_TYPES())
                        .WHERE(T_BASE_TYPES.T_INTEGER(), Condition.EQUALS_LESS, 5));

        sqlBuilder.delete(connection, delete);
    }

    private File createFile(int i) throws IOException {
        File file = File.createTempFile("file" + i, ".tst");
        FileUtil.write(file, Integer.toString(i).getBytes());
        return file;
    }

}
