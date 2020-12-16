package sk.r3n.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ScDfTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScDfTest.class);

    private static final String TEXT = "ľščťžýáíéňäúô ĽŠČŤŽÝÁÍÉŇÄÚÔ";
    private static final String DF_RESULT = "lsctzyaienauo LSCTZYAIENAUO";
    private static final String SCDF_RESULT = "lsctzyaienauo lsctzyaienauo";

    @Test
    public void test() {
        // remove diacritics
        assertThat(ScDf.toDf(TEXT)).isEqualTo(DF_RESULT);
        LOGGER.info("ScDf.toDf({}) = {}", TEXT, DF_RESULT);
        // remove diacritics and transform to lower case
        assertThat(ScDf.toScDf(TEXT)).isEqualTo(SCDF_RESULT);
        LOGGER.info("ScDf.toScDf({}) = {}", TEXT, SCDF_RESULT);
    }
}
