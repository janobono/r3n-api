package sk.r3n.jdbc.ora;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.OracleContainer;
import sk.r3n.dto.Dto;
import sk.r3n.jdbc.OraSqlBuilder;
import sk.r3n.jdbc.SqlBuilder;
import sk.r3n.jdbc.SqlUtil;
import sk.r3n.sql.ColumnFunction;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Order;
import sk.r3n.sql.Query;
import sk.r3n.sql.Query.Delete;
import sk.r3n.sql.Query.Insert;
import sk.r3n.sql.Query.Select;
import sk.r3n.sql.Query.Update;
import sk.r3n.util.FileUtil;
import sk.r3n.util.ScDf;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OraIT {

    private static final OracleContainer oracle = new OracleContainer("oracleinanutshell/oracle-xe-11g");

    @BeforeAll
    public static void init() {
        oracle.start();
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(oracle.getJdbcUrl(), oracle.getUsername(), oracle.getPassword());
            try {
                SqlUtil.execute(connection, "DROP SEQUENCE TEST_SEQUENCE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                SqlUtil.execute(connection, "DROP TABLE T_BASE_TYPES");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                SqlUtil.execute(connection, "DROP TABLE T_JOIN");
            } catch (Exception e) {
                e.printStackTrace();
            }
            SqlUtil.runSqlScript(connection, OraIT.class.getResourceAsStream("/install_ora.sql"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SqlUtil.close(connection);
        }
    }

    @Test
    public void test() throws Exception {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(oracle.getJdbcUrl(), oracle.getUsername(), oracle.getPassword());

            assertThat(connection.getAutoCommit()).isTrue();

            insertTest(connection);

            selectsTest(connection);

            updateTest(connection);

            deleteTest(connection);
        } finally {
            SqlUtil.close(connection);
        }
    }

    private TBaseTypesDto createTBaseTypesDto(int i) throws Exception {
        TBaseTypesDto tBaseTypesDto = new TBaseTypesDto();
        tBaseTypesDto.setTShort((short) i);
        tBaseTypesDto.setTInteger(i);
        tBaseTypesDto.setTLong((long) i);
        tBaseTypesDto.setTBigDecimal(new BigDecimal(i));
        tBaseTypesDto.setTStringChar("char" + i);
        tBaseTypesDto.setTStringClob("clob" + i);
        tBaseTypesDto.setTStringVarchar2("steč" + i);
        tBaseTypesDto.setTStringScdf(ScDf.toScDf(tBaseTypesDto.getTStringVarchar2()));
        tBaseTypesDto.setTBlob(createFile(i));
        tBaseTypesDto.setTTimeStamp(LocalDateTime.now());
        tBaseTypesDto.setTDate(LocalDate.now());
        tBaseTypesDto.setTBoolean(i % 2 == 0);
        return tBaseTypesDto;
    }

    private void insertTest(Connection connection) throws Exception {
        // create SqlBuilder instance
        SqlBuilder sqlBuilder = new OraSqlBuilder();
        // create Dto instance
        Dto dto = new Dto();

        for (int i = 0; i < 10; i++) {
            // create test TBaseTypesDto object
            TBaseTypesDto tBaseTypesDto = createTBaseTypesDto(i);
            // transform to array of objects
            Object[] row = dto.toArray(tBaseTypesDto, MetaColumnTBaseTypes.columns());
            // set id value to r3n sequence object
            row[0] = MetaSequence.TEST_SEQUENCE.sequence();
            // prepare insert object
            Insert insert = Query
                    .INSERT()
                    .INTO(MetaTable.T_BASE_TYPES.table(), MetaColumnTBaseTypes.columns())
                    .VALUES(row)
                    .RETURNING(MetaColumnTBaseTypes.ID.column());
            // execute insert witch returning value
            Long id = (Long) sqlBuilder.insert(connection, insert);

            for (int j = 0; j < 10; j++) {
                // create test TJoinDto object
                TJoinDto tJoinDto = new TJoinDto();
                tJoinDto.setTBaseTypesFk(id);
                tJoinDto.setTJoinString("join" + i);
                // transform to array of objects
                row = dto.toArray(tJoinDto, MetaColumnTJoin.columns());
                // set id value to r3n sequence object
                row[0] = MetaSequence.TEST_SEQUENCE.sequence();
                // prepare insert object
                insert = Query
                        .INSERT()
                        .INTO(MetaTable.T_JOIN.table(), MetaColumnTJoin.columns())
                        .VALUES(row);
                // execute insert
                sqlBuilder.insert(connection, insert);
            }
        }
    }

    private void selectsTest(Connection connection) throws Exception {
        selectTest(connection);
        paginatedSelectTest(connection);
        subSelectTest(connection);
        paginatedSubSelectTest(connection);
        unionSelectTest(connection);
        paginatedUnionSelectTest(connection);
    }

    private void selectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns()).COUNT()
                .FROM(MetaTable.T_BASE_TYPES.table());
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(1);
        assertThat((Integer) rows.get(0)[0]).isEqualTo(10);

        select = Query
                .SELECT(
                        new ColumnFunction(
                                "FUNC1",
                                "MAX({0})",
                                MetaColumnTBaseTypes.T_INTEGER.column().getDataType(),
                                MetaColumnTBaseTypes.T_INTEGER.column())
                )
                .FROM(MetaTable.T_BASE_TYPES.table());
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(1);
        assertThat((Integer) rows.get(0)[0]).isEqualTo(9);
    }

    private void paginatedSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns()).page(0, 1)
                .FROM(MetaTable.T_BASE_TYPES.table())
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(1);
        assertThat(rows.get(0)[2]).isEqualTo(0);
        List<TBaseTypesDto> dtoObjects = sqlBuilder.select(connection, select, TBaseTypesDto.class);
        assertThat(dtoObjects.size()).isEqualTo(1);
        assertThat(dtoObjects.get(0).getId()).isEqualTo(rows.get(0)[0]);

        for (int i = 0; i < 10; i++) {
            select = Query
                    .SELECT(MetaColumnTBaseTypes.columns()).page(i, 10)
                    .FROM(MetaTable.T_BASE_TYPES.table())
                    .INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column())
                    .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC)
                    .ORDER_BY(MetaColumnTJoin.T_JOIN_STRING.column(), Order.ASC);
            rows = sqlBuilder.select(connection, select);
            assertThat(rows.size()).isEqualTo(10);
        }
    }

    private void subSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns())
                .FROM(Query
                        .SELECT(MetaColumnTBaseTypes.columns())
                        .FROM(MetaTable.T_BASE_TYPES.table())
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(10);

        select = Query
                .SELECT(MetaColumnTBaseTypes.columns())
                .FROM(Query
                        .SELECT(MetaColumnTBaseTypes.columns())
                        .FROM(MetaTable.T_BASE_TYPES.table())
                        .INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column())
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(100);
    }

    private void paginatedSubSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns()).page(0, 5)
                .FROM(Query
                        .SELECT(MetaColumnTBaseTypes.columns())
                        .FROM(MetaTable.T_BASE_TYPES.table())
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(5);

        select = Query
                .SELECT(MetaColumnTBaseTypes.columns()).page(0, 5)
                .FROM(Query
                        .SELECT(MetaColumnTBaseTypes.columns())
                        .FROM(MetaTable.T_BASE_TYPES.table())
                        .INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column()))
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(5);
    }

    private void unionSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns("tst1"))
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(20);

        select = Query.SELECT(MetaColumnTBaseTypes.columns("tst1"))
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(200);
    }

    private void paginatedUnionSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns("tst1")).page(0, 5)
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(5);

        select = Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).page(0, 5)
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(5);
    }

    private void updateTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();
        LocalDateTime localDateTime = LocalDateTime.now();

        Update update = Query
                .UPDATE(MetaTable.T_BASE_TYPES.table())
                .SET(MetaColumnTBaseTypes.T_TIME_STAMP.column(), localDateTime)
                .WHERE(MetaColumnTBaseTypes.T_INTEGER.column(), Condition.MORE, 5);
        sqlBuilder.update(connection, update);

        update = Query
                .UPDATE(MetaTable.T_BASE_TYPES.table())
                .SET(MetaColumnTBaseTypes.T_TIME_STAMP.column(), localDateTime)
                .WHERE(MetaColumnTBaseTypes.ID.column(), Condition.IN, Query
                        .SELECT(MetaColumnTBaseTypes.ID.column())
                        .FROM(MetaTable.T_BASE_TYPES.table())
                        .WHERE(MetaColumnTBaseTypes.T_TIME_STAMP.column(), Condition.LESS, localDateTime)
                );
        sqlBuilder.update(connection, update);
    }

    private void deleteTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new OraSqlBuilder();

        Delete delete = Query
                .DELETE()
                .FROM(MetaTable.T_BASE_TYPES.table())
                .WHERE(MetaColumnTBaseTypes.T_INTEGER.column(), Condition.MORE, 5);
        sqlBuilder.delete(connection, delete);

        delete = Query
                .DELETE()
                .FROM(MetaTable.T_BASE_TYPES.table())
                .WHERE(MetaColumnTBaseTypes.ID.column(), Condition.IN, Query
                        .SELECT(MetaColumnTBaseTypes.ID.column())
                        .FROM(MetaTable.T_BASE_TYPES.table())
                        .WHERE(MetaColumnTBaseTypes.T_INTEGER.column(), Condition.EQUALS_LESS, 5)
                );
        sqlBuilder.delete(connection, delete);
    }

    private File createFile(int i) throws Exception {
        File file = File.createTempFile("file" + i, ".tst");
        FileUtil.write(file, Integer.toString(i).getBytes());
        return file;
    }
}
