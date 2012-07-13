package sk.r3n.util;

public class R3NException extends Exception {

	private static final long serialVersionUID = 35396454394749955L;

	public static String createErrorMessage(Throwable throwable) {
		StringBuffer result = new StringBuffer();
		if (throwable instanceof R3NException) {
			R3NException vException = (R3NException) throwable;
			result.append(vException.getLocalizedMessage());
			if (vException.getCause() != null) {
				result.append(" \r\n");
				result.append(createErrorMessage(vException.getCause()));
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
		this(message, errorCode, null);
	}

	public R3NException(String message, int errorCode, Throwable throwable) {
		super(message, throwable);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
