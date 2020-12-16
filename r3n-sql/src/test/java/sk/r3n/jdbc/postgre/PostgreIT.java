package sk.r3n.jdbc.postgre;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import sk.r3n.dto.Dto;
import sk.r3n.jdbc.PostgreSqlBuilder;
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
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgreIT {

    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:12-alpine");

    @BeforeAll
    public static void init() {
        postgres.start();
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
            SqlUtil.runSqlScript(connection, PostgreIT.class.getResourceAsStream("/clean_postgres.sql"));
            SqlUtil.runSqlScript(connection, PostgreIT.class.getResourceAsStream("/install_postgres.sql"));
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
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

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
        tBaseTypesDto.setTStringText("text" + i);
        tBaseTypesDto.setTStringVarchar("varcharč" + i);
        tBaseTypesDto.setTStringScdf(ScDf.toScDf(tBaseTypesDto.getTStringVarchar()));
        tBaseTypesDto.setTBlob(createFile(i));
        tBaseTypesDto.setTTimeStamp(LocalDateTime.now());
        tBaseTypesDto.setTTime(LocalTime.now());
        tBaseTypesDto.setTDate(LocalDate.now());
        tBaseTypesDto.setTBoolean(i % 2 == 0);
        return tBaseTypesDto;
    }

    private void insertTest(Connection connection) throws Exception {
        // create SqlBuilder instance
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();
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
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        // select count(*)
        //   from (
        //          select
        //                  t1.id as col0,
        //                  t1.t_short as col1,
        //                  t1.t_integer as col2,
        //                  t1.t_long as col3,
        //                  t1.t_big_decimal as col4,
        //                  t1.t_string_char as col5,
        //                  t1.t_string_text as col6,
        //                  t1.t_string_varchar as col7,
        //                  t1.t_string_scdf as col8,
        //                  t1.t_blob as col9,
        //                  t1.t_time_stamp as col10,
        //                  t1.t_time as col11,
        //                  t1.t_date as col12,
        //                  t1.t_boolean as col13
        //          from t_base_types t1
        //        ) as count_result
        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns())
                .COUNT()
                .FROM(MetaTable.T_BASE_TYPES.table());
        // execute select
        List<Object[]> rows = sqlBuilder.select(connection, select);
        // result will have one row
        assertThat(rows.size()).isEqualTo(1);
        // rows count is 10
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
        // select MAX(t1.t_integer) as col0 from t_base_types t1
        rows = sqlBuilder.select(connection, select);
        // result will have one row
        assertThat(rows.size()).isEqualTo(1);
        // max value in this column is 9
        assertThat((Integer) rows.get(0)[0]).isEqualTo(9);
    }

    private void paginatedSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        // select *
        //   from (
        //          select
        //                  t1.id as col0,
        //                  t1.t_short as col1,
        //                  t1.t_integer as col2,
        //                  t1.t_long as col3,
        //                  t1.t_big_decimal as col4,
        //                  t1.t_string_char as col5,
        //                  t1.t_string_text as col6,
        //                  t1.t_string_varchar as col7,
        //                  t1.t_string_scdf as col8,
        //                  t1.t_blob as col9,
        //                  t1.t_time_stamp as col10,
        //                  t1.t_time as col11,
        //                  t1.t_date as col12,
        //                  t1.t_boolean as col13
        //          from t_base_types t1
        //          order by t1.t_integer ASC
        //        ) as paginated_result limit ? offset ?;
        // limit=1, offset=0
        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns()).page(0, 1)
                .FROM(MetaTable.T_BASE_TYPES.table())
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        // execute select
        List<Object[]> rows = sqlBuilder.select(connection, select);
        // result will have one row
        assertThat(rows.size()).isEqualTo(1);
        // transform to dto object
        assertThat(rows.get(0)[2]).isEqualTo(0);
        // execute select and result will be transformed to dto objects
        List<TBaseTypesDto> dtoObjects = sqlBuilder.select(connection, select, TBaseTypesDto.class);
        // result will have one row
        assertThat(dtoObjects.size()).isEqualTo(1);
        // same SELECT so same results just different format dto/array
        assertThat(dtoObjects.get(0).getId()).isEqualTo(rows.get(0)[0]);

        for (int i = 0; i < 10; i++) {
            // select * from (
            //                  select
            //                          t1.id as col0,
            //                          t1.t_short as col1,
            //                          t1.t_integer as col2,
            //                          t1.t_long as col3,
            //                          t1.t_big_decimal as col4,
            //                          t1.t_string_char as col5,
            //                          t1.t_string_text as col6,
            //                          t1.t_string_varchar as col7,
            //                          t1.t_string_scdf as col8,
            //                          t1.t_blob as col9,
            //                          t1.t_time_stamp as col10,
            //                          t1.t_time as col11,
            //                          t1.t_date as col12,
            //                          t1.t_boolean as col13
            //                  from t_base_types t1
            //                  INNER join t_join t2 on (t2.t_base_types_fk = t1.id)
            //                  order by t1.t_integer ASC, t2.t_join_string ASC
            //               ) as paginated_result limit ? offset ?;
            // limit = 10, offset = 0, 10, 20, 30, 40, 50, 60, 70, 80, 90
            select = Query
                    .SELECT(MetaColumnTBaseTypes.columns()).page(i, 10)
                    .FROM(MetaTable.T_BASE_TYPES.table())
                    .INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column())
                    .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC)
                    .ORDER_BY(MetaColumnTJoin.T_JOIN_STRING.column(), Order.ASC);
            rows = sqlBuilder.select(connection, select);
            // select is paginated so result will have 10 rows
            assertThat(rows.size()).isEqualTo(10);
        }
    }

    private void subSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();
        // select col0, col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13
        //   from (
        //           select
        //                   t1.id as col0,
        //                   t1.t_short as col1,
        //                   t1.t_integer as col2,
        //                   t1.t_long as col3,
        //                   t1.t_big_decimal as col4,
        //                   t1.t_string_char as col5,
        //                   t1.t_string_text as col6,
        //                   t1.t_string_varchar as col7,
        //                   t1.t_string_scdf as col8,
        //                   t1.t_blob as col9,
        //                   t1.t_time_stamp as col10,
        //                   t1.t_time as col11,
        //                   t1.t_date as col12,
        //                   t1.t_boolean as col13
        //           from t_base_types t1
        //        ) as union_result  order by col2 ASC;
        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns())
                .FROM(Query
                        .SELECT(MetaColumnTBaseTypes.columns())
                        .FROM(MetaTable.T_BASE_TYPES.table())
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(10);

        // select col0, col1, col2, col3, col4, col5, col6, col7, col8, col9, col10, col11, col12, col13
        //   from (
        //           select
        //                   t1.id as col0,
        //                   t1.t_short as col1,
        //                   t1.t_integer as col2,
        //                   t1.t_long as col3,
        //                   t1.t_big_decimal as col4,
        //                   t1.t_string_char as col5,
        //                   t1.t_string_text as col6,
        //                   t1.t_string_varchar as col7,
        //                   t1.t_string_scdf as col8,
        //                   t1.t_blob as col9,
        //                   t1.t_time_stamp as col10,
        //                   t1.t_time as col11,
        //                   t1.t_date as col12,
        //                   t1.t_boolean as col13
        //           from t_base_types t1
        //           INNER join t_join t2 on (t2.t_base_types_fk = t1.id)
        //        ) as union_result order by col2 ASC
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
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

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
                        .INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column())
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column(), Order.ASC);
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(5);
    }

    private void unionSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns("tst1"))
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(20);

        select = Query
                .SELECT(MetaColumnTBaseTypes.columns("tst1"))
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(200);
    }

    private void paginatedUnionSelectTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Select select = Query
                .SELECT(MetaColumnTBaseTypes.columns("tst1")).page(0, 5)
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        List<Object[]> rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(5);

        select = Query
                .SELECT(MetaColumnTBaseTypes.columns("tst1")).page(0, 5)
                .FROM(
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst1")).FROM(MetaTable.T_BASE_TYPES.table("tst1")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst1")),
                        Query.SELECT(MetaColumnTBaseTypes.columns("tst2")).FROM(MetaTable.T_BASE_TYPES.table("tst2")).INNER_JOIN(MetaTable.T_JOIN.table(), MetaColumnTJoin.T_BASE_TYPES_FK.column(), MetaColumnTBaseTypes.ID.column("tst2"))
                )
                .ORDER_BY(MetaColumnTBaseTypes.T_INTEGER.column("tst1"), Order.ASC);
        rows = sqlBuilder.select(connection, select);
        assertThat(rows.size()).isEqualTo(5);
    }

    private void updateTest(Connection connection) throws Exception {
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();
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
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        Delete delete = Query
                .DELETE()
                .FROM(MetaTable.T_BASE_TYPES.table())
                .WHERE(MetaColumnTBaseTypes.T_INTEGER.column(), Condition.MORE, 5);
        sqlBuilder.delete(connection, delete);

        delete = Query
                .DELETE()
                .FROM(MetaTable.T_BASE_TYPES.table())
                .WHERE(MetaColumnTBaseTypes.ID.column(), Condition.IN,
                        Query
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
