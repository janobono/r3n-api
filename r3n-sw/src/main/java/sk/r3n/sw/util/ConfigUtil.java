package sk.r3n.sw.util;

import java.util.StringTokenizer;

public class ConfigUtil {

    public static final String KEY_SEPARATOR = "++";

    public static String createKey(String group, int id) {
        return createKey(group, id, KEY_SEPARATOR);
    }

    public static String createKey(String group, int id, String separator) {
        return new StringBuilder().append(group).append(separator).append(id).toString();
    }

    public static Object[] parseKey(String key) {
        return parseKey(key, KEY_SEPARATOR);
    }

    public static Object[] parseKey(String key, String separator) {
        Object[] result = new Object[2];
        StringTokenizer tokenizer = new StringTokenizer(key, separator);
        result[0] = tokenizer.nextToken();
        result[1] = Integer.parseInt(tokenizer.nextToken());
        return result;
    }

}
