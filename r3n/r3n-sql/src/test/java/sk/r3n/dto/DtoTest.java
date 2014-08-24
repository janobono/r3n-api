package sk.r3n.dto;

import java.util.Arrays;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import sk.r3n.example.ACCOUNT;
import sk.r3n.example.dto.Account;
import sk.r3n.jdbc.SqlBuilderTest;

public class DtoTest {

    private static final Log LOG = LogFactory.getLog(SqlBuilderTest.class);

    @Test
    public void selectTest() throws Exception {
        try {
            LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Account account = new Account();
        Dto dto = new Dto();
        dto.fill(account, new Object[]{10L, (short) 5, "name", "name_scdf", "note"}, ACCOUNT.columns());
        LOG.debug(account);
        LOG.debug(Arrays.toString(dto.toArray(account, ACCOUNT.columns())));
        Account accountCopy = new Account();
        dto.objToObj(account, accountCopy);
        LOG.debug(accountCopy);
    }
}
