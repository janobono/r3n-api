package sk.r3n.util;

public class R3NException extends RuntimeException {

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
