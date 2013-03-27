package sk.r3n.util;

public class R3NException extends Exception {

    public static String createErrorMessage(Throwable throwable) {
        StringBuilder result = new StringBuilder();
        if (throwable instanceof R3NException) {
            R3NException r3nException = (R3NException) throwable;
            result.append(r3nException.getLocalizedMessage());
            if (r3nException.getCause() != null) {
                result.append(" \r\n");
                result.append(createErrorMessage(r3nException.getCause()));
            }
        } else if (throwable instanceof RuntimeException) {
            result.append(throwable.toString());
        } else {
            result.append(throwable.getLocalizedMessage());
        }
        return result.toString();
    }
    private int errorCode;

    public R3NException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public R3NException(String message, int errorCode, Throwable throwable) {
        super(message, throwable);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
