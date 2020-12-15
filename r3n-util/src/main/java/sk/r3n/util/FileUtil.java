/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * {@link File} utility methods.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Append source file content to target file.
     *
     * @param file target file
     * @param data source file
     */
    public static void append(File file, File data) {
        fileStreaming(data, file, true);
    }

    /**
     * Append source data to target file.
     *
     * @param file target file
     * @param data source data
     */
    public static void append(File file, byte[] data) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file, true))) {
            os.write(data, 0, data.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copy file.
     *
     * @param source source file
     * @param target target file
     */
    public static void copy(File source, File target) {
        fileStreaming(source, target, false);
    }

    private static void fileStreaming(File source, File target, boolean append) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(source));
             OutputStream os = new BufferedOutputStream(new FileOutputStream(target, append))) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write file to stream.
     *
     * @param source source file
     * @param target target output stream
     */
    public static void fileToStream(File source, OutputStream target) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(source));
             OutputStream os = new BufferedOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write source stream to target stream.
     *
     * @param source source input stream
     * @param target target output stream
     */
    public static void streamToStream(InputStream source, OutputStream target) {
        try (InputStream is = new BufferedInputStream(source);
             OutputStream os = new BufferedOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write stream to file.
     *
     * @param source source input stream
     * @param target target file
     */
    public static void streamToFile(InputStream source, File target) {
        try (
                InputStream is = new BufferedInputStream(source);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(target, false))
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close closeable.
     *
     * @param closeable object to close
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
                LOGGER.warn("close", ex);
            }
        }
    }

    /**
     * Read file as byte array.
     *
     * @param file source file
     * @return file content as byte array
     */
    public static byte[] read(File file) {
        try (
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read java object from source file.
     *
     * @param file source file
     * @return java object
     */
    public static Object readObject(File file) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            return is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write byte array to target file.
     *
     * @param file target file
     * @param data byte array
     */
    public static void write(File file, byte[] data) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file, false))) {
            os.write(data, 0, data.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write java object to target file.
     *
     * @param file   target file
     * @param object java object
     */
    public static void writeObject(File file, Object object) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete file.
     *
     * @param file file to delete
     */
    public static void delete(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                for (File subFile : subFiles != null ? subFiles : new File[0]) {
                    delete(subFile);
                }
            }
            if (!file.delete()) {
                throw new RuntimeException("Can't delete file!");
            }
        }
    }

    /**
     * Create temporary file.
     *
     * @param prefix file prefix
     * @param suffix file suffix
     * @param dir    directory
     * @return created file
     */
    public static File createTempFile(String prefix, String suffix, File dir) {
        try {
            return File.createTempFile(prefix, suffix, dir);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create temporary directory.
     *
     * @param dir parent directory
     * @return created directory
     */
    public static File createTempDir(File dir) {
        File resultDir = createTempFile("TMP", "DIR", dir);
        File file = new File(resultDir.getAbsolutePath() + "ECTORY");
        if (!resultDir.delete()) {
            LOGGER.warn("Can't delete {}.", resultDir);
        }
        resultDir = file;
        if (!resultDir.mkdir()) {
            LOGGER.warn("Can't create {}.", resultDir);
        }
        return resultDir;
    }

    /**
     * File to URL.
     *
     * @param file source file
     * @return URL
     */
    public static URL toURL(File file) {
        URL result = null;

        if (file != null) {
            try {
                result = file.toURI().toURL();
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }

        return result;
    }

    /**
     * URL to file.
     *
     * @param url source URL
     * @return file
     */
    public static File toFile(URL url) {
        File result = null;
        if (url != null) {
            try {
                result = new File(url.toURI());
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    /**
     * Create zip archive.
     *
     * @param zipFile target file
     * @param files   archive content
     */
    public static void zip(File zipFile, File[] files) {
        byte[] buffer = new byte[1024];
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : files) {
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);
                try (FileInputStream in = new FileInputStream(file)) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
            }
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
