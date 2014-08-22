package sk.r3n.jdbc;

import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import static sk.r3n.example.TABLE.*;
import sk.r3n.example.*;
import sk.r3n.sql.*;

public class SqlBuilderTest {

    private static final Log LOG = LogFactory.getLog(SqlBuilderTest.class);

    @Test
    public void selectTest() throws Exception {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        SqlBuilder[] sqlBuilders = new SqlBuilder[]{new PostgreSqlBuilder(), new OraSqlBuilder()};
        for (SqlBuilder sqlBuilder : sqlBuilders) {

            // SELECT without condition
            Query query = new Query();
            query.SELECT(ACCOUNT.columns()).FROM(ACCOUNT());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT DISTINCT without condition
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).DISTINCT().FROM(ACCOUNT());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT COUNT without condition
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).COUNT().FROM(ACCOUNT());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT interval without condition
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).interval(0, 100).FROM(ACCOUNT());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT DISTINCT with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).DISTINCT().FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT COUNT with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).COUNT().FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            // SELECT interval with simple null value condition
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).interval(0, 100).FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.IS_NOT_NULL, null);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }

            //SELECT interval with inner select and multiple joins
            sqlBuilder.params().clear();
            query.SELECT(ACCOUNT.columns()).interval(0, 100)
                    .FROM(ACCOUNT())
                    .FULL_JOIN(ACCOUNT_ACTIVITY(), ACCOUNT_ACTIVITY.ACCOUNT_FK(), ACCOUNT.ID())
                    .INNER_JOIN(ACCOUNT_ACTIVITY(), ACCOUNT_ACTIVITY.ACCOUNT_FK(), ACCOUNT.ID())
                    .LEFT_JOIN(ACCOUNT_ACTIVITY(), ACCOUNT_ACTIVITY.ACCOUNT_FK(), ACCOUNT.ID())
                    .RIGHT_JOIN(ACCOUNT_ACTIVITY(), ACCOUNT_ACTIVITY.ACCOUNT_FK(), ACCOUNT.ID())
                    .WHERE(ACCOUNT.NAME(), Condition.EQUALS, "1").OR_IN().AND(ACCOUNT.NAME(), Condition.EQUALS, "2")
                    .AND_NEXT().AND(ACCOUNT.NAME(), Condition.EQUALS, "3").OUT().OR(ACCOUNT.NAME(), Condition.EQUALS, "4")
                    .ORDER_BY(ACCOUNT.NAME(), Order.ASC).ORDER_BY(ACCOUNT.ID(), Order.DESC);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toSelect(query));
                LOG.debug(sqlBuilder.params());
            }
        }
    }

    @Test
    public void insertTest() throws Exception {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        SqlBuilder[] sqlBuilders = new SqlBuilder[]{new PostgreSqlBuilder(), new OraSqlBuilder()};
        for (SqlBuilder sqlBuilder : sqlBuilders) {

            // INSERT without returning value
            Query query = new Query();
            query.INSERT().INTO(TABLE.ACCOUNT(), ACCOUNT.columns()).VALUES(1L, 10, "name", "name scdf", "note");

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toInsert(query));
                LOG.debug(sqlBuilder.params());
            }

            // INSERT with returning value
            sqlBuilder.params().clear();
            query.INSERT().INTO(TABLE.ACCOUNT(), ACCOUNT.columns()).VALUES(SEQUENCE.H_SEQUENCE(), 10, "name", "name scdf", "note").RETURNING(ACCOUNT.ID());

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toInsert(query));
                LOG.debug(sqlBuilder.params());
            }
        }
    }

    @Test
    public void updateTest() throws Exception {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        //UPDATE is same on supported DBs
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        // UPDATE without condition
        Query query = new Query();
        query.UPDATE(ACCOUNT()).SET(ACCOUNT.NAME(), "test");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with simple null value condition
        sqlBuilder.params().clear();
        query.UPDATE(ACCOUNT()).SET(ACCOUNT.NAME(), "test").WHERE(ACCOUNT.NAME(), Condition.IS_NOT_NULL, null);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with simple not null value condition
        sqlBuilder.params().clear();
        query.UPDATE(ACCOUNT()).SET(ACCOUNT.NAME(), "test").WHERE(ACCOUNT.ID(), Condition.MORE, 100L);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with functional condition
        sqlBuilder.params().clear();
        query.UPDATE(ACCOUNT()).SET(ACCOUNT.NAME(), "test").WHERE(ACCOUNT.ID(), Condition.MORE, 100L, "funct({0}) {1} funct(?)");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with two columns condition
        sqlBuilder.params().clear();
        query.UPDATE(ACCOUNT()).SET(ACCOUNT.NAME(), "test").WHERE(ACCOUNT.NAME(), Condition.EQUALS, ACCOUNT.ID());

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with two columns functional condition
        sqlBuilder.params().clear();
        query.UPDATE(ACCOUNT()).SET(ACCOUNT.NAME(), "test").WHERE(ACCOUNT.NAME(), Condition.EQUALS, ACCOUNT.ID(), "funct({0}) {1} funct({2})");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }

        // UPDATE with inner query condition
        Query innerQuery = new Query();
        innerQuery.SELECT(ACCOUNT.NAME("inner")).DISTINCT().FROM(ACCOUNT("inner")).WHERE(ACCOUNT.NAME("inner"), Condition.LIKE, "%test%");

        sqlBuilder.params().clear();
        query.UPDATE(ACCOUNT()).SET(ACCOUNT.NAME(), "test").WHERE(ACCOUNT.NAME(), Condition.IN, innerQuery);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toUpdate(query));
            LOG.debug(sqlBuilder.params());
        }
    }

    @Test
    public void deleteTest() throws Exception {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        //DELETE is same on supported DBs
        SqlBuilder sqlBuilder = new PostgreSqlBuilder();

        // DELETE without condition
        Query query = new Query();
        query.DELETE().FROM(ACCOUNT());

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with simple null value condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.IS_NOT_NULL, null);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with simple not null value condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(ACCOUNT()).WHERE(ACCOUNT.ID(), Condition.MORE, 100L);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with functional condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(ACCOUNT()).WHERE(ACCOUNT.ID(), Condition.MORE, 100L, "funct({0}) {1} funct(?)");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with two columns condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.EQUALS, ACCOUNT.ID());

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with two columns functional condition
        sqlBuilder.params().clear();
        query.DELETE().FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.EQUALS, ACCOUNT.ID(), "funct({0}) {1} funct({2})");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }

        // DELETE with inner query condition
        Query innerQuery = new Query();
        innerQuery.SELECT(ACCOUNT.NAME("inner")).DISTINCT().FROM(ACCOUNT("inner")).WHERE(ACCOUNT.NAME("inner"), Condition.LIKE, "%test%");

        sqlBuilder.params().clear();
        query.DELETE().FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.IN, innerQuery);

        if (LOG.isDebugEnabled()) {
            LOG.debug(sqlBuilder.toDelete(query));
            LOG.debug(sqlBuilder.params());
        }
    }

}
