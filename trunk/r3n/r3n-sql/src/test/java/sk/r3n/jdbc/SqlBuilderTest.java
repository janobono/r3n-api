package sk.r3n.jdbc;

import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import static sk.r3n.example.TABLE.*;
import sk.r3n.example.*;
import sk.r3n.sql.Column;
import sk.r3n.sql.Condition;
import sk.r3n.sql.Query;
import sk.r3n.sql.Table;

public class SqlBuilderTest {

    private static final Log LOG = LogFactory.getLog(SqlBuilderTest.class);

    @Test
    public void deleteTest() throws Exception {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        SqlBuilder[] sqlBuilders = new SqlBuilder[]{new PostgreSqlBuilder(), new OraSqlBuilder()};
        for (SqlBuilder sqlBuilder : sqlBuilders) {

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
            Table table = ACCOUNT();
            table.setAlias("inner");
            Column column = ACCOUNT.NAME();
            column.setTable(table);
            innerQuery.SELECT(column).DISTINCT().FROM(table).WHERE(column, Condition.LIKE, "%test%");
            
            sqlBuilder.params().clear();
            query.DELETE().FROM(ACCOUNT()).WHERE(ACCOUNT.NAME(), Condition.IN, innerQuery);

            if (LOG.isDebugEnabled()) {
                LOG.debug(sqlBuilder.toDelete(query));
                LOG.debug(sqlBuilder.params());
            }
        }
    }

}
