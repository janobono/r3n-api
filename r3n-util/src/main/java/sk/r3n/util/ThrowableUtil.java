/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * {@link Throwable} utility methods.
 *
 * <p>
 * This class provides easy-to-use methods to convert {@link Throwable} to message and stack trace as a {@link String}.
 *
 * @author janobono
 * @since 18 August 2014
 */
public class ThrowableUtil {

    /**
     * Transform the given {@link Throwable} to {@link String} message.
     *
     * @param throwable the {@link Throwable} to transform
     * @return message {@link String}
     */
    public static String createMessage(Throwable throwable) {
        String result;
        if (throwable.getLocalizedMessage() != null) {
            result = throwable.getLocalizedMessage();
        } else {
            result = throwable.toString();
        }
        return result;
    }

    /**
     * Transform the given {@link Throwable} to {@link String} stack trace.
     *
     * @param throwable the {@link Throwable} to transform
     * @return stack trace {@link String}
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            printWriter.flush();
        }
        return stringWriter.toString();
    }
}
