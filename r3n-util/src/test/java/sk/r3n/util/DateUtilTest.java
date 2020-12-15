package sk.r3n.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtilTest.class);

    @Test
    public void test() throws Exception {
        Date date = new Date();
        LOGGER.info("date = {}", date);

        date = DateUtil.getDateOnly(date);
        LOGGER.info("date only = {}", date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        assertThat(calendar.get(Calendar.HOUR_OF_DAY)).isEqualTo(0);
        assertThat(calendar.get(Calendar.MINUTE)).isEqualTo(0);
        assertThat(calendar.get(Calendar.SECOND)).isEqualTo(0);
        assertThat(calendar.get(Calendar.MILLISECOND)).isEqualTo(0);

        date = DateUtil.getDateOnlyFirstDayOfMonth(date);
        LOGGER.info("first day of month = {}", date);
        calendar.setTime(date);
        assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(1);

        date = DateUtil.getTimeOnly(date);
        LOGGER.info("time only = {}", date);
        calendar.setTime(date);
        assertThat(calendar.get(Calendar.YEAR)).isEqualTo(1970);
        assertThat(calendar.get(Calendar.DAY_OF_YEAR)).isEqualTo(1);
    }
}
