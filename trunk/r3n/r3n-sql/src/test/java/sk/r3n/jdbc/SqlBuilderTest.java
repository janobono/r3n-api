package sk.r3n.jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import sk.r3n.example.TABLE;
import sk.r3n.example.*;
import sk.r3n.sql.*;

public class SqlBuilderTest {

    private static final Log LOG = LogFactory.getLog(SqlBuilderTest.class);

    public SqlBuilderTest() {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void selectTest() throws Exception {
        SqlBuilder[] sqlBuilders = new SqlBuilder[]{new PostgreSqlBuilder(), new OraSqlBuilder()};
        for (SqlBuilder sqlBuilder : sqlBuilders) {

            // SELECT without condition
            Query query = new Query();
            query.SELECT(PERSON.columns()).FROM(TABLE.PERSON());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT DISTINCT without condition
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).DISTINCT().FROM(TABLE.PERSON());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT COUNT without condition
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).COUNT().FROM(TABLE.PERSON());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT interval without condition
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).interval(0, 100).FROM(TABLE.PERSON());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).FROM(TABLE.PERSON()).WHERE(PERSON.FIRST_NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT DISTINCT with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).DISTINCT().FROM(TABLE.PERSON()).WHERE(PERSON.FIRST_NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT COUNT with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).COUNT().FROM(TABLE.PERSON()).WHERE(PERSON.FIRST_NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT interval with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).interval(0, 100).FROM(TABLE.PERSON()).WHERE(PERSON.FIRST_NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            //SELECT interval with inner select and multiple joins
            List<String> list = new ArrayList<String>();
            list.add("l1");
            list.add("l2");
            sqlBuilder.params().clear();
            query.SELECT(PERSON.columns()).interval(0, 100)
                    .FROM(TABLE.PERSON())
                    .INNER_JOIN(TABLE.ADDRESS(), ADDRESS.PERSON_FK(), PERSON.ID())
                    .LEFT_JOIN(TABLE.ADDRESS(), ADDRESS.PERSON_FK(), PERSON.ID())
                    .RIGHT_JOIN(TABLE.ADDRESS(), ADDRESS.PERSON_FK(), PERSON.ID())
                    .FULL_JOIN(TABLE.ADDRESS(), ADDRESS.PERSON_FK(), PERSON.ID())
                    .WHERE(PERSON.FIRST_NAME(), Condition.EQUALS, "1")
                    .AND(PERSON.FIRST_NAME(), Condition.IN, new String[]{"in1", "in2"})
                    .OR(PERSON.FIRST_NAME(), Condition.IN, list)
                    .OR_IN().AND(PERSON.FIRST_NAME(), Condition.EQUALS, "2").AND_IN().AND(PERSON.FIRST_NAME(), Condition.EQUALS, "2")
                    .AND_NEXT().AND(PERSON.FIRST_NAME(), Condition.EQUALS, "3").OUT().OR(PERSON.FIRST_NAME(), Condition.EQUALS, "4")
                    .ORDER_BY(PERSON.FIRST_NAME(), Order.ASC).ORDER_BY(PERSON.ID(), Order.DESC);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }
        }
    }

    @Test
    public void innerSelectColumnTest() throws Exception {
        SqlBuilder[] sqlBuilders = new SqlBuilder[]{new PostgreSqlBuilder(), new OraSqlBuilder()};
        for (SqlBuilder sqlBuilder : sqlBuilders) {

            Query innerQuery = new Query();
            innerQuery.SELECT(new Function("COUNT({0})", PERSON.ID("in")))
                    .FROM(TABLE.PERSON("in"))
                    .WHERE(PERSON.BIRTH_DATE("in"), Condition.EQUALS, PERSON.BIRTH_DATE())
                    .AND(PERSON.BIRTH_DATE("in"), Condition.MORE, new Date());

            InnerSelect innerSelect = new InnerSelect(innerQuery, "INNER_COUNT", DataType.INTEGER);

            Query query = new Query();
            query.SELECT(PERSON.FIRST_NAME(), innerSelect).FROM(TABLE.PERSON());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }
        }
    }

    @Test
    public void insertTest() throws Exception {
        SqlBuilder[] sqlBuilders = new SqlBuilder[]{new PostgreSqlBuilder(), new OraSqlBuilder(), new H2SqlBuilder()};
        for (SqlBuilder sqlBuilder : sqlBuilders) {

            // INSERT without returning value
            Criterion criterion = new Criterion(null, Condition.EQUALS, this, "CURRENT_TIMESTAMP", Operator.AND);
            Query query = new Query();
            query.INSERT().INTO(TABLE.PERSON(), PERSON.columns()).VALUES(
                    10L, criterion, "creator", (short) 5, "0123456789",
                    "first_name", "first_name", "last_name", "last_name", new Date(), "note");

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toInsert(query));
                LOG.debug(sqlBuilder.params());
            }

            // INSERT with returning value
            Column[] columns;
            Object[] values;
            if (sqlBuilder instanceof H2SqlBuilder) {
                columns = Arrays.copyOfRange(PERSON.columns(), 1, PERSON.columns().length);
                values = new Object[]{
                    new Date(), "creator", (short) 5, "0123456789",
                    "first_name", "first_name", "last_name", "last_name", new Date(), "note"};
            } else {
                columns = PERSON.columns();
                values = new Object[]{
                    SEQUENCE.H_SEQUENCE(), new Date(), "creator", (short) 5, "0123456789",
                    "first_name", "first_name", "last_name", "last_name", new Date(), "note"};
            }

            sqlBuilder.params().clear();
            query.INSERT().INTO(TABLE.PERSON(), columns).VALUES(values).RETURNING(PERSON.ID());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toInsert(query));
                LOG.debug(sqlBuilder.params());
            }
        }
    }

    @Test
    public void updateTest() throws Exception {
        //UPDATE is same on supported DBs
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        // UPDATE without condition
        Query query = new Query();
        query.UPDATE(TABLE.PERSON()).SET(PERSON.LAST_NAME(), "test");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with simple null value condition
        sqlBuilder.params().clear();
        query.UPDATE(TABLE.PERSON()).SET(PERSON.LAST_NAME(), "test").WHERE(PERSON.LAST_NAME(), Condition.IS_NOT_NULL, null);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with simple not null value condition
        sqlBuilder.params().clear();
        query.UPDATE(TABLE.PERSON()).SET(PERSON.LAST_NAME(), "test").WHERE(PERSON.ID(), Condition.MORE, 100L);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with functional condition
        sqlBuilder.params().clear();
        query.UPDATE(TABLE.PERSON()).SET(PERSON.LAST_NAME(), "test").WHERE(PERSON.ID(), Condition.MORE, 100L, "funct({0}) {1} funct(?)");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with two columns condition
        sqlBuilder.params().clear();
        query.UPDATE(TABLE.PERSON()).SET(PERSON.LAST_NAME(), "test").WHERE(PERSON.LAST_NAME(), Condition.EQUALS, PERSON.ID());

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with two columns functional condition
        sqlBuilder.params().clear();
        query.UPDATE(TABLE.PERSON()).SET(PERSON.LAST_NAME(), "test").WHERE(PERSON.LAST_NAME(), Condition.EQUALS, PERSON.ID(), "funct({0}) {1} funct({2})");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with inner query condition
        Query innerQuery = new Query();
        innerQuery.SELECT(PERSON.LAST_NAME("inner")).DISTINCT().FROM(TABLE.PERSON("inner")).WHERE(PERSON.LAST_NAME("inner"), Condition.LIKE, "%test%");

        sqlBuilder.params().clear();
        query.UPDATE(TABLE.PERSON()).SET(PERSON.LAST_NAME(), "test").WHERE(PERSON.LAST_NAME(), Condition.IN, innerQuery);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }
    }

    @Test
    public void deleteTest() throws Exception {
        //DELETE is same on supported DBs
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        // DELETE without condition
        Query query = new Query();
        query.DELETE().FROM(TABLE.PERSON());

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with simple null value condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(TABLE.PERSON()).WHERE(PERSON.LAST_NAME(), Condition.IS_NOT_NULL, null);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with simple not null value condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(TABLE.PERSON()).WHERE(PERSON.ID(), Condition.MORE, 100L);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with functional condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(TABLE.PERSON()).WHERE(PERSON.ID(), Condition.MORE, 100L, "funct({0}) {1} funct(?)");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with two columns condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(TABLE.PERSON()).WHERE(PERSON.LAST_NAME(), Condition.EQUALS, PERSON.ID());

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with two columns functional condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(TABLE.PERSON()).WHERE(PERSON.LAST_NAME(), Condition.EQUALS, PERSON.ID(), "funct({0}) {1} funct({2})");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with inner query condition
        Query innerQuery = new Query();
        innerQuery.SELECT(PERSON.LAST_NAME("inner")).DISTINCT().FROM(TABLE.PERSON("inner")).WHERE(PERSON.LAST_NAME("inner"), Condition.LIKE, "%test%");

        sqlBuilder.params().clear();
        query.DELETE().FROM(TABLE.PERSON()).WHERE(PERSON.LAST_NAME(), Condition.IN, innerQuery);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }
    }

}
