package sk.r3n.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordGeneratorTest.class);

    @Test
    public void test() throws Exception {
        // Default algorithm
        PasswordGenerator passwordGenerator = new PasswordGenerator();

        String password = passwordGenerator.generatePassword(PasswordGenerator.Type.NUMERIC, 10);
        assertThat(password.matches("[0-9]+")).isTrue();
        assertThat(password.length()).isEqualTo(10);
        LOGGER.info("passwordGenerator.generatePassword({},{}) = {}", PasswordGenerator.Type.NUMERIC, 10, password);

        password = passwordGenerator.generatePassword(PasswordGenerator.Type.ALPHA, 15);
        assertThat(password.matches("[a-zA-Z]+")).isTrue();
        assertThat(password.length()).isEqualTo(15);
        LOGGER.info("passwordGenerator.generatePassword({},{}) = {}", PasswordGenerator.Type.ALPHA, 15, password);

        password = passwordGenerator.generatePassword(PasswordGenerator.Type.ALPHA_NUMERIC, 12);
        assertThat(password.matches("[0-9a-zA-Z]+")).isTrue();
        assertThat(password.length()).isEqualTo(12);
        LOGGER.info("passwordGenerator.generatePassword({},{}) = {}", PasswordGenerator.Type.ALPHA_NUMERIC, 12, password);

    }
}
