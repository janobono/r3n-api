/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.security.SecureRandom;

public class PasswordGenerator {

    public enum Type {

        NUMERIC, ALPHA, ALPHA_NUMERIC;

    }

    private static final char[] NUMERIC = new char[]{
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    private static final char[] ALPHA = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] ALPHA_NUMBERIC = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    private SecureRandom secureRandom;

    public PasswordGenerator() throws Exception {
        this("SHA1PRNG");
    }

    public PasswordGenerator(String algorithm) throws Exception {
        super();
        secureRandom = SecureRandom.getInstance(algorithm);
    }

    public String generatePassword(Type type, int length) {
        if (length <= 0) {
            return "";
        }
        switch (type) {
            case NUMERIC:
            case ALPHA:
            case ALPHA_NUMERIC:
                break;
            default:
                throw new IllegalArgumentException();
        }
        StringBuilder stringBuffer = new StringBuilder();
        switch (type) {
            case NUMERIC:
                for (int i = 0; i < length; i++) {
                    stringBuffer.append(NUMERIC[secureRandom.nextInt(NUMERIC.length)]);
                }
                break;
            case ALPHA:
                for (int i = 0; i < length; i++) {
                    stringBuffer.append(ALPHA[secureRandom.nextInt(ALPHA.length)]);
                }
                break;
            default:
                for (int i = 0; i < length; i++) {
                    stringBuffer.append(ALPHA_NUMBERIC[secureRandom.nextInt(ALPHA_NUMBERIC.length)]);
                }
                break;
        }
        return stringBuffer.toString();
    }

}
