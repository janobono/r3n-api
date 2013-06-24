package sk.r3n.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class FileUtil {

    public static void append(File file, File data) {
        fileStreaming(file, data, true);
    }

    public static void append(File file, byte[] data) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, true));
            os.write(data, 0, data.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(os);
        }
    }

    public static void copy(File source, File target) {
        fileStreaming(source, target, false);
    }

    private static void fileStreaming(File source, File target, boolean append) {
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] buffer = new byte[1024];
            is = new BufferedInputStream(new FileInputStream(source));
            os = new BufferedOutputStream(new FileOutputStream(target, append));
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(is);
            close(os);
        }
    }

    public static void fileToStream(File source, OutputStream target) {
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] buffer = new byte[1024];
            is = new BufferedInputStream(new FileInputStream(source));
            os = new BufferedOutputStream(target);
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(is);
            close(os);
        }
    }

    public static void streamToStream(InputStream source, OutputStream target) {
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] buffer = new byte[1024];
            is = new BufferedInputStream(source);
            os = new BufferedOutputStream(target);
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(is);
            close(os);
        }
    }

    public static void streamToFile(InputStream source, File target) {
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] buffer = new byte[1024];
            is = new BufferedInputStream(source);
            os = new BufferedOutputStream(new FileOutputStream(target, false));
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(is);
            close(os);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
            }
        }
    }

    public static byte[] read(File file) {
        InputStream is = null;
        OutputStream os = null;
        try {
            byte[] buffer = new byte[1024];
            is = new BufferedInputStream(new FileInputStream(file));
            os = new ByteArrayOutputStream();
            int bytesRead = 0;
            while (bytesRead != -1) {
                bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            return ((ByteArrayOutputStream) os).toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(is);
            close(os);
        }
    }

    public static Object readObject(File file) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new FileInputStream(file));
            return is.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(is);
        }
    }

    public static void write(File file, byte[] data) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, false));
            os.write(data, 0, data.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(os);
        }
    }

    public static void writeObject(File file, Object object) {
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(outputStream);
        }
    }
}
