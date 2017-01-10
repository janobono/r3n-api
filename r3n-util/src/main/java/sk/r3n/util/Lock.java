package sk.r3n.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lock {

    private enum Bundle {
        CREATE_LOCK;

        public String value() {
            return ResourceBundle.getBundle(Lock.class.getCanonicalName()).getString(name());
        }

        public String value(Object... arguments) {
            return MessageFormat.format(value(), arguments);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(Lock.class.getCanonicalName());

    private FileLock lock;

    private FileChannel lockChannel;

    private volatile boolean locked;

    public void lock(String lockName) {
        try {
            File file = new File(lockName);
            lockChannel = new RandomAccessFile(file, "rw").getChannel();
            lock = lockChannel.tryLock();
            locked = lock != null;
            if (!locked) {
                LOGGER.log(Level.SEVERE, Bundle.CREATE_LOCK.value(lockName));
                unlock();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, Bundle.CREATE_LOCK.value(lockName), e);
            unlock();
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        try {
            if (lock != null) {
                lock.release();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unlock", e);
        }
        try {
            if (lockChannel != null) {
                lockChannel.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unlock", e);
        }
        locked = false;
    }

}
