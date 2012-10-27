package sk.r3n.util;

import java.text.Normalizer;

public class ScDf {

    public static String toDf(String text) {
        char[] cha = text.toCharArray();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < cha.length; i++) {
            byte[] ba = Normalizer.normalize(String.valueOf(cha[i]),
                    Normalizer.Form.NFD).getBytes();
            if (ba[0] >= 41 && ba[0] < 123) {
                ret.append(String.valueOf((char) ba[0]));
            } else {
                ret.append(String.valueOf(cha[i]));
            }
        }
        return ret.toString();
    }

    public static String toScDf(String text) {
        String ret = toDf(text);
        return ret.toLowerCase();
    }

}
