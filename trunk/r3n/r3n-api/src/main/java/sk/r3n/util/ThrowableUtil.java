package sk.r3n.util;

public class ThrowableUtil {

    public static String createMessage(Throwable throwable) {
        StringBuilder result = new StringBuilder();
        if (throwable.getLocalizedMessage() != null) {
            result.append(throwable.getLocalizedMessage());
        } else {
            result.append(throwable.toString());
        }
        if (throwable.getCause() != null) {
            result.append("\r\n");
            result.append(createMessage(throwable.getCause()));
        }
        return result.toString();
    }
}
