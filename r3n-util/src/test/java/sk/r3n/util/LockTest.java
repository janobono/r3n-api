package sk.r3n.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class LockTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockTest.class);

    @Test
    public void test() throws Exception {
        // create lock object
        Lock lock = new Lock();

        // create temporary file
        File file = File.createTempFile("app", "pid");
        LOGGER.info("Lock file {}", file.getAbsolutePath());

        // lock file
        lock.lock(file.getAbsolutePath());
        assertThat(lock.isLocked()).isTrue();

        // unlock file
        lock.unlock();
        assertThat(lock.isLocked()).isFalse();
    }

}
