package sk.r3n.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Lock {

    private static final Log LOG = LogFactory.getLog(Lock.class);

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
                LOG.error(
                        MessageFormat.format(
                                ResourceBundle.getBundle(Lock.class.getCanonicalName()).getString("CREATE_LOCK"),
                                new Object[]{lockName}
                        )
                );
                unlock();
            }
        } catch (Exception e) {
            LOG.error(
                    MessageFormat.format(
                            ResourceBundle.getBundle(Lock.class.getCanonicalName()).getString("CREATE_LOCK"),
                            new Object[]{lockName}
                    ),
                    e
            );
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
        } catch (Exception e) {
            LOG.error("Unlock", e);
        }
        try {
            if (lockChannel != null) {
                lockChannel.close();
            }
        } catch (Exception e) {
            LOG.error("Unlock", e);
        }
        locked = false;
    }

}
