/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * This class provides easy-to-use methods to create xml content.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class PlainXML {

    public static final String NEW_LINE = "\r\n";

    public static final String OPEN_START = "<";

    public static final String OPEN_END = "</";

    public static final String SIMPLE_CLOSE = "/>";

    public static final String CLOSE = ">";

    /**
     * XML prolog.
     *
     * @param version  xml version
     * @param encoding xml encoding
     * @return xml prolog {@link String}
     */
    public static String getProlog(String version, String encoding) {
        return OPEN_START + "?xml version=\"" + version + "\" encoding=\"" + encoding + "\"?" + CLOSE;
    }

    /**
     * XML begin tag.
     *
     * @param tag         tag name
     * @param simpleClose simple closed tag flag
     * @return xml begin tag {@link String}
     */
    public static String beginTag(String tag, boolean simpleClose) {
        return beginTag(tag, null, simpleClose);
    }

    /**
     * XML begin tag.
     *
     * @param tag         tag name
     * @param attributes  tag attributes map
     * @param simpleClose simple closed tag flag
     * @return xml begin tag {@link String}
     */
    public static String beginTag(String tag, Map<String, String> attributes,
                                  boolean simpleClose) {
        StringBuilder result = new StringBuilder();
        result.append(OPEN_START);
        result.append(tag);
        if (attributes != null) {
            for (Entry<String, String> entry : attributes.entrySet()) {
                result.append(" ");
                result.append(encodeXmlChars(entry.getKey()));
                result.append("=\"");
                result.append(encodeXmlChars(entry.getValue()));
                result.append("\"");
            }
        }
        if (simpleClose) {
            result.append(SIMPLE_CLOSE);
        } else {
            result.append(CLOSE);
        }
        return result.toString();
    }

    /**
     * XML end tag.
     *
     * @param tag tag name
     * @return xml end tag {@link String}
     */
    public static String endTag(String tag) {
        return OPEN_END + tag + CLOSE;
    }

    /**
     * Filter XML special characters and replace them with valid equivalent.
     *
     * @param string text with possible special characters
     * @return filtered text
     */
    public static String encodeXmlChars(String string) {
        int length = string.length();
        char[] characters = new char[length];
        string.getChars(0, length, characters, 0);
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < length; i++) {
            encoded.append(getXmlEscapeChar(characters[i]));
        }
        return encoded.toString();
    }

    /**
     * Return special character valid equivalent.
     *
     * @param c character to check
     * @return special character valid equivalent
     */
    public static String getXmlEscapeChar(char c) {
        switch (c) {
            case ('<'):
                return "&lt;";
            case ('>'):
                return "&gt;";
            case ('&'):
                return "&amp;";
            case ('\''):
                return "&apos;";
            case ('\"'):
                return "&quot;";
        }
        return "" + c;
    }
}
