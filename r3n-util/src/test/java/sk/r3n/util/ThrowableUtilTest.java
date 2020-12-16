package sk.r3n.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ThrowableUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableUtilTest.class);

    private static final String EXCEPTION_MESSAGE = "Test exception";

    @Test
    public void test() {
        try {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        } catch (Exception e) {
            // create message
            assertThat(ThrowableUtil.createMessage(e)).isEqualTo(EXCEPTION_MESSAGE);
            LOGGER.info(ThrowableUtil.createMessage(e));
            // get stack trace
            assertThat(ThrowableUtil.getStackTrace(e)).isNotEmpty();
            LOGGER.info(ThrowableUtil.getStackTrace(e));
        }
    }
}
