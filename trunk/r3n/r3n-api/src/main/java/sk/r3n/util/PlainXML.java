package sk.r3n.util;

import java.util.Map;
import java.util.Map.Entry;

public class PlainXML {

    public static final String NEW_LINE = "\r\n";
    public static final String OPEN_START = "<";
    public static final String OPEN_END = "</";
    public static final String SIMPLE_CLOSE = "/>";
    public static final String CLOSE = ">";

    public static String getProlog(String version, String encoding) {
        StringBuilder result = new StringBuilder();
        result.append(OPEN_START);
        result.append("?xml version=\"");
        result.append(version);
        result.append("\" encoding=\"");
        result.append(encoding);
        result.append("\"?");
        result.append(CLOSE);
        return result.toString();
    }

    public static String beginTag(String tag, boolean simpleClose) {
        return beginTag(tag, null, simpleClose);
    }

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

    public static String endTag(String tag) {
        StringBuilder result = new StringBuilder();
        result.append(OPEN_END);
        result.append(tag);
        result.append(CLOSE);
        return result.toString();
    }

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
