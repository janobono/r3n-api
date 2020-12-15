/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.text.Normalizer;

/**
 * {@link String} diacritics and case utility.
 *
 * <p>
 * This class delivers functionality to replace diacritic characters
 * for non diacritic equivalent or non diacritic lower cases equivalent.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class ScDf {

    /**
     * Replace diacritic characters for non diacritic equivalent.
     *
     * @param text {@link String} to replace characters
     * @return {@link String} witch replaced characters
     */
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

    /**
     * Replace diacritic characters for non diacritic lower case equivalent.
     *
     * @param text {@link String} to replace characters
     * @return {@link String} witch replaced characters
     */
    public static String toScDf(String text) {
        return toDf(text).toLowerCase();
    }

}
