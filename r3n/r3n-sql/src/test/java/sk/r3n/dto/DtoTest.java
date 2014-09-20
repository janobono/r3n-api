package sk.r3n.dto;

import java.util.Arrays;
import java.util.Date;
import java.util.logging.LogManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import sk.r3n.example.PERSON;
import sk.r3n.example.dto.Person;
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

        Person person = new Person();
        Dto dto = new Dto();
        dto.fill(person, new Object[]{
            10L, new Date(), "creator", (short) 5, "0123456789",
            "first_name", "first_name", "last_name", "last_name", new Date(), "note"}, PERSON.columns());
        LOG.debug(person);
        LOG.debug(Arrays.toString(dto.toArray(person, PERSON.columns())));
        Person personCopy = new Person();
        dto.objToObj(person, personCopy);
        LOG.debug(personCopy);
    }
}
