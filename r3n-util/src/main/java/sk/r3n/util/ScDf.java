/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.text.Normalizer;

public class ScDf {

    public static String toDf(String text) {
        char[] cha = text.toCharArray();
        StringBuilder ret = new StringBuilder();
        for (char aCha : cha) {
            byte[] ba = Normalizer.normalize(String.valueOf(aCha), Normalizer.Form.NFD).getBytes();
            if (ba[0] >= 41 && ba[0] < 123) {
                ret.append(String.valueOf((char) ba[0]));
            } else {
                ret.append(String.valueOf(aCha));
            }
        }
        return ret.toString();
    }

    public static String toScDf(String text) {
        return toDf(text).toLowerCase();
    }

}
