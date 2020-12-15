/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * This class provides methods to create file lock. Something like PID.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class Lock {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lock.class);

    private FileLock lock;

    private FileChannel lockChannel;

    private volatile boolean locked;

    /**
     * Lock file.
     *
     * @param lockName file name
     */
    public void lock(String lockName) {
        try {
            File file = new File(lockName);
            lockChannel = new RandomAccessFile(file, "rw").getChannel();
            lock = lockChannel.tryLock();
            locked = lock != null;
            if (!locked) {
                LOGGER.error("Lock {} not created.", lockName);
                unlock();
            }
        } catch (IOException e) {
            LOGGER.error("Lock", e);
            unlock();
        }
    }

    /**
     * @return lock flag
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Unlock file.
     */
    public void unlock() {
        try {
            if (lock != null) {
                lock.release();
            }
        } catch (IOException e) {
            LOGGER.error("Unlock", e);
        }
        try {
            if (lockChannel != null) {
                lockChannel.close();
            }
        } catch (IOException e) {
            LOGGER.error("Unlock", e);
        }
        locked = false;
    }

}
