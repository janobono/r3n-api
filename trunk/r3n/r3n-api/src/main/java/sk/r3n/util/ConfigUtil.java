package sk.r3n.util;

import java.util.StringTokenizer;

public class ConfigUtil {

    public static String createKey(String group, int id) {
        return group + "++" + id;
    }

    public static Object[] parseKey(String key) {
        Object[] result = new Object[2];
        StringTokenizer tokenizer = new StringTokenizer(key, "++");
        result[0] = tokenizer.nextToken();
        result[1] = Integer.parseInt(tokenizer.nextToken());
        return result;
    }
}
