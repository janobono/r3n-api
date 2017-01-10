package sk.r3n.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtil {

    public static String createMessage(Throwable throwable) {
        String result;
        if (throwable.getLocalizedMessage() != null) {
            result = throwable.getLocalizedMessage();
        } else {
            result = throwable.toString();
        }
        return result;
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            printWriter.flush();
        }
        return stringWriter.toString();
    }
}
