package sk.r3n.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.r3n.protocol.zip.Handler;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilTest.class);

    @Getter
    @Setter
    @ToString
    public static class TestData implements Serializable {
        private String text;
    }

    public static class TestOutputStream extends OutputStream {

        private StringBuilder string;

        public TestOutputStream() {
            clear();
        }

        @Override
        public void write(int b) throws IOException {
            this.string.append((char) b);
        }

        public String toString() {
            return this.string.toString();
        }

        public void clear() {
            string = new StringBuilder();
        }
    }

    @Test
    public void test() throws Exception {
        // createTempFile
        File tmpFile = FileUtil.createTempFile("Test", ".tst", null);
        assertThat(tmpFile.isFile()).isTrue();
        LOGGER.info("Temporary file {}", tmpFile);

        // createTempDir
        File tmpDir = FileUtil.createTempDir(tmpFile.getParentFile());
        assertThat(tmpDir.isDirectory()).isTrue();
        LOGGER.info("Temporary directory {}", tmpDir);

        // to URL and to File
        URL url = FileUtil.toURL(tmpFile);
        assertThat(url).isNotNull();
        LOGGER.info("file to URL {}", url);
        File file = FileUtil.toFile(url);
        assertThat(file.isFile()).isTrue();
        LOGGER.info("URL to file {}", file);

        // write byte[]
        FileUtil.write(tmpFile, "test".getBytes());
        assertThat(new String(FileUtil.read(tmpFile))).isEqualTo("test");
        FileUtil.append(tmpFile, " and test".getBytes());
        assertThat(new String(FileUtil.read(tmpFile))).isEqualTo("test and test");

        // copy file
        File copyFile = FileUtil.createTempFile("Copy", ".tst", null);
        FileUtil.copy(tmpFile, copyFile);
        assertThat(new String(FileUtil.read(tmpFile))).isEqualTo(new String(FileUtil.read(copyFile)));
        FileUtil.append(tmpFile, copyFile);
        assertThat(new String(FileUtil.read(tmpFile))).isEqualTo("test and testtest and test");

        // streams
        TestOutputStream output = new TestOutputStream();

        FileUtil.fileToStream(tmpFile, output);
        LOGGER.info("fileToStream = {}", output);
        output.clear();

        InputStream input = new ByteArrayInputStream("stream".getBytes());
        FileUtil.streamToFile(input, tmpFile);
        assertThat(new String(FileUtil.read(tmpFile))).isEqualTo("stream");
        FileUtil.close(input);

        input = new ByteArrayInputStream("stream".getBytes());
        FileUtil.streamToStream(input, output);
        LOGGER.info("streamToStream = {}", output);
        FileUtil.close(input);
        output.clear();

        // write object
        TestData testData = new TestData();
        testData.setText("test text");
        FileUtil.writeObject(tmpFile, testData);
        assertThat(testData.getText()).isEqualTo(((TestData) FileUtil.readObject(tmpFile)).getText());

        // Zip
        File zip001File = FileUtil.createTempFile("zip", ".001", tmpDir);
        FileUtil.write(zip001File, "zip".getBytes());

        File[] zipFiles = new File[]{
                zip001File,
                FileUtil.createTempFile("zip", ".002", tmpDir),
                FileUtil.createTempFile("zip", ".003", tmpDir),
                FileUtil.createTempFile("zip", ".004", tmpDir)
        };
        File zipFile = FileUtil.createTempFile("arch", ".zip", tmpDir);
        FileUtil.zip(zipFile, zipFiles);

        Handler.register();
        url = new URL("zip:" + zipFile.getAbsolutePath() + "!" + zip001File.getName());
        URLConnection urlConnection = url.openConnection();
        FileUtil.streamToStream(urlConnection.getInputStream(), output);
        LOGGER.info("zip = {}", output);

        // Delete
        FileUtil.delete(tmpFile);
        assertThat(tmpFile.isFile()).isFalse();

        FileUtil.delete(copyFile);
        assertThat(copyFile.isFile()).isFalse();

        FileUtil.delete(tmpDir);
        assertThat(tmpDir.isDirectory()).isFalse();
    }

}
