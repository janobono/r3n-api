package sk.r3n.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptorTest.class);

    private static final String DATA = "Test data";

    @Test
    public void test() throws Exception {
        Encryptor encryptor = new Encryptor();

        encryptor.createKey("key".getBytes());

        // byte array
        byte[] result = encryptor.encrypt(DATA.getBytes());
        LOGGER.info(new String(result));
        result = encryptor.decrypt(result);
        assertThat(new String(result)).isEqualTo(DATA);
        LOGGER.info(new String(result));

        // stream
        File inputFile = FileUtil.createTempFile("INP", ".test", null);
        FileUtil.write(inputFile, DATA.getBytes());
        File outputFile = FileUtil.createTempFile("OUT", ".test", null);


        InputStream input = new FileInputStream(inputFile);
        OutputStream output = new FileOutputStream(outputFile);
        encryptor.encrypt(input, output);
        LOGGER.info(new String(FileUtil.read(outputFile)));
        input = new FileInputStream(outputFile);
        output = new FileUtilTest.TestOutputStream();
        encryptor.decrypt(input, output);
        assertThat(output.toString()).isEqualTo(DATA);
        LOGGER.info(output.toString());

        FileUtil.delete(outputFile);
        FileUtil.delete(inputFile);
    }
}
