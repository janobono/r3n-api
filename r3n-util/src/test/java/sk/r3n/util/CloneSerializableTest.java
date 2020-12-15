package sk.r3n.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;

public class CloneSerializableTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloneSerializableTest.class);

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class TestObject implements Serializable {
        String text;
    }

    @Test
    public void test() {
        TestObject source = new TestObject("Test");
        LOGGER.info("source = {}", source);
        TestObject target = (TestObject) CloneSerializable.clone(source);
        LOGGER.info("target = {}", target);
        assertThat(source).isNotEqualTo(target);
        assertThat(source.getText()).isEqualTo(target.getText());
    }
}
