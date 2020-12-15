package sk.r3n.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PlainXMLTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainXMLTest.class);

    @Test
    public void test() {
        // xml prolog
        assertThat(PlainXML.getProlog("1.0", "utf-8")).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        LOGGER.info("PlainXML.getProlog({},{}) = {}", "1.0", "utf-8", PlainXML.getProlog("1.0", "utf-8"));

        // begin tag
        assertThat(PlainXML.beginTag("a", false)).isEqualTo("<a>");
        LOGGER.info("PlainXML.beginTag({},{}) = {}", "a", false, PlainXML.beginTag("a", false));
        assertThat(PlainXML.beginTag("a", true)).isEqualTo("<a/>");
        LOGGER.info("PlainXML.beginTag({},{}) = {}", "a", true, PlainXML.beginTag("a", true));

        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        assertThat(PlainXML.beginTag("a", attributes, false)).isEqualTo("<a key=\"value\">");
        LOGGER.info("PlainXML.beginTag({},{},{}) = {}", "a", attributes, false, PlainXML.beginTag("a", attributes, false));
        assertThat(PlainXML.beginTag("a", attributes, true)).isEqualTo("<a key=\"value\"/>");
        LOGGER.info("PlainXML.beginTag({},{},{}) = {}", "a", attributes, true, PlainXML.beginTag("a", attributes, true));

        // end tag
        assertThat(PlainXML.endTag("a")).isEqualTo("</a>");
        LOGGER.info("PlainXML.endTag({}) = {}", "a", PlainXML.endTag("a"));

        // encode XML chars
        assertThat(PlainXML.encodeXmlChars("<>&\'\"")).isEqualTo("&lt;&gt;&amp;&apos;&quot;");
        LOGGER.info("PlainXML.encodeXmlChars({}) = {}", "<>&\'\"", PlainXML.encodeXmlChars("<>&\'\""));

        assertThat(PlainXML.getXmlEscapeChar('<')).isEqualTo("&lt;");
        LOGGER.info("PlainXML.getXmlEscapeChar({}) = {}", '<', PlainXML.getXmlEscapeChar('<'));

        assertThat(PlainXML.getXmlEscapeChar('>')).isEqualTo("&gt;");
        LOGGER.info("PlainXML.getXmlEscapeChar({}) = {}", '>', PlainXML.getXmlEscapeChar('>'));

        assertThat(PlainXML.getXmlEscapeChar('&')).isEqualTo("&amp;");
        LOGGER.info("PlainXML.getXmlEscapeChar({}) = {}", '&', PlainXML.getXmlEscapeChar('&'));

        assertThat(PlainXML.getXmlEscapeChar('\'')).isEqualTo("&apos;");
        LOGGER.info("PlainXML.getXmlEscapeChar({}) = {}", '\'', PlainXML.getXmlEscapeChar('\''));

        assertThat(PlainXML.getXmlEscapeChar('\"')).isEqualTo("&quot;");
        LOGGER.info("PlainXML.getXmlEscapeChar({}) = {}", '\"', PlainXML.getXmlEscapeChar('\"'));
    }
}
