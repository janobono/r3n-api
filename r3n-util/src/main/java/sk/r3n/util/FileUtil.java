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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    public static void append(File file, File data) {
        fileStreaming(data, file, true);
    }

    public static void append(File file, byte[] data) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file, true))) {
            os.write(data, 0, data.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static void streamToFile(InputStream source, File target) {
        try (InputStream is = new BufferedInputStream(source);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(target, false))) {
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

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
            }
        }
    }

    public static byte[] read(File file) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file));
                OutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
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
        }
    }

    public static Object readObject(File file) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            return is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(File file, byte[] data) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file, false))) {
            os.write(data, 0, data.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeObject(File file, Object object) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(object);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(File file) {
        if (file != null) {
            if (!file.delete()) {
                throw new RuntimeException("Can't delete file!");
            }
        }
    }

    public static File createTempFile(String prefix, String suffix, File dir) {
        try {
            return File.createTempFile(prefix, suffix, dir);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static File createTempDir(File dir) {
        File resultDir = createTempFile("TMP", "DIR", dir);
        File file = new File(resultDir.getAbsolutePath() + "ECTORY");
        resultDir.delete();
        resultDir = file;
        resultDir.mkdir();
        return resultDir;
    }

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
