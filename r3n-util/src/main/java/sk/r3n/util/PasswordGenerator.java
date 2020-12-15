/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.security.SecureRandom;

/**
 * This class provides methods to generate passwords.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class PasswordGenerator {

    /**
     * Generated password type.
     */
    public enum Type {
        NUMERIC, ALPHA, ALPHA_NUMERIC
    }

    private static final char[] NUMERIC = new char[]{
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    private static final char[] ALPHA = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] ALPHA_NUMERIC = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    private final SecureRandom secureRandom;

    /**
     * Default constructor.
     *
     * @throws Exception if no Provider supports a SecureRandomSpi implementation for the specified algorithm
     */
    public PasswordGenerator() throws Exception {
        this("SHA1PRNG");
    }

    /**
     * Constructor with algorithm parameter.
     *
     * @param algorithm the name of the RNG algorithm
     * @throws Exception if no Provider supports a SecureRandomSpi implementation for the specified algorithm
     */
    public PasswordGenerator(String algorithm) throws Exception {
        super();
        secureRandom = SecureRandom.getInstance(algorithm);
    }

    /**
     * Generate password.
     *
     * @param type   password type
     * @param length password length
     * @return generated password
     */
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
                    stringBuffer.append(ALPHA_NUMERIC[secureRandom.nextInt(ALPHA_NUMERIC.length)]);
                }
                break;
        }
        return stringBuffer.toString();
    }
}
